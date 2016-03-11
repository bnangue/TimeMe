package com.example.bricenangue.timeme;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.CopyOnWriteArrayList;

public class MainActivity extends AppCompatActivity {
    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;
    private IncomingNotification incomingNotification;
    private MySQLiteHelper mySQLiteHelper;
    private ArrayList<CalendarCollection> currentlist;
    private ProgressBar loadprogressBar;

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if ( "WIFI".equals(ni.getTypeName()))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if ("MOBILE".equals(ni.getTypeName()))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadprogressBar=(ProgressBar)findViewById(R.id.prbar);
        loadprogressBar.setVisibility(View.VISIBLE);
        mySQLiteHelper=new MySQLiteHelper(this);
        currentlist=new ArrayList<>();
        getEvents(mySQLiteHelper.getAllIncomingNotification());
        coordinatorLayout=(CoordinatorLayout)findViewById(R.id.coordinateLayoutmainactivity);
        startapp();



    }
    void startapp(){
        if (haveNetworkConnection()){
            getEventsfromMySQL();

        }else{
            showSnackBar();
            startActivity(new Intent(MainActivity.this, LoginScreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
    }

    private void getEventsfromMySQL() {
        ServerRequests serverRequests=new ServerRequests(this);
        serverRequests.getCalenderEventInBackgroung(new GetEventsCallbacks() {
            @Override
            public void done(ArrayList<CalendarCollection> returnedeventobject) {
                if(returnedeventobject.size()!=0){

                    if(currentlist.size()!=0){
                        ArrayList<CalendarCollection> list=new ArrayList<CalendarCollection>();

                        for(int i=0;i<returnedeventobject.size();i++) {
                                for (int j = 0; j < currentlist.size(); j++) {
                                    if (currentlist.get(j).hashid.equals(returnedeventobject.get(i).hashid)) {
                                        returnedeventobject.remove(i);
                                    }
                                }
                        }

                        saveeventtoSQl(returnedeventobject);
                            loadprogressBar.setIndeterminate(false);
                            loadprogressBar.setVisibility(View.INVISIBLE);
                            startActivity(new Intent(MainActivity.this,LoginScreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));


                    }else {

                        saveeventtoSQl(returnedeventobject);
                            loadprogressBar.setIndeterminate(false);
                            loadprogressBar.setVisibility(View.INVISIBLE);
                            startActivity(new Intent(MainActivity.this,LoginScreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                    }

                }else {
                    showSnackBar();
                }
            }

            @Override
            public void updated(String reponse) {

            }
        });
    }

    private void saveeventtoSQl(ArrayList<CalendarCollection> calendarCollections) {

        if(calendarCollections.size()!=0){

        }
        for(int i=0;i<calendarCollections.size();i++){
            try {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("title",calendarCollections.get(i).title);
                jsonObject.put("description",calendarCollections.get(i).description);
                jsonObject.put("datetime",calendarCollections.get(i).datetime);
                jsonObject.put("creator",calendarCollections.get(i).creator);
                jsonObject.put("category",calendarCollections.get(i).category);
                jsonObject.put("startingtime",calendarCollections.get(i).startingtime);
                jsonObject.put("endingtime",calendarCollections.get(i).endingtime);
                jsonObject.put("hashid",calendarCollections.get(i).hashid);
                jsonObject.put("alldayevent",calendarCollections.get(i).alldayevent);
                Calendar c=new GregorianCalendar();
                Date dat=c.getTime();
                //String day= String.valueOf(dat.getDay());
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                String date = (String) android.text.format.DateFormat.format("yyyy-MM-dd", dat);
                incomingNotification=new IncomingNotification(1,0,jsonObject.toString(),date);
                int incomingNotifiId =  mySQLiteHelper.addIncomingNotification(incomingNotification);

            }catch (Exception e){
                e.printStackTrace();

            }


        }

    }


    private void getEvents(ArrayList<IncomingNotification> incomingNotifications){

        for (int i=0;i<incomingNotifications.size();i++){
            JSONObject jo_inside = null;
            try {
                jo_inside = new JSONObject(incomingNotifications.get(i).body);

                String titel = jo_inside.getString("title");
                String infotext = jo_inside.getString("description");
                String creator = jo_inside.getString("creator");
                String creationTime = jo_inside.getString("datetime");
                String category = jo_inside.getString("category");
                String startingtime = jo_inside.getString("startingtime");
                String endingtime = jo_inside.getString("endingtime");
                String alldayevent = jo_inside.getString("alldayevent");
                String eventHash = jo_inside.getString("hashid");

                CalendarCollection  object =new CalendarCollection(titel,infotext,creator,creationTime,startingtime,endingtime,eventHash,category,alldayevent);

                currentlist.add(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    public void showSnackBar(){
        snackbar = Snackbar
                .make(coordinatorLayout, "No connection internet detected", Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.colorSnackbar));
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }
}
