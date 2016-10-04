package com.app.bricenangue.timeme;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
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
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.auth.api.signin.internal.SignInHubActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
import java.util.Locale;

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
    String [] userAccArray={"most used","price ascending","price descending","selected first","selected last"};

    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;

    private DatabaseReference firebaseReference;
    private String shoppingListid;
    private FirebaseAuth auth;
    private ValueEventListener valueEventListener;
    private ProgressDialog progressBar;
    private boolean sharedList;
    private UserLocalStore userLocalStore;
    private android.support.v7.app.AlertDialog alertDialog;

    private DatabaseReference databaseReference;
    private android.app.AlertDialog.Builder alert;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_shopping_list);

        auth=FirebaseAuth.getInstance();
        firebaseReference= FirebaseDatabase.getInstance().getReference().child(Config.FIREBASE_APP_URL_GROCERYLISTS);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseReference = FirebaseDatabase.getInstance().getReference().child(Config.FIREBASE_APP_URL_GROCERYLISTS);

        alert = new android.app.AlertDialog.Builder(this);
        userLocalStore=new UserLocalStore(this);
        coordinatorLayout=(CoordinatorLayout)findViewById(R.id.coordinateLayout_activity_details_shoppping_list);
        shoppinglistview=(ListView)findViewById(R.id.listView_activity_details_shoppping_list);
        textViewAlreadySpent=(TextView)findViewById(R.id.grocery_fragment_balance_amount_activity_details_shopping_list);
        textViewgroceryListName=(TextView)findViewById(R.id.textView_grocery_list_item_title_activity_details_shopping);

        spinner=(Spinner)findViewById(R.id.spinner_activity_details_shopping_list);
        SpinnerAdapter adap = new ArrayAdapter<>(this, R.layout.spinnerlayout, userAccArray);
        spinner.setAdapter(adap);

        spinner.setSelection(0);

        Bundle extras=getIntent().getExtras();
        if(extras!=null){

                shoppingListid=extras.getString("GroceryListId");
            sharedList=extras.getBoolean("GroceryListIsShared");

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

        if(savedInstanceState!=null){
            shoppingListid=savedInstanceState.getString("shoppingListid");

        }


    }

    private void fetchList(){
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);
        progressBar.setTitle("Loading items");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();
        assert  auth.getCurrentUser()!=null;
        final DatabaseReference shoopinglistItem;

        if(sharedList &&userLocalStore.getChatRoom().length()>2){
            shoopinglistItem=FirebaseDatabase
                    .getInstance().getReference().
                            child(Config.FIREBASE_APP_URL_GROCERYLISTS).child(Config.FIREBASE_APP_URL_GROCERYLISTS_SHARED).child(userLocalStore.getChatRoom());
        }else {
            shoopinglistItem=FirebaseDatabase
                    .getInstance().getReference().
                            child(Config.FIREBASE_APP_URL_GROCERYLISTS).child(auth.getCurrentUser().getUid());
        }


        shoopinglistItem.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(shoppingListid)){
                    GroceryListForFireBase groceryListForFireBase=dataSnapshot.child(shoppingListid).getValue(GroceryListForFireBase.class);
                    GroceryList groceryList1=new GroceryList().getGrocerylistFromGLFirebase(groceryListForFireBase);
                    groceryList=groceryList1;
                    itemsDB=groceryList1.getItemsOftheList();
                    String groceryListName= getResources().getString(R.string.grocery_list_item_title_text )+ " " + groceryList.getDatum();
                    textViewgroceryListName.setText(groceryListName);
                    populateListview();
                    //shoopinglistItem.removeEventListener(this);
                    if(progressBar!=null){
                        progressBar.dismiss();
                    }

                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if(progressBar!=null){
                    progressBar.dismiss();
                }
            }
        });
        /**
         * final Firebase shoopinglistItem=firebaseReference.child(shoppingListid)
         .child(Config.FIREBASE_APP_URL_SHARED_GROCERY_ITEMS_NODE);
         **/
        if(itemsDB.size()!=0){
            populateListview();

        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        fetchList();

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
                    .putExtra("shareList",groceryList.isToListshare())
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("shoppingListid",shoppingListid);
        outState.putBoolean("sharedList",sharedList);

    }

    @Override
    public void onShoppingItemBought(ShoppingItem item, boolean[] positions,int position) {

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
        assert  auth.getCurrentUser()!=null;
        DatabaseReference shoopinglistItem;

        if(sharedList && userLocalStore.getChatRoom().length()>2){
            shoopinglistItem=FirebaseDatabase
                    .getInstance().getReference().
                            child(Config.FIREBASE_APP_URL_GROCERYLISTS).child(Config.FIREBASE_APP_URL_GROCERYLISTS_SHARED).child(userLocalStore.getChatRoom())
                    .child(shoppingListid)
                    .child(Config.FIREBASE_APP_URL_SHARED_GROCERY_ITEMS_NODE);
        }else {
            shoopinglistItem=FirebaseDatabase
                    .getInstance().getReference().
                            child(Config.FIREBASE_APP_URL_GROCERYLISTS).child(auth.getCurrentUser().getUid())
                    .child(shoppingListid)
                    .child(Config.FIREBASE_APP_URL_SHARED_GROCERY_ITEMS_NODE);
        }

        shoopinglistItem.setValue(new GroceryList().getGrocerylistitemForGLFirebase(itemsDB));
        if (!new ServerRequests(this).haveNetworkConnection()) {
            showSnackBarNoInternet();

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
        if(!spinner.getSelectedItem().toString().equals("most used")){
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
            fragment.show(getSupportFragmentManager(), "SHOPPING LIST IS COMPLETE");
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

     void populateListview(){

         boolean isAllBought=groceryList.isListdone();

         if(isAllBought){
             textViewgroceryListName.setEnabled(false);
             textViewgroceryListName.setClickable(false);
         }
        listViewAdapter=new ListAdapterCreateShopList(this,itemsDB,this,isAllBought);

        itemsBought=new boolean[itemsDB.size()];

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
        assert auth.getCurrentUser()!=null;
        DatabaseReference refdate;

        if(sharedList && userLocalStore.getChatRoom().length()>2){
            refdate=FirebaseDatabase
                    .getInstance().getReference().
                            child(Config.FIREBASE_APP_URL_GROCERYLISTS).child(Config.FIREBASE_APP_URL_GROCERYLISTS_SHARED).child(userLocalStore.getChatRoom())
                    .child(shoppingListid)
                    .child("datum");
        }else {
            refdate=FirebaseDatabase
                    .getInstance().getReference().
                            child(Config.FIREBASE_APP_URL_GROCERYLISTS).child(auth.getCurrentUser().getUid())
                    .child(shoppingListid)
                    .child("datum");
        }

        refdate.setValue(date);
        if (!new ServerRequests(this).haveNetworkConnection()) {
            showSnackBarNoInternet();

        }
        groceryList.setDatum(date);
    }

    @Override
    public void onBackPressed() {

            super.onBackPressed();

    }


    private void fetchshared(final DatabaseReference ref, final boolean sharedList,ListView listView){
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.getChildren().iterator().hasNext()) {


                    alert.create();
                    alert.setMessage("Create an account first");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //start create Account Activity
                            startActivity(new Intent(DetailsShoppingListActivity.this,
                                    CreateFinanceAccountActivity.class).putExtra("shareList",sharedList)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        }
                    });
                    alert.setCancelable(true);

                    alert.show();


                } else {

                    if (valueEventListener != null) ref.removeEventListener(valueEventListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ref.addValueEventListener(valueEventListener);

        FirebaseListAdapter<FinanceAccountForFireBase> adapter = new FirebaseListAdapter<FinanceAccountForFireBase>(
                DetailsShoppingListActivity.this,
                FinanceAccountForFireBase.class,
                R.layout.custom_account_to_choose,
                ref

        ) {
            @Override
            protected void populateView(View v, final FinanceAccountForFireBase model, final int position) {
                final FinanceAccount financeAccount = new FinanceAccount(getApplicationContext())
                        .getFinanceAccountFromFirebase(model);


                TextView sortName = (TextView) v.findViewById(R.id.textView_sort_options_items);
                TextView balance = (TextView) v.findViewById(R.id.textView_sort_options_items_amount);

                final RadioButton buttonisChecked = (RadioButton) v.findViewById(R.id.radioButton_sort_options_items);

                sortName.setText(model.getAccountName());

                financeAccount.getAccountrecordsAmountUpdateBalance(getApplicationContext());
                String balancestr = financeAccount.getAccountBlanceTostring() + " €";
                if (balancestr.contains("-")) {
                    balance.setText(balancestr);
                    balance.setTextColor(getResources().getColor(R.color.warning_color));
                } else {
                    balance.setText(balancestr);
                    balance.setTextColor(getResources().getColor(R.color.color_account_balance_positive));
                }


                buttonisChecked.setChecked(false);

                buttonisChecked.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        buttonisChecked.setChecked(true);

                        createAndSaveGrocerylist(sharedList
                                ,new FinanceAccount(getApplicationContext()).getFinanceAccountFromFirebase(getItem(position)));
                    }
                });

            }
        };

        listView.setAdapter(adapter);

        // listView.setAdapter(chooseAccountAdapter);

        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                alertDialog.dismiss();
                FinanceAccount financeAccount = new FinanceAccount(getApplicationContext())
                        .getFinanceAccountFromFirebase((FinanceAccountForFireBase) adapterView.getAdapter().getItem(i));

                createAndSaveGrocerylist(sharedList,financeAccount);


            }
        });

    }
    private void fetchprivate(final DatabaseReference ref, final boolean sharedList,ListView listView){
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.getChildren().iterator().hasNext()) {


                    alert.create();
                    alert.setMessage("Create an account first");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //start create Account Activity
                            startActivity(new Intent(DetailsShoppingListActivity.this,
                                    CreateFinanceAccountActivity.class).putExtra("shareList",sharedList)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        }
                    });
                    alert.setCancelable(true);
                    alert.show();


                } else {

                    if (valueEventListener != null) ref.removeEventListener(valueEventListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ref.addValueEventListener(valueEventListener);

        FirebaseListAdapter<FinanceAccountForFireBase> adapter = new FirebaseListAdapter<FinanceAccountForFireBase>(
                DetailsShoppingListActivity.this,
                FinanceAccountForFireBase.class,
                R.layout.custom_account_to_choose,
                ref

        ) {
            @Override
            protected void populateView(View v, final FinanceAccountForFireBase model, final int position) {
                final FinanceAccount financeAccount = new FinanceAccount(getApplicationContext())
                        .getFinanceAccountFromFirebase(model);


                TextView sortName = (TextView) v.findViewById(R.id.textView_sort_options_items);
                TextView balance = (TextView) v.findViewById(R.id.textView_sort_options_items_amount);

                final RadioButton buttonisChecked = (RadioButton) v.findViewById(R.id.radioButton_sort_options_items);

                sortName.setText(model.getAccountName());

                financeAccount.getAccountrecordsAmountUpdateBalance(getApplicationContext());
                String balancestr = financeAccount.getAccountBlanceTostring() + " €";
                if (balancestr.contains("-")) {
                    balance.setText(balancestr);
                    balance.setTextColor(getResources().getColor(R.color.warning_color));
                } else {
                    balance.setText(balancestr);
                    balance.setTextColor(getResources().getColor(R.color.color_account_balance_positive));
                }


                buttonisChecked.setChecked(false);

                buttonisChecked.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        buttonisChecked.setChecked(true);

                        alertDialog.dismiss();
                        createAndSaveGrocerylist(sharedList
                                ,new FinanceAccount(getApplicationContext()).getFinanceAccountFromFirebase(getItem(position)));
                    }
                });

            }
        };

        listView.setAdapter(adapter);

        // listView.setAdapter(chooseAccountAdapter);

        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                alertDialog.dismiss();
                FinanceAccount financeAccount = new FinanceAccount(getApplicationContext())
                        .getFinanceAccountFromFirebase((FinanceAccountForFireBase) adapterView.getAdapter().getItem(i));

                createAndSaveGrocerylist(sharedList,financeAccount);


            }
        });

    }
    private void showDialogChoosingAccount() {


        alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.custom_sort_options_choose_account, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle(getString(R.string.activity_choose_account_for_grocery_list));
        final ListView listView = (ListView) convertView.findViewById(R.id.listView_addItmeListActivity_sort_options_account);
        final Switch btnswitch=(Switch)convertView.findViewById(R.id.button_switch_layout_custom_sort);

        assert auth.getCurrentUser() != null;
        DatabaseReference ref;
        if(btnswitch.isChecked()){
            ref=databaseReference.child(Config.FIREBASE_APP_URL_FINANCE_ACCOUNTS)
                    .child(Config.FIREBASE_APP_URL_FINANCE_ACCOUNTS_SHARED)
                    .child(userLocalStore.getChatRoom());
            btnswitch.setText("My shared accounts");
            fetchshared(ref,btnswitch.isChecked(),listView);

        }else{
            ref = databaseReference
                    .child(Config.FIREBASE_APP_URL_FINANCE_ACCOUNTS).child(auth.getCurrentUser().getUid());
            btnswitch.setText("My private accounts");
            fetchprivate(ref,btnswitch.isChecked(),listView);

        }
        btnswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnswitch.isChecked()){
                    btnswitch.setText("My shared accounts");
                    DatabaseReference ref=databaseReference.child(Config.FIREBASE_APP_URL_FINANCE_ACCOUNTS)
                            .child(Config.FIREBASE_APP_URL_FINANCE_ACCOUNTS_SHARED)
                            .child(userLocalStore.getChatRoom());
                    fetchshared(ref,btnswitch.isChecked(),listView);

                }else{
                    btnswitch.setText("My private accounts");
                   DatabaseReference ref = databaseReference
                            .child(Config.FIREBASE_APP_URL_FINANCE_ACCOUNTS).child(auth.getCurrentUser().getUid());
                    fetchprivate(ref,btnswitch.isChecked(),listView);

                }
            }
        });


        alertDialog.setCancelable(true);
        alertDialog.show();
    }


    private void createAndSaveGrocerylist(final boolean sharedList, final FinanceAccount account){

        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);
        progressBar.setTitle("Saving...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();
        assert auth.getCurrentUser()!=null;
        final DatabaseReference finRef;
        final DatabaseReference grRef;



        if(sharedList &&userLocalStore.getChatRoom().length()>2 ){
            finRef=FirebaseDatabase.getInstance().getReference().child(Config.FIREBASE_APP_URL_FINANCE_ACCOUNTS)
                    .child(Config.FIREBASE_APP_URL_FINANCE_ACCOUNTS_SHARED)
                    .child(userLocalStore.getChatRoom())
            ;

        }else {
            finRef=FirebaseDatabase.getInstance().getReference().child(Config.FIREBASE_APP_URL_FINANCE_ACCOUNTS)
                    .child(auth.getCurrentUser().getUid());

        }

        if(groceryList.isToListshare()){
            grRef=firebaseReference
                    .child(Config.FIREBASE_APP_URL_GROCERYLISTS_SHARED).child(userLocalStore.getChatRoom());
        }else {
            grRef=firebaseReference.child(auth.getCurrentUser().getUid()).child(shoppingListid);

        }


        Calendar c = new GregorianCalendar();
        Date dat = c.getTime();
        //String day= String.valueOf(dat.getDay());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMAN);
        SimpleDateFormat formatterr = new SimpleDateFormat("dd-MM-yyy HH:mm:ss");

        Date currentDate = new Date();
        String currentTimer = formatterr.format(currentDate);


        int hashid = (userLocalStore.getUserfullname() + format.format(dat)).hashCode();
        groceryList.setList_unique_id(String.valueOf(hashid));
        FinanceRecords financeRecords = new FinanceRecords(getApplicationContext(),
                getString(R.string.textInitialize_create_account_grocery_note), currentTimer.split(" ")[0],
                getString(R.string.textInitialize_create_account_grocery_note), groceryList.getGroceryListTotalPriceToPayString().replace(",", ".")
                , String.valueOf(hashid),
                getString(R.string.textInitialize_create_account_grocery_category), groceryList.getDatum()
                , userLocalStore.getUserfullname(), 0, true, false);


        account.setContext(getApplicationContext());
        account.addRecordToAccount(financeRecords);
        account.setLastchangeToAccount();
        groceryList.setAccountid(account.getAccountUniqueId());
        groceryList.setAccountisshared(sharedList);

        account.setLastchangeToAccount();
        account.getAccountrecordsAmountUpdateBalance(getApplicationContext());
        final DatabaseReference newRef=finRef.child(account.getAccountUniqueId());
        newRef.setValue(new FinanceAccount(getApplicationContext()).getFinanceAccountForFirebase(account))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            grRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        if (progressBar != null) {
                                            progressBar.dismiss();
                                        }
                                        finish();
                                    }else {
                                        if (progressBar != null) {
                                            progressBar.dismiss();
                                        }
                                        Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else {
                            if (progressBar != null) {
                                progressBar.dismiss();
                            }
                            Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        if (!new ServerRequests(this).haveNetworkConnection()) {
            showSnackBarNoInternet();
            if (progressBar != null) {
                progressBar.dismiss();
            }
        }
        //onBackPressed();
    }

    @Override
    public void changescanceled(boolean canceled) {
        if(!canceled){

        }else {
            showDialogChoosingAccount();
        }


    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    public void showSnackBar(final GroceryList groceryList, final CalendarCollection collection, final FinanceAccount financeAccount){
        snackbar = Snackbar
                .make(coordinatorLayout, "An error occured during the connection to the server", Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //updateGroceryAndFinance(groceryList,financeAccount,collection);
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
                .make(coordinatorLayout, getString(R.string.internet_connection_error_message )+" " +
                        getString(R.string.internet_connection_error_message_change_sycn_later ), Snackbar.LENGTH_INDEFINITE);
        snackbar.setActionTextColor(Color.WHITE);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.colorSnackbar));
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }
}
