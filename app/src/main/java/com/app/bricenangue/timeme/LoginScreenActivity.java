package com.app.bricenangue.timeme;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class LoginScreenActivity extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    private Button registerbtn,loginbtn;
    private EditText emailed,passworded;
    private String emailstr,passwordstr;
    private UserLocalStore userLocalStore;
    private IncomingNotification incomingNotification;
    public static boolean eventsareloaded=false;
    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;

    private int count=0;
    private MySQLiteHelper mySQLiteHelper;
    private SQLFinanceAccount sqlFinanceAccount;
    private SQLiteShoppingList sqLiteShoppingList;
    private FragmentProgressBarLoading progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        userLocalStore=new UserLocalStore(this);
        mySQLiteHelper=new MySQLiteHelper(this);
        sqlFinanceAccount=new SQLFinanceAccount(this);
        sqLiteShoppingList=new SQLiteShoppingList(this);

        registerbtn=(Button)findViewById(R.id.buttonregisterReg);
        loginbtn=(Button)findViewById(R.id.buttonlogin);
        emailed=(EditText)findViewById(R.id.editTextemailloggin);
        passworded=(EditText)findViewById(R.id.editTextpasswordloggin);
        coordinatorLayout=(CoordinatorLayout)findViewById(R.id.coordinateLayout_login_screen);

        loginbtn.setOnClickListener(this);
        registerbtn.setOnClickListener(this);
        passworded.setOnEditorActionListener(this);



    }

    private void dialg(String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(message);
        alert.setPositiveButton("OK", null);
        alert.show();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonlogin:
                if(internetConnection()){
                  logCurrentuserIn();

                }else {
                    showErrordialog("No internet connection detected");

                }
                break;
            case R.id.buttonregisterReg:
                startActivity(new Intent(LoginScreenActivity.this,RegisterUserActivity.class));
                break;
        }
    }


    private void saveloggedInuserPreferences(User returneduser){
        userLocalStore.setUserLoggedIn(true);
        userLocalStore.storeUserData(returneduser);
        userLocalStore.setUserPartnerEmail(returneduser.friendlist);

        userLocalStore.setUserPicturePath(userLocalStore.saveToInternalStorage(returneduser.picture));

        Intent intent=new Intent(LoginScreenActivity.this,NewCalendarActivty.class);

        startActivity(intent);
        progressDialog.dismiss(getSupportFragmentManager());
    }



    private void getEventsfromMySQL(final User user,final String username) {

        final ServerRequests serverRequests=new ServerRequests(this);
        serverRequests.getCalenderEventInBackgroung(username,new GetEventsCallbacks() {
            @Override
            public void done(ArrayList<CalendarCollection> returnedeventobject) {
                if(returnedeventobject.size()!=0){

                    saveeventtoSQl(returnedeventobject);
                    serverRequests.getFinanceAccountsAndUserInBackgroung(username,new FinanceAccountCallbacks() {
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
                    serverRequests.getGroceryListsInBackgroung(username,new GroceryListCallBacks() {
                        @Override
                        public void fetchDone(ArrayList<GroceryList> returnedGroceryLists) {
                            if(returnedGroceryLists.size()!=0){
                                //save to sql
                                saveGroceryListtoSQl(returnedGroceryLists);
                                saveGroceryListtoSQlIncome(returnedGroceryLists);

                                eventsareloaded=true;
                                saveloggedInuserPreferences(user);
                            }else{
                                showSnackBar();
                                eventsareloaded=false;
                                saveloggedInuserPreferences(user);
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

                    serverRequests.getFinanceAccountsAndUserInBackgroung(username,new FinanceAccountCallbacks() {
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
                    serverRequests.getGroceryListsInBackgroung(username,new GroceryListCallBacks() {
                        @Override
                        public void fetchDone(ArrayList<GroceryList> returnedGroceryLists) {
                            if(returnedGroceryLists.size()!=0){
                                //save to sql
                                saveGroceryListtoSQl(returnedGroceryLists);
                                saveGroceryListtoSQlIncome(returnedGroceryLists);
                                eventsareloaded=true;
                                saveloggedInuserPreferences(user);
                            }else{
                                showSnackBar();
                                eventsareloaded=false;
                                saveloggedInuserPreferences(user);
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
                jsonObject.put("list_account_id",groceryLists.get(i).getAccountid());

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
            if(!calendarCollections.get(i).category.equals("Grocery")){
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

    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
            if(internetConnection()){
               logCurrentuserIn();
            }else {
                showErrordialog("No internet connection detected");

            }

        }

        return false;
    }

    private void showErrordialog(String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(message);
        alert.setPositiveButton("OK", null);
        alert.show();
        if(progressDialog!=null){
            progressDialog.dismiss(getSupportFragmentManager());

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        count=0;
        if (authenticate()) {
            displayUserdetails();

        }

    }

    private  boolean internetConnection(){
        return new ServerRequests(this).haveNetworkConnection();
    }
    private void displayUserdetails() {
        User user = userLocalStore.getLoggedInUser();
        emailed.setText(user.email);
    }

    //true if user logged in
    private boolean authenticate() {
        return userLocalStore.getUserLoggedIn();
    }

    @Override
    public void onBackPressed() {
       count++;

        if(count==2){
            this.finishAffinity();
            System.exit(0);
        }
        Toast.makeText(getApplicationContext(),"Click a second time to exit application",Toast.LENGTH_SHORT).show();

    }

    /**
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
                Calendar c=new GregorianCalendar();
                Date dat=c.getTime();
                //String day= String.valueOf(dat.getDay());
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                String date = (String) android.text.format.DateFormat.format("yyyy-MM-dd", dat);
                IncomingNotification incomingNotification=new IncomingNotification(1,0,jsonObject.toString(),date);
                int incomingNotifiId =  mySQLiteHelper.addIncomingNotification(incomingNotification);

            }catch (Exception e){
                e.printStackTrace();

            }


        }

    }
    **/

    public void logCurrentuserIn(){

        ServerRequests.lockScreenOrientation(LoginScreenActivity.this);
        progressDialog = new FragmentProgressBarLoading();
        progressDialog.setCancelable(false);
        progressDialog.show(getSupportFragmentManager(), "logging_progress");
        sqlFinanceAccount.reInitializeFinanceSqliteTable();
        mySQLiteHelper.reInitializeSqliteTable();
        sqLiteShoppingList.reInitializeShoppingListSqliteTable();


        emailstr = emailed.getText().toString();
        passwordstr = passworded.getText().toString();

        if(TextUtils.isEmpty(emailstr)){
            emailed.setError("this field cannot be empty");
        }else if(TextUtils.isEmpty(passwordstr)){
            passworded.setError("this field cannot be empty");

        }else if(!TextUtils.isEmpty(emailstr) && !TextUtils.isEmpty(passwordstr)) {
            int passHash = passwordstr.hashCode();
            User usertologIn = new User(emailstr, String.valueOf(passHash), 1, null);

            if (userLocalStore.getUserRegistrationId().isEmpty() || userLocalStore.getUserRegistrationId()==null) {
                userLocalStore.setUserGCMregId(registerDevice(), 0);
                usertologIn.regId=userLocalStore.getUserRegistrationId();
            } else {
                usertologIn.regId = userLocalStore.getUserRegistrationId();
            }


            ServerRequests serverRequests=new ServerRequests(this);
            serverRequests.loggingUserinBackground(usertologIn, new GetUserCallbacks() {
                @Override
                public void done(User returneduser) {
                    if(returneduser !=null){
                        getEventsfromMySQL(returneduser,returneduser.getfullname());



                    }else {
                        showErrordialog("Could not log in.  Wrong email or password");
                        progressDialog.dismiss(getSupportFragmentManager());

                    }
                }

                @Override
                public void serverReponse(String reponse) {

                }

                @Override
                public void userlist(ArrayList<User> reponse) {

                }
            });


        }


    }



    private String registerDevice() {
        //Creating a firebase object
        FirebaseMessaging.getInstance().subscribeToTopic("timeMe");
        return FirebaseInstanceId.getInstance().getToken();


    }
    public void showSnackBar(){
        snackbar = Snackbar
                .make(coordinatorLayout, "No connection internet detected.", Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        logCurrentuserIn();
                    }
                });;
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.colorSnackbar));
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

}
