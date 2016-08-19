package com.app.bricenangue.timeme;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {
    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;
    private IncomingNotification incomingNotification;
    private MySQLiteHelper mySQLiteHelper;
    private ProgressBar loadprogressBar;
    public static boolean eventsareloaded=false;
    private SQLiteShoppingList sqLiteShoppingList;
    private SQLFinanceAccount sqlFinanceAccount;
    private UserLocalStore userLocalStore;


    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if ( "WIFI".equals(ni.getTypeName()))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if ("MOBILE".equals(ni.getTypeName()))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadprogressBar=(ProgressBar)findViewById(R.id.prbar);

        userLocalStore= new UserLocalStore(this);
         sqLiteShoppingList=new SQLiteShoppingList(this);
        sqlFinanceAccount=new SQLFinanceAccount(this);

        mySQLiteHelper=new MySQLiteHelper(this);
        coordinatorLayout=(CoordinatorLayout)findViewById(R.id.coordinateLayoutmainactivity);
        startapp();



    }
    void startapp(){
        loadprogressBar.setVisibility(View.VISIBLE);
        if (haveNetworkConnection()){
            sqlFinanceAccount.reInitializeFinanceSqliteTable();
            mySQLiteHelper.reInitializeSqliteTable();
            sqLiteShoppingList.reInitializeShoppingListSqliteTable();
            loadprogressBar.setIndeterminate(false);
            loadprogressBar.setVisibility(View.INVISIBLE);
            startActivity(new Intent(MainActivity.this,LoginScreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            //getEventsfromMySQL();

        }else{
            showSnackBar();
            //startActivity(new Intent(MainActivity.this, LoginScreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
    }

    private void getEventsfromMySQL() {
        final ServerRequests serverRequests=new ServerRequests(this);
        serverRequests.getCalenderEventInBackgroung(userLocalStore.getUserfullname(),new GetEventsCallbacks() {
            @Override
            public void done(ArrayList<CalendarCollection> returnedeventobject) {
                if(returnedeventobject.size()!=0){

                        saveeventtoSQl(returnedeventobject);
                    serverRequests.getFinanceAccountsAndUserInBackgroung(userLocalStore.getUserfullname(),new FinanceAccountCallbacks() {
                        @Override
                        public void fetchDone(ArrayList<FinanceAccount> returnedAccounts) {
                            if(returnedAccounts.size()!=0){
                                saveAccountLocally(returnedAccounts);
                            }
                        }

                        @Override
                        public void setServerResponse(String serverResponse) {

                        }
                    });
                    serverRequests.getGroceryListsInBackgroung(userLocalStore.getUserfullname(),new GroceryListCallBacks() {
                        @Override
                        public void fetchDone(ArrayList<GroceryList> returnedGroceryLists) {
                            if(returnedGroceryLists.size()!=0){
                                //save to sql
                                saveGroceryListtoSQl(returnedGroceryLists);
                                saveGroceryListtoSQlIncome(returnedGroceryLists);
                                loadprogressBar.setIndeterminate(false);
                                loadprogressBar.setVisibility(View.INVISIBLE);
                                startActivity(new Intent(MainActivity.this,LoginScreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                eventsareloaded=true;
                            }else{
                                showSnackBar();
                                eventsareloaded=false;
                                loadprogressBar.setIndeterminate(false);
                                loadprogressBar.setVisibility(View.INVISIBLE);
                                 startActivity(new Intent(MainActivity.this, LoginScreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                            }
                        }

                        @Override
                        public void setServerResponse(String serverResponse) {

                        }
                    });
                 /**   serverRequests.getItemsInBackgroung(new GetEventsCallbacks() {
                        @Override
                        public void done(ArrayList<CalendarCollection> returnedeventobject) {

                        }

                        @Override
                        public void itemslis(ArrayList<ShoppingItem> returnedShoppingItem) {

                            if(returnedShoppingItem.size()!=0){



                            }else{
                                serverRequests.getGroceryListsInBackgroung(new GroceryListCallBacks() {
                                    @Override
                                    public void fetchDone(ArrayList<GroceryList> returnedGroceryLists) {
                                        if(returnedGroceryLists.size()!=0){
                                            //save to sql
                                            saveGroceryListtoSQl(returnedGroceryLists);
                                            saveGroceryListtoSQlIncome(returnedGroceryLists);
                                            loadprogressBar.setIndeterminate(false);
                                            loadprogressBar.setVisibility(View.INVISIBLE);
                                            startActivity(new Intent(MainActivity.this,LoginScreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                            eventsareloaded=true;
                                        }else{
                                            showSnackBar();
                                            eventsareloaded=false;
                                            loadprogressBar.setIndeterminate(false);
                                            loadprogressBar.setVisibility(View.INVISIBLE);
                                            //startActivity(new Intent(MainActivity.this, LoginScreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                                        }
                                    }

                                    @Override
                                    public void setServerResponse(String serverResponse) {

                                    }
                                });

                            }
                        }

                        @Override
                        public void updated(String reponse) {

                        }
                    });
                    **/
                }else {

                    serverRequests.getFinanceAccountsAndUserInBackgroung(userLocalStore.getUserfullname(),new FinanceAccountCallbacks() {
                        @Override
                        public void fetchDone(ArrayList<FinanceAccount> returnedAccounts) {
                            if(returnedAccounts.size()!=0){
                                saveAccountLocally(returnedAccounts);
                            }
                        }

                        @Override
                        public void setServerResponse(String serverResponse) {

                        }
                    });
                    serverRequests.getGroceryListsInBackgroung(userLocalStore.getUserfullname(),new GroceryListCallBacks() {
                        @Override
                        public void fetchDone(ArrayList<GroceryList> returnedGroceryLists) {
                            if(returnedGroceryLists.size()!=0){
                                //save to sql
                                saveGroceryListtoSQl(returnedGroceryLists);
                                saveGroceryListtoSQlIncome(returnedGroceryLists);
                                loadprogressBar.setIndeterminate(false);
                                loadprogressBar.setVisibility(View.INVISIBLE);
                                startActivity(new Intent(MainActivity.this,LoginScreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                eventsareloaded=true;
                            }else{
                                showSnackBar();
                                eventsareloaded=false;
                                loadprogressBar.setIndeterminate(false);
                                loadprogressBar.setVisibility(View.INVISIBLE);
                                startActivity(new Intent(MainActivity.this, LoginScreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                            }
                        }

                        @Override
                        public void setServerResponse(String serverResponse) {

                        }
                    });

                /**    serverRequests.getItemsInBackgroung(new GetEventsCallbacks() {
                        @Override
                        public void done(ArrayList<CalendarCollection> returnedeventobject) {

                        }

                        @Override
                        public void itemslis(ArrayList<ShoppingItem> returnedShoppingItem) {

                            if(returnedShoppingItem.size()!=0){



                            }else{
                                serverRequests.getGroceryListsInBackgroung(new GroceryListCallBacks() {
                                    @Override
                                    public void fetchDone(ArrayList<GroceryList> returnedGroceryLists) {
                                        if(returnedGroceryLists.size()!=0){
                                            //save to sql
                                            saveGroceryListtoSQl(returnedGroceryLists);
                                            saveGroceryListtoSQlIncome(returnedGroceryLists);
                                            loadprogressBar.setIndeterminate(false);
                                            loadprogressBar.setVisibility(View.INVISIBLE);
                                            startActivity(new Intent(MainActivity.this,LoginScreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                            eventsareloaded=true;
                                        }else{
                                            showSnackBar();
                                            eventsareloaded=false;
                                            loadprogressBar.setIndeterminate(false);
                                            loadprogressBar.setVisibility(View.INVISIBLE);
                                            //startActivity(new Intent(MainActivity.this, LoginScreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                                        }
                                    }

                                    @Override
                                    public void setServerResponse(String serverResponse) {

                                    }
                                });

                            }
                        }

                        @Override
                        public void updated(String reponse) {

                        }
                    });

                    **/

                }
            }

            @Override
            public void itemslis(ArrayList<ShoppingItem> returnedShoppingItem) {

            }

            @Override
            public void updated(String reponse) {

            }
        });
    }

    private void saveAccountLocally(ArrayList<FinanceAccount> accounts) {
        for(int i=0;i<accounts.size();i++){
            sqlFinanceAccount.addFINANCEACCOUNT(accounts.get(i));

        }
    }

    private void saveGroceryListtoSQlIncome(ArrayList<GroceryList> groceryLists) {

        if(groceryLists.size()!=0){

        }
        for(int i=0;i<groceryLists.size();i++){
            try {
                JSONObject jsonObject=new JSONObject();

                int status=(groceryLists.get(i).isListdone())? 1 : 0;
                int shareStatus=(groceryLists.get(i).isToListshare())? 1 : 0;

                jsonObject.put("list_name",groceryLists.get(i).getDatum());
                jsonObject.put("list_creator",groceryLists.get(i).getCreatorName());
                jsonObject.put("list_status",String.valueOf(status));
                jsonObject.put("list_uniqueId",groceryLists.get(i).getList_unique_id());
                jsonObject.put("list_contain",groceryLists.get(i).getListcontain());
                jsonObject.put("list_isShareStatus",String.valueOf(shareStatus));
                jsonObject.put("list_note","nothing specified");

                Calendar c=new GregorianCalendar();
                Date dat=c.getTime();
                //String day= String.valueOf(dat.getDay());
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                String date = (String) android.text.format.DateFormat.format("yyyy-MM-dd", dat);
                incomingNotification=new IncomingNotification(2,0,jsonObject.toString(),date);
                int incomingNotifiId =  mySQLiteHelper.addIncomingNotification(incomingNotification);

            }catch (Exception e){
                e.printStackTrace();

            }


        }

    }

    private void saveGroceryListtoSQl(ArrayList<GroceryList> groceryLists) {

        if(groceryLists.size()!=0){

        }
        for(int i=0;i<groceryLists.size();i++){
            try {
                SQLiteShoppingList sqLiteShoppingList=new SQLiteShoppingList(this);
                sqLiteShoppingList.addShoppingList(groceryLists.get(i));
            }catch (Exception e){
                e.printStackTrace();

            }


        }

    }
    private void saveeventtoSQl(ArrayList<CalendarCollection> calendarCollections) {

        if(calendarCollections.size()!=0){

        }
        for(int i=0;i<calendarCollections.size();i++){
            try {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("title",calendarCollections.get(i).title);
                jsonObject.put("description",calendarCollections.get(i).description);
                jsonObject.put("datetime",calendarCollections.get(i).datetime);
                jsonObject.put("creator",calendarCollections.get(i).creator);
                jsonObject.put("category",calendarCollections.get(i).category);
                jsonObject.put("startingtime",calendarCollections.get(i).startingtime);
                jsonObject.put("endingtime",calendarCollections.get(i).endingtime);
                jsonObject.put("hashid",calendarCollections.get(i).hashid);
                jsonObject.put("alldayevent",calendarCollections.get(i).alldayevent);
                jsonObject.put("everymonth",calendarCollections.get(i).everymonth);
                jsonObject.put("defaulttime",calendarCollections.get(i).creationdatetime);

                Calendar c=new GregorianCalendar();
                Date dat=c.getTime();
                //String day= String.valueOf(dat.getDay());
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                String date = (String) android.text.format.DateFormat.format("yyyy-MM-dd", dat);
                incomingNotification=new IncomingNotification(1,0,jsonObject.toString(),date);
                int incomingNotifiId =  mySQLiteHelper.addIncomingNotification(incomingNotification);

            }catch (Exception e){
                e.printStackTrace();

            }


        }

    }


    private void saveItemtoSQl(ArrayList<ShoppingItem> items) {

        if(items.size()!=0){

        }
        for(int i=0;i<items.size();i++){
            try {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("name",items.get(i).getItemName());
                jsonObject.put("description",items.get(i).getDetailstoItem());
                jsonObject.put("price",items.get(i).getPrice());
                jsonObject.put("specification",items.get(i).getItemSpecification());
                jsonObject.put("unique_id",items.get(i).getUnique_item_id());

                Calendar c=new GregorianCalendar();
                Date dat=c.getTime();
                //String day= String.valueOf(dat.getDay());
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                String date = (String) android.text.format.DateFormat.format("dd-MM-yyyy", dat);
                incomingNotification=new IncomingNotification(6,0,jsonObject.toString(),date);
                int incomingNotifiId =  mySQLiteHelper.addIncomingNotification(incomingNotification);

            }catch (Exception e){
                e.printStackTrace();

            }


        }

    }

    private void getEvents(ArrayList<IncomingNotification> incomingNotifications){

        for (int i=0;i<incomingNotifications.size();i++){
            JSONObject jo_inside = null;
            try {
                jo_inside = new JSONObject(incomingNotifications.get(i).body);

                String titel = jo_inside.getString("title");
                String infotext = jo_inside.getString("description");
                String creator = jo_inside.getString("creator");
                String creationTime = jo_inside.getString("datetime");
                String category = jo_inside.getString("category");
                String startingtime = jo_inside.getString("startingtime");
                String endingtime = jo_inside.getString("endingtime");
                String alldayevent = jo_inside.getString("alldayevent");
                String eventHash = jo_inside.getString("hashid");
                String everymonth = jo_inside.getString("everymonth");
                String creationdatetime = jo_inside.getString("defaulttime");

                CalendarCollection  object =new CalendarCollection(titel,infotext,creator,creationTime,startingtime,endingtime,eventHash,category,alldayevent,everymonth,creationdatetime);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    public void showSnackBar(){
        snackbar = Snackbar
                .make(coordinatorLayout, "No connection internet detected.", Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startapp();
                    }
                });;
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.colorSnackbar));
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }
}
