package com.example.bricenangue.timeme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class AddNewEventActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdaterAdddNewEventListButton.OnButtonString {

    private Button button;
    private AdaterAdddNewEventListButton adapter;
    private ListView listView;
    private ArrayList<String> categorylist;
    private String categoryname;
    public static ArrayList<CalendarCollection> collections=new ArrayList<>();
    private CalendarCollection collection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_event);

        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            if(extras.containsKey("savedEvent")){
                collection=extras.getParcelable("savedEvent");
                collections.add(collection);
            }
        }
        categorylist=new ArrayList<>();
        String [] userAccArray={"Normal","Business","Birthdays","Grocery","Work Plans"};
        for(int i=0;i<userAccArray.length;i++){
            categorylist.add(userAccArray[i]);
        }
        button=(Button)findViewById(R.id.buttoncountaddedEventactivity);
        button.setText(collections.size()+ " added");
        listView=(ListView)findViewById(R.id.addeventbuttonlistview);
        adapter=new AdaterAdddNewEventListButton(this,categorylist,this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


    private void returnedCalenderEvent(ArrayList<CalendarCollection> calendarCollections) {
        Intent intent=new Intent(AddNewEventActivity.this,NewCalendarActivty.class);
        intent.putExtra("events",calendarCollections);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void buttonString(int position) {
        Intent intent=new Intent(AddNewEventActivity.this,AddNewEventFragment.class);
        intent.putExtra("category",position);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        returnedCalenderEvent(collections);
    }
}
