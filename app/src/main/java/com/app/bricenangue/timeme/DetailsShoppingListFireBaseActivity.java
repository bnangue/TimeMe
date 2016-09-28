package com.app.bricenangue.timeme;

import android.os.AsyncTask;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DetailsShoppingListFireBaseActivity extends AppCompatActivity implements
        ListAdapterCreateShopList.ShoppingItemBoughtListener,
        AdapterView.OnItemClickListener,AlertDialogChangeNotSaved.OnChangesCancel,DialogFragmentDatePicker.OnDateGet {

    private ArrayList<ShoppingItem> itemsDB=new ArrayList<>();
    private ListAdapterCreateShopList listViewAdapter;
    private GroceryList groceryList;
    private ListView shoppinglistview;
    private TextView textViewAlreadySpent,textViewgroceryListName;
    double totalpriceTopay;
    private Spinner spinner;
    private Menu menu;
    String [] userAccArray={"most used","price ascending","price descending","selected first","selected last"};

    private SQLiteShoppingList sqLiteShoppingList;
    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;
    private static boolean somethingChanged=false;
    private boolean isToUpdate=false;
    private MySQLiteHelper mySQLiteHelper;
    private SQLFinanceAccount sqlFinanceAccount;

    private UserLocalStore userLocalStore;
    private boolean listIsShared;
    private Firebase firebaseReference;
    private String shoppingListid;
    private boolean[] itemsBought;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_shopping_list_fire_base);
        firebaseReference=new Firebase(Config.FIREBASE_APP_URL).child(Config.FIREBASE_APP_URL_SHARED_GROCERY);
        Bundle extras=getIntent().getExtras();
        if(extras!=null && extras.containsKey("GroceryListId")){
            shoppingListid=extras.getString("GroceryListId");
        }
        shoppinglistview=(ListView)findViewById(R.id.listView_activity_details_shoppping_list_FireBase);
        userLocalStore= new UserLocalStore(this);
        coordinatorLayout=(CoordinatorLayout)findViewById(R.id.coordinateLayout_activity_details_shoppping_list_FireBase);
        shoppinglistview=(ListView)findViewById(R.id.listView_activity_details_shoppping_list_FireBase);
        textViewAlreadySpent=(TextView)findViewById(R.id.grocery_fragment_balance_amount_activity_details_shopping_list_FireBase);
        textViewgroceryListName=(TextView)findViewById(R.id.textView_grocery_list_item_title_activity_details_shopping_FireBase);

        spinner=(Spinner)findViewById(R.id.spinner_activity_details_shopping_list_FireBase);
        SpinnerAdapter adap = new ArrayAdapter<>(this, R.layout.spinnerlayout, userAccArray);
        spinner.setAdapter(adap);

        spinner.setSelection(0);
    }

    @Override
    public void changescanceled(boolean canceled) {

    }

    @Override
    public void dateSet(String date, boolean isstart) {

        //alertDialogDelete();
    }



    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }



    @Override
    protected void onStart() {
        super.onStart();


        final Firebase shoopinglistItem=firebaseReference;



        /**
         * final Firebase shoopinglistItem=firebaseReference.child(shoppingListid)
         .child(Config.FIREBASE_APP_URL_SHARED_GROCERY_ITEMS_NODE);
        **/
        shoopinglistItem.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getKey().equals(shoppingListid)){
                    GroceryListForFireBase groceryListForFireBase=dataSnapshot.getValue(GroceryListForFireBase.class);
                    GroceryList groceryList1=new GroceryList().getGrocerylistFromGLFirebase(groceryListForFireBase);
                    groceryList=groceryList1;
                   itemsDB=groceryList1.getItemsOftheList();
                    populateListview();


                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getKey().equals(shoppingListid)){
                    GroceryListForFireBase groceryListForFireBase=dataSnapshot.getValue(GroceryListForFireBase.class);
                    GroceryList groceryList1=new GroceryList().getGrocerylistFromGLFirebase(groceryListForFireBase);
                    groceryList=groceryList1;
                    itemsDB=groceryList1.getItemsOftheList();
                    listViewAdapter.notifyDataSetChanged();


                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        shoopinglistItem.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){

                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
      //  new LoadItemFireDBAsyncTask(shoopinglistItem).execute();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBooleanArray("itemsBought",itemsBought);
        outState.putParcelableArrayList("shoppinglist",itemsDB);
        outState.putDouble("totalpriceTopay",totalpriceTopay);
        outState.putParcelable("groceryList",groceryList);
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
            for(int i =0;i<itemsDB.size();i++){
                itemsBought[i]=itemsDB.get(i).isItemIsBought();
            }
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

       // groceryList.setItemsOftheList(itemsDB);

        shoppinglistview.setVisibility(View.VISIBLE);
        shoppinglistview.setAdapter(listViewAdapter);
        shoppinglistview.setOnItemClickListener(this);

    }

    private FirebaseListAdapter<ShoppingItemForFireBase> initAdapter(final DatabaseReference shoopinglistItem){
        final holder viewHolder = new holder();
        FirebaseListAdapter<ShoppingItemForFireBase> adapter=new FirebaseListAdapter<ShoppingItemForFireBase>(this,
                ShoppingItemForFireBase.class,
                R.layout.shopping_list_items,
                shoopinglistItem) {
            @Override
            protected void populateView(View view, ShoppingItemForFireBase shoppingItemForFireBase, final int i) {

                final ShoppingItem shoppingItem=new ShoppingItem().getitemFromFirebase(shoppingItemForFireBase);

                viewHolder.itemName = (TextView) view.findViewById(R.id.TextView_list_item_title_itemcreate_shopping_list);
                viewHolder.itemPrice = (TextView) view.findViewById(R.id.TextView_list_item_price_itemcreate_shopping_list);

                viewHolder.itemCategory = (TextView) view.findViewById(R.id.TextView_shopping_list_item_categorycreate_shopping_list);
                viewHolder.itemMArket = (TextView) view.findViewById(R.id.TextView_shopping_list_item_marketcreate_shopping_list);
                viewHolder.nummberitemSelected = (TextView) view.findViewById(R.id.TextView_list_item_number_of_item_selectedcreate_shopping_list);
                viewHolder.totalPrice = (TextView) view.findViewById(R.id.TextView_list_item_total_item_pricecreate_shopping_list);
                viewHolder.buttonisbought = (ImageButton) view.findViewById(R.id.checkerImageViewcreate_shopping_list);


                viewHolder.nummberitemSelected.setTextColor(getResources().getColor(R.color.black));
                // fill Data
                viewHolder.itemName.setText(shoppingItem.getItemName());
                viewHolder.itemPrice.setText(shoppingItem.getPrice()+" €");

                String itemCategory="("+shoppingItem.getItemcategory()+")";
                viewHolder.itemCategory.setText(itemCategory);
                viewHolder.itemMArket.setText(shoppingItem.getItemmarket());


                int numb=shoppingItem.getNumberofItemsetForList();
                double price= Double.parseDouble(shoppingItem.getPrice())*numb;
                DecimalFormat df = new DecimalFormat("0.00");
                df.setMaximumFractionDigits(2);
                String priceStr = df.format(price);


                viewHolder.nummberitemSelected.setText(String.valueOf(numb));
                if(priceStr.length()==5){
                    viewHolder.totalPrice.setTextSize(17f);
                    viewHolder.totalPrice.setText(priceStr+" €");
                }else if(priceStr.length()>5){
                    viewHolder.totalPrice.setTextSize(14f);
                    viewHolder.totalPrice.setText(priceStr+ " €");
                }else {
                    viewHolder.totalPrice.setText(priceStr+" €");
                }




                viewHolder.buttonisbought.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(shoppingItem.isItemIsBought()){
                            shoppingItem.setItemIsBought(false);

                            shoopinglistItem.child(String.valueOf(i)).setValue(new ShoppingItem().getitemForFirebase(shoppingItem));
                            onShoppingItemBoughts();
                        }else {
                            shoppingItem.setItemIsBought(true);

                            shoopinglistItem.child(String.valueOf(i)).setValue(new ShoppingItem().getitemForFirebase(shoppingItem));

                            onShoppingItemBoughts();
                        }

                    }
                });

                if (shoppingItem.isItemIsBought()) {
                    viewHolder.buttonisbought.setImageResource(R.drawable.checked);
                    onShoppingItemBoughts();
                }else{
                    viewHolder.buttonisbought.setImageResource(R.drawable.unchecked);
                    onShoppingItemBoughts();
                }

                if(itemsDB.size()!=0){
                    boolean replaced=false;
                    for(int h=0; h<itemsDB.size();h++){
                        if(itemsDB.get(h).getUnique_item_id().equals(shoppingItem.getUnique_item_id())){
                            itemsDB.set(h,shoppingItem);
                            replaced=true;
                        }
                    }
                    if(!replaced){
                        itemsDB.add(shoppingItem);
                    }
                }else if(itemsDB.size()==0){
                    itemsDB.add(shoppingItem);
                }

            }

        };
        return adapter;
    }

    public void onShoppingItemBoughts(){

        double totalspent=0;
        for(int i =0;i<itemsDB.size();i++){
            if(itemsDB.get(i).isItemIsBought()){
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
/**
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
        **/
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
    public void onShoppingItemBought(ShoppingItem item, boolean[] positions,int position) {

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
        Firebase shoopinglistItem=firebaseReference.child(shoppingListid)
                .child(Config.FIREBASE_APP_URL_SHARED_GROCERY_ITEMS_NODE);
        shoopinglistItem.setValue(new GroceryList().getGrocerylistitemForGLFirebase(itemsDB));
       // shoopinglistItem.child(String.valueOf(position)).setValue(new ShoppingItem().getitemForFirebase(item));

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
        sort(spinner.getSelectedItem().toString());


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


    class LoadItemFireDBAsyncTask extends AsyncTask<Void ,Void, FirebaseListAdapter> {

        private FragmentProgressBarLoading progressDialog;
        private DatabaseReference firebase;

        public LoadItemFireDBAsyncTask(DatabaseReference firebase) {
            this.firebase = firebase;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //start progressBar
            progressDialog = new FragmentProgressBarLoading();
            progressDialog.setCancelable(false);
            progressDialog.show(getSupportFragmentManager(), "task_progress_firebase");
        }

        @Override
        protected FirebaseListAdapter doInBackground(Void... params) {
            return initAdapter(firebase);
        }

        @Override
        protected void onPostExecute(FirebaseListAdapter adapter) {
            //end progressBar
            progressDialog.dismiss(getSupportFragmentManager());
            shoppinglistview.setAdapter(adapter);

        }
    }

    static class holder {
        public TextView itemName;
        public TextView itemPrice;
        public TextView itemCategory;
        public TextView itemMArket;
        public TextView nummberitemSelected;
        public TextView totalPrice;
        public ImageButton buttonisbought;

    }
    /**
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
     **/
}
