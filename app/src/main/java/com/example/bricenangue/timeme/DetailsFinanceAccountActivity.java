package com.example.bricenangue.timeme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;



public class DetailsFinanceAccountActivity extends AppCompatActivity implements View.OnFocusChangeListener, SearchView.OnQueryTextListener, AdapterView.OnItemClickListener, SearchView.OnCloseListener {

    private Button button;
    private AdaterAdddNewEventListButton adapter;
    private ListView listViewAccountdetails;
    private ArrayList<FinanceRecords> financeRecordses;
    private String accountName;
    private TextView accountNameTextView,accountBalanceTextView;
    private FinanceAccount financeAccount;
    private SQLFinanceAccount sqlFinanceAccount;
    private SearchView searchViewfinanceDetails;

    private android.support.v7.app.AlertDialog alertDialog;
    private Menu menu;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_finance_account);

        alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();
        accountNameTextView=(TextView)findViewById(R.id.textView_grocery_list_item_title_activity_details_shopping_detail_fiannce_account);
        accountBalanceTextView=(TextView)findViewById(R.id.finance_fragment_balance_amount_detail_fiannce_account);
        listViewAccountdetails=(ListView) findViewById(R.id.listView_activity_details_shoppping_list_detail_fiannce_account);
        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            if(extras.containsKey("Account")){
                financeAccount=extras.getParcelable("Account");
                assert financeAccount != null;
                accountName=financeAccount.getAccountName();
                financeRecordses=financeAccount.getRecords();
            }
        }

        populateListViewFinanceRecords();


    }

    private void populateListViewFinanceRecords() {
        String nameAcc=getString(R.string.View_account_accountName) + " " + accountName;
        accountNameTextView.setText(nameAcc);
        accountBalanceTextView.setText(financeAccount.getAccountBlanceTostring()+ " €");
        DetailsFinanceAccountAdapter accountAdapter=new DetailsFinanceAccountAdapter(this,financeRecordses);
        listViewAccountdetails.setAdapter(accountAdapter);
        listViewAccountdetails.setOnItemClickListener(this);
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
        }else if (id == R.id.action_account_details_refresh){
            //refresh
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onClose() {
        removeSearchview();
        return false;
    }
}
