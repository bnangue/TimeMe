package com.example.bricenangue.timeme;


import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddNewEventFragment extends AppCompatActivity implements DialogFragmentDatePicker.OnDateGet,DialogFragmentTimePicker.OnTimeSet, View.OnClickListener {
    private String dateSet="";
    private CalendarCollection calendarCollection;
    private  EditText dateed,titleed;
    private Button btnstartdate,btnstarttime, btnenddate,btnendtime;
    private CheckBox checkboxadd;
    private Spinner spinner;
    private UserLocalStore userLocalStore;
    int categoryname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_new_events);

        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            categoryname=extras.getInt("category");
        }

        userLocalStore=new UserLocalStore(this);
        final EditText descriptioned=(EditText)findViewById(R.id.eddescription);
        titleed=(EditText)findViewById(R.id.edtitle);
        btnstartdate=(Button)findViewById(R.id.dateaddeventstart);
        btnstarttime=(Button)findViewById(R.id.timeaddeventstart);
        btnenddate=(Button)findViewById(R.id.dateaddeventend);
        btnendtime=(Button)findViewById(R.id.timeaddeventend);
        checkboxadd=(CheckBox)findViewById(R.id.checkboxalldayevent);
        spinner=(Spinner)findViewById(R.id.edspinnercategory);
        btnstartdate.setOnClickListener(this);
        btnstarttime.setOnClickListener(this);
        btnenddate.setOnClickListener(this);
        btnendtime.setOnClickListener(this);

        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd, HH:mm");
        formatter.setLenient(false);

        Date curDate = new Date();
        String curTime = formatter.format(curDate);
        String dgdh[]=curTime.split(",");
        String [] userAccArray={"Normal","Business","Birthdays","Grocery","Work Plans"};
        SpinnerAdapter adap = new ArrayAdapter<>(this, R.layout.spinnerlayout, userAccArray);
        spinner.setAdapter(adap);
        spinner.setSelection(categoryname);

        btnstartdate.setText(dgdh[0]);
        btnstarttime.setText(dgdh[1]);
        btnenddate.setText(dgdh[0]);
        btnendtime.setText(dgdh[1]);

        Button savebtn=(Button)findViewById(R.id.buttonsavenewevent);
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String allday;
                if(checkboxadd.isChecked()){
                    allday="1";
                }else {
                    allday="0";
                }
                Calendar c = Calendar.getInstance();

                int eventHashcode=(dateSet+userLocalStore.getUserfullname()+formatter.format(c.getTime())).hashCode();


                dateSet=titleed.getText().toString();
                calendarCollection=new CalendarCollection(dateSet,descriptioned.getText().toString(),
                        userLocalStore.getUserfullname(),btnstartdate.getText().toString(),btnstartdate.getText().toString(),
                        btnenddate.getText().toString(),String.valueOf(eventHashcode),spinner.getSelectedItem().toString(),allday);
                saveEvent(calendarCollection);

            }
        });
    }


   void saveEvent(final CalendarCollection collection){
       ServerRequests serverRequests=new ServerRequests(this);
       serverRequests.saveCalenderEventInBackgroung(collection, new GetEventsCallbacks() {
           @Override
           public void done(ArrayList<CalendarCollection> returnedeventobject) {

           }

           @Override
           public void updated(String reponse) {
               if (reponse.contains("Event added successfully")) {
                   Intent intent=new Intent(AddNewEventFragment.this,AddNewEventActivity.class);
                   intent.putExtra("savedEvent",collection);
                   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                   startActivity(intent);
               }
           }
       });

   }
    @Override
    public void dateSet(String date,boolean isstart) {

        if(isstart){
            btnstartdate.setText(date);
            btnenddate.setText(date);

        }else {
            btnenddate.setText(date);
        }
    }

    @Override
    public void timeSet(String time,boolean isstarttime) {
        if(isstarttime){
            btnstarttime.setText(time);
        }else {
            btnendtime.setText(time);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dateaddeventstart:
                onDatePickercliced(true);
                break;
            case R.id.timeaddeventstart:
                onTimePickercliced(true);
                break;
            case R.id.dateaddeventend:
                onDatePickercliced(false);
                break;
            case R.id.timeaddeventend:
                onTimePickercliced(false);
                break;
        }
    }


    public void onTimePickercliced(boolean bol){
        android.support.v4.app.FragmentManager manager=getSupportFragmentManager();
        DialogFragment fragmentDatePicker=DialogFragmentTimePicker.newInstance(bol);
        fragmentDatePicker.show(manager,"timePickerfr");
    }

    public void onDatePickercliced(boolean bol){
        android.support.v4.app.FragmentManager manager=getSupportFragmentManager();
        DialogFragment fragmentDatePicker=DialogFragmentDatePicker.newInstance(bol);

        fragmentDatePicker.show(manager,"datePickerfr");
    }



}
