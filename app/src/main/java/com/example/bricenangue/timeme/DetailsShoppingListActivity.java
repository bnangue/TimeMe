package com.example.bricenangue.timeme;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
    private int vlsort=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_shopping_list);

        sqLiteShoppingList=new SQLiteShoppingList(this);

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
            textViewAlreadySpent.setText(priceStr+"€");
            textViewAlreadySpent.setTextColor(getResources().getColor(R.color.grey));
        }else {
            textViewAlreadySpent.setText("- "+priceStr+"€");
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

        listViewAdapter=new ListAdapterCreateShopList(this,itemsDB,this);
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
             textViewAlreadySpent.setText(priceStr+"€");
             textViewAlreadySpent.setTextColor(getResources().getColor(R.color.grey));
         }else {
             textViewAlreadySpent.setText("- "+priceStr+"€");
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
        String groceryListName= getResources().getString(R.string.grocery_list_item_title_text )+ " " + date;

        textViewgroceryListName.setText(groceryListName);
        groceryList.setDatum(date);
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

    @Override
    public void changescanceled(boolean canceled) {
        if(!canceled){

        }else {
            onBackPressed();
        }
    }
}
