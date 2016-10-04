package com.app.bricenangue.timeme;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CreateNewShoppingListActivity extends AppCompatActivity implements View.OnClickListener,DialogDeleteEventFragment.OnDeleteListener {

    private Button addItemToListbutton;

    private ArrayList<GroceryList> grocerylistSqlDB=new ArrayList<>();
    private static GroceryList groceryList;

    private TextView textViewlistIsempty;
    private StateActivitiesPreference stateActivitiesPreference;


    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter<GroceryListForFireBase,GroceryListViewHolder> adapter;
    private ProgressDialog progressBar;
    private ValueEventListener valueEventListener;
    private android.support.v7.app.AlertDialog alertDialog;
    private UserLocalStore userLocalStore;
    private Switch btnswitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_shopping_list);
        auth=FirebaseAuth.getInstance();
        userLocalStore=new UserLocalStore(this);
        databaseReference= FirebaseDatabase.getInstance().getReference();

        stateActivitiesPreference=new StateActivitiesPreference(this);

        addItemToListbutton=(Button)findViewById(R.id.grocery_create_list_add_item_button);
        textViewlistIsempty=(TextView)findViewById(R.id.textView_create_list_List_empty);

        btnswitch=(Switch)findViewById(R.id.button_switch_grocery_create_list_add_item);
        mRecyclerView = (RecyclerView)findViewById(R.id.shoppingrecycleViewCreateLisactivity);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        addItemToListbutton.setOnClickListener(this);

        if(!stateActivitiesPreference.getCopyExcelFileFromAssetToInterneMemory()){
            new InitItemToDBAsyncTask(this).execute();
        }


        if(btnswitch.isChecked()){
            btnswitch.setText("My shared grocery lists");

        }else{
            btnswitch.setText("My private grocery lists");

        }
        btnswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnswitch.isChecked()){
                    btnswitch.setText("My shared grocery lists");
                    showshared();

                }else{
                    btnswitch.setText("My private grocery lists");
                    showprivate();

                }
            }
        });



    }

    private void showshared(){
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);
        progressBar.setTitle("Loading");
        progressBar.setMessage("in progress ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();
        assert auth.getCurrentUser()!=null;
        final DatabaseReference firebaseRef=databaseReference.child(Config.FIREBASE_APP_URL_GROCERYLISTS)
                .child(Config.FIREBASE_APP_URL_GROCERYLISTS_SHARED)
                .child(userLocalStore.getChatRoom());

        firebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                grocerylistSqlDB.clear();
                for(DataSnapshot child: dataSnapshot.getChildren()){
                    grocerylistSqlDB.add(new GroceryList().getGrocerylistFromGLFirebase(
                            child.getValue(GroceryListForFireBase.class)
                    ));
                }
                if(grocerylistSqlDB.size()==0){
                    mRecyclerView.setVisibility(View.GONE);
                    textViewlistIsempty.setText(R.string.text_create_list_List_empty);
                }else{
                    mRecyclerView.setVisibility(View.VISIBLE);
                    textViewlistIsempty.setText(R.string.text_create_list_List_not_empty_more_than_one);
                }

                if(progressBar!=null){
                    progressBar.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


        adapter=
                new FirebaseRecyclerAdapter<GroceryListForFireBase, GroceryListViewHolder>(
                        GroceryListForFireBase.class,
                        R.layout.create_shopping_list_card,
                        GroceryListViewHolder.class,
                        firebaseRef
                ) {
                    @Override
                    protected void populateViewHolder(GroceryListViewHolder viewHolder, GroceryListForFireBase model, final int position) {
                        final GroceryList groceryList=new GroceryList().getGrocerylistFromGLFirebase(model);

                        CreateNewShoppingListActivity.groceryList=groceryList;
                        String name=getResources().getString(R.string.grocery_list_item_title_text ).toLowerCase()  +" " + groceryList.getDatum();

                        viewHolder.listname.setText(name);
                        viewHolder.listcreator.setText(groceryList.getCreatorName());
                        viewHolder.listStatus.setText(groceryList.isListdone() ? R.string.grocery_list_status_done_text : R.string.grocery_list_status__not_done_text);
                        if(groceryList.isToListshare()){
                            viewHolder.share.setVisibility(View.GONE);
                        }


                        viewHolder.share.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //shared

                            }
                        });
                        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                DialogFragment dialogFragment = DialogDeleteEventFragment.newInstance(getItem(position).getList_unique_id()
                                        ,getItem(position).getAccountid(),getItem(position).isToListshare());
                                dialogFragment.setCancelable(false);
                                dialogFragment.show(getSupportFragmentManager(), "DELETEListFRAGMENT");
                            }
                        });


                        viewHolder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startGroceryListOverview(groceryList);
                            }
                        });
                    }
                };

        mRecyclerView.setAdapter(adapter);
    }

    private void showprivate(){

        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);
        progressBar.setTitle("Loading");
        progressBar.setMessage("in progress ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();
        assert auth.getCurrentUser()!=null;
        final DatabaseReference firebaseRef=databaseReference.child(Config.FIREBASE_APP_URL_GROCERYLISTS)
                .child(auth.getCurrentUser().getUid());

        firebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                grocerylistSqlDB.clear();
                for(DataSnapshot child: dataSnapshot.getChildren()){
                    grocerylistSqlDB.add(new GroceryList().getGrocerylistFromGLFirebase(
                            child.getValue(GroceryListForFireBase.class)
                    ));
                }
                if(grocerylistSqlDB.size()==0){
                    mRecyclerView.setVisibility(View.GONE);
                    textViewlistIsempty.setText(R.string.text_create_list_List_empty);
                }else{
                    mRecyclerView.setVisibility(View.VISIBLE);
                    textViewlistIsempty.setText(R.string.text_create_list_List_not_empty_more_than_one);
                }

                if(progressBar!=null){
                    progressBar.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


        adapter=
                new FirebaseRecyclerAdapter<GroceryListForFireBase, GroceryListViewHolder>(
                        GroceryListForFireBase.class,
                        R.layout.create_shopping_list_card,
                        GroceryListViewHolder.class,
                        firebaseRef
                ) {
                    @Override
                    protected void populateViewHolder(GroceryListViewHolder viewHolder, GroceryListForFireBase model, final int position) {
                        final GroceryList groceryList=new GroceryList().getGrocerylistFromGLFirebase(model);

                        CreateNewShoppingListActivity.groceryList=groceryList;
                        String name=getResources().getString(R.string.grocery_list_item_title_text ).toLowerCase()  +" " + groceryList.getDatum();

                        viewHolder.listname.setText(name);
                        viewHolder.listcreator.setText(groceryList.getCreatorName());
                        viewHolder.listStatus.setText(groceryList.isListdone() ? R.string.grocery_list_status_done_text : R.string.grocery_list_status__not_done_text);
                        if(groceryList.isToListshare()){
                            viewHolder.share.setVisibility(View.GONE);
                        }


                        viewHolder.share.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //shared

                            }
                        });
                        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                DialogFragment dialogFragment = DialogDeleteEventFragment.newInstance(getItem(position).getList_unique_id()
                                        ,getItem(position).getAccountid(),getItem(position).isToListshare());
                                dialogFragment.setCancelable(false);
                                dialogFragment.show(getSupportFragmentManager(), "DELETEListFRAGMENT");
                            }
                        });


                        viewHolder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startGroceryListOverview(groceryList);
                            }
                        });
                    }
                };

        mRecyclerView.setAdapter(adapter);
    }
    @Override
    protected void onStart() {
        super.onStart();

        if(btnswitch.isChecked()){
            btnswitch.setText("My shared grocery lists");
            showshared();
        }else{
            btnswitch.setText("My private grocery lists");
            showprivate();
        }


    }

    public static class GroceryListViewHolder extends RecyclerView.ViewHolder
    {
        TextView listname,listcreator,listStatus;
        Button share,delete;

        View view;
        public GroceryListViewHolder(View itemView) {
            super(itemView);

            view=itemView;

            listname = (TextView) itemView.findViewById(R.id.TextView_listname_create_shopping_list_card);
            listcreator = (TextView) itemView.findViewById(R.id.textView_Listcreator_create_shopping_list_card);
            listStatus = (TextView) itemView.findViewById(R.id.textView_LisStatus_create_shopping_list_card);
            share = (Button) itemView.findViewById(R.id.buttonsharecardview_create_shopping_list_card);
            delete = (Button) itemView.findViewById(R.id.buttondeletecardview_create_shopping_list_card);



        }

    }
    private  boolean saveExcelXLSXFileFirstInit(Context context) {
        boolean success= false;

        FileHelper fileHelper=new FileHelper(context);
        XSSFWorkbook wb = null;
        String workbooksFolderpath= fileHelper.getWorkbooksFolder();
        String filesDirectorypath= fileHelper.getFilesDirectory();
        File filedirectory=new File(filesDirectorypath);

        if (!filedirectory.exists()){
            filedirectory.mkdirs();
        }
        File workbookfolder=new File(workbooksFolderpath);
        if(!workbookfolder.exists()){
            workbookfolder.mkdir();
        }
        // Create a path where we will place our List of objects on external storage
        File file=null;
        FileOutputStream os = null;

        try {
            AssetManager manager=getAssets();
            InputStream in=manager.open("shopping_list_items.xlsx");
            wb = new XSSFWorkbook(in);

            file = new File(fileHelper.getExcelfile("shopping_list_items"));
            if(!file.exists()){
                file.createNewFile();

            }

            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return success;
    }

    private  boolean addExcelXLSXFileToWoorkbook(Context context,String filename) {
        boolean success= false;

        FileHelper fileHelper=new FileHelper(context);
        XSSFWorkbook wb = null;

        File file=null;
        FileOutputStream os = null;

        try {
            AssetManager manager=getAssets();
            InputStream in=manager.open(filename+ ".xlsx");
            wb = new XSSFWorkbook(in);

            file = new File(fileHelper.getExcelfile("shopping_list_items"));

            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return success;
    }

    private void startGroceryListOverview(GroceryList item) {
        startActivity(new Intent(this,DetailsShoppingListActivity.class)
                .putExtra("GroceryListId",item.getList_unique_id())
                .putExtra("GroceryListIsShared",item.isToListshare()).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));

    }

    void showDialogshareoption(){

        alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();

        alertDialog.setTitle(getString(R.string.activity_create_new_grocery_shared_option_dialog_title));


        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.alert_dialog_create_new_grocery_shared_option_dialog_title_buttonNO),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(CreateNewShoppingListActivity.this,AddItemToListActivity.class)
                        .putExtra("shareList",false));
                        alertDialog.dismiss();
                    }
                });

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.alert_dialog_create_new_grocery_shared_option_dialog_title_buttonYES)
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(CreateNewShoppingListActivity.this,AddItemToListActivity.class)
                                .putExtra("shareList",true));
                        alertDialog.dismiss();


                    }
                });

        alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();
                    }
                });
        alertDialog.setCancelable(true);
        alertDialog.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.grocery_create_list_add_item_button:
                //add item return shopping list to show here
                    showDialogshareoption();

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    class InitItemToDBAsyncTask extends AsyncTask<Void ,Void, Boolean> {

        private FragmentProgressBarLoading progressDialog;
        private Context context;
        private boolean success=false;
        public InitItemToDBAsyncTask(Context context){
            this.context=context;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //start progressBar
            progressDialog = new FragmentProgressBarLoading();
            progressDialog.setCancelable(false);
            progressDialog.show(getSupportFragmentManager(), "task_progress");
        }

        @Override
        protected Boolean doInBackground(Void... params) {


                success=saveExcelXLSXFileFirstInit(context);


            return success;

        }

        @Override
        protected void onPostExecute(Boolean params) {
            //end progressBar
            progressDialog.dismiss(getSupportFragmentManager());
            if(params){
                // initializeDatePicker();

                    stateActivitiesPreference.setCopyExcelFileFromAssetToInterneMemory(true);
                Toast.makeText(getApplicationContext(),"Database loaded",Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(getApplicationContext(),"Error initializing database",Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void delete(final String id,final String accid,boolean shareList) {

        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);
        progressBar.setTitle("Deleting grocery list");
        progressBar.setMessage("in progress ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();
        assert auth.getCurrentUser()!=null;
        final DatabaseReference finRef;

        final DatabaseReference grRef;
        if(shareList && userLocalStore.getChatRoom().length()>2){
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
                FinanceAccountForFireBase child=dataSnapshot.child(accid).getValue(FinanceAccountForFireBase.class);
                FinanceAccount financeAccount=new FinanceAccount(getApplicationContext()).getFinanceAccountFromFirebase(child);
                ArrayList<FinanceRecords> list=financeAccount.getAccountsRecord();
                for(int i=0;i<list.size();i++){
                    if(list.get(i).getRecordUniquesId()
                            .equals(id)){
                        list.remove(list.get(i));
                    }
                }
                financeAccount.setAccountsRecords(list);
                financeAccount.setAccountsRecord(list);
                financeAccount.setLastchangeToAccount();
                financeAccount.getAccountrecordsAmountUpdateBalance(getApplicationContext());
                DatabaseReference newRef=finRef.child(financeAccount.getAccountUniqueId());
                newRef.setValue(new FinanceAccount(getApplicationContext()).getFinanceAccountForFirebase(financeAccount))
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
                                                Toast.makeText(getApplicationContext(),"Deleted",Toast.LENGTH_SHORT).show();
                                            }else {
                                                if (progressBar!=null){
                                                    progressBar.dismiss();
                                                }
                                                Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }else {
                                    if (progressBar!=null){
                                        progressBar.dismiss();
                                    }
                                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
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
/**
        final DatabaseReference finReff=FirebaseDatabase.getInstance().getReference().child(Config.FIREBASE_APP_URL_FINANCE_ACCOUNTS)
                .child(auth.getCurrentUser().getUid()).child(accid)
                .child(Config.FIREBASE_APP_URL_FINANCE_ACCOUNTS_RECORDS);

        final DatabaseReference grRef=FirebaseDatabase.getInstance()
                .getReference().child(Config.FIREBASE_APP_URL_GROCERYLISTS).child(auth.getCurrentUser().getUid()).child(id);

        valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child: dataSnapshot.getChildren()){
                    if(child.child("recordUniquesId").getValue().equals(id)){
                        DatabaseReference data= finReff.child(child.getKey());
                        data.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Calendar c=new GregorianCalendar();
                                    Date dat=c.getTime();
                                    String date = (String) android.text.format.DateFormat.format("dd-MM-yyyy", dat);
                                    DatabaseReference dataref=FirebaseDatabase.getInstance().getReference()
                                            .child(Config.FIREBASE_APP_URL_FINANCE_ACCOUNTS)
                                            .child(auth.getCurrentUser().getUid()).child(accid);
                                    dataref.child("lastchangeToAccount").setValue(date).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                           if(task.isSuccessful()){
                                               grRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                   @Override
                                                   public void onComplete(@NonNull Task<Void> task) {
                                                       if(task.isSuccessful()){

                                                           if(valueEventListener!=null){
                                                               finReff.removeEventListener(valueEventListener);
                                                           }
                                                           if (progressBar!=null){
                                                               progressBar.dismiss();
                                                           }
                                                           Toast.makeText(getApplicationContext(),"Deleted",Toast.LENGTH_SHORT).show();
                                                       }else {
                                                           if (progressBar!=null){
                                                               progressBar.dismiss();
                                                           }
                                                           Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                                       }
                                                   }
                                               });
                                           }else {
                                               Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                           }
                                        }
                                    });
                                }else {
                                    if (progressBar!=null){
                                        progressBar.dismiss();
                                    }
                                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (progressBar!=null){
                    progressBar.dismiss();
                }
            }
        };

        finReff.addValueEventListener(valueEventListener);
        **/

/**
        valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FinanceAccountForFireBase child=dataSnapshot.child(groceryList.getAccountid()).getValue(FinanceAccountForFireBase.class);
                FinanceAccount financeAccount=new FinanceAccount(getApplicationContext()).getFinanceAccountFromFirebase(child);

                financeAccount.setLastchangeToAccount();
                financeAccount.getAccountrecordsAmountUpdateBalance(getApplicationContext());
                DatabaseReference newRef=finRef.child(financeAccount.getAccountUniqueId());
                newRef.setValue(new FinanceAccount(getApplicationContext()).getFinanceAccountForFirebase(financeAccount))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    if(valueEventListener!=null){
                                        finRef.removeEventListener(valueEventListener);
                                    }
                                    if (progressBar!=null){
                                        progressBar.dismiss();
                                    }
                                    Toast.makeText(getApplicationContext(),"Deleted",Toast.LENGTH_SHORT).show();
                                }else {
                                    if (progressBar!=null){
                                        progressBar.dismiss();
                                    }
                                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
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
        **/

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
