package com.example.bricenangue.timeme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CreateFinanceAccountActivity extends AppCompatActivity implements TextView.OnEditorActionListener {

    private Spinner userOwnerSpinner, currencySpinner;
    private EditText editTextAccOwner, editTextAccBalnace;
    private String [] curencyArray={"EUR","USD"};
    private String [] userAccArray=new String[2];
    private SQLFinanceAccount sqlFinanceAccount;
    private UserLocalStore userLocalStore;
    private FinanceAccount financeAccount=new FinanceAccount(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_finance_account);
        sqlFinanceAccount=new SQLFinanceAccount(this);
        userLocalStore=new UserLocalStore(this);

        userAccArray[0]= userLocalStore.getUserfullname();
        userAccArray[1]="no owner";
        userOwnerSpinner=(Spinner)findViewById(R.id.spinner_activity_create_finance_Account_owners);
        SpinnerAdapter adap = new ArrayAdapter<>(this, R.layout.spinnerlayout, userAccArray);
        userOwnerSpinner.setAdapter(adap);

        userOwnerSpinner.setSelection(0);
        userOwnerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(editTextAccOwner.getText().length()==0){
                    editTextAccOwner.setText(userOwnerSpinner.getSelectedItem().toString());
                }else if(userOwnerSpinner.getSelectedItem().toString().equals(userAccArray[1])) {
                    String text= userOwnerSpinner.getSelectedItem().toString();

                    editTextAccOwner.setText(text);
                }else {
                    String text=editTextAccOwner.getText().toString() +", "+
                            userOwnerSpinner.getSelectedItem().toString();

                    editTextAccOwner.setText(text);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        currencySpinner=(Spinner)findViewById(R.id.spinner_activity_create_finance_Account_currency);
        SpinnerAdapter adapcur = new ArrayAdapter<>(this, R.layout.spinnerlayout, curencyArray);
        currencySpinner.setAdapter(adapcur);

        currencySpinner.setSelection(0);
        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        editTextAccBalnace=(EditText)findViewById(R.id.editText_Create_Account_AccountBalance);
        editTextAccOwner=(EditText)findViewById(R.id.editText_Create_Account_AccountOwner);
        editTextAccBalnace.setOnEditorActionListener(this);
    }

    public void OnAccountCreatedClickced(View view){
        //create account save on serer and sql
        String accowners=editTextAccOwner.getText().toString();
        String accBalance=editTextAccBalnace.getText().toString();

        if(TextUtils.isEmpty(accowners)){
            editTextAccOwner.setError("this field cannot be empty");
        }else if(TextUtils.isEmpty(accBalance)){
            editTextAccBalnace.setError("set an amount for this record");

        }else{
            FinanceAccount financeAccount=new FinanceAccount(this);
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
            financeAccount.setAccountName(String.valueOf(hashid).replace("-","A"));
            financeAccount.setLastchangeToAccount();
            FinanceRecords financeRecords=new FinanceRecords(getString(R.string.textInitialize_create_account_record_name),dateForid.split(" ")[0],
                    getString(R.string.textInitialize_create_account_record_note),accBalance,String.valueOf(hashid),
                    getString(R.string.textInitialize_create_account_record_category),dateForid.split(" ")[0],owners.get(0),0,true,true);

            financeAccount.addRecordToAccount(financeRecords);


            this.financeAccount=financeAccount;

            saveFinanceAccountToServer(financeAccount);
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
            //create account save on serer and sql
            String accowners=editTextAccOwner.getText().toString();
            String accBalance=editTextAccBalnace.getText().toString();
            if(TextUtils.isEmpty(accowners)){
                editTextAccOwner.setError("this field cannot be empty");
            }else if(TextUtils.isEmpty(accBalance)){
                editTextAccBalnace.setError("set an amount for this record");

            }else{
                FinanceAccount financeAccount=new FinanceAccount(this);
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
                financeAccount.setAccountName(String.valueOf(hashid).replace("-","A"));
                financeAccount.setLastchangeToAccount();
                FinanceRecords financeRecords=new FinanceRecords(getString(R.string.textInitialize_create_account_record_name),dateForid.split(" ")[0],
                        getString(R.string.textInitialize_create_account_record_note),accBalance,String.valueOf(hashid),
                        getString(R.string.textInitialize_create_account_record_category),dateForid.split(" ")[0],userLocalStore.getUserfullname(),0,true,true);

                financeAccount.addRecordToAccount(financeRecords);


                this.financeAccount=financeAccount;

                saveFinanceAccountToServer(financeAccount);
            }


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
