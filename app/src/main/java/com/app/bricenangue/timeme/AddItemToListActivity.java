package com.app.bricenangue.timeme;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Base64;
import android.util.Pair;
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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.util.Locale;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;



public class AddItemToListActivity extends AppCompatActivity implements View.OnFocusChangeListener, SearchView.OnQueryTextListener,
        View.OnClickListener,ListViewAdapter.ShoppingItemSetListener,DialogFragmentDatePicker.OnDateGet {
    private  SearchView search;
    private Button createnewitem,setDateButtuon;
    private ListView addItemtolistListview;
    private TextView textViewNoDatainDB, textViewAmontToPay;
    private  ArrayList<String> sortOptionslist=new ArrayList<>();
    private  ArrayList<String> itemNamee=new ArrayList<>();
    private ArrayList<String> itemPrice=new ArrayList<>();
    private ArrayList<String> itemUsageFrequency=new ArrayList<>();
    private ArrayList<ShoppingItem> itemsDB=new ArrayList<>();
    private ArrayList<ShoppingItem> itemstoShopListFromSorting=new ArrayList<>();
    private ShoppingItem []itemstoShoparray;
    private  Menu menu;
    private String listName="";
    private String sortName="";
    private boolean areItemsAdded=false;
    private UserLocalStore userLocalStore;
    private SQLiteShoppingList sqLiteShoppingList;
    private int[] status ;


    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;
    private MySQLiteHelper mySQLiteHelper;


    private boolean isFromDetails=false;
    private GroceryList groceryListFromDetails;
    public static boolean newItem=false;

    //This arraylist will have data as pulled from server. This will keep cumulating.
   private ArrayList<ShoppingItem> productResults = new ArrayList<ShoppingItem>();
    //Based on the search string, only filtered products will be moved here from productResults
    private ArrayList<ShoppingItem> filteredProductResults = new ArrayList<ShoppingItem>();
    private SQLFinanceAccount sqlFinanceAccount;
    private ArrayList<String> itemCategory=new ArrayList<>();
    private ArrayList<String> itemMarket=new ArrayList<>();
    private Button buttonSort;
    private android.support.v7.app.AlertDialog alertDialog;
    private boolean[] selectedOnRows;
    private ArrayList<FinanceAccount> accountsforshopping=new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_to_list);
        userLocalStore=new UserLocalStore(this);
        sqLiteShoppingList=new SQLiteShoppingList(this);
        mySQLiteHelper=new MySQLiteHelper(this);

        sqlFinanceAccount=new SQLFinanceAccount(this);


        accountsforshopping=sqlFinanceAccount.getAllFinanceAccount();

        coordinatorLayout=(CoordinatorLayout)findViewById(R.id.coordinateLayout__creatList_add_item_to_list);
        setDateButtuon=(Button)findViewById(R.id.dateaddeventstart_creatList_add_item_to_list);
        createnewitem=(Button) findViewById(R.id.grocery_activity_add_itemtoList_button);
        addItemtolistListview=(ListView)findViewById(R.id.shoppinglistViewaddItemToListactivity);
        textViewNoDatainDB=(TextView) findViewById(R.id.textView_add_item_to_list_List_empty);
        textViewAmontToPay=(TextView)findViewById(R.id.grocery_fragment_balance_amount_activity_add_item_to_list);
        setDateButtuon.setOnClickListener(this);

        buttonSort=(Button)findViewById(R.id.button_sort_activity_add_item_to_list);


        Bundle extras=getIntent().getExtras();

        if(extras!=null){
            if(extras.containsKey("itemDB")){
                itemsDB=extras.getParcelableArrayList("itemDB");
                assert itemsDB != null;
                itemstoShoparray=new ShoppingItem[itemsDB.size()];
                initArray();

            }else if(extras.containsKey("ListToChange")){
              isFromDetails=extras.getBoolean("isFromDetails");
                groceryListFromDetails=extras.getParcelable("ListToChange");
            }

        }

         search=(SearchView)findViewById(R.id.actionbarsearch_add_item_to_list);
        search.setQueryHint("Start typing to search...");
        if(search.hasFocus()){
            search.setBackgroundColor(getResources().getColor(R.color.white));
        }
        search.setOnQueryTextFocusChangeListener(this);
        search.setOnQueryTextListener(this);
        createnewitem.setOnClickListener(this);

        buttonSort.setOnClickListener(this);


        if(savedInstanceState==null){
            if(groceryListFromDetails!=null){
                setDateButtuon.setText(groceryListFromDetails.getDatum());
                listName=groceryListFromDetails.getDatum();
            }else {
                initializeDatePicker();
            }

             if(itemsDB.size()==0 || itemsDB==null){

                 new LoadItemDBAsyncTask().execute();
             }else {
                 populateListview();
             }

        }else {
            itemsDB=savedInstanceState.getParcelableArrayList("itemDB");
            assert itemsDB != null;
            itemstoShoparray=new ShoppingItem[itemsDB.size()];
            initArray();
            listName=savedInstanceState.getString("listname");
            sortName=savedInstanceState.getString("sortType");


            setDateButtuon.setText(listName);

            initTotalPricetextView();
            populateListview();
            sort(sortName);
        }

        if(sortName.isEmpty()){
            sortName=getString(R.string.alphabetic);


        }else {
            sort(sortName);
        }


    }

    private void initArray() {
        for (int i=0;i<itemsDB.size();i++){
            if(itemsDB.get(i).getNumberofItemsetForList()!=0){
                itemstoShoparray[i]=itemsDB.get(i);
            }
        }
        initTotalPricetextView();
    }

    void prepareListviewofSorted(ArrayList<ShoppingItem> shoppingItems){

        if(shoppingItems.size()==0){
            textViewNoDatainDB.setText(R.string.text_search_itme_to_list_DataBase_empty);
        }else{

            textViewNoDatainDB.setText(R.string.text_search_itme_to_list_DataBase_not_empty);
        }
        ListViewAdapter listViewAdapter=new ListViewAdapter(this,shoppingItems,this);
        addItemtolistListview.setVisibility(View.VISIBLE);
        addItemtolistListview.setAdapter(listViewAdapter);
    }

    void sort(String sortname){

        sortName=sortname;

        if(sortName.equals(getString(R.string.market_Rewe))){
            if(itemstoShopListFromSorting.size()!=0){
                itemstoShopListFromSorting.clear();
                itemstoShopListFromSorting=new ArrayList<>();
            }
            for(int i=0;i<itemsDB.size();i++){
                if(itemsDB.get(i).getItemmarket().equals(getString(R.string.rewe_Market))){
                    itemstoShopListFromSorting.add(itemsDB.get(i));
                }
            }
            prepareListviewofSorted(itemstoShopListFromSorting);

        }else if(sortName.equals(getString(R.string.market_DM))){

            if(itemstoShopListFromSorting.size()!=0){
                itemstoShopListFromSorting.clear();
                itemstoShopListFromSorting=new ArrayList<>();
            }
            for(int i=0;i<itemsDB.size();i++){
                if(itemsDB.get(i).getItemmarket().equals(getString(R.string.dm_Market))){
                    itemstoShopListFromSorting.add(itemsDB.get(i));
                }
            }
            prepareListviewofSorted(itemstoShopListFromSorting);
        }else if(sortName.equals(getString(R.string.market_Norma))){

            if(itemstoShopListFromSorting.size()!=0){
                itemstoShopListFromSorting.clear();
                itemstoShopListFromSorting=new ArrayList<>();
            }
            for(int i=0;i<itemsDB.size();i++){
                if(itemsDB.get(i).getItemmarket().equals(getString(R.string.norma_Market))){
                    itemstoShopListFromSorting.add(itemsDB.get(i));
                }
            }
            prepareListviewofSorted(itemstoShopListFromSorting);
        }else if(sortName.equals(getString(R.string.alphabetic))){

            Collections.sort(itemsDB, new ComparatorShoppingItemName());
            itemstoShoparray=new ShoppingItem[itemsDB.size()];
            initArray();
            initTotalPricetextView();
            populateListview();

        }else if(sortName.equals(getString(R.string.household))){
            if(itemstoShopListFromSorting.size()!=0){
                itemstoShopListFromSorting.clear();
                itemstoShopListFromSorting=new ArrayList<>();
            }
            for(int i=0;i<itemsDB.size();i++){
                if(itemsDB.get(i).getItemcategory().equals(getString(R.string.household))){
                    itemstoShopListFromSorting.add(itemsDB.get(i));
                }
            }
            prepareListviewofSorted(itemstoShopListFromSorting);

        }else if(sortName.equals(getString(R.string.fruit))){
            if(itemstoShopListFromSorting.size()!=0){
                itemstoShopListFromSorting.clear();
                itemstoShopListFromSorting=new ArrayList<>();
            }
            for(int i=0;i<itemsDB.size();i++){
                if(itemsDB.get(i).getItemcategory().equals(getString(R.string.fruit))){
                    itemstoShopListFromSorting.add(itemsDB.get(i));
                }
            }
            prepareListviewofSorted(itemstoShopListFromSorting);


        }else if(sortName.equals(getString(R.string.vegetables))){
            if(itemstoShopListFromSorting.size()!=0){
                itemstoShopListFromSorting.clear();
                itemstoShopListFromSorting=new ArrayList<>();
            }
            for(int i=0;i<itemsDB.size();i++){
                if(itemsDB.get(i).getItemcategory().equals(getString(R.string.vegetables))){
                    itemstoShopListFromSorting.add(itemsDB.get(i));
                }
            }
            prepareListviewofSorted(itemstoShopListFromSorting);


        }else if(sortName.equals(getString(R.string.grain_products))){

            if(itemstoShopListFromSorting.size()!=0){
                itemstoShopListFromSorting.clear();
                itemstoShopListFromSorting=new ArrayList<>();
            }
            for(int i=0;i<itemsDB.size();i++){
                if(itemsDB.get(i).getItemcategory().equals(getString(R.string.grain_products))){
                    itemstoShopListFromSorting.add(itemsDB.get(i));
                }
            }
            prepareListviewofSorted(itemstoShopListFromSorting);

        }else if(sortName.equals(getString(R.string.technology))){

            if(itemstoShopListFromSorting.size()!=0){
                itemstoShopListFromSorting.clear();
                itemstoShopListFromSorting=new ArrayList<>();
            }
            for(int i=0;i<itemsDB.size();i++){
                if(itemsDB.get(i).getItemcategory().equals(getString(R.string.technology))){
                    itemstoShopListFromSorting.add(itemsDB.get(i));
                }
            }
            prepareListviewofSorted(itemstoShopListFromSorting);

        }else if(sortName.equals(getString(R.string.drinks))){

            if(itemstoShopListFromSorting.size()!=0){
                itemstoShopListFromSorting.clear();
                itemstoShopListFromSorting=new ArrayList<>();
            }
            for(int i=0;i<itemsDB.size();i++){
                if(itemsDB.get(i).getItemcategory().equals(getString(R.string.drinks))){
                    itemstoShopListFromSorting.add(itemsDB.get(i));
                }
            }
            prepareListviewofSorted(itemstoShopListFromSorting);

        }else if(sortName.equals(getString(R.string.fats_and_oils))){

            if(itemstoShopListFromSorting.size()!=0){
                itemstoShopListFromSorting.clear();
                itemstoShopListFromSorting=new ArrayList<>();
            }
            for(int i=0;i<itemsDB.size();i++){
                if(itemsDB.get(i).getItemcategory().equals(getString(R.string.fats_and_oils))){
                    itemstoShopListFromSorting.add(itemsDB.get(i));
                }
            }
            prepareListviewofSorted(itemstoShopListFromSorting);

        }else if(sortName.equals(getString(R.string.milk_products))){

            if(itemstoShopListFromSorting.size()!=0){
                itemstoShopListFromSorting.clear();
                itemstoShopListFromSorting=new ArrayList<>();
            }
            for(int i=0;i<itemsDB.size();i++){
                if(itemsDB.get(i).getItemcategory().equals(getString(R.string.milk_products))){
                    itemstoShopListFromSorting.add(itemsDB.get(i));
                }
            }
            prepareListviewofSorted(itemstoShopListFromSorting);

        }else if(sortName.equals(getString(R.string.spices))){

            if(itemstoShopListFromSorting.size()!=0){
                itemstoShopListFromSorting.clear();
                itemstoShopListFromSorting=new ArrayList<>();
            }
            for(int i=0;i<itemsDB.size();i++){
                if(itemsDB.get(i).getItemcategory().equals(getString(R.string.spices))){
                    itemstoShopListFromSorting.add(itemsDB.get(i));
                }
            }
            prepareListviewofSorted(itemstoShopListFromSorting);

        }else if(sortName.equals(getString(R.string.drugstore))){

            if(itemstoShopListFromSorting.size()!=0){
                itemstoShopListFromSorting.clear();
                itemstoShopListFromSorting=new ArrayList<>();
            }
            for(int i=0;i<itemsDB.size();i++){
                if(itemsDB.get(i).getItemcategory().equals(getString(R.string.drugstore))){
                    itemstoShopListFromSorting.add(itemsDB.get(i));
                }
            }
            prepareListviewofSorted(itemstoShopListFromSorting);

        }else if(sortName.equals(getString(R.string.others))){

            if(itemstoShopListFromSorting.size()!=0){
                itemstoShopListFromSorting.clear();
                itemstoShopListFromSorting=new ArrayList<>();
            }
            for(int i=0;i<itemsDB.size();i++){
                if(itemsDB.get(i).getItemcategory().equals(getString(R.string.others))){
                    itemstoShopListFromSorting.add(itemsDB.get(i));
                }
            }
            prepareListviewofSorted(itemstoShopListFromSorting);

        }else if(sortName.equals(getString(R.string.meat))){

            if(itemstoShopListFromSorting.size()!=0){
                itemstoShopListFromSorting.clear();
                itemstoShopListFromSorting=new ArrayList<>();
            }
            for(int i=0;i<itemsDB.size();i++){
                if(itemsDB.get(i).getItemcategory().equals(getString(R.string.meat))){
                    itemstoShopListFromSorting.add(itemsDB.get(i));
                }
            }
            prepareListviewofSorted(itemstoShopListFromSorting);

        }else if(sortName.equals(getString(R.string.sweets))){

            if(itemstoShopListFromSorting.size()!=0){
                itemstoShopListFromSorting.clear();
                itemstoShopListFromSorting=new ArrayList<>();
            }
            for(int i=0;i<itemsDB.size();i++){
                if(itemsDB.get(i).getItemcategory().equals(getString(R.string.sweets))){
                    itemstoShopListFromSorting.add(itemsDB.get(i));
                }
            }
            prepareListviewofSorted(itemstoShopListFromSorting);

        }
        switch (sortname){

            case "standard":
                Collections.sort(itemsDB, new ComparatorShoppingItemName());
                itemstoShoparray=new ShoppingItem[itemsDB.size()];
                initArray();
                initTotalPricetextView();
                populateListview();
                break;
            case "price ascending":
                Collections.sort(itemsDB, new ComparatorShoppingItemPrice());
                itemstoShoparray=new ShoppingItem[itemsDB.size()];
                initArray();
                initTotalPricetextView();
                populateListview();
                break;
            case "price descending":
                Comparator<ShoppingItem> comparator_type = Collections.reverseOrder(new ComparatorShoppingItemPrice());
                Collections.sort(itemsDB, comparator_type);
                itemstoShoparray=new ShoppingItem[itemsDB.size()];
                initArray();
                initTotalPricetextView();
                populateListview();
                break;
            case "most used":
                Comparator<ShoppingItem> comparator_type2 = Collections.reverseOrder(new ComparatorShoppingItemMostUsed());
                Collections.sort(itemsDB, comparator_type2);
                itemstoShoparray=new ShoppingItem[itemsDB.size()];
                initArray();
                initTotalPricetextView();
                populateListview();

                break;
            case "selected first":
                Comparator<ShoppingItem> comparator_type1 = Collections.reverseOrder(new CompareAddItemAlreadyadded());
                Collections.sort(itemsDB, comparator_type1);
                itemstoShoparray=new ShoppingItem[itemsDB.size()];
                initArray();
                initTotalPricetextView();
                populateListview();
                break;
            case "selected last":
                Collections.sort(itemsDB, new CompareAddItemAlreadyadded());
                itemstoShoparray=new ShoppingItem[itemsDB.size()];
                initArray();
                initTotalPricetextView();
                populateListview();
                break;


        }




    }
    void populateListview(){


        if(itemsDB.size()==0){
            textViewNoDatainDB.setText(R.string.text_add_itme_to_list_DataBase_empty);
        }else{

            textViewNoDatainDB.setText(R.string.text_add_itme_to_list_DataBase_not_empty);
        }

        if(productResults.size()!=0){
            merge();
        }
        if(groceryListFromDetails!=null){
            merge(groceryListFromDetails.getListItems());
            initArray();

        }
        if(itemstoShopListFromSorting!=null){
            merge(itemstoShopListFromSorting);
            initArray();

        }
        if(itemstoShoparray==null){
            itemstoShoparray=new ShoppingItem[itemsDB.size()];
            initArray();
        }
        ListViewAdapter listViewAdapter=new ListViewAdapter(this,itemsDB,this);
        addItemtolistListview.setVisibility(View.VISIBLE);
        addItemtolistListview.setAdapter(listViewAdapter);
    }

    private void merge() {
        for (int i=0;i<productResults.size();i++){
            for(int j=0;j<itemsDB.size();j++){
                if(productResults.get(i).getUnique_item_id().equals(itemsDB.get(j).getUnique_item_id())&&
                        productResults.get(i).getNumberofItemsetForList()!=itemsDB.get(j).getNumberofItemsetForList()){

                    itemsDB.set(j,productResults.get(i));
                }
            }

        }

    }

    private void merge(ArrayList<ShoppingItem> productResults) {
        for (int i=0;i<productResults.size();i++){
            for(int j=0;j<itemsDB.size();j++){
                if(productResults.get(i).getUnique_item_id().equals(itemsDB.get(j).getUnique_item_id())&&
                        productResults.get(i).getNumberofItemsetForList()!=itemsDB.get(j).getNumberofItemsetForList()){

                    itemsDB.set(j,productResults.get(i));
                }
            }

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu=menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_item_to_list_done, menu);

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

    private void createGrocerylistAndSave(FinanceAccount financeAccount,ArrayList<ShoppingItem> list){

            Calendar c=new GregorianCalendar();
            Date dat=c.getTime();
            //String day= String.valueOf(dat.getDay());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMAN);

            String date = (String) android.text.format.DateFormat.format("dd-MM-yyyy", dat);
            GroceryList groceryList=new GroceryList();
            groceryList.setItemsOftheList(list);
            groceryList.getListcontain();
            int hashid=(userLocalStore.getUserfullname() + format.format(dat)).hashCode();
            groceryList.setList_unique_id(String.valueOf(hashid));
            groceryList.setCreatorName(userLocalStore.getUserfullname());

            listName=setDateButtuon.getText().toString();
            if(!listName.isEmpty()){
                groceryList.setDatum(listName);
            }

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd, HH:mm");
            formatter.setLenient(false);

            Date currentDate = new Date();


            String currentTime = formatter.format(currentDate);
            CalendarCollection    calendarCollection=new CalendarCollection(listName,groceryList.getListcontain(),
                    groceryList.getCreatorName(),listName, listName + " 17:00",
                    listName +" 20:00",String.valueOf(hashid),getString(R.string.Event_Category_Category_Shopping),"0","0",currentTime);

            SimpleDateFormat formatterr = new SimpleDateFormat("dd-MM-yyy HH:mm:ss");
            String currentTimer = formatterr.format(currentDate);
            FinanceRecords financeRecords=new FinanceRecords(getString(R.string.textInitialize_create_account_grocery_note),currentTimer.split(" ")[0],
                    getString(R.string.textInitialize_create_account_grocery_note),groceryList.getGroceryListTotalPriceToPayString().replace(",",".")
                    ,String.valueOf(hashid),
                    getString(R.string.textInitialize_create_account_grocery_category),groceryList.getDatum(),userLocalStore.getUserfullname(),0,false,false);


            if(financeAccount==null){
                ArrayList<FinanceAccount> financeAccountArrayList= sqlFinanceAccount.getAllFinanceAccount();
                if(financeAccountArrayList.size()!=0){
                    financeAccount=financeAccountArrayList.get(0);
                    financeAccount.addRecordToAccount(financeRecords);
                    financeAccount.setLastchangeToAccount();
                    groceryList.setAccountid(financeAccount.getAccountUniqueId());
                }
            }else {
                financeAccount.addRecordToAccount(financeRecords);
                financeAccount.setLastchangeToAccount();
                groceryList.setAccountid(financeAccount.getAccountUniqueId());

            }

        createGroceryAndUpdateFinance(financeAccount,groceryList,calendarCollection);


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_items_added_done) {

            if(isFromDetails){

                ArrayList<ShoppingItem> list=new ArrayList<>();
                for(int i=0;i<itemstoShoparray.length;i++){
                    if(itemstoShoparray[i]!=null && itemstoShoparray[i].getNumberofItemsetForList()>0){

                        list.add(itemstoShoparray[i]);
                    }

                }

                if(list.size()!=0){
                    groceryListFromDetails.setItemsOftheList(list);
                    groceryListFromDetails.getListcontain();

                    Toast.makeText(getApplicationContext(),"Grocery list on " +groceryListFromDetails.getDatum()+ " updated",Toast.LENGTH_SHORT).show();
                    updateExcelFile(this,groceryListFromDetails.getItemsOftheList());
                    startActivity(new Intent(this, DetailsShoppingListActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            .putExtra("GroceryList",groceryListFromDetails).putExtra("isFromDetails",true));
                    finish();


                }else{

                    //error
                    Toast.makeText(getApplicationContext(),"An Error occured updating list",Toast.LENGTH_SHORT).show();
                }

            }else {
                final ArrayList<ShoppingItem> list=new ArrayList<>();
                for(int j=0;j<itemstoShoparray.length;j++){
                    if(itemstoShoparray[j]!=null){

                        list.add(itemstoShoparray[j]);
                    }

                }
                if(list.size()!=0){
                    showDialogChoosingAccount(list);
                }else {

                    Toast.makeText(getApplicationContext(),"No items added",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, CreateNewShoppingListActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                }

            }


            return true;
        }else if (id == R.id.action_item_list_refresh){
            new LoadItemDBAsyncTask().execute();
            Toast.makeText(getApplicationContext(),"List updated",Toast.LENGTH_SHORT).show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveGroceryListLocally(GroceryList groceryList){



            if(sqLiteShoppingList.addShoppingList(groceryList)!= -1) {
                String registrationId=userLocalStore.getUserPartnerRegId();
                String email=userLocalStore.getUserPartnerEmail();
                if(!registrationId.isEmpty() && !email.isEmpty()){
                   sendNotification(registrationId,email);
                }


                Toast.makeText(getApplicationContext(),"Grocery list on " +groceryList.getDatum()+ " created",Toast.LENGTH_SHORT).show();
                updateExcelFile(this,groceryList.getItemsOftheList());
                startActivity(new Intent(this, CreateNewShoppingListActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("GroceryshoppingList",groceryList));
                finish();
            }else{
                Toast.makeText(getApplicationContext(),"could not save the grocery list locally",Toast.LENGTH_SHORT).show();
            }


    }

    private static String getData(ArrayList<Pair<String, String>> values) throws UnsupportedEncodingException {
        StringBuilder result=new StringBuilder();
        for(Pair<String,String> pair : values){

            if(result.length()!=0)

                result.append("&");
            result.append(URLEncoder.encode(pair.first, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.second, "UTF-8"));

        }
        return result.toString();
    }


    public  void sendNotification(final String regId,final String email) {

        if (!userLocalStore.getUserRegistrationId().isEmpty()) {


            Thread thread = new Thread() {
                @Override
                public void run() {
                    HttpURLConnection conn=null;
                    try {

                        String regid = userLocalStore.getUserRegistrationId();
                        String sendertname=userLocalStore.getLoggedInUser().getfullname();
                        ArrayList<Pair<String,String>> data=new ArrayList<>();


                        String message=getString(R.string.fcm_Notification_message_groceryList_created);
                        String title=getString(R.string.fcm_Notification_title_groceryList_created);

                        data.add(new Pair<String, String>("message", message+ " " +sendertname ));
                        data.add(new Pair<String, String>("registrationReceiverIDs",regId));
                        data.add(new Pair<String, String>("receiver",email));
                        data.add(new Pair<String, String>("sender", userLocalStore.getLoggedInUser().email));

                        data.add(new Pair<String, String>("registrationSenderIDs", regid));
                        data.add(new Pair<String, String>("title",title ));
                        data.add(new Pair<String, String>("apiKey", Config.FIREBASESERVER_KEY));

                        byte[] bytes = getData(data).getBytes("UTF-8");


                        URL url=new URL(Config.YOUR_SERVER_URL+ "FireBaseConnection.php");
                        conn=(HttpURLConnection)url.openConnection();
                        conn.setDoOutput(true);
                        conn.setUseCaches(false);
                        conn.setFixedLengthStreamingMode(bytes.length);
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type",
                                "application/x-www-form-urlencoded;charset=UTF-8");
                        // post the request
                        OutputStream out = conn.getOutputStream();
                        out.write(bytes);
                        out.close();

                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(conn.getInputStream()));
                        String inputLine;
                        StringBuffer reponse = new StringBuffer();

                        while ((inputLine = in.readLine()) != null) {
                            reponse.append(inputLine);
                        }
                        final String response =reponse.toString();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        if(conn!=null){
                            conn.disconnect();
                        }
                    }
                }
            };

            thread.start();

        }

    }
    public void saveFinanceAccountToServer(final FinanceAccount financeAccount, final GroceryList groceryList){
        ServerRequests serverRequests =new ServerRequests(this);
        serverRequests.updateFinanceAccountInBackgroung(financeAccount, new FinanceAccountCallbacks() {
            @Override
            public void fetchDone(ArrayList<FinanceAccount> returnedAccounts) {

            }

            @Override
            public void setServerResponse(String serverResponse) {

                if(serverResponse.contains("Account successfully updated")){
                    saveFinanceAccountLocally(financeAccount,groceryList);
                }else {
                    Toast.makeText(getApplicationContext(),"Error creating account "+ financeAccount.getAccountName(),Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    public void updateGroceryAndUpdateFinance(final FinanceAccount financeAccount, final GroceryList groceryList, final CalendarCollection calendarCollection){
        ServerRequests serverRequests =new ServerRequests(this);
        serverRequests.createGroceryAndUpdateFinanceAccountInBackgroung(groceryList, financeAccount, new FinanceAccountCallbacks() {
            @Override
            public void fetchDone(ArrayList<FinanceAccount> returnedAccounts) {

            }

            @Override
            public void setServerResponse(String serverResponse) {
                if(serverResponse.contains("Account Updated and Grocery list successfully created")){
                    saveEvent(calendarCollection,groceryList,financeAccount);
                }else {
                    Toast.makeText(getApplicationContext(),"Error creating account "+ financeAccount.getAccountName(),Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void createGroceryAndUpdateFinance(final FinanceAccount financeAccount, final GroceryList groceryList, final CalendarCollection calendarCollection){
        ServerRequests serverRequests =new ServerRequests(this);
        serverRequests.createGroceryAndUpdateFinanceAccountInBackgroung(groceryList, financeAccount, new FinanceAccountCallbacks() {
            @Override
            public void fetchDone(ArrayList<FinanceAccount> returnedAccounts) {

            }

            @Override
            public void setServerResponse(String serverResponse) {
                if(serverResponse.contains("Account Updated and Grocery list successfully created")){
                    String email=userLocalStore.getUserPartnerEmail();
                    fetchUserforFCMNotification(email,calendarCollection,groceryList,financeAccount);

                }else {
                    Toast.makeText(getApplicationContext(),"Error creating account "+ financeAccount.getAccountName(),Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
    public void fetchUserforFCMNotification(String email, final CalendarCollection calendarCollection, final GroceryList groceryList, final FinanceAccount financeAccount){
        ServerRequests serverRequests =new ServerRequests(this);
        serverRequests.fetchUserForFCMNotificationinBackground(email, new GetUserCallbacks() {
            @Override
            public void done(User returneduser) {
                if(returneduser!=null){
                    userLocalStore.storeUserPartnerData(returneduser);
                    userLocalStore.setUserPartnerRegId(returneduser.regId);
                    saveEvent(calendarCollection,groceryList,financeAccount);
                }else{
                    Toast.makeText(getApplicationContext(),"Error fetching partner data "+ financeAccount.getAccountName(),Toast.LENGTH_SHORT).show();
                    saveEvent(calendarCollection,groceryList,financeAccount);
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
    public void saveFinanceAccountLocally(FinanceAccount financeAccount, GroceryList groceryList){

        if (sqlFinanceAccount.updateFinanceAccount(financeAccount)!=0){
           saveGroceryListLocally(groceryList);
        }
    }
    private  boolean updateExcelFile(Context context, ArrayList<ShoppingItem> items) {
        boolean success= false;

        FileHelper fileHelper=new FileHelper(context);
        // Creating Input Stream

        // Create a path where we will place our List of objects on external storage
        File file=null;
        FileOutputStream os = null;

        try {

            // Creating Input Stream
            file = new File(fileHelper.getExcelfile("shopping_list_items"));


            FileInputStream myInput = new FileInputStream(file);


            XSSFWorkbook wb = new  XSSFWorkbook(myInput);
            XSSFSheet sheet = wb.getSheetAt(0);
            XSSFRow row;
            XSSFCell cell;

            Iterator rows = sheet.rowIterator();

            while (rows.hasNext())
            {
                row=(XSSFRow) rows.next();

                cell=row.getCell(0);

                for(int i=0;i<items.size();i++){
                    if(cell.getStringCellValue().equals(items.get(i).getItemName())){

                        row.getCell(2).setCellValue(items.get(i).getNumberoftimeAddedAnyToList());
                        System.out.print(cell.getStringCellValue()+" "+row.getCell(2).getNumericCellValue());
                    }
                }

                System.out.println();
            }



            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return success;
    }




    public ArrayList<ShoppingItem> getItemFromDB(ArrayList<String> itemsname,
                                                 ArrayList<String> itemsprice,
                                                 ArrayList<String> itemUsageFrequency
                                                ,ArrayList<String> itemCategory,
                                                 ArrayList<String> itemMarket){

        ArrayList<ShoppingItem> itemsdb=new ArrayList<>();
        for(int i=0;i<itemsname.size();i++){

            ShoppingItem item=new ShoppingItem();
            item.setPrice(itemsprice.get(i));
            item.setItemName(itemsname.get(i));
            int hashid=(itemsname.get(i) + itemsprice.get(i)).hashCode();
            item.setUnique_item_id((String.valueOf(hashid)));
            item.setDetailstoItem("");
            item.setItemSpecification("");
            switch (itemCategory.get(i)){
                case "Haushalt":
                    item.setItemcategory(getString(R.string.household));
                    break;
                case "Obst":
                    item.setItemcategory(getString(R.string.fruit));
                    break;
                case "Gemüse":
                    item.setItemcategory(getString(R.string.vegetables));
                    break;
                case "Getreideprodukte":
                    item.setItemcategory(getString(R.string.grain_products));
                    break;
                case "Technik":
                    item.setItemcategory(getString(R.string.technology));
                    break;
                case "Getränke":
                    item.setItemcategory(getString(R.string.drinks));
                    break;
                case "Gewürze":
                    item.setItemcategory(getString(R.string.spices));
                    break;
                case "Drogerie":
                    item.setItemcategory(getString(R.string.drugstore));
                    break;
                case "Sonstiges":
                    item.setItemcategory(getString(R.string.others));
                    break;
                case "Fette&Öle":
                    item.setItemcategory(getString(R.string.fats_and_oils));
                    break;
                case "Milchprodukte":
                    item.setItemcategory(getString(R.string.milk_products));
                    break;
                case "Fleisch":
                    item.setItemcategory(getString(R.string.meat));
                    break;
                case "Süßigkeiten":
                    item.setItemcategory(getString(R.string.sweets));
                    break;
            }

            item.setItemmarket(itemMarket.get(i));
            item.setNumberofItemsetForList(0);
            DecimalFormat df = new DecimalFormat("0.00");
            df.setMaximumFractionDigits(2);
            try {
                Number number=df.parse(itemUsageFrequency.get(i));
                item.setNumberoftimeAddedAnyToList(number.intValue());
            } catch (Exception e) {
                e.printStackTrace();
            }

            itemsdb.add(item);

        }
        itemstoShoparray=new ShoppingItem[itemsdb.size()];
        return itemsdb;
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
            super.onSaveInstanceState(state);

            state.putParcelableArrayList("itemDB", itemsDB);
        state.putParcelableArray("arrayitems", itemstoShoparray);
        state.putString("listname",listName);
        state.putString("sortType",sortName);

    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() > 0) {

            new SearchAsyncTask().execute(newText);
        } else {
           populateListview();
        }


        return false;
    }



    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.grocery_activity_add_itemtoList_button:
                startActivity(new Intent(this,CreatANewItemActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("itemDB",itemsDB));
                break;
            case R.id.dateaddeventstart_creatList_add_item_to_list:
                onDatePickercliced(true);
                break;
            case R.id.button_sort_activity_add_item_to_list:
               showDialogSortingOptions();
                break;
        }

    }


    void showDialogSortingOptions(){



        alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.custom_sort_options, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle(getString(R.string.activity_details_options_list_text_sort_by));
        ListView listView = (ListView) convertView.findViewById(R.id.listView_addItmeListActivity_sort_options);
        sortOptionslist=new ArrayList<>();
        sortOptionslist.add(getString(R.string.alphabetic));
        sortOptionslist.add(getString(R.string.market_Rewe));
        sortOptionslist.add(getString(R.string.market_DM));
        sortOptionslist.add(getString(R.string.market_Norma));
        sortOptionslist.add(getString(R.string.household));
        sortOptionslist.add(getString(R.string.fruit));
        sortOptionslist.add(getString(R.string.grain_products));
        sortOptionslist.add(getString(R.string.technology));
        sortOptionslist.add(getString(R.string.drinks));
        sortOptionslist.add(getString(R.string.fats_and_oils));
        sortOptionslist.add(getString(R.string.milk_products));
        sortOptionslist.add(getString(R.string.spices));
        sortOptionslist.add(getString(R.string.drugstore));
        sortOptionslist.add(getString(R.string.meat));
        sortOptionslist.add(getString(R.string.others));
        sortOptionslist.add(getString(R.string.sweets));

        if(selectedOnRows==null){
            selectedOnRows=new boolean[sortOptionslist.size()];
        }
        final SortOptionListAdapter sortOptionListAdapter=new SortOptionListAdapter(this, sortOptionslist, new SortOptionListAdapter.OnSortOptionsListener() {
            @Override
            public void onSortOption(String sortOption,boolean[] selected) {

                selectedOnRows=selected;
                if(!sortOption.isEmpty()){
                    sortName=sortOption;
                    alertDialog.dismiss();
                    sort(sortOption);
                }
            }
        });

        listView.setAdapter(sortOptionListAdapter);

        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(!sortOptionslist.get(i).isEmpty()){
                    alertDialog.dismiss();
                    sortName=sortOptionslist.get(i);
                    sort(sortOptionslist.get(i));
                    for(int j=0;j<selectedOnRows.length;j++){
                        selectedOnRows[j]=false;
                    }
                    selectedOnRows[i]=true;
                    sortOptionListAdapter.setNumberSelectedOnrow(selectedOnRows);
                }
            }
        });

        alertDialog.setCancelable(true);
        alertDialog.show();
    }
    @Override
    public void dateSet(String date, boolean isstart) {
        setDateButtuon.setText(date);
        listName=date;
    }

    private void initializeDatePicker(){
        final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        formatter.setLenient(false);

        Date curDate = new Date();
        String curTime = formatter.format(curDate);
        setDateButtuon.setText(curTime);
        listName=curTime;
    }
    public void onDatePickercliced(boolean bol){
        android.support.v4.app.FragmentManager manager=getSupportFragmentManager();
        DialogFragment fragmentDatePicker=DialogFragmentDatePicker.newInstance(bol);

        fragmentDatePicker.show(manager,"datePickerfr");
    }


    private  boolean saveExcelXLSXFileFirstInit(Context context) {
        boolean success= false;

        FileHelper fileHelper=new FileHelper(context);
        XSSFWorkbook  wb = null;
        String workbooksFolderpath= fileHelper.getWorkbooksFolder();
        String filesDirectorypath= fileHelper.getFilesDirectory();
        File filedirectory=new File(filesDirectorypath);

        if (!filedirectory.exists()){
            filedirectory.mkdirs();
        }
        File workbookfolder=new File(workbooksFolderpath);
        if(!workbookfolder.exists()){
            workbookfolder.mkdir();
        }
        // Create a path where we will place our List of objects on external storage
        File file=null;
        FileOutputStream os = null;

        try {
            AssetManager manager=getAssets();
            InputStream in=manager.open("book_shopping_item.xlsx");
            wb = new XSSFWorkbook(in);

             file = new File(fileHelper.getExcelfile("book_shopping_item"));
            if(!file.exists()){
                file.createNewFile();

            }

            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return success;
    }


    public static byte[] decodeBase64File(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return decodedByte;
    }

    public class FetchFileAsynckTacks extends AsyncTask<Void,Void,String>
    {

        FileHelper fileHelper=new FileHelper(getApplicationContext());
        @Override
        protected void onPostExecute(String file) {
            super.onPostExecute(file);
           File files = new File(fileHelper.getExcelfile("shopping_list_items"));
            FileOutputStream fos = null;

            try {
                fos = new FileOutputStream(files);
                fos.write(decodeBase64File(file));
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }


            new LoadItemDBAsyncTask().execute();

        }

        @Override
        protected String doInBackground(Void... params) {

          String file ="";
            URL url;
            HttpURLConnection urlConnection=null;

            try {


                url=new URL(ServerRequests.SERVER_ADDRESS + "FetchFileFromServer.php");
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(ServerRequests.CONNECTION_TIMEOUT);
                urlConnection.setConnectTimeout(ServerRequests.CONNECTION_TIMEOUT);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                InputStream in =urlConnection.getInputStream();
                String respons="";
                StringBuilder bi=new StringBuilder();
                BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                String line;
                while((line=reader.readLine())!=null){
                    bi.append(line).append("\n");
                }
                reader.close();
                in.close();

                respons =bi.toString();
                JSONArray jsonArray= new JSONArray(respons);
                JSONObject jo_inside = jsonArray.getJSONObject(0);

               file = jo_inside.getString("file");



                // fetch data to a jason object
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                assert urlConnection != null;
                urlConnection.disconnect();
            }

            return file;
        }


    }
    private void readExcelXLSXFile(Context context) {

        FileHelper fileHelper=new FileHelper(context);

        File file=null;
        try{
            // Creating Input Stream
            file = new File(fileHelper.getExcelfile("shopping_list_items"));


            FileInputStream myInput = new FileInputStream(file);


            XSSFWorkbook wb = new  XSSFWorkbook(myInput);
            XSSFSheet sheet = wb.getSheetAt(0);
            XSSFRow row;
            XSSFCell cell;

            Iterator rows = sheet.rowIterator();

            while (rows.hasNext())
            {
                row=(XSSFRow) rows.next();

                cell=row.getCell(0);

                if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING)
                {
                    System.out.print(cell.getStringCellValue()+" ");
                    itemNamee.add(cell.getStringCellValue());
                }
                cell=row.getCell(1);
               if(cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC)
                {
                    System.out.print(cell.getNumericCellValue()+" ");
                    itemPrice.add(String.valueOf(cell.getNumericCellValue()));
                }
                cell=row.getCell(2);
                if(cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC)
                {
                    System.out.print(cell.getNumericCellValue()+" ");
                    itemUsageFrequency.add(String.valueOf(cell.getNumericCellValue()));
                }
                cell=row.getCell(3);
                if(cell.getCellType() == XSSFCell.CELL_TYPE_STRING)
                {
                    System.out.print(cell.getStringCellValue()+" ");
                    itemMarket.add(String.valueOf(cell.getStringCellValue()));
                }
                cell=row.getCell(4);
                if(cell.getCellType() == XSSFCell.CELL_TYPE_STRING)
                {
                    System.out.print(cell.getStringCellValue()+" ");
                    itemCategory.add(String.valueOf(cell.getStringCellValue()));
                }

                System.out.println();
            }

        }catch (Exception e){e.printStackTrace(); }


    }

    public  void readXLSXFile()
    {


        XSSFWorkbook  wb = null;
        try {
            AssetManager manager=getAssets();
            InputStream in=manager.open("book_shopping_item.xlsx");
            wb = new XSSFWorkbook(in);
            XSSFWorkbook test = new XSSFWorkbook();

            XSSFSheet sheet = wb.getSheetAt(1);
            XSSFRow row;
            XSSFCell cell;

            Iterator rows = sheet.rowIterator();

            while (rows.hasNext())
            {
                row=(XSSFRow) rows.next();
                Iterator cells = row.cellIterator();
                while (cells.hasNext())
                {
                    cell=(XSSFCell) cells.next();

                    if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING)
                    {
                        System.out.print(cell.getStringCellValue()+" ");
                        itemNamee.add(cell.getStringCellValue());
                    }
                    else if(cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC)
                    {
                        System.out.print(cell.getNumericCellValue()+" ");
                        itemPrice.add(String.valueOf(cell.getNumericCellValue()));
                    }
                    else
                    {
                        //U Can Handel Boolean, Formula, Errors
                    }
                }
                System.out.println();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void writeXLSXFile() throws IOException {

        String excelFileName = "C:/Test.xlsx";//name of excel file


        String sheetName = "Sheet1";//name of sheet

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet(sheetName) ;

        //iterating r number of rows
        for (int r=0;r < 5; r++ )
        {

            XSSFRow row = sheet.createRow(r);


            //iterating c number of columns
            for (int c=0;c < 5; c++ )
            {
                XSSFCell cell = row.createCell(c);

                cell.setCellValue("Cell "+r+" "+c);
            }
        }

        FileOutputStream fileOut = new FileOutputStream(excelFileName);

        //write this workbook to an Outputstream.
        wb.write(fileOut);
        fileOut.flush();
        fileOut.close();
    }


    private static boolean saveExcelFile(Context context, String fileName) {

        boolean success = false;

        //New Workbook
        Workbook wb = new HSSFWorkbook();

        Cell c = null;

        //Cell style for header row
        CellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.LIME.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        //New Sheet
        Sheet sheet1 = null;
        sheet1 = wb.createSheet("myOrder");

        // Generate column headings
        Row row = sheet1.createRow(0);

        c = row.createCell(0);
        c.setCellValue("Item Number");
        c.setCellStyle(cs);

        c = row.createCell(1);
        c.setCellValue("Quantity");
        c.setCellStyle(cs);

        c = row.createCell(2);
        c.setCellValue("Price");
        c.setCellStyle(cs);

        sheet1.setColumnWidth(0, (15 * 500));
        sheet1.setColumnWidth(1, (15 * 500));
        sheet1.setColumnWidth(2, (15 * 500));

        // Create a path where we will place our List of objects on external storage
        File file = new File(context.getExternalFilesDir(null), fileName);
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return success;
    }

    private static void readExcelFile(Context context, String filename) {

        try{
            // Creating Input Stream
            File file = new File(context.getExternalFilesDir(null), filename);
            FileInputStream myInput = new FileInputStream(file);

            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);

            /** We now need something to iterate through the cells.**/
            Iterator<Row> rowIter = mySheet.rowIterator();

            while(rowIter.hasNext()){
                HSSFRow myRow = (HSSFRow) rowIter.next();
                Iterator<Cell> cellIter = myRow.cellIterator();
                while(cellIter.hasNext()){
                    HSSFCell myCell = (HSSFCell) cellIter.next();
                    Log.w("FileUtils", "Cell Value: " +  myCell.toString());
                    Toast.makeText(context, "cell Value: " + myCell.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }catch (Exception e){e.printStackTrace(); }

        return;
    }

    @Override
    public void onShoppingOtemSet(ShoppingItem item,int position) {

        for (int i=0; i<itemsDB.size();i++){
            if(itemsDB.get(i).getUnique_item_id().equals(item.getUnique_item_id())){
                itemstoShoparray[i]=item;
                break;
            }
        }


        initTotalPricetextView();

    }

    private void initTotalPricetextView(){
        double totalPrice=0;
        for (int i=0;i<itemstoShoparray.length;i++){
            if(itemstoShoparray[i]!=null){
                int numb=itemstoShoparray[i].getNumberofItemsetForList();
                totalPrice= totalPrice+Double.parseDouble(itemstoShoparray[i].getPrice())*numb;
            }
        }
        DecimalFormat df = new DecimalFormat("0.00");
        df.setMaximumFractionDigits(2);
        String priceStr = df.format(totalPrice);
        if((priceStr.replace(",",".")).equals("0.00")){
            textViewAmontToPay.setText(priceStr+"€");
            textViewAmontToPay.setTextColor(getResources().getColor(R.color.grey));
        }else {
            textViewAmontToPay.setText("- "+priceStr+"€");
            textViewAmontToPay.setTextColor(getResources().getColor(R.color.warning_color));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();
        newItem=false;
    }

    //in this myAsyncTask, we are fetching data from server for the search string entered by user.
    class SearchAsyncTask extends AsyncTask<String, Void, String> {
        ArrayList<ShoppingItem>  productList;
        String textSearch;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            productList =new ArrayList<>();
        }

        @Override
        protected String doInBackground(String... sText) {

            // url="http://lawgo.in/lawgo/products/user/1/search/"+sText[0];
            String returnResult = getProductList();
            this.textSearch = sText[0];
            return returnResult;

        }

        public String getProductList() {

            ShoppingItem object;
            String matchFound = "N";


            try {

                productList=itemsDB;
                //parse date for dateList
                for (int i = 0; i < productList.size(); i++) {





                    object=productList.get(i);


                    //check if this product is already there in productResults, if yes, then don't add it again.
                    matchFound = "N";

                    for (int j = 0; j < productResults.size(); j++) {

                        if (productResults.get(j).getItemName().equals(object.getItemName())) {
                            matchFound = "Y";
                        }
                    }

                    if (matchFound == "N") {
                        productResults.add(object);
                    }

                }


                return ("OK");

            } catch (Exception e) {
                e.printStackTrace();
                return ("Exception Caught");
            }




        }

        @Override
        protected void onPostExecute (String result){

            super.onPostExecute(result);

            if (result.equalsIgnoreCase("Exception Caught")) {
                Toast.makeText(getApplicationContext(), "Unable to connect to server,please try later", Toast.LENGTH_LONG).show();

            } else {
                //calling this method to filter the search results from productResults and move them to
                //filteredProductResults
                filterProductArray(textSearch);

                prepareListviewall(filteredProductResults);

            }
        }


    }

    void prepareListviewall(ArrayList<ShoppingItem> shoppingItems){

        if(shoppingItems.size()==0){
            textViewNoDatainDB.setText(R.string.text_search_itme_to_list_DataBase_empty);
        }else{

            textViewNoDatainDB.setText(R.string.text_search_itme_to_list_DataBase_not_empty);
        }
        ListViewAdapter listViewAdapter=new ListViewAdapter(this,shoppingItems,this);
        addItemtolistListview.setVisibility(View.VISIBLE);
        addItemtolistListview.setAdapter(listViewAdapter);
    }

    public void filterProductArray(String newText) {

        String pName;

        filteredProductResults.clear();
        for (int i = 0; i < productResults.size(); i++) {
            pName = productResults.get(i).getItemName().toLowerCase();
            if (pName.contains(newText.toLowerCase())) {
                filteredProductResults.add(productResults.get(i));



            }
        }

    }


    class LoadItemDBAsyncTask extends AsyncTask<Void ,Void, Void> {

        private FragmentProgressBarLoading progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //start progressBar
            progressDialog = new FragmentProgressBarLoading();
            progressDialog.setCancelable(false);
            progressDialog.show(getSupportFragmentManager(), "task_progress");
        }

        @Override
        protected Void doInBackground(Void... params) {

            //readXLSXFile();

            readExcelXLSXFile(getApplicationContext());
            return null;

        }

        @Override
        protected void onPostExecute(Void params) {
            //end progressBar
            progressDialog.dismiss(getSupportFragmentManager());

            if(itemsDB.size()==0 || itemsDB==null ){
                itemsDB=getItemFromDB(itemNamee,itemPrice,itemUsageFrequency,itemCategory,itemMarket);
            }
            populateListview();
        }
    }


   private void saveGroceryListToServer(final GroceryList groceryList, final CalendarCollection collection, final FinanceAccount financeAccount){
       ServerRequests serverRequests=new ServerRequests(this);
       serverRequests.saveGroceryListInBackgroung(groceryList, new GroceryListCallBacks() {
           @Override
           public void fetchDone(ArrayList<GroceryList> returnedGroceryLists) {

           }

           @Override
           public void setServerResponse(String serverResponse) {
               if(serverResponse.contains("Grocery list added successfully")){
                   // list save to server save it locally
                   saveEvent(collection,groceryList,financeAccount);

               }else {
                 // error snackbar
                   showSnackBar(groceryList,collection,financeAccount);
               }
           }
       });
   }

    void saveEvent(final CalendarCollection collection, final
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
                    /**if(financeAccount==null){
                        saveGroceryListLocally(groceryList);
                        saveeventtoSQl(collection);
                    }else {
                        saveFinanceAccountToServer(financeAccount,groceryList);
                        saveeventtoSQl(collection);
                    }
                    **/
                    saveFinanceAccountLocally(financeAccount,groceryList);
                    saveeventtoSQl(collection);

                }
            }
        });

    }

    void showDialogChoosingAccount(final ArrayList<ShoppingItem> list){




        alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();
        LayoutInflater inflater =  (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.custom_sort_options, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle(getString(R.string.activity_choose_account_for_grocery_list));
        ListView listView = (ListView) convertView.findViewById(R.id.listView_addItmeListActivity_sort_options);

        if(selectedOnRows==null){
            selectedOnRows=new boolean[accountsforshopping.size()];
        }
        final ChooseAccountAdapter chooseAccountAdapter=new ChooseAccountAdapter(this, accountsforshopping, new ChooseAccountAdapter.OnAccountChooseListener() {
            @Override
            public void onAccountChoosed(FinanceAccount financeAccount, boolean[] selected) {
                alertDialog.dismiss();
                sortName=financeAccount.getAccountName();
                selectedOnRows=selected;
                createGrocerylistAndSave(financeAccount
                        ,list);



            }
        });

        listView.setAdapter(chooseAccountAdapter);

        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(accountsforshopping.get(i)!=null){
                    alertDialog.dismiss();
                    sortName=accountsforshopping.get(i).getAccountName();

                    for(int j=0;j<selectedOnRows.length;j++){
                        selectedOnRows[j]=false;
                    }
                    selectedOnRows[i]=true;
                    chooseAccountAdapter.setNumberSelectedOnrow(selectedOnRows);
                    createGrocerylistAndSave(accountsforshopping.get(i)
                            ,list);

                }
            }
        });

        alertDialog.setCancelable(true);
        alertDialog.show();
    }

    private void saveeventtoSQl(CalendarCollection calendarCollections) {


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
                int incomingNotifiId =  mySQLiteHelper.addIncomingNotification(incomingNotification);

            }catch (Exception e){
                e.printStackTrace();

            }


    }
    public void showSnackBar(final GroceryList groceryList, final CalendarCollection collection, final FinanceAccount financeAccount){
        snackbar = Snackbar
                .make(coordinatorLayout, "An error occured during the connection to the server", Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createGroceryAndUpdateFinance(financeAccount,groceryList,collection);

                        // saveGroceryListToServer(groceryList,collection,financeAccount);
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
