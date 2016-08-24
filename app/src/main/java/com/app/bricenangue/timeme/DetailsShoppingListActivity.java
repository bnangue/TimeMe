package com.app.bricenangue.timeme;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;

public class DetailsShoppingListActivity extends AppCompatActivity implements
        ListAdapterCreateShopList.ShoppingItemBoughtListener,
        AdapterView.OnItemClickListener,AlertDialogChangeNotSaved.OnChangesCancel,DialogFragmentDatePicker.OnDateGet {

    private ArrayList<ShoppingItem> itemsDB=new ArrayList<>();
    private ListAdapterCreateShopList listViewAdapter;
    private boolean[] itemsBought;
    private GroceryList groceryList;
    private ListView shoppinglistview;
    private TextView textViewAlreadySpent,textViewgroceryListName;
    double totalpriceTopay;
    private Spinner spinner;
    private Menu menu;
    String [] userAccArray={"standard","most used","price ascending","price descending","selected first","selected last"};

    private SQLiteShoppingList sqLiteShoppingList;
    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;
    private static boolean somethingChanged=false;
    private boolean isToUpdate=false;
    private MySQLiteHelper mySQLiteHelper;
    private SQLFinanceAccount sqlFinanceAccount;

    private UserLocalStore userLocalStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_shopping_list);

        sqLiteShoppingList=new SQLiteShoppingList(this);
        mySQLiteHelper= new MySQLiteHelper(this);
        sqlFinanceAccount=new SQLFinanceAccount(this);

        userLocalStore= new UserLocalStore(this);
        coordinatorLayout=(CoordinatorLayout)findViewById(R.id.coordinateLayout_activity_details_shoppping_list);
        shoppinglistview=(ListView)findViewById(R.id.listView_activity_details_shoppping_list);
        textViewAlreadySpent=(TextView)findViewById(R.id.grocery_fragment_balance_amount_activity_details_shopping_list);
        textViewgroceryListName=(TextView)findViewById(R.id.textView_grocery_list_item_title_activity_details_shopping);

        spinner=(Spinner)findViewById(R.id.spinner_activity_details_shopping_list);
        SpinnerAdapter adap = new ArrayAdapter<>(this, R.layout.spinnerlayout, userAccArray);
        spinner.setAdapter(adap);

        spinner.setSelection(0);

        Bundle extras=getIntent().getExtras();
        if(extras!=null && extras.containsKey("GroceryList")){
            groceryList= extras.getParcelable("GroceryList");
            if(extras.containsKey("isFromDetails")){
                somethingChanged=extras.getBoolean("isFromDetails");
                isToUpdate=somethingChanged;
            }
        }

        if(groceryList!=null){
            itemsDB=groceryList.getListItems();
        }

        if(savedInstanceState!=null){
            itemsDB=savedInstanceState.getParcelableArrayList("shoppinglist");
            itemsBought=savedInstanceState.getBooleanArray("itemsBought");
            totalpriceTopay=savedInstanceState.getDouble("totalpriceTopay");
            groceryList=savedInstanceState.getParcelable("groceryList");

            DecimalFormat df = new DecimalFormat("0.00");
            df.setMaximumFractionDigits(2);
            String priceStr = df.format(totalpriceTopay);

            if(priceStr.equals("0.00")){
                textViewAlreadySpent.setText(priceStr+" €");
                textViewAlreadySpent.setTextColor(getResources().getColor(R.color.grey));
            }else {
                textViewAlreadySpent.setText("- "+priceStr+" €");
                textViewAlreadySpent.setTextColor(getResources().getColor(R.color.warning_color));
            }
            prepareOrientationchange();
        }else{

                populateListview();



        }
        String groceryListName= getResources().getString(R.string.grocery_list_item_title_text )+ " " + groceryList.getDatum();
        textViewgroceryListName.setText(groceryListName);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(itemsDB.size()!=0){
                    sort(spinner.getSelectedItem().toString());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sort(spinner.getSelectedItem().toString());


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu=menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_item_to_list_done, menu);

        hideOption(R.id.action_items_added_done);
        return true;
    }

    private void hideOption(int id){
        MenuItem item=menu.findItem(id);
        item.setVisible(false);
    }
    private void showOption(int id){
        MenuItem item=menu.findItem(id);
        item.setVisible(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_items_added_done) {
           onBackPressed();

            return true;
        }
        if (id== R.id.action_items_added_edit){
            //open add item to list an edit
            startActivity(new Intent(this,AddItemToListActivity.class).putExtra("ListToChange",groceryList).putExtra("isFromDetails",true)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBooleanArray("itemsBought",itemsBought);
        outState.putParcelableArrayList("shoppinglist",itemsDB);
        outState.putDouble("totalpriceTopay",totalpriceTopay);
        outState.putParcelable("groceryList",groceryList);
    }

    @Override
    public void onShoppingItemBought(ShoppingItem item, boolean[] positions) {

        showOption(R.id.action_items_added_done);
        somethingChanged=true;
        itemsBought=positions;
        listViewAdapter.setSelectedItems(itemsBought,0);
       double totalspent=0;
        for(int i =0;i<itemsBought.length;i++){
            itemsDB.get(i).setItemIsBought(itemsBought[i]);
            if(itemsBought[i]){

                itemsDB.get(i).setItemIsBought(itemsBought[i]);
                int numb=itemsDB.get(i).getNumberofItemsetForList();
                double price= Double.parseDouble(itemsDB.get(i).getPrice())*numb;
                totalspent=totalspent+price;
            }

        }
        DecimalFormat df = new DecimalFormat("0.00");
        df.setMaximumFractionDigits(2);
        String priceStr = df.format(totalspent);
        if((priceStr.replace(",",".")).equals("0.00")){
            textViewAlreadySpent.setText(priceStr+" €");
            textViewAlreadySpent.setTextColor(getResources().getColor(R.color.grey));
        }else {
            textViewAlreadySpent.setText("- "+priceStr+" €");
            textViewAlreadySpent.setTextColor(getResources().getColor(R.color.warning_color));
        }

        try {
            Number nm=df.parse(priceStr);
            totalpriceTopay= nm.doubleValue();
        } catch (ParseException e) {
            e.printStackTrace();
        }



        groceryList.setItemsOftheList(itemsDB);
        if(!spinner.getSelectedItem().toString().equals("standard")){
            sort(spinner.getSelectedItem().toString());
        }

        if(groceryList.allItemsbought()){
            //signalize user and save list
            DialogFragment fragment=AlertDialogChangeNotSaved.newInstance(getString(
                    R.string.alert_dialog_changed_not_saved_shopping_list_done_title_text)
                    ,getString(R.string.alert_dialog_changed_not_saved_shopping_list_done_message)
                    ,getString(R.string.alert_dialog_changed_not_saved_shopping_list_done_buttonok
                    ),getString(R.string.alert_dialog_changed_not_saved_shopping_list_done_buttoncancel));
            fragment.setCancelable(false);
            fragment.show(getSupportFragmentManager(), "SHOPPING LIST NOT COMPLETE");
        }
    }



    void sort(String sortname){
        switch (sortname){
            case "standard":
                Collections.sort(itemsDB, new ComparatorShoppingItemName());
                break;
            case "price ascending":
                Collections.sort(itemsDB, new ComparatorShoppingItemPrice());
                break;
            case "price descending":
                Comparator<ShoppingItem> comparator_type = Collections.reverseOrder(new ComparatorShoppingItemPrice());
                Collections.sort(itemsDB, comparator_type);
                break;
            case "most used":
                Comparator<ShoppingItem> comparator_type2 = Collections.reverseOrder(new ComparatorShoppingItemMostUsed());
                Collections.sort(itemsDB, comparator_type2);
                break;
            case "selected first":
                Comparator<ShoppingItem> comparator_type1 = Collections.reverseOrder(new ComparatorItemSelected());
                Collections.sort(itemsDB, comparator_type1);
                break;
            case "selected last":
                Collections.sort(itemsDB, new ComparatorItemSelected());
                break;


        }

      populateListview();


    }

     void populateListview(){

         boolean isAllBought=groceryList.isListdone();

         if(isAllBought){
             textViewgroceryListName.setEnabled(false);
             textViewgroceryListName.setClickable(false);
         }
        listViewAdapter=new ListAdapterCreateShopList(this,itemsDB,this,isAllBought);
        if(itemsBought==null){
        itemsBought=new boolean[itemsDB.size()];
         }
         double totalspent=0.00;
         for(int i=0;i<itemsDB.size();i++){
             itemsBought[i]=itemsDB.get(i).isItemIsBought();
             if(itemsDB.get(i).isItemIsBought()){
                 int numb=itemsDB.get(i).getNumberofItemsetForList();
                 double price= Double.parseDouble(itemsDB.get(i).getPrice())*numb;
                 totalspent=totalspent+price;
             }

         }
         listViewAdapter.setSelectedItems(itemsBought,0);
         DecimalFormat df = new DecimalFormat("0.00");
         df.setMaximumFractionDigits(2);

         String priceStr = df.format(totalspent);
         if((priceStr.replace(",",".")).equals("0.00")){
             textViewAlreadySpent.setText(priceStr+" €");
             textViewAlreadySpent.setTextColor(getResources().getColor(R.color.grey));
         }else {
             textViewAlreadySpent.setText("- "+priceStr+" €");
             textViewAlreadySpent.setTextColor(getResources().getColor(R.color.warning_color));
         }


         try {
             Number nm=df.parse(priceStr);
             totalpriceTopay= nm.doubleValue();
         } catch (ParseException e) {
             e.printStackTrace();
         }

         groceryList.setItemsOftheList(itemsDB);

         shoppinglistview.setVisibility(View.VISIBLE);
        shoppinglistview.setAdapter(listViewAdapter);
         shoppinglistview.setOnItemClickListener(this);

     }


     private void prepareOrientationchange() {

        if(itemsBought.length!=0){
            populateListview();
            listViewAdapter.setSelectedItems(itemsBought,0);
            listViewAdapter.notifyDataSetChanged();
        }else {
            populateListview();
        }
     }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    public void OnDateOfListCHangeClicked(View view){

        alertDialogDelete();
    }

    public void alertDialogDelete(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        LayoutInflater inflater=(LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View dialoglayout = inflater.inflate(R.layout.dialog_warning_delete_event, null);

        final android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.setView(dialoglayout);
        Button change= (Button)dialoglayout.findViewById(R.id.buttonDeleteaccount);
        Button cancel= (Button)dialoglayout.findViewById(R.id.buttonCancelaccount);
        TextView title= (TextView)dialoglayout.findViewById(R.id.textViewTitelinfo);
        TextView msg= (TextView)dialoglayout.findViewById(R.id.textViewMessageinfo);

        title.setText(getString(R.string.alertdialog_change_grocery_list_name_title_text));
        msg.setText(getString(R.string.alertdialog_change_grocery_list_name_message_text));
        change.setText(getString(R.string.alertdialog_change_grocery_list_name_button_change_text));

        cancel.setText(getString(R.string.alertdialog_change_grocery_list_name_button_cancel_text));
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

            }
        });
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showOption(R.id.action_items_added_done);
                onDatePickerclicked(true);
                    alertDialog.dismiss();
            }
        });

        alertDialog.setCancelable(false);
        // show it
        alertDialog.show();
    }
    public void onDatePickerclicked(boolean bol){
        android.support.v4.app.FragmentManager manager=getSupportFragmentManager();
        DialogFragment fragmentDatePicker=DialogFragmentDatePicker.newInstance(bol);

        fragmentDatePicker.show(manager,"datePickerfr");
    }

    @Override
    public void dateSet(String date, boolean isstart) {
        somethingChanged=true;
        String groceryListName= getResources().getString(R.string.grocery_list_item_title_text )+ " " + date;

        textViewgroceryListName.setText(groceryListName);
        groceryList.setDatum(date);
    }

    @Override
    public void onBackPressed() {

        if(somethingChanged){
            if(isToUpdate){

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd, HH:mm");
                formatter.setLenient(false);

                Date currentDate = new Date();


                String currentTime = formatter.format(currentDate);
                CalendarCollection    calendarCollection=new CalendarCollection(groceryList.getDatum(),groceryList.getListcontain(),
                        groceryList.getCreatorName(),groceryList.getDatum(), groceryList.getDatum() + " 17:00",
                        groceryList.getDatum() +" 20:00",groceryList.getList_unique_id(),
                        getString(R.string.Event_Category_Category_Shopping),"0","0",currentTime);


                //update finance record and lsi to server

                ArrayList<FinanceAccount> financeAccountArrayList= sqlFinanceAccount.getAllFinanceAccount();
                FinanceAccount financeAccount=null;
                if(financeAccountArrayList.size()!=0){
                    for (int i=0;i<financeAccountArrayList.size();i++){
                        if(financeAccountArrayList.get(i).getAccountUniqueId().equals(groceryList.getAccountid())){
                            financeAccount=financeAccountArrayList.get(i);
                            break;
                        }else if(groceryList.getAccountid().isEmpty()){
                            financeAccount=financeAccountArrayList.get(0);
                            break;
                        }
                    }

                    ArrayList<FinanceRecords> recordsArrayList=new ArrayList<>();
                    FinanceRecords financeRecords1=null;
                    recordsArrayList=financeAccount.getRecords();
                    for (int i =0; i<recordsArrayList.size();i++){
                        if(recordsArrayList.get(i).getRecordUniquesId().equals(groceryList.getList_unique_id())){
                            financeRecords1=recordsArrayList.get(i);
                            financeRecords1.setRecordAmount(groceryList.getGroceryListTotalPriceToPayString().replace(",","."));
                            recordsArrayList.set(i,financeRecords1);
                        }
                    }
                    financeAccount.setAccountsRecord(recordsArrayList);
                    financeAccount.getAccountrecordsAmountUpdateBalance();

                    financeAccount.getAccountRecordsString();
                    financeAccount.setLastchangeToAccount();


                    updateGroceryAndFinance(groceryList,financeAccount,calendarCollection);


                }




            }else {

                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                formatter.setLenient(false);

                Date currentDate = new Date();


                String currentTime = formatter.format(currentDate);
                CalendarCollection    calendarCollection=new CalendarCollection(groceryList.getDatum(),groceryList.getListcontain(),
                        groceryList.getCreatorName(),groceryList.getDatum(), groceryList.getDatum() + " 17:00",
                        groceryList.getDatum() +" 20:00",groceryList.getList_unique_id(),getString(R.string.Event_Category_Category_Shopping),"0","0",currentTime);

                if(groceryList.allItemsbought()){

                    FinanceRecords financeRecords=new FinanceRecords(getString(R.string.textInitialize_create_account_grocery_note),currentTime.split(" ")[0],
                            getString(R.string.textInitialize_create_account_grocery_note),groceryList.getGroceryListTotalPriceToPayString().replace(",",".")
                            ,groceryList.getList_unique_id(),
                            getString(R.string.textInitialize_create_account_grocery_category),currentTime.split(" ")[0],userLocalStore.getUserfullname(),0,true,false);


                    ArrayList<FinanceAccount> financeAccountArrayList= sqlFinanceAccount.getAllFinanceAccount();
                    FinanceAccount financeAccount=null;
                    if(financeAccountArrayList.size()!=0){
                        for (int i=0;i<financeAccountArrayList.size();i++){
                            if(financeAccountArrayList.get(i).getAccountUniqueId().equals(groceryList.getAccountid())){
                                financeAccount=financeAccountArrayList.get(i);
                                break;
                            }else if(groceryList.getAccountid().isEmpty()){
                                financeAccount=financeAccountArrayList.get(0);
                                break;
                            }
                        }

                    }
                    ArrayList<FinanceRecords> recordses=financeAccount.getRecords();
                    for(int i=0;i<recordses.size();i++){
                        if(recordses.get(i).getRecordUniquesId().equals(financeRecords.getRecordUniquesId())){
                            recordses.set(i,financeRecords);
                        }
                    }

                    financeAccount.setAccountsRecord(recordses);
                    financeAccount.getAccountrecordsAmountUpdateBalance();
                    financeAccount.setLastchangeToAccount();

                    updateGroceryAndFinance(groceryList,financeAccount,calendarCollection);

                }else {
                    FinanceRecords financeRecords=new FinanceRecords(getString(R.string.textInitialize_create_account_grocery_note),currentTime.split(" ")[0],
                            getString(R.string.textInitialize_create_account_grocery_note),groceryList.getGroceryListTotalPriceToPayString().replace(",",".")
                            ,groceryList.getList_unique_id(),
                            getString(R.string.textInitialize_create_account_grocery_category),groceryList.getDatum(),userLocalStore.getUserfullname(),0,false,false);


                    ArrayList<FinanceAccount> financeAccountArrayList= sqlFinanceAccount.getAllFinanceAccount();
                    FinanceAccount financeAccount=null;
                    if(financeAccountArrayList.size()!=0){
                        for (int i=0;i<financeAccountArrayList.size();i++){
                            if(financeAccountArrayList.get(i).getAccountUniqueId().equals(groceryList.getAccountid())){
                                financeAccount=financeAccountArrayList.get(i);
                                break;
                            }else if(groceryList.getAccountid().isEmpty()){
                                financeAccount=financeAccountArrayList.get(0);
                                break;
                            }
                        }


                    }
                    ArrayList<FinanceRecords> recordses=financeAccount.getRecords();
                    for(int i=0;i<recordses.size();i++){
                        if(recordses.get(i).getRecordUniquesId().equals(financeRecords.getRecordUniquesId())){
                            recordses.set(i,financeRecords);
                        }
                    }

                    financeAccount.setAccountsRecord(recordses);
                    financeAccount.getAccountrecordsAmountUpdateBalance();
                    financeAccount.setLastchangeToAccount();
                    updateGroceryAndFinance(groceryList,financeAccount,calendarCollection);

                }

            }


        }else {

            super.onBackPressed();
        }


    }

    private void updateGroceryAndFinance(final GroceryList groceryList, final FinanceAccount financeAccount, final CalendarCollection calendarCollection){
        ServerRequests serverRequests= new ServerRequests(this);
        serverRequests.updateGroceryAndFinanceAccountInBackgroung(groceryList, financeAccount, new FinanceAccountCallbacks() {
            @Override
            public void fetchDone(ArrayList<FinanceAccount> returnedAccounts) {

            }

            @Override
            public void setServerResponse(String serverResponse) {
                if(serverResponse!=null && serverResponse.contains("Account and Grocery list successfully updated")){
                    if(groceryList.allItemsbought()){

                        deleteEvent(calendarCollection,groceryList,financeAccount);
                    }else {
                        updateEvent(calendarCollection,groceryList,financeAccount);
                    }

                }else {
                    showSnackBar(groceryList,calendarCollection,financeAccount);
                }
            }
        });
    }
    void deleteEvent(final CalendarCollection calendarCollection, final GroceryList groceryList, final FinanceAccount financeAccount){
        final ServerRequests serverRequests= new ServerRequests(this);
        serverRequests.deleteCalenderEventInBackgroung(calendarCollection, new GetEventsCallbacks() {
            @Override
            public void done(ArrayList<CalendarCollection> returnedeventobject) {

            }

            @Override
            public void itemslis(ArrayList<ShoppingItem> returnedShoppingItem) {

            }

            @Override
            public void updated(String reponse) {
                if (reponse.contains("Event successfully deleted")) {
                    mySQLiteHelper.deleteIncomingNotification(calendarCollection.incomingnotifictionid);
                    updateFinanceAccountLocally(financeAccount,groceryList);


                }
            }
        });
    }
    private void updateGroceryListOnServer(final GroceryList groceryList, final CalendarCollection calendarCollection, final FinanceAccount financeAccount){
        ServerRequests serverRequests=new ServerRequests(this);
        serverRequests.updateGroceryListInBackgroung(groceryList, new GroceryListCallBacks() {
            @Override
            public void fetchDone(ArrayList<GroceryList> returnedGroceryLists) {

            }

            @Override
            public void setServerResponse(String serverResponse) {
                if(serverResponse.contains("Grocery list successfully updated")){

                    updateEvent(calendarCollection,groceryList,financeAccount);



                }else{
                    showSnackBar(groceryList,calendarCollection,financeAccount);
                }
            }
        });

    }
    private void updateGroceryListLocally(GroceryList groceryList){

        groceryList.allItemsbought();
        if (sqLiteShoppingList.updateShoppingList(groceryList)==1){

            somethingChanged=false;
            super.onBackPressed();
        }else {
            Toast.makeText(getApplicationContext()," Error: List not saved",Toast.LENGTH_SHORT).show();
        }
    }


    void updateEvent(final CalendarCollection collection, final
    GroceryList groceryList, final FinanceAccount financeAccount){
        ServerRequests serverRequests=new ServerRequests(this);
        serverRequests.saveCalenderEventInBackgroung(collection, new GetEventsCallbacks() {
            @Override
            public void done(ArrayList<CalendarCollection> returnedeventobject) {

            }

            @Override
            public void itemslis(ArrayList<ShoppingItem> returnedShoppingItem) {

            }

            @Override
            public void updated(String reponse) {
                if (reponse.contains("Event added successfully")) {
                    /**
                    if(financeAccount!=null){
                        updateeventtoSQl(collection);
                        updateFinanceAccountToServer(financeAccount,groceryList);
                    }else {
                        updateeventtoSQl(collection);
                        updateGroceryListLocally(groceryList);
                    }

                    **/
                updateFinanceAccountLocally(financeAccount,groceryList);
                updateeventtoSQl(collection);
                  //  updateGroceryListLocally(groceryList);

                }else {
                    Toast.makeText(getApplicationContext()," Error: List not store as event",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    public void updateFinanceAccountToServer(final FinanceAccount financeAccount, final GroceryList groceryList){
        ServerRequests serverRequests =new ServerRequests(this);
        serverRequests.updateFinanceAccountInBackgroung(financeAccount, new FinanceAccountCallbacks() {
            @Override
            public void fetchDone(ArrayList<FinanceAccount> returnedAccounts) {

            }

            @Override
            public void setServerResponse(String serverResponse) {

                if(serverResponse.contains("Account successfully updated")){
                    updateFinanceAccountLocally(financeAccount,groceryList);
                }else {
                    Toast.makeText(getApplicationContext(),"Error creating account "+ financeAccount.getAccountName(),Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    public void updateFinanceAccountLocally(FinanceAccount financeAccount, GroceryList groceryList){

        if (sqlFinanceAccount.updateFinanceAccount(financeAccount)!=0){
            updateGroceryListLocally(groceryList);
        }
    }

    private void updateeventtoSQl(CalendarCollection calendarCollections) {


        try {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("title",calendarCollections.title);
            jsonObject.put("description",calendarCollections.description);
            jsonObject.put("datetime",calendarCollections.datetime);
            jsonObject.put("creator",calendarCollections.creator);
            jsonObject.put("category",calendarCollections.category);
            jsonObject.put("startingtime",calendarCollections.startingtime);
            jsonObject.put("endingtime",calendarCollections.endingtime);
            jsonObject.put("hashid",calendarCollections.hashid);
            jsonObject.put("alldayevent",calendarCollections.alldayevent);
            jsonObject.put("everymonth",calendarCollections.everymonth);
            jsonObject.put("defaulttime",calendarCollections.creationdatetime);
            Calendar c=new GregorianCalendar();
            Date dat=c.getTime();
            //String day= String.valueOf(dat.getDay());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String date = (String) android.text.format.DateFormat.format("yyyy-MM-dd", dat);
            IncomingNotification incomingNotification=new IncomingNotification(1,0,jsonObject.toString(),date);
            int incomingNotifiId =  mySQLiteHelper.updateIncomingNotification(incomingNotification);

        }catch (Exception e){
            e.printStackTrace();

        }


    }
    @Override
    public void changescanceled(boolean canceled) {
        if(!canceled){

        }else {
            onBackPressed();
        }
    }



    public void showSnackBar(final GroceryList groceryList, final CalendarCollection collection, final FinanceAccount financeAccount){
        snackbar = Snackbar
                .make(coordinatorLayout, "An error occured during the connection to the server", Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateGroceryAndFinance(groceryList,financeAccount,collection);
                    }
                });
        snackbar.setActionTextColor(Color.WHITE);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.colorSnackbar));
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }
}
