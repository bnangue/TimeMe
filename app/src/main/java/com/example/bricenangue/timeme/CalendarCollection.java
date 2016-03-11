package com.example.bricenangue.timeme;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by bricenangue on 07/03/16.
 */
public class CalendarCollection implements Parcelable {
    String title, description,creator, datetime,startingtime,endingtime,hashid,category,alldayevent;

    public String date="";
    public String event_message="";

    public static ArrayList<CalendarCollection> date_collection_arr;


    public CalendarCollection(String date,String event_message){

        this.date=date;
        this.event_message=event_message;

    }


    public CalendarCollection(String title,String description,String creator,String datetime, String startingtime,String endingtime,String hashid,String category,String alldayevent){
        this.title = title;
        this.description = description;
        this.creator=creator;
        this.datetime = datetime;
        this.startingtime=startingtime;
        this.endingtime=endingtime;
        this.hashid = hashid;
        this.category=category;
        this.alldayevent=alldayevent;

    }


    private CalendarCollection(Parcel in){

        title = in.readString();
        description = in.readString();
        creator = in.readString();
        datetime =in.readString();
        startingtime=in.readString();
        endingtime=in.readString();
       category=in.readString();
        hashid =in.readString();
        alldayevent=in.readString();

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {


        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(creator);
        dest.writeString(datetime);
        dest.writeString(startingtime);
        dest.writeString(endingtime);
        dest.writeString(category);
        dest.writeString(hashid);
        dest.writeString(alldayevent);
    }
    public static final Parcelable.Creator<CalendarCollection> CREATOR = new Parcelable.Creator<CalendarCollection>() {
        public CalendarCollection createFromParcel(Parcel in) {
            return new CalendarCollection(in);
        }

        public CalendarCollection[] newArray(int size) {
            return new CalendarCollection[size];
        }
    };

}
