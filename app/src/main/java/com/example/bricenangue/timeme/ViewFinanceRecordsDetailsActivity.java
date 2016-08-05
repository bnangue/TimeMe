package com.example.bricenangue.timeme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class ViewFinanceRecordsDetailsActivity extends AppCompatActivity {

    private FinanceRecords financeRecords;
    private EditText editTextName,editTextValueDate,editTextBookingDate,editTextCreatedBy,editTextAmount,editTextId,editTextCategory,editTextNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_finance_records_details);
        editTextAmount=(EditText)findViewById(R.id.editText_View_Finance_records_details_Amount) ;

        editTextName=(EditText)findViewById(R.id.editText_View_Finance_records_details_NAme) ;
        editTextValueDate=(EditText)findViewById(R.id._editText_View_Finance_records_details_VAlue_DAte) ;
        editTextBookingDate=(EditText)findViewById(R.id.editText_View_Finance_records_details_Booking_Date) ;
        editTextCreatedBy=(EditText)findViewById(R.id.editText_View_Finance_records_details_creator) ;
        editTextId=(EditText)findViewById(R.id.editText_View_Finance_records_ID) ;
        editTextCategory=(EditText)findViewById(R.id.editText_View_Finance_records_Categoriy) ;
        editTextNote=(EditText)findViewById(R.id.editText_View_Finance_records_note) ;

        Bundle bundle=getIntent().getExtras();
        if(bundle!=null && bundle.containsKey("financeRecord")){
            financeRecords= bundle.getParcelable("financeRecord");
        }

        populate();
    }

    private void populate(){
        if(financeRecords!=null){
            editTextName.setText(financeRecords.getRecordNAme());
            editTextValueDate.setText(financeRecords.getRecordValueDate());
            editTextBookingDate.setText(financeRecords.getRecordBookingDate());
            editTextCreatedBy.setText(financeRecords.getRecordCreator());
            String amount = financeRecords.getRecordAmount() +" €";
            editTextAmount.setText(amount);
            editTextId.setText(financeRecords.getRecordUniquesId());
            editTextCategory.setText(financeRecords.getRecordCategorie());
            editTextNote.setText(financeRecords.getRecordNote());
        }
    }
}
