package com.example.bricenangue.timeme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class DetailsShoppingListActivity extends AppCompatActivity implements ListAdapterCreateShopList.ShoppingItemBoughtListener, AdapterView.OnItemClickListener {

    private ArrayList<ShoppingItem> itemsDB=new ArrayList<>();
    private ListAdapterCreateShopList listViewAdapter;
    private boolean[] itemsBought;
    private GroceryList groceryList;
    private ListView shoppinglistview;
    private TextView textViewAlreadySpent;
    double totalpriceTopay;

    private SQLiteShoppingList sqLiteShoppingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_shopping_list);

        sqLiteShoppingList=new SQLiteShoppingList(this);

        shoppinglistview=(ListView)findViewById(R.id.listView_activity_details_shoppping_list);
        textViewAlreadySpent=(TextView)findViewById(R.id.grocery_fragment_balance_amount_activity_details_shopping_list);

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
        if(groceryList.allItemsbought()){
            //signalize user and save list
        }
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
