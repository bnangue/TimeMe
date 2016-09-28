package com.app.bricenangue.timeme;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.ss.formula.functions.T;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


public class FragmentOverview extends Fragment{



    private Spinner spinnerAccounts;
    private String [] nameAccArray;
    private String [] idAccArray;


    private ArrayList<FinanceAccount> financeAccountsArrayList = new ArrayList<>();


    private AlertDialog alertDialog;

    private ListView listViewShopping, listViewFinance;
    private ImageButton buttonOpenChart;
    private boolean isShown;
    private int position;
    private FirebaseAuth auth;
    private DatabaseReference databaseReferenceList;
    private DatabaseReference databaseReferenceAcc;


    public FragmentOverview() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        databaseReferenceList= FirebaseDatabase.getInstance().getReference().child(Config.FIREBASE_APP_URL_GROCERYLISTS);
        databaseReferenceAcc= FirebaseDatabase.getInstance().getReference().child(Config.FIREBASE_APP_URL_FINANCE_ACCOUNTS);

        auth=FirebaseAuth.getInstance();

        View rootView = inflater.inflate(R.layout.fragment_fragment_overview_layout, container, false);
        listViewShopping=(ListView)rootView.findViewById(R.id.listView_overview_fragment_shopping_list);
        listViewFinance=(ListView)rootView.findViewById(R.id.listView_overview_fragment_finance_records);

        spinnerAccounts=(Spinner)rootView.findViewById(R.id.spinner_overview_fragment_which_account);
        buttonOpenChart=(ImageButton)rootView.findViewById(R.id.button_overview_fragment_show_finance_graphic);
        buttonOpenChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(financeAccountsArrayList!=null && financeAccountsArrayList.size()!=0){
                    startActivity(new Intent(getActivity(),AllChartsActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .putExtra("financeAccount",financeAccountsArrayList.get(position)));
                }

            }
        });



        return rootView;

    }
    private void populateSpinner(){
        if(nameAccArray!=null){
            SpinnerAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.spinnerlayout, nameAccArray);
            spinnerAccounts.setAdapter(adapter);
            spinnerAccounts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                        setAdapterAcc(financeAccountsArrayList.get(i).getAccountUniqueId());
                    position=i;

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    setAdapterAcc(financeAccountsArrayList.get(0).getAccountUniqueId());
                    position=0;
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        assert auth.getCurrentUser()!=null;

        DatabaseReference refList=databaseReferenceList.child(auth.getCurrentUser().getUid());
        DatabaseReference refAcc=databaseReferenceAcc.child(auth.getCurrentUser().getUid());
        refAcc.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                financeAccountsArrayList.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    financeAccountsArrayList.add(new FinanceAccount(getContext())
                    .getFinanceAccountFromFirebase(child.getValue(FinanceAccountForFireBase.class)));
                }
                if(financeAccountsArrayList!=null &&financeAccountsArrayList.size()!=0){
                    nameAccArray=new String[financeAccountsArrayList.size()];
                    idAccArray=new String[financeAccountsArrayList.size()];
                    for(int i=0;i<financeAccountsArrayList.size();i++){
                        nameAccArray[i]= getActivity().getString(R.string.View_account_accountName)+"  "+financeAccountsArrayList.get(i).getAccountName();
                        idAccArray[i]=financeAccountsArrayList.get(i).getAccountUniqueId();
                    }
                }

                populateSpinner();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseListAdapter<GroceryListForFireBase> adapterList=new FirebaseListAdapter<GroceryListForFireBase>(
                getActivity(),
                GroceryListForFireBase.class,
                R.layout.custom_overview_shopping,
                refList
        ) {
            @Override
            protected void populateView(View v, final GroceryListForFireBase model, final int position) {

                TextView sortName = (TextView) v.findViewById(R.id.textView_sort_options_items_overview_shopping);
                TextView balance=(TextView)v.findViewById(R.id.textView_sort_options_items_amount_overview_shopping);
                TextView status=(TextView)v.findViewById(R.id.textView_sort_options_items_status_overview_shopping);

                String name=getContext().getResources().getString(R.string.grocery_list_item_title_text ).toLowerCase()
                        + " " +model.getDatum();
                sortName.setText(name);
                String balancestr=new GroceryList().getGrocerylistFromGLFirebase(model).getGroceryListTotalPriceToPayString()+" €";

                balance.setText(balancestr);
                balance.setTextColor(getResources().getColor(R.color.warning_color));
                if(!model.isListdone()){
                    status.setText(getString(R.string.grocery_list_status__not_done_text));
                }
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(getActivity(),DetailsShoppingListActivity.class)
                                .putExtra("GroceryListId",getItem(position).getList_unique_id())
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                });
            }
        };

        listViewShopping.setAdapter(adapterList);




    }

    private void setAdapterAcc(String Accid){
        DatabaseReference refAcc=databaseReferenceAcc.child(auth.getCurrentUser().getUid())
                .child(Accid).child(Config.FIREBASE_APP_URL_FINANCE_ACCOUNTS_RECORDS);

        FirebaseListAdapter<FinanceRecordsForFireBase> adapterAcc=new FirebaseListAdapter<FinanceRecordsForFireBase>(
                getActivity(),
                FinanceRecordsForFireBase.class,
                R.layout.custom_overview_finance,
                refAcc
        ) {
            @Override
            protected void populateView(View v, final FinanceRecordsForFireBase model, int position) {

                TextView sortName = (TextView) v.findViewById(R.id.textView_sort_options_items_overview_finance);
                TextView balance=(TextView)v.findViewById(R.id.textView_sort_options_items_amount_overview_finance);

                sortName.setText(model.getRecordNAme());
                String balancestr=model.getRecordAmount()+" €";
                if(!model.isIncome()){
                    balancestr="-"+balancestr;
                    balance.setText(balancestr);
                    balance.setTextColor( getContext().getResources().getColor(R.color.warning_color));
                }else {
                    balance.setText(balancestr);
                    balance.setTextColor(getContext().getResources().getColor(R.color.color_account_balance_positive));
                }
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(getActivity(),ViewFinanceRecordsDetailsActivity.class)
                                .putExtra("financeRecord",new FinanceRecords(getContext()).getRecordsFromFirebase(model))
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                });
            }

        };
        listViewFinance.setAdapter(adapterAcc);
    }


    private void startGroceryListOverview(GroceryList item) {
        startActivity(new Intent(getActivity(),DetailsShoppingListActivity.class)
                .putExtra("GroceryListId",item.getList_unique_id())
                .putExtra("GroceryListIsShared",item.isToListshare()).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));


    }

    private void startFinanceRecordOverview(ArrayList<FinanceRecords> financeRecordses,int position) {
        FinanceRecords financeRecords=financeRecordses.get(position);
        startActivity(new Intent(getActivity(),ViewFinanceRecordsDetailsActivity.class)
                .putExtra("financeRecord",financeRecords)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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

    }

    @Override
    public void onPause() {
        super.onPause();

    }



}


