package com.app.bricenangue.timeme;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    private String [] categoryArray=new String[4];
    private String [] nameAccArray;
    private String [] idAccArray;
    private android.support.v7.app.AlertDialog alertDialog;
    private FirebaseAuth auth;
    private DatabaseReference dataref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_finance_records);
        sqlFinanceAccount=new SQLFinanceAccount(this);
        userLocalStore=new UserLocalStore(this);
        auth=FirebaseAuth.getInstance();
        dataref= FirebaseDatabase.getInstance().getReference().child(Config.FIREBASE_APP_URL_FINANCE_ACCOUNTS);
        Bundle bundle=getIntent().getExtras();
        if(bundle!=null && bundle.containsKey("AccountToUpdate")){
            accounts=bundle.getParcelableArrayList("AccountToUpdate");
        }

        editTextRecordAmount=(EditText)findViewById(R.id.editText_Create_Finance_Records_Amount);
        editTextRecordName=(EditText)findViewById(R.id.editText_activity__Create_Finance_Record_name);
        editTextRecordNote=(EditText)findViewById(R.id.editText_Create_Finance_Records_note);

        if(accounts!=null &&accounts.size()!=0){
            nameAccArray=new String[accounts.size()+1];
            idAccArray=new String[accounts.size()+1];
            nameAccArray[0]=getString(R.string.Choose_An_Account);
            idAccArray[0]="0";
            for(int i=0;i<accounts.size();i++){
                nameAccArray[i+1]=accounts.get(i).getAccountName();
                idAccArray[i+1]=accounts.get(i).getAccountUniqueId();
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

        categoryArray[0]=getString(R.string.Choose_A_Category);
        categoryArray[1]="Leisure";
        categoryArray[2]="Traveling";
        categoryArray[3]="Personal";

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

    void showDialogConfirmCreationRecord(){


        String recordName = editTextRecordName.getText().toString();
        String recordAmount=editTextRecordAmount.getText().toString();
        String recordCategory=spinnerSelectCategory.getSelectedItem().toString();
        String bookingDate=buttonBookingDate.getText().toString();
        String recordNote=editTextRecordNote.getText().toString();

        alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();
        LayoutInflater inflater =  (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.confirmation_layout_create_finance_record, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle(getString(R.string.activity_create_finance_record_dialog_title));
        EditText editTextName=(EditText)convertView.findViewById(R.id.editText_View_Finance_records_details_NAme_confirm_record) ;
        EditText editTextBookingDate=(EditText)convertView.findViewById(R.id.editText_View_Finance_records_details_Booking_Date_confirm_record) ;
        EditText editTextAmount=(EditText)convertView.findViewById(R.id.editText_View_Finance_records_details_Amount_confirm_record) ;
        EditText editTextCategory=(EditText)convertView.findViewById(R.id.editText_View_Finance_records_Categoriy_confirm_record) ;
        EditText   editTextNote=(EditText)convertView.findViewById(R.id.editText_View_Finance_records_note_confirm_record) ;

        editTextName.setText(recordName);
        editTextBookingDate.setText(bookingDate);
        editTextAmount.setText(recordAmount);
        editTextCategory.setText(recordCategory);
        if(recordNote.isEmpty()){
            editTextNote.setText(getString(R.string.Text_Create_Finance_Record_Nothing_Specified));
        }else {
            editTextNote.setText(recordNote);
        }


        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.alert_dialog_changed_not_saved_shopping_list_done_buttoncancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        alertDialog.dismiss();
                    }
                });

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.alert_dialog_changed_not_saved_shopping_list_done_buttonok)
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

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
                            FinanceRecords financeRecord=new FinanceRecords(getApplicationContext());
                            financeRecord.setRecordNAme(recordName);
                            financeRecord.setRecordCategorie(recordCategory);
                            financeRecord.setRecordValueDate(valueDate);
                            financeRecord.setRecordBookingDate(bookingDate);
                            financeRecord.setRecordCreator(creator);
                            financeRecord.setRecordUniquesId(String.valueOf(hashID));
                            financeRecord.setRecordAmount(recordAmount);
                            financeRecord.setRecordUpdateVersion(0);
                            financeRecord.setSecured(true);
                            financeRecord.setIncome(radioButtonincome.isChecked());
                            if(recordNote.isEmpty()){
                                financeRecord.setRecordNote("");
                            }else {
                                financeRecord.setRecordNote(recordNote);
                            }


                            saveRecords(financeRecord);
                            alertDialog.dismiss();

                        }
                    }
                });
        alertDialog.setCancelable(true);
        alertDialog.show();
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
            String category = spinnerSelectCategory.getSelectedItem().toString();
            String accountNAme=spinnerAccounts.getSelectedItem().toString();

            if(TextUtils.isEmpty(recordName)){
                editTextRecordName.setError("this field cannot be empty");
                editTextRecordName.requestFocus();
            }else if(TextUtils.isEmpty(recordAmount)){
                editTextRecordAmount.setError("set an amount for this record");
                editTextRecordAmount.requestFocus();

            }else if(category.equals(getString(R.string.Choose_A_Category))){
                spinnerSelectCategory.requestFocus();
                spinnerSelectCategory.performClick();

            }else if(accountNAme.equals(getString(R.string.Choose_An_Account))){
                spinnerAccounts.requestFocus();
                spinnerAccounts.performClick();
            }else {
                showDialogConfirmCreationRecord();
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
                financeAccount=new FinanceAccount(this);
                financeAccount=financeAcc;
            }
        }

        if(financeAccount!=null){

            financeAccount.setContext(this);
            financeAccount.addRecordToAccount(financeRecord);
            financeAccount.setLastchangeToAccount();


            assert auth.getCurrentUser()!=null;

            DatabaseReference ref=dataref.child(auth.getCurrentUser().getUid())
                    .child(financeAccount.getAccountUniqueId());
            ref.setValue(new FinanceAccount(getApplicationContext()).getFinanceAccountForFirebase(financeAccount)).addOnCompleteListener(
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"Record "+financeRecord.getRecordNAme() +" created",Toast.LENGTH_SHORT).show();
                                finish();
                            }else {
                                Toast.makeText(getApplicationContext(),"Error while saving "+financeRecord.getRecordNAme() +" locally",Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

        }
    }


}
