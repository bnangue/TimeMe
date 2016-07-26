package com.example.bricenangue.timeme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.alamkanak.weekview.WeekViewEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BasicActivity extends BaseActivity {


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        CalendarCollection.date_collection_arr=new ArrayList<>();
        Bundle extras=getIntent().getExtras();
        if(intent.getExtras()!=null && extras.containsKey("listevent")){

            CalendarCollection.date_collection_arr=extras.getParcelableArrayList("listevent");

        }
        Toast.makeText(getApplicationContext(),CalendarCollection.date_collection_arr.size(),Toast.LENGTH_SHORT).show();
    }



    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();

        Calendar startTime;

        Calendar endTime;

        WeekViewEvent event;

        CalendarCollection collection;
        for (int i=0;i<CalendarCollection.date_collection_arr.size();i++){
            collection=CalendarCollection.date_collection_arr.get(i);
            String[]startdatetime=collection.startingtime.split(" ");
            String[] sdatet=startdatetime[0].split("-");
            String[] stimet;
            if(startdatetime[1].isEmpty()){
                stimet=startdatetime[2].split(":");
            }else {
                stimet=startdatetime[1].split(":");
            }

            String sdday=sdatet[2];
            String sdmounth=sdatet[1];
            if(sdmounth.startsWith("0")){
                sdmounth=sdmounth.substring(1);
            }
            int m=Integer.parseInt(sdmounth);
            String sdyear=sdatet[0];
            int y=Integer.parseInt(sdyear);
            String sthour=stimet[0];
            String sminute=stimet[1];

            String[]enddatetime=collection.endingtime.split(" ");
            String[] edatet=enddatetime[0].split("-");
            String[] etimet;
            if(enddatetime[1].isEmpty()){
                etimet =enddatetime[2].split(":");
            }else {
                etimet=enddatetime[1].split(":");
            }

            String edday=edatet[2];
            String edmounth=edatet[1];
            if(edmounth.startsWith("0")){
                edmounth=edmounth.substring(1);
            }
            String edyear=edatet[0];
            String ethour=etimet[0];
            String eminute=etimet[1];
            newYear=y;
            newMonth=m;

            startTime = Calendar.getInstance();
            startTime.set(Calendar.HOUR_OF_DAY, i+3);
            startTime.set(Calendar.MINUTE, 0);
            startTime.set(Calendar.MONTH, newMonth-1);
            startTime.set(Calendar.YEAR, Integer.parseInt(edyear));
            endTime = (Calendar) startTime.clone();
            endTime.add(Calendar.HOUR_OF_DAY, 5+i);
            endTime.set(Calendar.MINUTE, 20);


           WeekViewEvent eevent = new WeekViewEvent(i+1,collection.title,Integer.parseInt(sdyear),Integer.parseInt(sdmounth),
                    Integer.parseInt(sdday),Integer.parseInt(sthour),Integer.parseInt(sminute),
                    Integer.parseInt(edyear),Integer.parseInt(edmounth),Integer.parseInt(edday),Integer.parseInt(ethour),
                    Integer.parseInt(eminute));

            event=new WeekViewEvent(i+1,collection.title,startTime,endTime);

           int ij= event.getStartTime().get(Calendar.MONTH);
            if(ij +1 ==newMonth
                    && event.getStartTime().get(Calendar.YEAR)==newYear){
                switch (collection.category){
                    case "Normal":
                        event.setColor(getResources().getColor(R.color.event_color_01));
                        events.add(event);
                        break;
                    case "Business":
                        event.setColor(getResources().getColor(R.color.event_color_02));
                        events.add(event);
                        break;
                    case "Birthdays":
                        event.setColor(getResources().getColor(R.color.event_color_03));
                        events.add(event);
                        break;
                    case "Grocery":
                        event.setColor(getResources().getColor(R.color.event_color_04));
                        events.add(event);
                        break;
                    case "Work Plans":
                        event.setColor(getResources().getColor(R.color.event_color_05));
                        events.add(event);
                        break;
                }
            }


        }


        return events;
    }

}
