package com.example.bricenangue.timeme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DetailsShoppingListActivity extends AppCompatActivity implements ListAdapterCreateShopList.ShoppingItemBoughtListener, AdapterView.OnItemClickListener {

    private ArrayList<ShoppingItem> itemsDB=new ArrayList<>();
    private ListAdapterCreateShopList listViewAdapter;
    private boolean[] itemsBought;
    private GroceryList groceryList;
    private ListView shoppinglistview;
    private TextView textViewAlreadySpent;
    double totalpriceTopay;
    private Spinner spinner;
    String [] userAccArray={"standard","most used","price ascending","price descending","selected first","selected last"};


    private SQLiteShoppingList sqLiteShoppingList;
    private int vlsort=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_shopping_list);

        sqLiteShoppingList=new SQLiteShoppingList(this);

        shoppinglistview=(ListView)findViewById(R.id.listView_activity_details_shoppping_list);
        textViewAlreadySpent=(TextView)findViewById(R.id.grocery_fragment_balance_amount_activity_details_shopping_list);

        spinner=(Spinner)findViewById(R.id.spinner_activity_details_shopping_list);
        SpinnerAdapter adap = new ArrayAdapter<>(this, R.layout.spinnerlayout, userAccArray);
        spinner.setAdapter(adap);

        spinner.setSelection(0);

        Bundle extras=getIntent().getExtras();
        if(extras!=null && extras.containsKey("GroceryList")){
            groceryList= extras.getParcelable("GroceryList");
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
                textViewAlreadySpent.setText(priceStr+"€");
                textViewAlreadySpent.setTextColor(getResources().getColor(R.color.grey));
            }else {
                textViewAlreadySpent.setText("- "+priceStr+"€");
                textViewAlreadySpent.setTextColor(getResources().getColor(R.color.warning_color));
            }
            prepareOrientationchange();
        }else{

                populateListview();



        }
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBooleanArray("itemsBought",itemsBought);
        outState.putParcelableArrayList("shoppinglist",itemsDB);
        outState.putDouble("totalpriceTopay",totalpriceTopay);
        outState.putParcelable("groceryList",groceryList);
    }

    @Override
    public void onShoppingItemBought(ShoppingItem item, boolean[] positions) {
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
        if(priceStr.equals("0.00")){
            textViewAlreadySpent.setText(priceStr+"€");
            textViewAlreadySpent.setTextColor(getResources().getColor(R.color.grey));
        }else {
            textViewAlreadySpent.setText("- "+priceStr+"€");
            textViewAlreadySpent.setTextColor(getResources().getColor(R.color.warning_color));
        }


        totalpriceTopay= Double.parseDouble(priceStr);
        groceryList.setItemsOftheList(itemsDB);
        sort(spinner.getSelectedItem().toString());
        if(groceryList.allItemsbought()){
            //signalize user and save list
        }
    }


    void sort(String sortname){
        switch (sortname){
            case "standard":
                Collections.sort(itemsDB, new ComparatorCreatorName());
                break;
            case "price ascending":
                Collections.sort(itemsDB, new ComparatorValueDown());
                break;
            case "price descending":
                Comparator<ShoppingItem> comparator_type = Collections.reverseOrder(new ComparatorValueDown());
                Collections.sort(itemsDB, comparator_type);
                break;
            case "most used":
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

        listViewAdapter=new ListAdapterCreateShopList(this,itemsDB,this);
        if(itemsBought==null){
        itemsBought=new boolean[itemsDB.size()];
         }
         double totalspent=0;
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
         if(priceStr.equals("0.00")){
             textViewAlreadySpent.setText(priceStr+"€");
             textViewAlreadySpent.setTextColor(getResources().getColor(R.color.grey));
         }else {
             textViewAlreadySpent.setText("- "+priceStr+"€");
             textViewAlreadySpent.setTextColor(getResources().getColor(R.color.warning_color));
         }


         totalpriceTopay= Double.parseDouble(priceStr);
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

    @Override
    public void onBackPressed() {
        groceryList.allItemsbought();
       if( sqLiteShoppingList.updateShoppingList(groceryList)==1){

           super.onBackPressed();
       }else {
           Toast.makeText(getApplicationContext()," Error: List not saved",Toast.LENGTH_SHORT).show();
       }


    }
}
