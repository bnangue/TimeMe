package com.app.bricenangue.timeme;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class CreateFinanceRecordsActivity extends AppCompatActivity implements View.OnClickListener,DialogFragmentDatePicker.OnDateGet {

    private EditText editTextRecordName,editTextRecordAmount,editTextRecordNote;
    private Button buttonBookingDate;
    private Spinner spinnerSelectCategory;
    private Menu menu;
    private SQLFinanceAccount sqlFinanceAccount;
    private FinanceAccount financeAccount;
    private ArrayList<FinanceAccount> accounts=new ArrayList<>();
    private UserLocalStore userLocalStore;
    private RadioGroup radioGroupexpInc;
    private RadioButton radioButtonexpenditure,radioButtonincome;
    private Spinner spinnerAccounts;

    private String [] categoryArray={"Grocery","Leisure","Traveling","Personal"};
    private String [] nameAccArray;
    private String [] idAccArray;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_finance_records);
        sqlFinanceAccount=new SQLFinanceAccount(this);
        userLocalStore=new UserLocalStore(this);
        Bundle bundle=getIntent().getExtras();
        if(bundle!=null && bundle.containsKey("AccountToUpdate")){
            accounts=bundle.getParcelableArrayList("AccountToUpdate");
        }
        editTextRecordAmount=(EditText)findViewById(R.id.editText_Create_Finance_Records_Amount);
        editTextRecordName=(EditText)findViewById(R.id.editText_activity__Create_Finance_Record_name);
        editTextRecordNote=(EditText)findViewById(R.id.editText_Create_Finance_Records_note);

        if(accounts!=null &&accounts.size()!=0){
            nameAccArray=new String[accounts.size()];
            idAccArray=new String[accounts.size()];
            for(int i=0;i<accounts.size();i++){
                nameAccArray[i]=accounts.get(i).getAccountName();
                idAccArray[i]=accounts.get(i).getAccountUniqueId();
            }
        }
        spinnerAccounts=(Spinner)findViewById(R.id.spinner_activity_Create_Finance_Record_which_account);
        if(nameAccArray!=null){
            SpinnerAdapter adapter = new ArrayAdapter<>(this, R.layout.spinnerlayout, nameAccArray);
            spinnerAccounts.setAdapter(adapter);
            spinnerAccounts.setSelection(0);
        }


        radioGroupexpInc=(RadioGroup) findViewById(R.id.activity_Create_Finance_Records_radioGroup_expenditure_income);
        radioButtonexpenditure=(RadioButton) findViewById(R.id.radiobutton_activity_Create_Finance_Records_Account_expenditure);
        radioButtonincome=(RadioButton)findViewById(R.id.radiobutton_activity_Create_Finance_Records_Account_income);


        buttonBookingDate=(Button) findViewById(R.id.button_booking_activity_Create_Finance_Record);
        spinnerSelectCategory=(Spinner) findViewById(R.id.spinner_activity_Create_Finance_Record);
        SpinnerAdapter adap = new ArrayAdapter<>(this, R.layout.spinnerlayout, categoryArray);
        spinnerSelectCategory.setAdapter(adap);

        spinnerSelectCategory.setSelection(0);
        buttonBookingDate.setOnClickListener(this);


        initializeDatePicker();
    }


    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.button_booking_activity_Create_Finance_Record){
            //call date picker
            onDatePickerclicked(true);

        }
    }

    @Override
    public void dateSet(String date, boolean isstart) {
        buttonBookingDate.setText(date);
    }

    private void initializeDatePicker(){
        final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        formatter.setLenient(false);

        Date curDate = new Date();
        String curTime = formatter.format(curDate);
        buttonBookingDate.setText(curTime);
    }
    public void onDatePickerclicked(boolean bol){
        android.support.v4.app.FragmentManager manager=getSupportFragmentManager();
        DialogFragment fragmentDatePicker=DialogFragmentDatePicker.newInstance(bol);

        fragmentDatePicker.show(manager,"datePickerfr");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu=menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_item_to_list_done, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_items_added_done) {

           String recordName = editTextRecordName.getText().toString();
            String recordAmount=editTextRecordAmount.getText().toString();
            String recordCategory=spinnerSelectCategory.getSelectedItem().toString();
            String bookingDate=buttonBookingDate.getText().toString();
            String recordNote=editTextRecordNote.getText().toString();
            Calendar c=new GregorianCalendar();
            Date dat=c.getTime();

            String dateForid= (String) android.text.format.DateFormat.format("dd-MM-yyyy HH:mm:ss", dat);

            String valueDate = dateForid.split(" ")[0];
            String creator=userLocalStore.getUserfullname();
            int hashID=(dateForid + creator).hashCode();

            if(TextUtils.isEmpty(recordName)){
                editTextRecordName.setError("this field cannot be empty");
            }else if(TextUtils.isEmpty(recordAmount)){
                editTextRecordAmount.setError("set an amount for this record");

            }else{
                //save to server
                FinanceRecords financeRecord=new FinanceRecords();
                financeRecord.setRecordNAme(recordName);
                financeRecord.setRecordCategorie(recordCategory);
                financeRecord.setRecordValueDate(valueDate);
                financeRecord.setRecordBookingDate(bookingDate);
                financeRecord.setRecordCreator(creator);
                financeRecord.setRecordUniquesId(String.valueOf(hashID));
                financeRecord.setRecordAmount(recordAmount);
                financeRecord.setRecordUpdateVersion(0);
                financeRecord.setSecured(true);
                if(radioGroupexpInc.getCheckedRadioButtonId()==R.id.radiobutton_activity_Create_Finance_Records_Account_expenditure){
                    financeRecord.setIncome(false);
                }else if(radioGroupexpInc.getCheckedRadioButtonId()==R.id.radiobutton_activity_Create_Finance_Records_Account_income) {
                    financeRecord.setIncome(true);
                }
                if(recordNote.isEmpty()){
                    financeRecord.setRecordNote("");
                }else {
                    financeRecord.setRecordNote(recordNote);
                }

                saveRecords(financeRecord);

           }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveRecords(final FinanceRecords financeRecord) {

        String recordAccount=spinnerAccounts.getSelectedItem().toString();
        int position=spinnerAccounts.getSelectedItemPosition();

        for (FinanceAccount financeAcc : accounts){

            if(financeAcc.getAccountName().equals(recordAccount)
                    && idAccArray[position].equals(financeAcc.getAccountUniqueId()) ){
                financeAccount=financeAcc;
            }
        }

        if(financeAccount!=null){

            financeAccount.addRecordToAccount(financeRecord);

            ServerRequests serverRequests=new ServerRequests(this);
            serverRequests.updateFinanceAccountInBackgroung(financeAccount, new FinanceAccountCallbacks() {
                @Override
                public void fetchDone(ArrayList<FinanceAccount> returnedAccounts) {

                }

                @Override
                public void setServerResponse(String serverResponse) {
                    if(serverResponse.contains("Account successfully updated")){
                        if(sqlFinanceAccount.updateFinanceAccount(financeAccount)!=0){
                            Toast.makeText(getApplicationContext(),"Record "+financeRecord.getRecordNAme() +" created",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(CreateFinanceRecordsActivity.this,NewCalendarActivty.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                        }else {
                            Toast.makeText(getApplicationContext(),"Error while saving "+financeRecord.getRecordNAme() +" locally",Toast.LENGTH_SHORT).show();

                        }
                    }else {
                        Toast.makeText(getApplicationContext(),"Error while saving "+financeRecord.getRecordNAme() +" on server",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


}
