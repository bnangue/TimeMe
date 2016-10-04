package com.app.bricenangue.timeme;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCategoryFinance extends Fragment implements View.OnClickListener {


    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "RecyclerViewActivity";
    private ArrayList<FinanceAccount> accounts=new ArrayList<>();

    private Button buttonCreateAccount,buttonAddFinanceRecords;
    private TextView textViewBalance;
    private UserLocalStore userLocalStore;
    private FirebaseAuth auth;
    private DatabaseReference databaseReferenceToAccounts;
    private DatabaseReference databaseReferenceToUsers;


    private boolean isShown=false;
    private Fragment fragment=this;
    private ArrayList<FinanceAccount> collectionArrayList = new ArrayList<>();
    private Firebase firebase;
    private FirebaseRecyclerAdapter<FinanceAccountForFireBase,FinanceAccountViewHolder> adapter;
    private int count;
    private ValueEventListener valueEventListener;
    private ProgressDialog progressBar;
    private Switch btnswitch;


    public FragmentCategoryFinance() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_fragment_category_business, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        btnswitch=(Switch) rootView.findViewById(R.id.button_switch_fragment_finance_add_account);
        textViewBalance=(TextView)rootView.findViewById(R.id.finance_fragment_balance_amount);
        buttonCreateAccount=(Button)rootView.findViewById(R.id.fragment_finance_add_account_button) ;
        buttonCreateAccount.setOnClickListener(this);
        buttonAddFinanceRecords=(Button)rootView.findViewById(R.id.fragment_finance_add_finance_record_button) ;
        buttonAddFinanceRecords.setOnClickListener(this);


        if(btnswitch.isChecked()){
            btnswitch.setText("My shared accounts");

        }else{
            btnswitch.setText("My private accounts");

        }
        btnswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnswitch.isChecked()){
                    btnswitch.setText("My shared accounts");
                    fetchshared();

                }else{
                    btnswitch.setText("My private accounts");
                    fetchprivate();

                }
            }
        });


        return rootView;
    }

//use separte Value listner
    private void showFilterPopup(View v, final String financeAccountid) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        // Inflate the menu from xml
        popup.getMenuInflater().inflate(R.menu.finance_account_menu, popup.getMenu());
        // Setup menu item selection

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_delete_Account:
                        progressBar = new ProgressDialog(getContext());
                        progressBar.setCancelable(false);
                        progressBar.setTitle("Deleting your account and all attached grocery lists");
                        progressBar.setMessage("in progress ...");
                        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressBar.show();
                        assert auth.getCurrentUser()!=null;

                        final DatabaseReference firebaseRef;

                        if(btnswitch.isChecked() && userLocalStore.getChatRoom().length()>2){
                            firebaseRef=databaseReferenceToAccounts.child(Config.FIREBASE_APP_URL_FINANCE_ACCOUNTS)
                                    .child(Config.FIREBASE_APP_URL_FINANCE_ACCOUNTS_SHARED)
                                    .child(userLocalStore.getChatRoom());
                        }else {
                            firebaseRef=databaseReferenceToAccounts.child(Config.FIREBASE_APP_URL_FINANCE_ACCOUNTS)
                                    .child(auth.getCurrentUser().getUid());
                        }
                        valueEventListener=new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(financeAccountid)){
                                    FinanceAccountForFireBase finanAcc=dataSnapshot.child(financeAccountid).getValue(FinanceAccountForFireBase.class);
                                    final ArrayList<FinanceRecordsForFireBase> list=new ArrayList<FinanceRecordsForFireBase>();
                                    for(FinanceRecordsForFireBase fr:finanAcc.getAccountsRecords()){
                                        if(fr.getRecordAmount().equals(getString(R.string.textInitialize_create_account_grocery_note))
                                                ||fr.getRecordNAme().equals("Grocery list")||
                                                fr.getRecordNAme().equals("Einkaufsliste")){
                                            list.add(fr);
                                        }
                                    }

                                    final DatabaseReference grRef=FirebaseDatabase.getInstance().getReference().child(Config.FIREBASE_APP_URL_GROCERYLISTS)
                                            .child(auth.getCurrentUser().getUid());
                                    grRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot child : dataSnapshot.getChildren()){
                                                for(int j=0;j<list.size();j++){
                                                    if(child.getKey().equals(list.get(j).getRecordUniquesId())){
                                                        DatabaseReference reference=grRef.child(child.getKey());
                                                        reference.removeValue();
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }

                                firebaseRef.child(financeAccountid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            if(valueEventListener!=null){
                                                firebaseRef.removeEventListener(valueEventListener);
                                            }
                                            if(progressBar!=null){
                                                progressBar.dismiss();
                                            }
                                            Toast.makeText(getActivity(), "Account deleted", Toast.LENGTH_SHORT).show();
                                        }else {
                                            if(progressBar!=null){
                                                progressBar.dismiss();
                                            }
                                            Toast.makeText(getActivity(), task.getException()
                                                    .getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });



                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                if(progressBar!=null){
                                    progressBar.dismiss();
                                }
                            }
                        };

                        firebaseRef.addValueEventListener(valueEventListener);

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



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReferenceToAccounts= FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        auth=FirebaseAuth.getInstance();

        databaseReferenceToUsers=FirebaseDatabase.getInstance().getReference().child(Config.FIREBASE_APP_URL_USERS);
        firebase=new Firebase(Config.FIREBASE_APP_URL);



        userLocalStore=new UserLocalStore(getContext());


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

    private void fetchshared(){
        progressBar = new ProgressDialog(getContext());
        progressBar.setCancelable(false);
        progressBar.setTitle("Loading");
        progressBar.setMessage("in progress ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();
        //on Child event chnage
        assert auth.getCurrentUser()!=null;
        final DatabaseReference firebaseRef=databaseReferenceToAccounts.child(Config.FIREBASE_APP_URL_FINANCE_ACCOUNTS)
                .child(Config.FIREBASE_APP_URL_FINANCE_ACCOUNTS_SHARED)
                .child(userLocalStore.getChatRoom());

        firebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                accounts.clear();
                for(DataSnapshot child: dataSnapshot.getChildren()){
                    accounts.add(new FinanceAccount(getActivity()).getFinanceAccountFromFirebase(
                            child.getValue(FinanceAccountForFireBase.class)
                    ));
                }

                count= (int) dataSnapshot.getChildrenCount();
                if(count==0){
                    buttonAddFinanceRecords.setVisibility(View.GONE);
                    buttonCreateAccount.setVisibility(View.VISIBLE);
                }else if (count>0 && count<4){
                    buttonCreateAccount.setVisibility(View.VISIBLE);
                    buttonAddFinanceRecords.setVisibility(View.VISIBLE);
                }else if(count>=4){
                    buttonCreateAccount.setVisibility(View.GONE);
                    buttonAddFinanceRecords.setVisibility(View.VISIBLE);
                }
                setBalance(getContext());
                if (progressBar!=null){
                    progressBar.dismiss();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (progressBar!=null){
                    progressBar.dismiss();
                }
            }
        });


        adapter=
                new FirebaseRecyclerAdapter<FinanceAccountForFireBase, FinanceAccountViewHolder>(

                        FinanceAccountForFireBase.class,
                        R.layout.create_account_card,
                        FinanceAccountViewHolder.class,
                        firebaseRef) {
                    @Override
                    protected void populateViewHolder(FinanceAccountViewHolder financeAccountViewHolder
                            , FinanceAccountForFireBase financeAccountForFireBase, final int i) {

                        FinanceAccount financeAccount= new FinanceAccount(getContext())
                                .getFinanceAccountFromFirebase(financeAccountForFireBase);

                        String name=getContext().getResources().getString(R.string.Account_list_item_title_text ).toLowerCase()  +" " + financeAccount.getAccountName();


                        financeAccountViewHolder.accountnametv.setText(name);

                        financeAccountViewHolder.accountidtv.setText(financeAccount.getAccountUniqueId().replace("-","A"));
                        String p=financeAccount.getAccountBlanceTostring() +" €";
                        if(p.contains("-")){
                            financeAccountViewHolder.balancetv.setText(p);
                            financeAccountViewHolder.balancetv.setTextColor(getContext().getResources().getColor(R.color.warning_color));
                        }else {
                            financeAccountViewHolder.balancetv.setText(p);
                            financeAccountViewHolder.balancetv.setTextColor(getContext().getResources().getColor(R.color.color_account_balance_positive));
                        }

                        if(financeAccount.getAccountOwnersFirebase().size()!=0){
                            String owner=financeAccount.getAccountOwnersFirebase().get(0).getfullname();
                            financeAccountViewHolder.accountOwnertv.setText(owner);
                        }

                        financeAccountViewHolder.lastCHangetv.setText(financeAccount.getLastchangeToAccount());
                        financeAccountViewHolder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                startActivity(new Intent(getActivity(), DetailsFinanceAccountActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        .putExtra("Accountid",getItem(i).getAccountUniqueId())
                                        .putExtra("sharedAccount",true));

                            }
                        });
                        financeAccountViewHolder.view.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                showFilterPopup(view, getItem(i).getAccountUniqueId());
                                return false;
                            }
                        });




                    }
                };

        mRecyclerView.setAdapter(adapter);

    }
    private void fetchprivate(){
        progressBar = new ProgressDialog(getContext());
        progressBar.setCancelable(false);
        progressBar.setTitle("Loading");
        progressBar.setMessage("in progress ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();
        //on Child event chnage
        assert auth.getCurrentUser()!=null;
        final DatabaseReference firebaseRef=databaseReferenceToAccounts.child(Config.FIREBASE_APP_URL_FINANCE_ACCOUNTS)
                .child(auth.getCurrentUser().getUid());

        firebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                accounts.clear();
                for(DataSnapshot child: dataSnapshot.getChildren()){
                    accounts.add(new FinanceAccount(getActivity()).getFinanceAccountFromFirebase(
                            child.getValue(FinanceAccountForFireBase.class)
                    ));
                }

                count= (int) dataSnapshot.getChildrenCount();
                if(count==0){
                    buttonAddFinanceRecords.setVisibility(View.GONE);
                    buttonCreateAccount.setVisibility(View.VISIBLE);
                }else if (count>0 && count<4){
                    buttonCreateAccount.setVisibility(View.VISIBLE);
                    buttonAddFinanceRecords.setVisibility(View.VISIBLE);
                }else if(count>=4){
                    buttonCreateAccount.setVisibility(View.GONE);
                    buttonAddFinanceRecords.setVisibility(View.VISIBLE);
                }
                setBalance(getContext());
                if (progressBar!=null){
                    progressBar.dismiss();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (progressBar!=null){
                    progressBar.dismiss();
                }
            }
        });


        adapter=
                new FirebaseRecyclerAdapter<FinanceAccountForFireBase, FinanceAccountViewHolder>(

                        FinanceAccountForFireBase.class,
                        R.layout.create_account_card,
                        FinanceAccountViewHolder.class,
                        firebaseRef) {
                    @Override
                    protected void populateViewHolder(FinanceAccountViewHolder financeAccountViewHolder
                            , FinanceAccountForFireBase financeAccountForFireBase, final int i) {

                        FinanceAccount financeAccount= new FinanceAccount(getContext())
                                .getFinanceAccountFromFirebase(financeAccountForFireBase);

                        String name=getContext().getResources().getString(R.string.Account_list_item_title_text ).toLowerCase()  +" " + financeAccount.getAccountName();


                        financeAccountViewHolder.accountnametv.setText(name);

                        financeAccountViewHolder.accountidtv.setText(financeAccount.getAccountUniqueId().replace("-","A"));
                        String p=financeAccount.getAccountBlanceTostring() +" €";
                        if(p.contains("-")){
                            financeAccountViewHolder.balancetv.setText(p);
                            financeAccountViewHolder.balancetv.setTextColor(getContext().getResources().getColor(R.color.warning_color));
                        }else {
                            financeAccountViewHolder.balancetv.setText(p);
                            financeAccountViewHolder.balancetv.setTextColor(getContext().getResources().getColor(R.color.color_account_balance_positive));
                        }

                        if(financeAccount.getAccountOwnersFirebase().size()!=0){
                            String owner=financeAccount.getAccountOwnersFirebase().get(0).getfullname();
                            financeAccountViewHolder.accountOwnertv.setText(owner);
                        }

                        financeAccountViewHolder.lastCHangetv.setText(financeAccount.getLastchangeToAccount());
                        financeAccountViewHolder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                startActivity(new Intent(getActivity(), DetailsFinanceAccountActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        .putExtra("Accountid",getItem(i).getAccountUniqueId())
                                .putExtra("sharedAccount",false));

                            }
                        });
                        financeAccountViewHolder.view.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                showFilterPopup(view, getItem(i).getAccountUniqueId());
                                return false;
                            }
                        });




                    }
                };

        mRecyclerView.setAdapter(adapter);

    }
    @Override
    public void onStart() {
        super.onStart();
        if(btnswitch.isChecked()){
            btnswitch.setText("My shared accounts");
            fetchshared();
        }else{
            btnswitch.setText("My private accounts");
            fetchprivate();
        }


    }

    @Override
    public void onResume() {
        super.onResume();


        if(count==0){
            buttonAddFinanceRecords.setVisibility(View.GONE);
            buttonCreateAccount.setVisibility(View.VISIBLE);
        }else if (count>0 && count<4){
            buttonCreateAccount.setVisibility(View.VISIBLE);
            buttonAddFinanceRecords.setVisibility(View.VISIBLE);
        }else if(count>=4){
            buttonCreateAccount.setVisibility(View.GONE);
            buttonAddFinanceRecords.setVisibility(View.VISIBLE);
        }
          setBalance(getContext());


    }

    public static class FinanceAccountViewHolder extends RecyclerView.ViewHolder
    {
        TextView accountnametv,accountOwnertv,lastCHangetv,balancetv,pendingtv,accountidtv;

        View view;
        public FinanceAccountViewHolder(View itemView) {
            super(itemView);

            view=itemView;

            accountnametv = (TextView) itemView.findViewById(R.id.textView_AccountName_create_account_finance_card);
            accountOwnertv = (TextView) itemView.findViewById(R.id.textView_AccountOwner_create_account_finance_card);
            lastCHangetv = (TextView) itemView.findViewById(R.id.textView_last_changeOnAccount_create_account_finance_card);
            balancetv = (TextView) itemView.findViewById(R.id.textView_AccountBalance_create_account_finance_card);
            pendingtv = (TextView) itemView.findViewById(R.id.textView_pending_expenses_create_account_finance_card);
            accountidtv=(TextView) itemView.findViewById(R.id.textView_AccountID_create_account_finance_card);



        }

    }


    void setBalance(Context context){
        if(accounts!=null){
            double balce=0;
            for (int i=0; i<accounts.size();i++){
                accounts.get(i).setContext(context);
                accounts.get(i).getAccountrecordsAmountUpdateBalance(context);
                balce=balce+accounts.get(i).getAccountBalance();
            }
            DecimalFormat df = new DecimalFormat("0.00");
            df.setMaximumFractionDigits(2);

            String priceStr = df.format(balce);
            if(priceStr.contains("-")){
                textViewBalance.setText(priceStr+" €");
                userLocalStore.setUserAccountBalance(priceStr);
                if(isAdded()){
                    textViewBalance.setTextColor(getResources().getColor(R.color.warning_color));
                }
            }else {
                textViewBalance.setText(priceStr+" €");
                userLocalStore.setUserAccountBalance(priceStr);

            }


        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.fragment_finance_add_account_button){
            //start create Account Activity
            startActivity(new Intent(getActivity(),CreateFinanceAccountActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        }else if( view.getId()==R.id.fragment_finance_add_finance_record_button){
            //start activity add finance record

                startActivity(new Intent(getActivity(),CreateFinanceRecordsActivity.class).
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra("AccountToUpdate",accounts));


        }
    }
}
