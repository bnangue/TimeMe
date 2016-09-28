package com.app.bricenangue.timeme;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CreateFinanceAccountActivity extends AppCompatActivity implements TextView.OnEditorActionListener {

    private Spinner userOwnerSpinner, currencySpinner;
    private EditText editTextAccOwner, editTextAccBalnace,etEditTextAccName;
    private String [] curencyArray={"EUR","USD"};
    private String [] userAccArray=new String[2];
    private SQLFinanceAccount sqlFinanceAccount;
    private UserLocalStore userLocalStore;
    private FinanceAccount financeAccount=new FinanceAccount(this);
    private String ownerName;
    private DatabaseReference databaseReferenceToAccounts;
    private DatabaseReference databaseReferenceToUsers;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_finance_account);
        auth= FirebaseAuth.getInstance();
        databaseReferenceToAccounts= FirebaseDatabase.getInstance().getReference().child(Config.FIREBASE_APP_URL_FINANCE_ACCOUNTS);
        databaseReferenceToUsers=FirebaseDatabase.getInstance().getReference().child(Config.FIREBASE_APP_URL_USERS);
        sqlFinanceAccount=new SQLFinanceAccount(this);
        userLocalStore=new UserLocalStore(this);

        editTextAccBalnace=(EditText)findViewById(R.id.editText_Create_Account_AccountBalance);
        editTextAccOwner=(EditText)findViewById(R.id.editText_Create_Account_AccountOwner);
        etEditTextAccName=(EditText)findViewById(R.id.editText_Create_Account_AccountName);

        etEditTextAccName.setOnEditorActionListener(this);
        userAccArray[0]= userLocalStore.getUserfullname();
        userAccArray[1]="no owner";
        userOwnerSpinner=(Spinner)findViewById(R.id.spinner_activity_create_finance_Account_owners);
        SpinnerAdapter adap = new ArrayAdapter<>(this, R.layout.spinnerlayout, userAccArray);
        userOwnerSpinner.setAdapter(adap);



        currencySpinner=(Spinner)findViewById(R.id.spinner_activity_create_finance_Account_currency);
        SpinnerAdapter adapcur = new ArrayAdapter<>(this, R.layout.spinnerlayout, curencyArray);
        currencySpinner.setAdapter(adapcur);
       // userOwnerSpinner.setSelection(0);

        if(savedInstanceState!=null){
            ownerName=savedInstanceState.getString("ownerName");
            editTextAccOwner.setText(ownerName);
        }
        userOwnerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(editTextAccOwner.getText().length()==0){
                    editTextAccOwner.setText(userOwnerSpinner.getSelectedItem().toString());
                    ownerName=editTextAccOwner.getText().toString();
                }else if(userOwnerSpinner.getSelectedItem().toString().equals(userAccArray[1])) {
                    String text= userOwnerSpinner.getSelectedItem().toString();

                    editTextAccOwner.setText(text);
                    ownerName=editTextAccOwner.getText().toString();
                }else if(!editTextAccOwner.getText().toString().equals(userAccArray[1])&&
                        !editTextAccOwner.getText().toString().contains(userOwnerSpinner.getSelectedItem().toString())){
                    String text=editTextAccOwner.getText().toString() +", "+
                            userOwnerSpinner.getSelectedItem().toString();

                    editTextAccOwner.setText(text);
                    ownerName=editTextAccOwner.getText().toString();
                }else {
                    editTextAccOwner.setText(userOwnerSpinner.getSelectedItem().toString());
                    ownerName=editTextAccOwner.getText().toString();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("ownerName",ownerName);
    }

    public void OnAccountCreatedClickced(View view){
      createAccount();
    }

    private void createAccount(){
        //create account save on serer and sql
        String accowners=editTextAccOwner.getText().toString();
        String accBalance=editTextAccBalnace.getText().toString();
        String accNAme=etEditTextAccName.getText().toString();

        if(TextUtils.isEmpty(accowners)){
            editTextAccOwner.setError("this field cannot be empty");
            editTextAccOwner.requestFocus();
        }else if(TextUtils.isEmpty(accBalance)){
            editTextAccBalnace.setError("set an amount for this record");
            editTextAccBalnace.requestFocus();

        }else{
            final FinanceAccount financeAccount=new FinanceAccount(this);
            financeAccount.setAccountOwnersToString(accowners);
            // financeAccount.setAccountBalanceToString(accBalance);
            ArrayList<String> owners=new ArrayList<>();
            owners.add(accowners);

            financeAccount.setAccountOwners(owners);
            //financeAccount.setAccountBalanceToString("");


            Calendar c=new GregorianCalendar();
            Date dat=c.getTime();

            String dateForid= (String) android.text.format.DateFormat.format("dd-MM-yyyy HH:mm:ss", dat);

            int hashid=( dateForid).hashCode();
            financeAccount.setAccountUniqueId(String.valueOf(hashid));
            if(accNAme.isEmpty()){
                financeAccount.setAccountName(String.valueOf(hashid).replace("-","A"));
            }else {
                financeAccount.setAccountName(accNAme);
            }

            financeAccount.setLastchangeToAccount();
            int hash=( dateForid +"Initial Records").hashCode();
            final FinanceRecords financeRecords=new FinanceRecords(this,getString(R.string.textInitialize_create_account_record_name),dateForid.split(" ")[0],
                    getString(R.string.textInitialize_create_account_record_note),accBalance,String.valueOf(hash),
                    getString(R.string.textInitialize_create_account_record_category),dateForid.split(" ")[0],owners.get(0),0,true,true);



            financeAccount.addRecordToAccount(financeRecords);

            this.financeAccount=financeAccount;
           User user= userLocalStore.getLoggedInUser();
            user.status=1;
            user.regId=userLocalStore.getUserRegistrationId();
            user.friendlist=userLocalStore.getUserfriendliststring();
            user.pictureurl=userLocalStore.getUserPicturePath();
            UserForFireBase userForFireBase=new User().getUserForFireBase(user);
            assert auth.getCurrentUser()!=null;
           final DatabaseReference data= databaseReferenceToAccounts.child(auth.getCurrentUser().getUid()).child(financeAccount.getAccountUniqueId());
            FinanceAccountForFireBase financeAccountForFireBase=new FinanceAccount(this).getFinanceAccountForFirebase(financeAccount);
            ArrayList<UserForFireBase> arrayList=new ArrayList<>();
            arrayList.add(userForFireBase);
            financeAccountForFireBase.setAccountOwners(arrayList);
            data.setValue(financeAccountForFireBase)
            .addOnCompleteListener(CreateFinanceAccountActivity.this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        finish();
                        /**
                        DatabaseReference ref =data.child(Config.FIREBASE_APP_URL_FINANCE_ACCOUNTS_RECORDS)
                                .child(financeRecords.getRecordUniquesId());
                        ref.setValue(new FinanceRecords(getApplicationContext()).getRecordsForFirebase(financeRecords))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                }else {
                                    Toast.makeText(getApplicationContext(),task.getException().getMessage()
                                            ,Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        **/
                    }else {
                        Toast.makeText(getApplicationContext(),task.getException().getMessage()
                                ,Toast.LENGTH_SHORT).show();
                    }
                }
            });

           // saveFinanceAccountToServer(financeAccount);
        }


    }
    public void saveFinanceAccountToServer(final FinanceAccount financeAccount){
        ServerRequests serverRequests =new ServerRequests(this);
        serverRequests.saveFinanceAccountInBackgroung(financeAccount, new FinanceAccountCallbacks() {
            @Override
            public void fetchDone(ArrayList<FinanceAccount> returnedAccounts) {

            }

            @Override
            public void setServerResponse(String serverResponse) {

                if(serverResponse.contains("Account added successfully")){
                    saveFinanceAccountLocally(financeAccount);
                }else {
                    Toast.makeText(getApplicationContext(),"Error creating account "+ financeAccount.getAccountName(),Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
         createAccount();


        }

        return false;
    }
    public void saveFinanceAccountLocally(FinanceAccount financeAccount){

        if (sqlFinanceAccount.addFINANCEACCOUNT(financeAccount)!=0){
            Toast.makeText(this,"Account "+ financeAccount.getAccountName()+ " created",Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(CreateFinanceAccountActivity.this,NewCalendarActivty.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}
