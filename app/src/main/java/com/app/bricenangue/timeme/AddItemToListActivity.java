package com.app.bricenangue.timeme;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.format.DateFormat;
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
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
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

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class AddItemToListActivity extends AppCompatActivity implements View.OnFocusChangeListener, SearchView.OnQueryTextListener,
        View.OnClickListener, ListViewAdapter.ShoppingItemSetListener, DialogFragmentDatePicker.OnDateGet {
    private SearchView search;
    private Button createnewitem, setDateButtuon;
    private ListView addItemtolistListview;
    private TextView textViewNoDatainDB, textViewAmontToPay;
    private ArrayList<String> sortOptionslist = new ArrayList<>();
    private ArrayList<String> itemNamee = new ArrayList<>();
    private ArrayList<String> itemPrice = new ArrayList<>();
    private ArrayList<String> itemUsageFrequency = new ArrayList<>();
    private ArrayList<ShoppingItem> itemsDB = new ArrayList<>();
    private ArrayList<ShoppingItem> itemstoShopListFromSorting = new ArrayList<>();
    private ShoppingItem[] itemstoShoparray;
    private Menu menu;
    private String listName = "";
    private String sortName = "";
    private boolean areItemsAdded = false;
    private UserLocalStore userLocalStore;
    private int[] status;


    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;


    private boolean isFromDetails = false;
    private GroceryList groceryListFromDetails;
    public static boolean newItem = false;

    //This arraylist will have data as pulled from server. This will keep cumulating.
    private ArrayList<ShoppingItem> productResults = new ArrayList<ShoppingItem>();
    //Based on the search string, only filtered products will be moved here from productResults
    private ArrayList<ShoppingItem> filteredProductResults = new ArrayList<ShoppingItem>();
    private ArrayList<String> itemCategory = new ArrayList<>();
    private ArrayList<String> itemMarket = new ArrayList<>();
    private Button buttonSort;
    private android.support.v7.app.AlertDialog alertDialog;
    private boolean[] selectedOnRows;
    private DatabaseReference firebaseReference;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private ProgressDialog progressBar;
    private ValueEventListener valueEventListener;
    private AlertDialog.Builder alert;
    private ArrayList<ShoppingItem> listofchoosenItem=new ArrayList<>();
    private DatabaseReference listofChossenReference;
    private Switch btnswitch;
    private boolean shareList =false;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_to_list);
        userLocalStore = new UserLocalStore(this);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseReference = FirebaseDatabase.getInstance().getReference().child(Config.FIREBASE_APP_URL_GROCERYLISTS);

        listofChossenReference=FirebaseDatabase.getInstance().getReference().child(Config.FIREBASE_APP_URL_SHOPPING_ITEMS_XSL)
                .child(auth.getCurrentUser().getUid()).child(Config.FIREBASE_APP_URL_SHOPPING_ITEMS_XSL_USER_LIST);

        alert = new AlertDialog.Builder(this);
        btnswitch=(Switch)findViewById(R.id.button_switch_activity_add_item_to_list);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinateLayout__creatList_add_item_to_list);
        setDateButtuon = (Button) findViewById(R.id.dateaddeventstart_creatList_add_item_to_list);
        createnewitem = (Button) findViewById(R.id.grocery_activity_add_itemtoList_button);
        addItemtolistListview = (ListView) findViewById(R.id.shoppinglistViewaddItemToListactivity);
        textViewNoDatainDB = (TextView) findViewById(R.id.textView_add_item_to_list_List_empty);
        textViewAmontToPay = (TextView) findViewById(R.id.grocery_fragment_balance_amount_activity_add_item_to_list);
        setDateButtuon.setOnClickListener(this);

        buttonSort = (Button) findViewById(R.id.button_sort_activity_add_item_to_list);



        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            if (extras.containsKey("itemDB")) {
                itemsDB = extras.getParcelableArrayList("itemDB");
                assert itemsDB != null;
                itemstoShoparray = new ShoppingItem[itemsDB.size()];

                initArray();

            } else if (extras.containsKey("ListToChange")) {
                isFromDetails = extras.getBoolean("isFromDetails");
                groceryListFromDetails = extras.getParcelable("ListToChange");
            }
            shareList=extras.getBoolean("shareList");
        }

        search = (SearchView) findViewById(R.id.actionbarsearch_add_item_to_list);
        search.setQueryHint(getString(R.string.start_typing_to_search));
        if (search.hasFocus()) {
            search.setBackgroundColor(getResources().getColor(R.color.white));
        }
        search.setOnQueryTextFocusChangeListener(this);
        search.setOnQueryTextListener(this);
        createnewitem.setOnClickListener(this);

        buttonSort.setOnClickListener(this);


        if (savedInstanceState == null) {
            if (groceryListFromDetails != null) {
                setDateButtuon.setText(groceryListFromDetails.getDatum());
                listName = groceryListFromDetails.getDatum();
            } else {
                initializeDatePicker();
            }

            //populate if itemdb!=0
            getShoppingListitmes();

        } else {
            itemsDB = savedInstanceState.getParcelableArrayList("itemDB");
            assert itemsDB != null;
            itemstoShoparray = new ShoppingItem[itemsDB.size()];
            initArray();
            listName = savedInstanceState.getString("listname");
            sortName = savedInstanceState.getString("sortType");


            setDateButtuon.setText(listName);

            initTotalPricetextView();
            //populate
            getShoppingListitmes();
            sort(sortName);
        }

        if (sortName.isEmpty()) {
            sortName = getString(R.string.alphabetic);


        } else {
            sort(sortName);
        }

       if(groceryListFromDetails!=null){
           btnswitch.setChecked(groceryListFromDetails.isToListshare());
       }else {
           btnswitch.setChecked(shareList);
       }
        if(btnswitch.isChecked()){
            btnswitch.setText("Shared");

        }else{
            btnswitch.setText("Private");

        }
        btnswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnswitch.isChecked()){
                    btnswitch.setText("Shared");

                }else{
                    btnswitch.setText("Private");

                }
            }
        });


        //readExcelXLSXFile(this);
        //createitemList(getItemFromDB(itemNamee,itemPrice,itemUsageFrequency,itemCategory,itemMarket));
    }

    private void initArray() {
        for (int i = 0; i < itemsDB.size(); i++) {

            if (itemsDB.get(i).getNumberofItemsetForList() != 0) {
                itemstoShoparray[i] = itemsDB.get(i);
            }


        }
        initTotalPricetextView();
    }

    void prepareListviewofSorted(ArrayList<ShoppingItem> shoppingItems) {

        if (shoppingItems.size() == 0) {
            textViewNoDatainDB.setText(R.string.text_search_itme_to_list_DataBase_empty);
        } else {

            textViewNoDatainDB.setText(R.string.text_search_itme_to_list_DataBase_not_empty);
        }
        ListViewAdapter listViewAdapter = new ListViewAdapter(this, shoppingItems, this);
        addItemtolistListview.setVisibility(View.VISIBLE);
        addItemtolistListview.setAdapter(listViewAdapter);
    }

    void sort(ArrayList<ShoppingItem> items) {

        Collections.sort(items, new ComparatorShoppingItemName());
    }

    void sort(String sortname) {

        sortName = sortname;

        if (sortName.equals(getString(R.string.market_Rewe))) {
            if (itemstoShopListFromSorting.size() != 0) {
                itemstoShopListFromSorting.clear();
                itemstoShopListFromSorting = new ArrayList<>();
            }
            for (int i = 0; i < itemsDB.size(); i++) {
                if (itemsDB.get(i).getItemmarket().equals(getString(R.string.rewe_Market))) {
                    itemstoShopListFromSorting.add(itemsDB.get(i));
                }
            }
            prepareListviewofSorted(itemstoShopListFromSorting);

        } else if (sortName.equals(getString(R.string.market_DM))) {

            if (itemstoShopListFromSorting.size() != 0) {
                itemstoShopListFromSorting.clear();
                itemstoShopListFromSorting = new ArrayList<>();
            }
            for (int i = 0; i < itemsDB.size(); i++) {
                if (itemsDB.get(i).getItemmarket().equals(getString(R.string.dm_Market))) {
                    itemstoShopListFromSorting.add(itemsDB.get(i));
                }
            }
            prepareListviewofSorted(itemstoShopListFromSorting);
        } else if (sortName.equals(getString(R.string.market_Norma))) {

            if (itemstoShopListFromSorting.size() != 0) {
                itemstoShopListFromSorting.clear();
                itemstoShopListFromSorting = new ArrayList<>();
            }
            for (int i = 0; i < itemsDB.size(); i++) {
                if (itemsDB.get(i).getItemmarket().equals(getString(R.string.norma_Market))) {
                    itemstoShopListFromSorting.add(itemsDB.get(i));
                }
            }
            prepareListviewofSorted(itemstoShopListFromSorting);
        } else if (sortName.equals(getString(R.string.alphabetic))) {

            Collections.sort(itemsDB, new ComparatorShoppingItemName());
            itemstoShoparray = new ShoppingItem[itemsDB.size()];
            initArray();
            initTotalPricetextView();
            populateListview();

        } else if (sortName.equals(getString(R.string.household))) {
            if (itemstoShopListFromSorting.size() != 0) {
                itemstoShopListFromSorting.clear();
                itemstoShopListFromSorting = new ArrayList<>();
            }
            for (int i = 0; i < itemsDB.size(); i++) {
                if (itemsDB.get(i).getItemcategory().equals(getString(R.string.household))) {
                    itemstoShopListFromSorting.add(itemsDB.get(i));
                }
            }
            prepareListviewofSorted(itemstoShopListFromSorting);

        } else if (sortName.equals(getString(R.string.fruit))) {
            if (itemstoShopListFromSorting.size() != 0) {
                itemstoShopListFromSorting.clear();
                itemstoShopListFromSorting = new ArrayList<>();
            }
            for (int i = 0; i < itemsDB.size(); i++) {
                if (itemsDB.get(i).getItemcategory().equals(getString(R.string.fruit))) {
                    itemstoShopListFromSorting.add(itemsDB.get(i));
                }
            }
            prepareListviewofSorted(itemstoShopListFromSorting);


        } else if (sortName.equals(getString(R.string.vegetables))) {
            if (itemstoShopListFromSorting.size() != 0) {
                itemstoShopListFromSorting.clear();
                itemstoShopListFromSorting = new ArrayList<>();
            }
            for (int i = 0; i < itemsDB.size(); i++) {
                if (itemsDB.get(i).getItemcategory().equals(getString(R.string.vegetables))) {
                    itemstoShopListFromSorting.add(itemsDB.get(i));
                }
            }
            prepareListviewofSorted(itemstoShopListFromSorting);


        } else if (sortName.equals(getString(R.string.grain_products))) {

            if (itemstoShopListFromSorting.size() != 0) {
                itemstoShopListFromSorting.clear();
                itemstoShopListFromSorting = new ArrayList<>();
            }
            for (int i = 0; i < itemsDB.size(); i++) {
                if (itemsDB.get(i).getItemcategory().equals(getString(R.string.grain_products))) {
                    itemstoShopListFromSorting.add(itemsDB.get(i));
                }
            }
            prepareListviewofSorted(itemstoShopListFromSorting);

        } else if (sortName.equals(getString(R.string.technology))) {

            if (itemstoShopListFromSorting.size() != 0) {
                itemstoShopListFromSorting.clear();
                itemstoShopListFromSorting = new ArrayList<>();
            }
            for (int i = 0; i < itemsDB.size(); i++) {
                if (itemsDB.get(i).getItemcategory().equals(getString(R.string.technology))) {
                    itemstoShopListFromSorting.add(itemsDB.get(i));
                }
            }
            prepareListviewofSorted(itemstoShopListFromSorting);

        } else if (sortName.equals(getString(R.string.drinks))) {

            if (itemstoShopListFromSorting.size() != 0) {
                itemstoShopListFromSorting.clear();
                itemstoShopListFromSorting = new ArrayList<>();
            }
            for (int i = 0; i < itemsDB.size(); i++) {
                if (itemsDB.get(i).getItemcategory().equals(getString(R.string.drinks))) {
                    itemstoShopListFromSorting.add(itemsDB.get(i));
                }
            }
            prepareListviewofSorted(itemstoShopListFromSorting);

        } else if (sortName.equals(getString(R.string.fats_and_oils))) {

            if (itemstoShopListFromSorting.size() != 0) {
                itemstoShopListFromSorting.clear();
                itemstoShopListFromSorting = new ArrayList<>();
            }
            for (int i = 0; i < itemsDB.size(); i++) {
                if (itemsDB.get(i).getItemcategory().equals(getString(R.string.fats_and_oils))) {
                    itemstoShopListFromSorting.add(itemsDB.get(i));
                }
            }
            prepareListviewofSorted(itemstoShopListFromSorting);

        } else if (sortName.equals(getString(R.string.milk_products))) {

            if (itemstoShopListFromSorting.size() != 0) {
                itemstoShopListFromSorting.clear();
                itemstoShopListFromSorting = new ArrayList<>();
            }
            for (int i = 0; i < itemsDB.size(); i++) {
                if (itemsDB.get(i).getItemcategory().equals(getString(R.string.milk_products))) {
                    itemstoShopListFromSorting.add(itemsDB.get(i));
                }
            }
            prepareListviewofSorted(itemstoShopListFromSorting);

        } else if (sortName.equals(getString(R.string.spices))) {

            if (itemstoShopListFromSorting.size() != 0) {
                itemstoShopListFromSorting.clear();
                itemstoShopListFromSorting = new ArrayList<>();
            }
            for (int i = 0; i < itemsDB.size(); i++) {
                if (itemsDB.get(i).getItemcategory().equals(getString(R.string.spices))) {
                    itemstoShopListFromSorting.add(itemsDB.get(i));
                }
            }
            prepareListviewofSorted(itemstoShopListFromSorting);

        } else if (sortName.equals(getString(R.string.drugstore))) {

            if (itemstoShopListFromSorting.size() != 0) {
                itemstoShopListFromSorting.clear();
                itemstoShopListFromSorting = new ArrayList<>();
            }
            for (int i = 0; i < itemsDB.size(); i++) {
                if (itemsDB.get(i).getItemcategory().equals(getString(R.string.drugstore))) {
                    itemstoShopListFromSorting.add(itemsDB.get(i));
                }
            }
            prepareListviewofSorted(itemstoShopListFromSorting);

        } else if (sortName.equals(getString(R.string.others))) {

            if (itemstoShopListFromSorting.size() != 0) {
                itemstoShopListFromSorting.clear();
                itemstoShopListFromSorting = new ArrayList<>();
            }
            for (int i = 0; i < itemsDB.size(); i++) {
                if (itemsDB.get(i).getItemcategory().equals(getString(R.string.others))) {
                    itemstoShopListFromSorting.add(itemsDB.get(i));
                }
            }
            prepareListviewofSorted(itemstoShopListFromSorting);

        } else if (sortName.equals(getString(R.string.meat))) {

            if (itemstoShopListFromSorting.size() != 0) {
                itemstoShopListFromSorting.clear();
                itemstoShopListFromSorting = new ArrayList<>();
            }
            for (int i = 0; i < itemsDB.size(); i++) {
                if (itemsDB.get(i).getItemcategory().equals(getString(R.string.meat))) {
                    itemstoShopListFromSorting.add(itemsDB.get(i));
                }
            }
            prepareListviewofSorted(itemstoShopListFromSorting);

        } else if (sortName.equals(getString(R.string.sweets))) {

            if (itemstoShopListFromSorting.size() != 0) {
                itemstoShopListFromSorting.clear();
                itemstoShopListFromSorting = new ArrayList<>();
            }
            for (int i = 0; i < itemsDB.size(); i++) {
                if (itemsDB.get(i).getItemcategory().equals(getString(R.string.sweets))) {
                    itemstoShopListFromSorting.add(itemsDB.get(i));
                }
            }
            prepareListviewofSorted(itemstoShopListFromSorting);

        }
        switch (sortname) {

            case "standard":
                Collections.sort(itemsDB, new ComparatorShoppingItemName());
                itemstoShoparray = new ShoppingItem[itemsDB.size()];
                initArray();
                initTotalPricetextView();
                populateListview();
                break;
            case "price ascending":
                Collections.sort(itemsDB, new ComparatorShoppingItemPrice());
                itemstoShoparray = new ShoppingItem[itemsDB.size()];
                initArray();
                initTotalPricetextView();
                populateListview();
                break;
            case "price descending":
                Comparator<ShoppingItem> comparator_type = Collections.reverseOrder(new ComparatorShoppingItemPrice());
                Collections.sort(itemsDB, comparator_type);
                itemstoShoparray = new ShoppingItem[itemsDB.size()];
                initArray();
                initTotalPricetextView();
                populateListview();
                break;
            case "most used":
                Comparator<ShoppingItem> comparator_type2 = Collections.reverseOrder(new ComparatorShoppingItemMostUsed());
                Collections.sort(itemsDB, comparator_type2);
                itemstoShoparray = new ShoppingItem[itemsDB.size()];
                initArray();
                initTotalPricetextView();
                populateListview();

                break;
            case "selected first":
                Comparator<ShoppingItem> comparator_type1 = Collections.reverseOrder(new CompareAddItemAlreadyadded());
                Collections.sort(itemsDB, comparator_type1);
                itemstoShoparray = new ShoppingItem[itemsDB.size()];
                initArray();
                initTotalPricetextView();
                populateListview();
                break;
            case "selected last":
                Collections.sort(itemsDB, new CompareAddItemAlreadyadded());
                itemstoShoparray = new ShoppingItem[itemsDB.size()];
                initArray();
                initTotalPricetextView();
                populateListview();
                break;


        }


    }

    void populateListview() {


        if (itemsDB.size() == 0) {
            textViewNoDatainDB.setText(R.string.text_add_itme_to_list_DataBase_empty);
        } else {

            textViewNoDatainDB.setText(R.string.text_add_itme_to_list_DataBase_not_empty);
        }

        if (productResults.size() != 0) {
            merge();
        }
        if (groceryListFromDetails != null) {
            for(ShoppingItem item :groceryListFromDetails.getItemsOftheList()){
                DatabaseReference ref=listofChossenReference.child(item.getUnique_item_id());
                ref.setValue(new ShoppingItem().getitemForFirebase(item));
            }
            merge(groceryListFromDetails.getItemsOftheList());
            initArray();

        }
        if (itemstoShopListFromSorting != null) {
            merge(itemstoShopListFromSorting);
            initArray();

        }
        if (itemstoShoparray == null) {
            itemstoShoparray = new ShoppingItem[itemsDB.size()];
            initArray();
        }
        ListViewAdapter listViewAdapter = new ListViewAdapter(this, itemsDB, this);
        addItemtolistListview.setVisibility(View.VISIBLE);
        addItemtolistListview.setAdapter(listViewAdapter);
    }

    private void merge() {
        for (int i = 0; i < productResults.size(); i++) {
            for (int j = 0; j < itemsDB.size(); j++) {
                if (productResults.get(i).getUnique_item_id().equals(itemsDB.get(j).getUnique_item_id()) &&
                        productResults.get(i).getNumberofItemsetForList() != itemsDB.get(j).getNumberofItemsetForList()) {

                    itemsDB.set(j, productResults.get(i));
                }
            }

        }

    }

    private void merge(ArrayList<ShoppingItem> productResults) {
        for (int i = 0; i < productResults.size(); i++) {
            for (int j = 0; j < itemsDB.size(); j++) {
                if (productResults.get(i).getUnique_item_id().equals(itemsDB.get(j).getUnique_item_id()) &&
                        productResults.get(i).getNumberofItemsetForList() != itemsDB.get(j).getNumberofItemsetForList()) {

                    itemsDB.set(j, productResults.get(i));
                }
            }

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_item_to_list_done, menu);

        return true;
    }

    private void hideOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    private void showOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }

    private void creategroceryListStep1(ArrayList<ShoppingItem> list){
        Calendar c = new GregorianCalendar();
        Date dat = c.getTime();
        //String day= String.valueOf(dat.getDay());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMAN);

        String date = (String) DateFormat.format("dd-MM-yyyy", dat);
        GroceryList groceryList = new GroceryList();
        groceryList.setItemsOftheList(list);
        groceryList.getListcontain();
        int hashid = (userLocalStore.getUserfullname() + format.format(dat)).hashCode();
        groceryList.setList_unique_id(String.valueOf(hashid));
        groceryList.setCreatorName(userLocalStore.getUserfullname());

        listName = setDateButtuon.getText().toString();
        if (!listName.isEmpty()) {
            groceryList.setDatum(listName);
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd, HH:mm");
        formatter.setLenient(false);

        Date currentDate = new Date();


        String currentTime = formatter.format(currentDate);
        CalendarCollection calendarCollection = new CalendarCollection(listName, groceryList.getListcontain(),
                groceryList.getCreatorName(), listName, listName + " 17:00",
                listName + " 20:00", String.valueOf(hashid), getString(R.string.Event_Category_Category_Shopping), "0", "0", currentTime);

        SimpleDateFormat formatterr = new SimpleDateFormat("dd-MM-yyy HH:mm:ss");
        String currentTimer = formatterr.format(currentDate);
        FinanceRecords financeRecords = new FinanceRecords(this, getString(R.string.textInitialize_create_account_grocery_note), currentTimer.split(" ")[0],
                getString(R.string.textInitialize_create_account_grocery_note), groceryList.getGroceryListTotalPriceToPayString().replace(",", ".")
                , String.valueOf(hashid),
                getString(R.string.textInitialize_create_account_grocery_category), groceryList.getDatum(), userLocalStore.getUserfullname(), 0, false, false);



        groceryList.setAccountid("");
        groceryList.setToListshare(btnswitch.isChecked());


        createGroceryStep2(groceryList);
    }

    private void createGroceryStep2(final GroceryList groceryList) {
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);
        progressBar.setTitle("Creating grocery list");
        progressBar.setMessage("in progress ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();

        DatabaseReference groceryRef;
        assert auth.getCurrentUser() != null;
        if(groceryList.isToListshare() && userLocalStore.getChatRoom().length()>2){
            groceryRef = databaseReference.child(Config.FIREBASE_APP_URL_GROCERYLISTS).child(Config.FIREBASE_APP_URL_GROCERYLISTS_SHARED)
                    .child(userLocalStore.getChatRoom());
        }else {
            groceryRef = databaseReference.child(Config.FIREBASE_APP_URL_GROCERYLISTS)
                    .child(auth.getCurrentUser().getUid());
        }

        GroceryListForFireBase groceryListForFirebase = new GroceryListForFireBase();
        groceryListForFirebase.setAccountid(groceryList.getAccountid());
        groceryListForFirebase.setCreatorName(groceryList.getCreatorName());
        groceryListForFirebase.setDatum(groceryList.getDatum());
        groceryListForFirebase.setList_unique_id(groceryList.getList_unique_id());
        groceryListForFirebase.setListcontain(groceryList.getListcontain());
        for (int i = 0; i < groceryList.getItemsOftheList().size(); i++) {
            groceryListForFirebase.getItems().add(new ShoppingItem().getitemForFirebase(groceryList.getItemsOftheList().get(i)));
        }
        groceryListForFirebase.setListdone(groceryList.isListdone());
        groceryListForFirebase.setToListshare(groceryList.isToListshare());

        groceryRef.child(groceryList.getList_unique_id()).setValue(groceryListForFirebase).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            updateExcelFile(groceryList.getItemsOftheList());
                            listofChossenReference.removeValue();
                            if (progressBar != null) {
                                progressBar.dismiss();
                            }

                            startActivity(new Intent(AddItemToListActivity.this,NewCalendarActivty.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                            Toast.makeText(getApplicationContext(), "Grocery list on " + groceryList.getDatum() + " created", Toast.LENGTH_SHORT).show();

                        } else {
                            if (progressBar != null) {
                                progressBar.dismiss();
                            }
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        if (!new ServerRequests(this).haveNetworkConnection()) {
            if (progressBar != null) {
                progressBar.dismiss();
                finish();
            }
        }

    }

    private void createGrocerylistAndSave(FinanceAccount financeAccount, ArrayList<ShoppingItem> list) {

        Calendar c = new GregorianCalendar();
        Date dat = c.getTime();
        //String day= String.valueOf(dat.getDay());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMAN);

        String date = (String) DateFormat.format("dd-MM-yyyy", dat);
        GroceryList groceryList = new GroceryList();
        groceryList.setItemsOftheList(list);
        groceryList.getListcontain();
        int hashid = (userLocalStore.getUserfullname() + format.format(dat)).hashCode();
        groceryList.setList_unique_id(String.valueOf(hashid));
        groceryList.setCreatorName(userLocalStore.getUserfullname());

        listName = setDateButtuon.getText().toString();
        if (!listName.isEmpty()) {
            groceryList.setDatum(listName);
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd, HH:mm");
        formatter.setLenient(false);

        Date currentDate = new Date();


        String currentTime = formatter.format(currentDate);
        CalendarCollection calendarCollection = new CalendarCollection(listName, groceryList.getListcontain(),
                groceryList.getCreatorName(), listName, listName + " 17:00",
                listName + " 20:00", String.valueOf(hashid), getString(R.string.Event_Category_Category_Shopping), "0", "0", currentTime);

        SimpleDateFormat formatterr = new SimpleDateFormat("dd-MM-yyy HH:mm:ss");
        String currentTimer = formatterr.format(currentDate);
        FinanceRecords financeRecords = new FinanceRecords(this, getString(R.string.textInitialize_create_account_grocery_note), currentTimer.split(" ")[0],
                getString(R.string.textInitialize_create_account_grocery_note), groceryList.getGroceryListTotalPriceToPayString().replace(",", ".")
                , String.valueOf(hashid),
                getString(R.string.textInitialize_create_account_grocery_category), groceryList.getDatum(), userLocalStore.getUserfullname(), 0, false, false);


        financeAccount.setContext(this);
        financeAccount.addRecordToAccount(financeRecords);
        financeAccount.setLastchangeToAccount();
        groceryList.setAccountid(financeAccount.getAccountUniqueId());
        groceryList.setToListshare(btnswitch.isChecked());


        createGroceryAndUpdateFinance(financeAccount, groceryList, calendarCollection);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_items_added_done) {

            if (isFromDetails) {

                ArrayList<ShoppingItem> list = new ArrayList<>();
                for (int i = 0; i < itemstoShoparray.length; i++) {
                    if (itemstoShoparray[i] != null && itemstoShoparray[i].getNumberofItemsetForList() > 0) {

                        list.add(itemstoShoparray[i]);
                    }

                }

                if (list.size() != 0) {
                    String shdate = setDateButtuon.getText().toString();
                    groceryListFromDetails.setDatum(shdate);
                    groceryListFromDetails.setItemsOftheList(list);
                    groceryListFromDetails.getListcontain();

                    DatabaseReference shoopinglistItem;
                    DatabaseReference refdate;
                    assert  auth.getCurrentUser()!=null;
                    if(groceryListFromDetails.isToListshare() && userLocalStore.getChatRoom().length()>2){
                        refdate=firebaseReference.child(Config.FIREBASE_APP_URL_GROCERYLISTS_SHARED)
                                .child(userLocalStore.getChatRoom())
                                .child(groceryListFromDetails.getList_unique_id()).child("datum");
                        shoopinglistItem=firebaseReference
                                .child(Config.FIREBASE_APP_URL_GROCERYLISTS_SHARED)
                                .child(userLocalStore.getChatRoom())
                                .child(groceryListFromDetails.getList_unique_id())
                                .child(Config.FIREBASE_APP_URL_SHARED_GROCERY_ITEMS_NODE);
                    }else {
                        refdate=firebaseReference.child(auth.getCurrentUser().getUid())
                                .child(groceryListFromDetails.getList_unique_id()).child("datum");
                        shoopinglistItem=firebaseReference
                                .child(auth.getCurrentUser().getUid())
                                .child(groceryListFromDetails.getList_unique_id())
                                .child(Config.FIREBASE_APP_URL_SHARED_GROCERY_ITEMS_NODE);
                    }


                    shoopinglistItem.setValue(new GroceryList().getGrocerylistitemForGLFirebase(list));


                    refdate.setValue(shdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(AddItemToListActivity.this,
                                        DetailsShoppingListActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        .putExtra("isFromDetails", true)
                                        .putExtra("GroceryListIsShared",groceryListFromDetails.isToListshare())
                                        .putExtra("GroceryListId", groceryListFromDetails.getList_unique_id()));

                                listofChossenReference.removeValue();
                                finish();

                            } else {
                                Toast.makeText(getApplicationContext(),
                                        task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                    Toast.makeText(getApplicationContext(), "Grocery list on " + groceryListFromDetails.getDatum() + " updated", Toast.LENGTH_SHORT).show();
                   /**
                    setDateButtuon.setText(groceryListFromDetails.getDatum());
                    updateExcelFile( groceryListFromDetails.getItemsOftheList());


                    assert auth.getCurrentUser() != null;
                    DatabaseReference shoopinglistItem = firebaseReference.child(auth.getCurrentUser().getUid()).
                            child(groceryListFromDetails.getList_unique_id());
                    GroceryListForFireBase groceryListForFirebase = new GroceryListForFireBase();
                    groceryListForFirebase.setAccountid(groceryListFromDetails.getAccountid());
                    groceryListForFirebase.setCreatorName(groceryListFromDetails.getCreatorName());

                    groceryListForFirebase.setDatum(groceryListFromDetails.getDatum());
                    groceryListForFirebase.setList_unique_id(groceryListFromDetails.getList_unique_id());
                    groceryListForFirebase.setListcontain(groceryListFromDetails.getListcontain());
                    for (int i = 0; i < list.size(); i++) {
                        groceryListForFirebase.getItems().add(new ShoppingItem().getitemForFirebase(list.get(i)));
                    }
                    /**
                    ArrayList<ShoppingItemForFireBase> arrayList = new ArrayList();
                    for (int i = 0; i < groceryListFromDetails.getItemsOftheList().size(); i++) {
                        arrayList.add(new ShoppingItem().getitemForFirebase(groceryListFromDetails.getItemsOftheList().get(i)));
                    }
                    groceryListForFirebase.setItems(arrayList);
                   //
                    groceryListForFirebase.setListdone(groceryListFromDetails.isListdone());
                    groceryListForFirebase.setToListshare(groceryListFromDetails.isToListshare());
                    shoopinglistItem.setValue(groceryListForFirebase).addOnCompleteListener(
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        startActivity(new Intent(AddItemToListActivity.this,
                                                DetailsShoppingListActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                .putExtra("isFromDetails", true)
                                                .putExtra("GroceryListId", groceryListFromDetails.getList_unique_id()));

                                        listofChossenReference.removeValue();
                                        finish();

                                    } else {
                                        Toast.makeText(getApplicationContext(),
                                                task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                    );
                    **/

                } else {

                    //error
                    Toast.makeText(getApplicationContext(), getString(R.string.an_error_occured_while_updating_the_list), Toast.LENGTH_SHORT).show();
                }

            } else {

                final ArrayList<ShoppingItem> list = new ArrayList<>();
                for (int j = 0; j < itemstoShoparray.length; j++) {
                    if (itemstoShoparray[j] != null
                            && itemstoShoparray[j].getNumberofItemsetForList() != 0) {

                        list.add(itemstoShoparray[j]);
                    }

                }
                if (list.size() != 0) {
                    creategroceryListStep1(list);
                } else {

                    listofChossenReference.removeValue();
                    Toast.makeText(getApplicationContext(), getString(R.string.no_item_added), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, CreateNewShoppingListActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                }

            }


            return true;
        } else if (id == R.id.action_item_list_refresh) {
            getShoppingListitmes();
            Toast.makeText(getApplicationContext(), getString(R.string.list_updated), Toast.LENGTH_SHORT).show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static String getData(ArrayList<Pair<String, String>> values) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        for (Pair<String, String> pair : values) {

            if (result.length() != 0)

                result.append("&");
            result.append(URLEncoder.encode(pair.first, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.second, "UTF-8"));

        }
        return result.toString();
    }


    // after List is created
    public void sendNotification(final String regId, final String email) {

        if (!userLocalStore.getUserRegistrationId().isEmpty()) {


            Thread thread = new Thread() {
                @Override
                public void run() {
                    HttpURLConnection conn = null;
                    try {

                        String regid = userLocalStore.getUserRegistrationId();
                        String sendertname = userLocalStore.getLoggedInUser().getfullname();
                        ArrayList<Pair<String, String>> data = new ArrayList<>();


                        String message = getString(R.string.fcm_Notification_message_groceryList_created);
                        String title = getString(R.string.fcm_Notification_title_groceryList_created);

                        data.add(new Pair<String, String>("message", message + " " + sendertname));
                        data.add(new Pair<String, String>("registrationReceiverIDs", regId));
                        data.add(new Pair<String, String>("receiver", email));
                        data.add(new Pair<String, String>("sender", userLocalStore.getLoggedInUser().email));

                        data.add(new Pair<String, String>("registrationSenderIDs", regid));
                        data.add(new Pair<String, String>("title", title));
                        data.add(new Pair<String, String>("chatRoom", "chatroom"));
                        data.add(new Pair<String, String>("apiKey", Config.FIREBASESERVER_KEY));

                        byte[] bytes = getData(data).getBytes("UTF-8");


                        URL url = new URL(Config.YOUR_SERVER_URL + "FireBaseConnection.php");
                        conn = (HttpURLConnection) url.openConnection();
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
                        final String response = reponse.toString();

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (conn != null) {
                            conn.disconnect();
                        }
                    }
                }
            };

            thread.start();

        }

    }

    public void createGroceryAndUpdateFinance(final FinanceAccount financeAccount, final GroceryList groceryList, final CalendarCollection calendarCollection) {
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);
        progressBar.setTitle("Creating grocery list");
        progressBar.setMessage("in progress ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();

        DatabaseReference groceryRef;
        assert auth.getCurrentUser() != null;
        if(groceryList.isToListshare() && userLocalStore.getChatRoom().length()>2){
            groceryRef = databaseReference.child(Config.FIREBASE_APP_URL_GROCERYLISTS).child(Config.FIREBASE_APP_URL_GROCERYLISTS_SHARED)
                    .child(userLocalStore.getChatRoom());
        }else {
            groceryRef = databaseReference.child(Config.FIREBASE_APP_URL_GROCERYLISTS)
                    .child(auth.getCurrentUser().getUid());
        }

        GroceryListForFireBase groceryListForFirebase = new GroceryListForFireBase();
        groceryListForFirebase.setAccountid(groceryList.getAccountid());
        groceryListForFirebase.setCreatorName(groceryList.getCreatorName());
        groceryListForFirebase.setDatum(groceryList.getDatum());
        groceryListForFirebase.setList_unique_id(groceryList.getList_unique_id());
        groceryListForFirebase.setListcontain(groceryList.getListcontain());
        for (int i = 0; i < groceryList.getItemsOftheList().size(); i++) {
            groceryListForFirebase.getItems().add(new ShoppingItem().getitemForFirebase(groceryList.getItemsOftheList().get(i)));
        }
        groceryListForFirebase.setListdone(groceryList.isListdone());
        groceryListForFirebase.setToListshare(groceryList.isToListshare());

        groceryRef.child(groceryList.getList_unique_id()).setValue(groceryListForFirebase).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            DatabaseReference financeref ;
                            if(btnswitch.isChecked() && userLocalStore.getChatRoom().length()>2){

                                financeref = databaseReference
                                        .child(Config.FIREBASE_APP_URL_FINANCE_ACCOUNTS)
                                        .child(Config.FIREBASE_APP_URL_FINANCE_ACCOUNTS_SHARED).child(userLocalStore.getChatRoom());
                            }else {
                                financeref = databaseReference
                                        .child(Config.FIREBASE_APP_URL_FINANCE_ACCOUNTS).child(auth.getCurrentUser().getUid());
                            }

                            financeref.child(financeAccount.getAccountUniqueId()).setValue(
                                    new FinanceAccount(getApplicationContext()).getFinanceAccountForFirebase(financeAccount)
                            ).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //send notification
                                        Toast.makeText(getApplicationContext(), "Grocery list on " + groceryList.getDatum() + " created", Toast.LENGTH_SHORT).show();
                                        updateExcelFile(groceryList.getItemsOftheList());
                                        if (progressBar != null) {
                                            progressBar.dismiss();
                                        }
                                        listofChossenReference.removeValue();
                                       startActivity(new Intent(AddItemToListActivity.this,NewCalendarActivty.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    } else {
                                        if (progressBar != null) {
                                            progressBar.dismiss();
                                        }
                                        if (task.getException().getMessage().equals(FirebaseError.NETWORK_ERROR)) {
                                            startActivity(new Intent(AddItemToListActivity.this,NewCalendarActivty.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        }
                                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            if (progressBar != null) {
                                progressBar.dismiss();
                            }
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        if (!new ServerRequests(this).haveNetworkConnection()) {
            if (progressBar != null) {
                progressBar.dismiss();
                finish();
            }
        }

    }

    private boolean updateExcelFile(ArrayList<ShoppingItem> items) {

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(Config.FIREBASE_APP_URL_SHOPPING_ITEMS_XSL)
                .child(Config.FIREBASE_APP_URL_SHOPPING_ITEMS_XSL_ITEMS_NODE);

        boolean success = false;

        for(int i=0; i<items.size();i++){
            items.get(i).setNumberofItemsetForList(0);
           success = ref.child(items.get(i).getUnique_item_id()).setValue(
                    new ShoppingItem().getitemForFirebase(items.get(i))
            ).isSuccessful();
        }

        listofChossenReference.removeValue();
        return success;
    }


    public void setCategoriesForItems(ArrayList<ShoppingItem> list){
        for(ShoppingItem item: list){
            switch (item.getItemcategory()) {
                case "Household":
                    item.setItemcategory(getString(R.string.household));
                    break;
                case "Fruit":
                    item.setItemcategory(getString(R.string.fruit));
                    break;
                case "Vegetables":
                    item.setItemcategory(getString(R.string.vegetables));
                    break;
                case "Grain products":
                    item.setItemcategory(getString(R.string.grain_products));
                    break;
                case "Technology":
                    item.setItemcategory(getString(R.string.technology));
                    break;
                case "Drinks":
                    item.setItemcategory(getString(R.string.drinks));
                    break;
                case "Spices":
                    item.setItemcategory(getString(R.string.spices));
                    break;
                case "Drugstore":
                    item.setItemcategory(getString(R.string.drugstore));
                    break;
                case "Others":
                    item.setItemcategory(getString(R.string.others));
                    break;
                case "Fats and oils":
                    item.setItemcategory(getString(R.string.fats_and_oils));
                    break;
                case "Milk products":
                    item.setItemcategory(getString(R.string.milk_products));
                    break;
                case "Meat":
                    item.setItemcategory(getString(R.string.meat));
                    break;
                case "Sweets":
                    item.setItemcategory(getString(R.string.sweets));
                    break;
            }
        }
    }
    public ArrayList<ShoppingItem> getItemFromDB(ArrayList<String> itemsname,
                                                 ArrayList<String> itemsprice,
                                                 ArrayList<String> itemUsageFrequency
            , ArrayList<String> itemCategory,
                                                 ArrayList<String> itemMarket) {

        ArrayList<ShoppingItem> itemsdb = new ArrayList<>();
        for (int i = 0; i < itemsname.size(); i++) {

            ShoppingItem item = new ShoppingItem();
            item.setPrice(itemsprice.get(i));
            item.setItemName(itemsname.get(i));
            int hashid = (itemsname.get(i) + itemsprice.get(i) + itemMarket.get(i)).hashCode();
            item.setUnique_item_id((String.valueOf(hashid)));
            item.setDetailstoItem("");
            item.setItemSpecification("");
            switch (itemCategory.get(i)) {
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
                Number number = df.parse(itemUsageFrequency.get(i));
                item.setNumberoftimeAddedAnyToList(number.intValue());
            } catch (Exception e) {
                e.printStackTrace();
            }

            itemsdb.add(item);

        }
        itemstoShoparray = new ShoppingItem[itemsdb.size()];
        sort(itemsdb);
        return itemsdb;
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        state.putParcelableArrayList("itemDB", itemsDB);
        state.putParcelableArray("arrayitems", itemstoShoparray);
        state.putString("listname", listName);
        state.putString("sortType", sortName);

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
        int id = v.getId();
        switch (id) {
            case R.id.grocery_activity_add_itemtoList_button:
                startActivity(new Intent(this, CreatANewItemActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("itemDB",itemsDB)
                .putExtra("shareList",btnswitch.isChecked()));
                break;
            case R.id.dateaddeventstart_creatList_add_item_to_list:
                onDatePickercliced(true);
                break;
            case R.id.button_sort_activity_add_item_to_list:
                showDialogSortingOptions();
                break;
        }

    }


    void showDialogSortingOptions() {


        alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.custom_sort_options, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle(getString(R.string.activity_details_options_list_text_sort_by));
        ListView listView = (ListView) convertView.findViewById(R.id.listView_addItmeListActivity_sort_options);
        sortOptionslist = new ArrayList<>();
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

        if (selectedOnRows == null) {
            selectedOnRows = new boolean[sortOptionslist.size()];
        }
        final SortOptionListAdapter sortOptionListAdapter = new SortOptionListAdapter(this, sortOptionslist, new SortOptionListAdapter.OnSortOptionsListener() {
            @Override
            public void onSortOption(String sortOption, boolean[] selected) {

                selectedOnRows = selected;
                if (!sortOption.isEmpty()) {
                    sortName = sortOption;
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
                if (!sortOptionslist.get(i).isEmpty()) {
                    alertDialog.dismiss();
                    sortName = sortOptionslist.get(i);
                    sort(sortOptionslist.get(i));
                    for (int j = 0; j < selectedOnRows.length; j++) {
                        selectedOnRows[j] = false;
                    }
                    selectedOnRows[i] = true;
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
        listName = date;
    }

    private void initializeDatePicker() {
        final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        formatter.setLenient(false);

        Date curDate = new Date();
        String curTime = formatter.format(curDate);
        setDateButtuon.setText(curTime);
        listName = curTime;
    }

    public void onDatePickercliced(boolean bol) {
        FragmentManager manager = getSupportFragmentManager();
        DialogFragment fragmentDatePicker = DialogFragmentDatePicker.newInstance(bol);

        fragmentDatePicker.show(manager, "datePickerfr");
    }

    @Override
    public void onShoppingOtemSet(ShoppingItem item, int position) {

        for (int i = 0; i < itemsDB.size(); i++) {
            if (itemsDB.get(i).getUnique_item_id().equals(item.getUnique_item_id())) {
                item.setItemIsBought(itemsDB.get(i).isItemIsBought());
                itemstoShoparray[i] = item;
               DatabaseReference ref=listofChossenReference.child(item.getUnique_item_id());
                if(item.getNumberofItemsetForList()!=0){
                    ref.setValue(new ShoppingItem().getitemForFirebase(item));
                }else {
                    ref.removeValue();
                }

                break;
            }
        }


        initTotalPricetextView();

    }

    private void initTotalPricetextView() {
        double totalPrice = 0;
        for (int i = 0; i < itemstoShoparray.length; i++) {
            if (itemstoShoparray[i] != null) {
                int numb = itemstoShoparray[i].getNumberofItemsetForList();
                totalPrice = totalPrice + Double.parseDouble(itemstoShoparray[i].getPrice()) * numb;
            }
        }
        DecimalFormat df = new DecimalFormat("0.00");
        df.setMaximumFractionDigits(2);
        String priceStr = df.format(totalPrice);
        if ((priceStr.replace(",", ".")).equals("0.00")) {
            textViewAmontToPay.setText(priceStr + "€");
            textViewAmontToPay.setTextColor(getResources().getColor(R.color.grey));
        } else {
            textViewAmontToPay.setText("- " + priceStr + "€");
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
        newItem = false;
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    //in this myAsyncTask, we are fetching data from server for the search string entered by user.
    class SearchAsyncTask extends AsyncTask<String, Void, String> {
        ArrayList<ShoppingItem> productList;
        String textSearch;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            productList = new ArrayList<>();
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

                productList = itemsDB;
                //parse date for dateList
                for (int i = 0; i < productList.size(); i++) {


                    object = productList.get(i);


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
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            if (result.equalsIgnoreCase("Exception Caught")) {
                Toast.makeText(getApplicationContext(), getString(R.string.unable_to_connect_to_server_try_later), Toast.LENGTH_LONG).show();

            } else {
                //calling this method to filter the search results from productResults and move them to
                //filteredProductResults
                filterProductArray(textSearch);

                prepareListviewall(filteredProductResults);

            }
        }


    }

    void prepareListviewall(ArrayList<ShoppingItem> shoppingItems) {

        if (shoppingItems.size() == 0) {
            textViewNoDatainDB.setText(R.string.text_search_itme_to_list_DataBase_empty);
        } else {

            textViewNoDatainDB.setText(R.string.text_search_itme_to_list_DataBase_not_empty);
        }
        ListViewAdapter listViewAdapter = new ListViewAdapter(this, shoppingItems, this);
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




    private void getShoppingListitmes() {

        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);
        progressBar.setTitle("Loading items");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();

        final ArrayList<ShoppingItem> list=new ArrayList<>();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(Config.FIREBASE_APP_URL_SHOPPING_ITEMS_XSL)
                .child(Config.FIREBASE_APP_URL_SHOPPING_ITEMS_XSL_ITEMS_NODE);
        final DatabaseReference listRef=FirebaseDatabase.getInstance().getReference().child(Config.FIREBASE_APP_URL_SHOPPING_ITEMS_XSL)
                .child(auth.getCurrentUser().getUid()).child(Config.FIREBASE_APP_URL_SHOPPING_ITEMS_XSL_USER_LIST);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemsDB.clear();
                list.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    ShoppingItem item = new ShoppingItem().getitemFromFirebase(child.getValue(ShoppingItemForFireBase.class));
                    list.add(item);
                }

                setCategoriesForItems(list);
                itemsDB=list;
                listRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        listofchoosenItem.clear();
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            ShoppingItem item = new ShoppingItem().getitemFromFirebase(child.getValue(ShoppingItemForFireBase.class));
                            listofchoosenItem.add(item);
                        }

                        merge(listofchoosenItem);
                        itemstoShoparray = new ShoppingItem[itemsDB.size()];

                        sort(itemsDB);
                        initArray();
                        populateListview();
                        listRef.removeEventListener(this);
                        if (progressBar != null) {
                            progressBar.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        if (progressBar != null) {
                            progressBar.dismiss();
                        }
                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(getApplicationContext(),
                        databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                if (progressBar != null) {
                    progressBar.dismiss();
                }
            }
        });
        if (!new ServerRequests(this).haveNetworkConnection()) {
            showSnackBarNoInternet();
            if (progressBar != null) {
                progressBar.dismiss();
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        listofChossenReference.removeValue();
    }

    private void createitemList(ArrayList<ShoppingItem> list) {
        ArrayList<ShoppingItemForFireBase> arrayList = new ArrayList<>();
        for (ShoppingItem item : list) {
            arrayList.add(new ShoppingItem().getitemForFirebase(item));
        }
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(Config.FIREBASE_APP_URL_SHOPPING_ITEMS_XSL)
                .child(Config.FIREBASE_APP_URL_SHOPPING_ITEMS_XSL_ITEMS_NODE);

        for (ShoppingItemForFireBase item : arrayList) {
            ref.child(item.getUnique_item_id()).setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                    }
                }
            });
        }

    }




    public void showSnackBar(final GroceryList groceryList, final CalendarCollection collection, final FinanceAccount financeAccount) {
        snackbar = Snackbar
                .make(coordinatorLayout, getString(R.string.error_occured_during_connecting_to_server), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.retry), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
        snackbar.setActionTextColor(Color.WHITE);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.colorSnackbar));
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public void showSnackBarNoInternet() {
        snackbar = Snackbar
                .make(coordinatorLayout, getString(R.string.internet_connection_error_message), Snackbar.LENGTH_INDEFINITE);
        snackbar.setActionTextColor(Color.WHITE);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.colorSnackbar));
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
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

}
