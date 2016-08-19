package com.app.bricenangue.timeme;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCategoryFinance extends Fragment implements View.OnClickListener {


    private MySQLiteHelper mySQLiteHelper;
    private SQLFinanceAccount sqlFinanceAccount;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "RecyclerViewActivity";
    private ArrayList<FinanceAccount> accounts=new ArrayList<>();

    private Button buttonCreateAccount,buttonAddFinanceRecords;
    private TextView textViewBalance;
    private UserLocalStore userLocalStore;


    private RecyclerViewAdapterCreateAccount.MyRecyclerAdaptaterCreateAccountClickListener myClickListener;
    private boolean isShown=false;
    private Fragment fragment=this;
    private ArrayList<FinanceAccount> newItems = new ArrayList<>();
    private ArrayList<FinanceAccount> collectionArrayList = new ArrayList<>();

    private void prepareRecyclerView(Context context,ArrayList<FinanceAccount> arrayList){

        mAdapter = new RecyclerViewAdapterCreateAccount(((NewCalendarActivty)getActivity()),arrayList,myClickListener);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void prepareRecyclerView(ArrayList<FinanceAccount> arrayList){

        mAdapter = new RecyclerViewAdapterCreateAccount(((NewCalendarActivty)getActivity()),arrayList,myClickListener);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

    }


    public FragmentCategoryFinance() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        //getEvents(mySQLiteHelper.getAllIncomingNotification());
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_fragment_category_business, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        textViewBalance=(TextView)rootView.findViewById(R.id.finance_fragment_balance_amount);
        buttonCreateAccount=(Button)rootView.findViewById(R.id.fragment_finance_add_account_button) ;
        buttonCreateAccount.setOnClickListener(this);
        buttonAddFinanceRecords=(Button)rootView.findViewById(R.id.fragment_finance_add_finance_record_button) ;
        buttonAddFinanceRecords.setOnClickListener(this);
        myClickListener=new RecyclerViewAdapterCreateAccount.MyRecyclerAdaptaterCreateAccountClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                //open account details
                startActivity(new Intent(getActivity(), DetailsFinanceAccountActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra("Account",accounts.get(position)));
                accounts.get(position).setLastchangeToAccount();
            }

            @Override
            public void onLongClick(int position, View v) {
                //show popup menu delete

                showFilterPopup(v, accounts.get(position),position);

            }
        };



        return rootView;
    }


    private void showFilterPopup(View v, final FinanceAccount account, final int position) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        // Inflate the menu from xml
        popup.getMenuInflater().inflate(R.menu.finance_account_menu, popup.getMenu());
        // Setup menu item selection
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_delete_Account:
                        deleteFinanceAccount(account,position);
                        mAdapter.notifyDataSetChanged();
                        return true;
                    default:
                        return false;
                }
            }
        });
        // Handle dismissal with: popup.setOnDismissListener(...);
        // Show the menu
        popup.show();
    }

    private void deleteFinanceAccount(final FinanceAccount account, final int position) {
        ServerRequests serverRequests= new ServerRequests((AppCompatActivity)getContext());
        serverRequests.deleteFinanceAccountInBackgroung(account, new FinanceAccountCallbacks() {
            @Override
            public void fetchDone(ArrayList<FinanceAccount> returnedAccounts) {

            }

            @Override
            public void setServerResponse(String serverResponse) {

                if(serverResponse.contains("Account successfully deleted")){

                    deleteFinanceAccountLocally(account,position);
                }else {
                    Toast.makeText(getActivity(), "Error removing account "+ account.getAccountName() +" on server", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void deleteFinanceAccountLocally(FinanceAccount account,int position) {
        int i =sqlFinanceAccount.deleteFinanceAccount(account.getAccountUniqueId());
        if(i!=0){
            ((RecyclerViewAdapterCreateAccount)mAdapter).deleteItem(position);
            onResume();
            Toast.makeText(getActivity(), "account " + account.getAccountName() + " deleted", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getActivity(), "Error removing account "+ account.getAccountName() +" locally", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mySQLiteHelper=new MySQLiteHelper(getContext());
        sqlFinanceAccount=new SQLFinanceAccount(getContext());

        mRecyclerView.setHasFixedSize(true);
        userLocalStore=new UserLocalStore(getContext());
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerViewAdapterCreateAccount(((NewCalendarActivty)getActivity()),collectionArrayList,myClickListener);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null) {
            isShown = true;
            // fetchdata() contains logic to show data when page is selected mostly asynctask to fill the data
        } else {
            isShown = false;

        }
    }

    @Override
    public void onResume() {
        super.onResume();



            accounts=sqlFinanceAccount.getAllFinanceAccount();
                prepareRecyclerView(getContext(),accounts);
            if(accounts.size()==0){
                buttonAddFinanceRecords.setVisibility(View.GONE);
                buttonCreateAccount.setVisibility(View.VISIBLE);
            }else if (accounts.size()!=0 && accounts.size()<4){
                buttonCreateAccount.setVisibility(View.VISIBLE);
                buttonAddFinanceRecords.setVisibility(View.VISIBLE);
            }else if(accounts.size()>=4){
                buttonCreateAccount.setVisibility(View.GONE);
                buttonAddFinanceRecords.setVisibility(View.VISIBLE);
            }
           setBalance();



        ((RecyclerViewAdapterCreateAccount) mAdapter).setOnCreateAccountlistClickListener(myClickListener);




    }

    private void refresh(){
        accounts=sqlFinanceAccount.getAllFinanceAccount();
        prepareRecyclerView(getContext(),accounts);
        if(accounts.size()==0){
            buttonAddFinanceRecords.setVisibility(View.GONE);
            buttonCreateAccount.setVisibility(View.VISIBLE);
        }else if (accounts.size()!=0 && accounts.size()<4){
            buttonCreateAccount.setVisibility(View.VISIBLE);
            buttonAddFinanceRecords.setVisibility(View.VISIBLE);
        }else if(accounts.size()>=4){
            buttonCreateAccount.setVisibility(View.GONE);
            buttonAddFinanceRecords.setVisibility(View.VISIBLE);
        }
        setBalance();
    }

    void setBalance(){
        if(accounts!=null){
            double balce=0;
            for (int i=0; i<accounts.size();i++){
                balce=balce+accounts.get(i).getAccountBalance();
            }
            DecimalFormat df = new DecimalFormat("0.00");
            df.setMaximumFractionDigits(2);

            String priceStr = df.format(balce);
            if(priceStr.contains("-")){
                textViewBalance.setText(priceStr+" €");
                userLocalStore.setUserAccountBalance(priceStr);
                textViewBalance.setTextColor(getResources().getColor(R.color.warning_color));
            }else {
                textViewBalance.setText(priceStr+" €");
                userLocalStore.setUserAccountBalance(priceStr);

            }


        }
    }
    private void getAccount(){
        sqlFinanceAccount.reInitializeFinanceSqliteTable();
        ServerRequests serverRequests=new ServerRequests((AppCompatActivity) getActivity());
        serverRequests.getFinanceAccountsAndUserInBackgroung(userLocalStore.getUserfullname(),new FinanceAccountCallbacks() {
            @Override
            public void fetchDone(ArrayList<FinanceAccount> returnedAccounts) {
                if(returnedAccounts.size()!=0){
                    accounts=returnedAccounts;
                    saveAccountLocally(returnedAccounts);
                }
            }

            @Override
            public void setServerResponse(String serverResponse) {

            }
        });

    }
    private void saveAccountLocally(ArrayList<FinanceAccount> accounts) {
        for(int i=0;i<accounts.size();i++){
            sqlFinanceAccount.addFINANCEACCOUNT(accounts.get(i));

        }
    }
    @Override
    public void onPause() {
        super.onPause();
        ((RecyclerViewAdapterCreateAccount)mAdapter).setOnCreateAccountlistClickListener(null);
    }


    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.fragment_finance_add_account_button){
            //start create Account Activity
            startActivity(new Intent(getActivity(),CreateFinanceAccountActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }else if( view.getId()==R.id.fragment_finance_add_finance_record_button){
            //start activity add finance record

                startActivity(new Intent(getActivity(),CreateFinanceRecordsActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra("AccountToUpdate",accounts));


        }
    }
}
