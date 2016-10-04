package com.app.bricenangue.timeme;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


public class
DetailsFinanceAccountActivity extends AppCompatActivity implements View.OnFocusChangeListener, SearchView.OnQueryTextListener, AdapterView.OnItemClickListener, SearchView.OnCloseListener {

    private Button button;
    private ListView listViewAccountdetails;
    private ArrayList<FinanceRecords> financeRecordses;
    private String accountName;
    private TextView accountNameTextView,accountBalanceTextView;
    private FinanceAccount financeAccount;
    private SQLFinanceAccount sqlFinanceAccount;
    private SearchView searchViewfinanceDetails;

    private android.support.v7.app.AlertDialog alertDialog;
    private ArrayList<FinanceRecords> productResults = new ArrayList<FinanceRecords>();
    //Based on the search string, only filtered products will be moved here from productResults
    private ArrayList<FinanceRecords> filteredProductResults = new ArrayList<FinanceRecords>();
    private Menu menu;
    private String financeAccountid;
    private DatabaseReference databaseReference;
    private  DetailsFinanceAccountAdapter accountAdapter;
    private FirebaseAuth auth;
    private UserLocalStore userLocalStore;
    private boolean sharedAccount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_finance_account);

        auth=FirebaseAuth.getInstance();
        userLocalStore=new UserLocalStore(this);
        sqlFinanceAccount=new SQLFinanceAccount(this);
        financeAccount=new FinanceAccount(this);
        databaseReference= FirebaseDatabase.getInstance().getReference().child(Config.FIREBASE_APP_URL_FINANCE_ACCOUNTS);
        alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();
        accountNameTextView=(TextView)findViewById(R.id.textView_grocery_list_item_title_activity_details_shopping_detail_fiannce_account);
        accountBalanceTextView=(TextView)findViewById(R.id.finance_fragment_balance_amount_detail_fiannce_account);
        listViewAccountdetails=(ListView) findViewById(R.id.listView_activity_details_shoppping_list_detail_fiannce_account);
        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            if(extras.containsKey("Accountid")){
                financeAccountid=extras.getString("Accountid");
                sharedAccount=extras.getBoolean("sharedAccount");
            }
        }

       // populateListViewFinanceRecords();

    }

    private void fetchshared(){
        assert auth.getCurrentUser()!=null;
        DatabaseReference ref=databaseReference.child(Config.FIREBASE_APP_URL_FINANCE_ACCOUNTS_SHARED)
                .child(userLocalStore.getChatRoom());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(financeAccountid)){
                    FinanceAccountForFireBase financeAccountForFireBase=dataSnapshot.child(financeAccountid)
                            .getValue(FinanceAccountForFireBase.class);
                    financeAccount= new FinanceAccount(getApplicationContext())
                            .getFinanceAccountFromFirebase(financeAccountForFireBase);
                    financeRecordses=financeAccount.getAccountsRecord();
                    accountName=financeAccount.getAccountName();
                    populateListViewFinanceRecords();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void fetchprivate(){
        assert auth.getCurrentUser()!=null;
        DatabaseReference ref=databaseReference.child(auth.getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(financeAccountid)){
                    FinanceAccountForFireBase financeAccountForFireBase=dataSnapshot.child(financeAccountid)
                            .getValue(FinanceAccountForFireBase.class);
                    financeAccount= new FinanceAccount(getApplicationContext())
                            .getFinanceAccountFromFirebase(financeAccountForFireBase);
                    financeRecordses=financeAccount.getAccountsRecord();
                    accountName=financeAccount.getAccountName();
                    populateListViewFinanceRecords();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
      if(sharedAccount){
          fetchshared();
      }else {
          fetchprivate();
      }


    }


    private void populateListViewFinanceRecords() {
        String nameAcc=getString(R.string.View_account_accountName) + " " + accountName;
        accountNameTextView.setText(nameAcc);

        assert financeAccount != null;
        financeAccount.getAccountrecordsAmountUpdateBalance(getApplicationContext());
        String balcance=financeAccount.getAccountBlanceTostring()+ " €";
        accountBalanceTextView.setText(balcance);
        if(balcance.contains("-")){
            accountBalanceTextView.setText(balcance);
            accountBalanceTextView.setTextColor(getResources().getColor(R.color.warning_color));
        }else {
            accountBalanceTextView.setText(balcance);

        }

       accountAdapter=new DetailsFinanceAccountAdapter(this,financeRecordses);
        listViewAccountdetails.setAdapter(accountAdapter);
        listViewAccountdetails.setOnItemClickListener(this);

        sort();
    }

    private void callSearchView(){
        searchViewfinanceDetails=(SearchView)findViewById(R.id.actionbarsearch_add_item_to_list_detail_fiannce_account);
        searchViewfinanceDetails.setVisibility(View.VISIBLE);
        searchViewfinanceDetails.setFocusable(true);
        searchViewfinanceDetails.setIconified(false);
        accountNameTextView.setVisibility(View.GONE);
        searchViewfinanceDetails.setQueryHint("Start typing to search...");

        if(searchViewfinanceDetails.hasFocus()){
            searchViewfinanceDetails.setBackgroundColor(getResources().getColor(R.color.white));
        }
        searchViewfinanceDetails.setOnQueryTextFocusChangeListener(this);
        searchViewfinanceDetails.setOnQueryTextListener(this);
        searchViewfinanceDetails.setOnCloseListener(this);

    }

    @Override
    public void onFocusChange(View view, boolean b) {
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if(newText.length()>1){
            //search
            new SearchDetailsAsyncTask().execute(newText);
        }else {
            populateListViewFinanceRecords();
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //open details records activity
        FinanceRecords financeRecords=financeRecordses.get(i);
        startActivity(new Intent(DetailsFinanceAccountActivity.this,ViewFinanceRecordsDetailsActivity.class).putExtra("financeRecord",financeRecords)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu=menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_details_account_menu, menu);

        return true;
    }


   void  removeSearchview(){
       searchViewfinanceDetails.setVisibility(View.GONE);
       accountNameTextView.setVisibility(View.VISIBLE);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search_account_details) {
        callSearchView();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onClose() {
        removeSearchview();
        return false;
    }






    //in this myAsyncTask, we are fetching data from server for the search string entered by user.
    class SearchDetailsAsyncTask extends AsyncTask<String, Void, String> {
        ArrayList<FinanceRecords>  productList;
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

            FinanceRecords object;
            String matchFound = "N";


            try {

                productList=financeRecordses;
                //parse date for dateList
                for (int i = 0; i < productList.size(); i++) {

                    object=productList.get(i);


                    //check if this product is already there in productResults, if yes, then don't add it again.
                    matchFound = "N";

                    for (int j = 0; j < productResults.size(); j++) {

                        if (productResults.get(j).getRecordNAme().equals(object.getRecordNAme())) {
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
                Toast.makeText(getApplicationContext(), "Unable to connect to database,please try later", Toast.LENGTH_LONG).show();

            } else {
                //calling this method to filter the search results from productResults and move them to
                //filteredProductResults
                filterProductArray(textSearch);

                prepareListviewall(filteredProductResults);

            }
        }


    }

    public void sort(){
        Collections.sort(financeRecordses, new ComparatorSortFinanceDetailsBookingDate());
    }
    void prepareListviewall(ArrayList<FinanceRecords> financeRecordsArrayList){


        DetailsFinanceAccountAdapter accountAdapter=new DetailsFinanceAccountAdapter(this,financeRecordsArrayList);
        listViewAccountdetails.setAdapter(accountAdapter);
        listViewAccountdetails.setOnItemClickListener(this);

    }

    public void filterProductArray(String newText) {

        String pName;
        String pDate;

        filteredProductResults.clear();
        for (int i = 0; i < productResults.size(); i++) {
            pName = productResults.get(i).getRecordNAme().toLowerCase();
            pDate=productResults.get(i).getRecordBookingDate();

            if (pDate.contains(newText) || pName.contains(newText.toLowerCase())) {
                filteredProductResults.add(productResults.get(i));

            }
        }

    }


}
