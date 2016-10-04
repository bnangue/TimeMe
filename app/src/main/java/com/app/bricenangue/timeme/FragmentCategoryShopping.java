package com.app.bricenangue.timeme;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class FragmentCategoryShopping extends Fragment implements View.OnClickListener{

    private String mParam1;
    private String mParam2;
    private TextView textBalance;
    private Button add_List;
    private boolean isExpanded = false;
    private float mCurrentRotation = 360.0f;
    private android.support.v7.app.AlertDialog alertDialog;
    private ProgressDialog progressBar;
    private Switch aSwitch;




    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private static String LOG_TAG = "RecyclerViewActivity_Fragment_Shopping";
    private Fragment fragment=this;
    private ArrayList<GroceryList> shoppingListsrecent = new ArrayList<>();

    private boolean isShown=false;
    private UserLocalStore userLocalStore;
    private TextView textswitch;
    private LinearLayout linearLayout;
    private String sortName;
    private FirebaseRecyclerAdapter<GroceryListForFireBase,GroceryFrangmentViewHolder> adapter;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;


    public FragmentCategoryShopping() {
        // Required empty public constructor
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's state here
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        userLocalStore=new UserLocalStore(getContext());
        alertDialog = new android.support.v7.app.AlertDialog.Builder(getContext()).create();

        View v = inflater.inflate(R.layout.fragment_grocery_list, container, false);
        textBalance = (TextView) v.findViewById(R.id.grocery_fragment_balance_amount);
        aSwitch=(Switch)v.findViewById(R.id.text_grocery_fragment_switch_to_shared);


        add_List = (Button) v.findViewById(R.id.grocery_fragment_add_recently_button);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.shoppingrecycleViewfragöment_grocery_list_recentlityl_added);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);


        add_List.setOnClickListener(this);

        if(userLocalStore.getUserAccountBalance().contains("-")){
            textBalance.setText(userLocalStore.getUserAccountBalance()+" €");
            textBalance.setTextColor(getResources().getColor(R.color.warning_color));
        }else {
            textBalance.setText(userLocalStore.getUserAccountBalance()+" €");
        }

        if(aSwitch.isChecked()){
            aSwitch.setText("Your Shared Lists");
        }else{
            aSwitch.setText("Your Private Lists");

        }

        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(aSwitch.isChecked()){
                    aSwitch.setText("Your Shared Lists");
                    showSharedList();
                }else{
                    aSwitch.setText("Your Private Lists");
                    showprivateList();
                }
            }
        });
        return v;
    }

    private void startGroceryListOverview(String id,boolean itemisToListshare ) {
        startActivity(new Intent(getActivity(),DetailsShoppingListActivity.class)
              .putExtra("GroceryListId",id)
                .putExtra("GroceryListIsShared",itemisToListshare)
               .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

    }

    @Override
    public void onClick(View v) {

        int id=v.getId();
        switch (id){
            case R.id.grocery_fragment_add_recently_button:
                startActivity(new Intent(getActivity(),CreateNewShoppingListActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                break;


        }
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }


    @Override
    public void onCreate(@Nullable  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseReference= FirebaseDatabase.getInstance().getReference();

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        auth=FirebaseAuth.getInstance();

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
    private void showprivateList(){
        progressBar = new ProgressDialog(getContext());
        progressBar.setCancelable(false);
        progressBar.setTitle("Loading");
        progressBar.setMessage("in progress ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();
        assert auth.getCurrentUser()!=null;
        final DatabaseReference reference= databaseReference.child(Config.FIREBASE_APP_URL_GROCERYLISTS)
                .child(auth.getCurrentUser().getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                shoppingListsrecent.clear();

                for(DataSnapshot child: dataSnapshot.getChildren()){
                    shoppingListsrecent.add(new GroceryList().getGrocerylistFromGLFirebase(
                            child.getValue(GroceryListForFireBase.class)
                    ));
                }

                if(progressBar!=null){
                    progressBar.dismiss();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if(progressBar!=null){
                    progressBar.dismiss();
                }
            }

        });
        adapter=new FirebaseRecyclerAdapter<GroceryListForFireBase, GroceryFrangmentViewHolder>(
                GroceryListForFireBase.class,
                R.layout.card_shop_list_small,
                GroceryFrangmentViewHolder.class,
                reference) {
            @Override
            protected void populateViewHolder(GroceryFrangmentViewHolder viewHolder,
                                              final GroceryListForFireBase model, int position) {

                final GroceryList groceryList=new GroceryList().getGrocerylistFromGLFirebase(model);
                String name=getContext().getResources().getString(R.string.grocery_list_item_title_text ).toLowerCase() + " " + groceryList.getDatum();
                viewHolder.listname.setText(name);
                viewHolder.listStatus.setText(groceryList.isListdone() ?
                        groceryList.getGroceryListTotalPriceString() : getContext().getString(R.string.grocery_list_status__not_done_text));
                viewHolder.listStatus.setTextColor(groceryList.isListdone() ?
                        getContext().getResources().getColor(R.color.warning_color) : getContext().getResources().getColor(R.color.grey_light));

                viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialogDelete(model.getList_unique_id(), model);
                    }
                });
                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startGroceryListOverview(model.getList_unique_id(),model.isToListshare());
                    }
                });
            }
        };

        mRecyclerView.setAdapter(adapter);
    }

    private void showSharedList(){
        progressBar = new ProgressDialog(getContext());
        progressBar.setCancelable(false);
        progressBar.setTitle("Loading");
        progressBar.setMessage("in progress ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();
        assert auth.getCurrentUser()!=null;
        final DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child(Config.FIREBASE_APP_URL_GROCERYLISTS)
                .child(Config.FIREBASE_APP_URL_GROCERYLISTS_SHARED)
                .child(userLocalStore.getChatRoom());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                shoppingListsrecent.clear();

                for(DataSnapshot child: dataSnapshot.getChildren()){
                    shoppingListsrecent.add(new GroceryList().getGrocerylistFromGLFirebase(
                            child.getValue(GroceryListForFireBase.class)
                    ));
                }

                if(progressBar!=null){
                    progressBar.dismiss();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if(progressBar!=null){
                    progressBar.dismiss();
                }
            }

        });
        adapter=new FirebaseRecyclerAdapter<GroceryListForFireBase, GroceryFrangmentViewHolder>(
                GroceryListForFireBase.class,
                R.layout.card_shop_list_small,
                GroceryFrangmentViewHolder.class,
                reference) {
            @Override
            protected void populateViewHolder(GroceryFrangmentViewHolder viewHolder,
                                              final GroceryListForFireBase model, int position) {

                final GroceryList groceryList=new GroceryList().getGrocerylistFromGLFirebase(model);
                String name=getContext().getResources().getString(R.string.grocery_list_item_title_text ).toLowerCase() + " " + groceryList.getDatum();
                viewHolder.listname.setText(name);
                viewHolder.listStatus.setText(groceryList.isListdone() ?
                        groceryList.getGroceryListTotalPriceString() : getContext().getString(R.string.grocery_list_status__not_done_text));
                viewHolder.listStatus.setTextColor(groceryList.isListdone() ?
                        getContext().getResources().getColor(R.color.warning_color) : getContext().getResources().getColor(R.color.grey_light));

                viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialogDelete(model.getList_unique_id(), model);
                    }
                });
                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startGroceryListOverview(model.getList_unique_id(),model.isToListshare());
                    }
                });
            }
        };

        mRecyclerView.setAdapter(adapter);
    }
    @Override
    public void onStart() {
        super.onStart();
        if(aSwitch.isChecked()){
            aSwitch.setText("My shared lists");
            showSharedList();
        }else{
            aSwitch.setText("My private lists");
            showprivateList();
        }

    }

    public void alertDialogDelete(final String id, final GroceryListForFireBase groceryList){

        LayoutInflater inflater= (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getContext());
        View dialoglayout = inflater.inflate(R.layout.dialog_warning_delete_event, null);

        final android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.setView(dialoglayout);
        Button delete= (Button)dialoglayout.findViewById(R.id.buttonDeleteaccount);
        Button cancel= (Button)dialoglayout.findViewById(R.id.buttonCancelaccount);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar = new ProgressDialog(getContext());
                progressBar.setCancelable(false);
                progressBar.setTitle("Deleting grocery list");
                progressBar.setMessage("in progress ...");
                progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressBar.show();
                assert auth.getCurrentUser()!=null;
                final DatabaseReference finRef;

                final DatabaseReference grRef;
                if(groceryList.isToListshare() && userLocalStore.getChatRoom().length()>2){
                    grRef=FirebaseDatabase.getInstance()
                            .getReference().child(Config.FIREBASE_APP_URL_GROCERYLISTS)
                            .child(Config.FIREBASE_APP_URL_GROCERYLISTS_SHARED)
                            .child(userLocalStore.getChatRoom()).child(id);

                }else {
                    grRef=FirebaseDatabase.getInstance()
                            .getReference().child(Config.FIREBASE_APP_URL_GROCERYLISTS).child(auth.getCurrentUser().getUid()).child(id);

                }
                if(groceryList.isAccountisshared()){
                    finRef=FirebaseDatabase.getInstance().getReference().child(Config.FIREBASE_APP_URL_FINANCE_ACCOUNTS)
                            .child(Config.FIREBASE_APP_URL_FINANCE_ACCOUNTS_SHARED)
                            .child(userLocalStore.getChatRoom());
                }else {

                    finRef=FirebaseDatabase.getInstance().getReference().child(Config.FIREBASE_APP_URL_FINANCE_ACCOUNTS)
                            .child(auth.getCurrentUser().getUid());
                }

                valueEventListener=new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        FinanceAccountForFireBase child=dataSnapshot.child(groceryList.getAccountid()).getValue(FinanceAccountForFireBase.class);
                        FinanceAccount financeAccount=new FinanceAccount(getContext()).getFinanceAccountFromFirebase(child);
                        ArrayList<FinanceRecords> list=financeAccount.getAccountsRecord();
                        for(int i=0;i<list.size();i++){
                            if(list.get(i).getRecordUniquesId()
                                    .equals(groceryList.getList_unique_id())){
                                list.remove(list.get(i));
                            }
                        }
                        financeAccount.setAccountsRecords(list);
                        financeAccount.setAccountsRecord(list);
                        financeAccount.setLastchangeToAccount();
                        financeAccount.getAccountrecordsAmountUpdateBalance(getContext());
                        DatabaseReference newRef=finRef.child(financeAccount.getAccountUniqueId());
                        newRef.setValue(new FinanceAccount(getContext()).getFinanceAccountForFirebase(financeAccount))
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            grRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        if(valueEventListener!=null){
                                                            finRef.removeEventListener(valueEventListener);
                                                        }
                                                        if (progressBar!=null){
                                                            progressBar.dismiss();
                                                        }
                                                        Toast.makeText(getContext(),"Deleted",Toast.LENGTH_SHORT).show();
                                                    }else {
                                                        if (progressBar!=null){
                                                            progressBar.dismiss();
                                                        }
                                                        Toast.makeText(getContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }else {
                                            if (progressBar!=null){
                                                progressBar.dismiss();
                                            }
                                            Toast.makeText(getContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        if (progressBar!=null){
                            progressBar.dismiss();
                        }
                    }
                };
                finRef.addValueEventListener(valueEventListener);


                alertDialog.dismiss();
            }
        });

        alertDialog.setCancelable(false);
        // show it
        alertDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(userLocalStore.getUserAccountBalance().contains("-")){
            textBalance.setText(userLocalStore.getUserAccountBalance()+" €");
            textBalance.setTextColor(getResources().getColor(R.color.warning_color));
        }else {
            textBalance.setText(userLocalStore.getUserAccountBalance()+" €");
        }
    }


    public static class GroceryFrangmentViewHolder extends RecyclerView.ViewHolder
    {
        TextView listname,listStatus;
        Button delete;
        View view;
        public GroceryFrangmentViewHolder(View itemView) {
            super(itemView);

            view=itemView;

            listname = (TextView) itemView.findViewById(R.id.textView_Grocery_listname_create_shopping_list_small_card);
            listStatus = (TextView) itemView.findViewById(R.id.textView_LisStatus_create_shopping_list_small_card);
            delete = (Button) itemView.findViewById(R.id.buttondeletecardview_create_shopping_list_small_card);
        }

    }




}
