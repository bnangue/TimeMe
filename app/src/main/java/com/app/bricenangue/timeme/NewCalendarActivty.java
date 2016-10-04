package com.app.bricenangue.timeme;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.CalendarDayEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bricenangue on 27/02/16.
 */
public class NewCalendarActivty extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, View.OnClickListener
        ,DialogLogoutFragment.YesNoListenerDeleteAccount,OnCalendarEventsChanged{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */



    private ProgressDialog progressBar;
    ViewPager viewPager;
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private SQLiteShoppingList sqLiteShoppingList;

    private FragmentOverview fragmentOverview;
    private FragmentCategoryFinance fragmentCategoryFinance;
    private FragmentMyEvent fragmentMyEvent;
    private FragmentCategoryShopping fragmentCategoryShopping;

    private AppBarLayout mAppBarLayout;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy", /*Locale.getDefault()*/Locale.ENGLISH);

    private CompactCalendarView mCompactCalendarView;

    private boolean isExpanded = false;
    public static boolean eventHaschanged=false;
    private float mCurrentRotation = 360.0f;
    private String datestr;
    private FloatingActionButton fab;
    private ListView lv_android;
    private AndroidListAdapter list_adapter;
    String d;
   private ArrayList<CalendarCollection> allnewEvents=new ArrayList<>();
    ArrayList<CalendarCollection> arrayListcollections=new ArrayList<>();
    private MySQLiteHelper mySQLiteHelper;
    private UserLocalStore userLocalStore;
    public static ArrayList<CalendarCollection> calendarCollectionArrayList;
    public FragmentCommunicator fragmentCommunicator;

    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    private  String chatRoomName;



    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private int pagePosition;
    private GroceryList groceryListTodelete;
    private int positionToDelete=0;
    private ArrayList<GroceryList> groceryListsToSend=new ArrayList<>();
    private int countout=1;
    private ArrayList<String> chatRoomsArraylist=new ArrayList<>();
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private Firebase firebase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_sheet_act_test);
        auth=FirebaseAuth.getInstance();
        userLocalStore=new UserLocalStore(this);

        mySQLiteHelper=new MySQLiteHelper(this);
         fragmentOverview=new FragmentOverview();
       fragmentCategoryFinance=new FragmentCategoryFinance();
  fragmentMyEvent=new FragmentMyEvent();
         fragmentCategoryShopping=new FragmentCategoryShopping();

        progressBar=new ProgressDialog(this);

        databaseReference=FirebaseDatabase.getInstance().getReference().child(Config.FIREBASE_APP_URL_USERS);
        firebase=new Firebase(Config.FIREBASE_APP_URL);

        sqLiteShoppingList=new SQLiteShoppingList(this);

        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            if(extras.containsKey("events")){
                allnewEvents=extras.getParcelableArrayList("events");


            }
        }




        calendarCollectionArrayList=new ArrayList<>();
        ArrayList<IncomingNotification> arrayList=new ArrayList<>();
        arrayList=mySQLiteHelper.getAllIncomingNotification();

        getEvents(arrayList);


        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        Toolbar toolbar = (Toolbar) findViewById(R.id.ttoolbar);
        setSupportActionBar(toolbar);


        fab=(FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(this);
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);

        // Set up the CompactCalendarView
        mCompactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        mCompactCalendarView.drawSmallIndicatorForEvents(false);

        // Force English
        mCompactCalendarView.setLocale(/*Locale.getDefault()*/Locale.ENGLISH);

        mCompactCalendarView.setShouldDrawDaysHeader(true);

        mCompactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                setSubtitle(dateFormat.format(dateClicked));
                String d = dateFormat.format(dateClicked);
                Toast.makeText(getApplicationContext(), "You have event on this date: " + d, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                setSubtitle(dateFormat.format(firstDayOfNewMonth));
            }
        });

        if(allnewEvents.size()!=0){
            for (int i=0; i<allnewEvents.size();i++){

                setEventinCalendar(allnewEvents.get(i));

            }
        }
        if(arrayListcollections.size()!=0){
            for (int i=0; i<arrayListcollections.size();i++){

                setEventinCalendar(arrayListcollections.get(i));


            }

        }

        fab.setVisibility(View.GONE);

        initViewPagerAndTabs();
        // Set current date to today
        datestr=setCurrentDate(new Date());
        d =setCurrentDate(new Date());

        final ImageView arrow = (ImageView) findViewById(R.id.date_picker_arrow);

        RelativeLayout datePickerButton = (RelativeLayout) findViewById(R.id.date_picker_button);

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded) {
                    RotateAnimation anim = new RotateAnimation(mCurrentRotation, mCurrentRotation + 180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    mCurrentRotation = (mCurrentRotation + 180.0f) % 360.0f;
                    anim.setInterpolator(new LinearInterpolator());
                    anim.setFillAfter(true);
                    anim.setFillEnabled(true);
                    anim.setDuration(300);
                    arrow.startAnimation(anim);
                    mAppBarLayout.setExpanded(false, true);
                    isExpanded = false;
                } else {
                    RotateAnimation anim = new RotateAnimation(mCurrentRotation, mCurrentRotation - 180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    mCurrentRotation = (mCurrentRotation - 180.0f) % 360.0f;
                    anim.setInterpolator(new LinearInterpolator());
                    anim.setFillAfter(true);
                    anim.setFillEnabled(true);
                    anim.setDuration(300);
                    arrow.startAnimation(anim);
                    mAppBarLayout.setExpanded(true, true);
                    isExpanded = true;
                }
            }
        });

        chatRoomName=userLocalStore.getChatRoom();


    }


// create chatRommAndShare hwile inviting

    private void initViewPagerAndTabs() {

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(1);
        SlidingTabLayout tabLayout = (SlidingTabLayout) findViewById(R.id.tabLayout);

        final ViewPagerAdapter pagerAdapter=new ViewPagerAdapter(this,getSupportFragmentManager());

        pagerAdapter.addFragment(fragmentOverview,getString(R.string.ViewPager_Fragment_FRAGMENTOVERVIEW));
        pagerAdapter.addFragment(fragmentMyEvent,getString(R.string.ViewPager_Fragment_FRAGMENTEVENTS));
        pagerAdapter.addFragment(fragmentCategoryFinance,getString(R.string.ViewPager_Fragment_FRAGMENTFINANCE));
        pagerAdapter.addFragment(fragmentCategoryShopping,getString(R.string.ViewPager_Fragment_FRAGMENTSHOPPING));

        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int currentposition =0;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


                if(position==2 ){
                   // fragmentCategoryFinance.onResume();

                    fab.setVisibility(View.GONE);
                }else if(position==3){
                   // fragmentCategoryShopping.onResume();

                    fab.setVisibility(View.GONE);
                }else if(position==1){
                  //  fragmentMyEvent.onResume();

                    fab.setVisibility(View.VISIBLE);
                }else {
                   // fragmentOverview.onResume();

                    fab.setVisibility(View.GONE);
                }




                ArrayList<IncomingNotification> arrayList=new ArrayList<>();
                arrayList=mySQLiteHelper.getAllIncomingNotification();

                if(fragmentCommunicator != null)
                    fragmentCommunicator.passDataToFragment(getCalendarEvents(arrayList));

                //Toast.makeText(getApplicationContext(),"this "+ String.valueOf(getCalendarEvents(arrayList).size()),Toast.LENGTH_SHORT).show();
               currentposition=position;


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        tabLayout.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                switch (position){

                    case 0:
                        return getResources().getColor(R.color.normal);
                    case 1:
                        return getResources().getColor(R.color.event_color_01);

                    case 2:
                        return getResources().getColor(R.color.business);

                    case 3:
                        return getResources().getColor(R.color.grocery);
                    default:return getResources().getColor(R.color.tabsScrollColor);

                }

            }
        });
        tabLayout.setViewPager(viewPager);
    }

    @Override
    public void eventsCahnged(boolean haschanged) {
    }


    public interface YourFragmentInterface {
        void fragmentBecameVisible();
    }

    public String setCurrentDate(Date date) {
        setSubtitle(dateFormat.format(date));
        if (mCompactCalendarView != null) {
            mCompactCalendarView.setCurrentDate(date);
        }
        return dateFormat.format(date);
    }

    @Override
    public void setTitle(CharSequence title) {
        TextView tvTitle = (TextView) findViewById(R.id.title);

        if (tvTitle != null) {
            tvTitle.setText(title);
        }
    }

    public void setSubtitle(String subtitle) {
        TextView datePickerTextView = (TextView) findViewById(R.id.date_picker_text_view);

        if (datePickerTextView != null) {
            datePickerTextView.setText(subtitle);
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments


        switch (position){
            case 0:

                break;
            case 1:
                startActivity(new Intent(NewCalendarActivty.this, BaseActivity.class));
                break;
            case 2:
                checkifHasFriend();
                break;
            case 3:
                startActivity(new Intent(NewCalendarActivty.this, PreferenceAppActivity.class));
                break;
            case 4:
                break;

        }


    }


    private void checkifHasFriend(){
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Config.FIREBASE_APP_URL_USERS)
                .child(auth.getCurrentUser().getUid()).child(Config.FIREBASE_APP_URL_USERS_privateProfileInfo);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("friendlist") &&
                        !dataSnapshot.child("friendlist").getValue(String.class).isEmpty()){
                    final DatabaseReference friendRef=FirebaseDatabase.getInstance().getReference().child(Config.FIREBASE_APP_URL_USERS)
                            .child(dataSnapshot.child("friendlist").getValue(String.class))
                            .child(Config.FIREBASE_APP_URL_USERS_publicProfilInfos);
                    friendRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            PublicInfos publicInfos=dataSnapshot.getValue(PublicInfos.class);
                            Intent intent =new Intent(NewCalendarActivty.this,TimeMeChatActivity.class);

                            intent.putExtra("Chatroom",chatRoomName);
                            intent.putExtra("chatPartnerName",publicInfos.getFirstname()+" "+publicInfos.getLastname());
                            intent.putExtra("chatPartnerPicURL",publicInfos.getPicturefirebaseUrl());


                            startActivity(intent);
                            friendRef.removeEventListener(this);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                   ref.removeEventListener(this);
                }else {
                  //no friends
                    startActivity(new Intent(NewCalendarActivty.this,InviteFriendActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void fetchUserforFCMNotification(String email){
        ServerRequests serverRequests =new ServerRequests(this);
        serverRequests.fetchUserForFCMNotificationinBackground(email, new GetUserCallbacks() {
            @Override
            public void done(final User returneduser) {
                if(returneduser!=null){
                    userLocalStore.storeUserPartnerData(returneduser);
                    userLocalStore.setUserPartnerRegId(returneduser.regId);
                    if(returneduser.picture!=null){
                        userLocalStore.setUserPartnerPicturePath(userLocalStore.savePartnerPicToInternalStorage(returneduser.picture));
                    }
                    String recievername= returneduser.getfullname();
                    String reciverregId= returneduser.regId;



                    Intent intent =new Intent(NewCalendarActivty.this,TimeMeChatActivity.class);


                    intent.putExtra("Chatroom",chatRoomName);
                    intent.putExtra("chatPartner",returneduser);

                    startActivity(intent);


              /**
                    if(chatRoomName == null || (chatRoomName.isEmpty())){
                        Map<String,Object> map=new HashMap<String, Object>();

                        chatRoomName=userLocalStore.getLoggedInUser().email.replace(".","-")+returneduser.email.replace(".","-");
                        map.put(chatRoomName ,"");
                        root.updateChildren(map);

                        Intent intent =new Intent(NewCalendarActivty.this,TimeMeChatActivity.class);

                        intent.putExtra("Chatroom",chatRoomName);
                        intent.putExtra("chatPartner",returneduser);

                        startActivity(intent);
                    }else {
               chatRoomName=userLocalStore.getChatRoom();
               intent.putExtra("Chatroom",chatRoomName);
               intent.putExtra("chatPartner",returneduser);

               startActivity(intent);

                    }

**/

                }else{
                    Toast.makeText(getApplicationContext(),"Error fetching partner data ",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(NewCalendarActivty.this,InviteFriendActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }

            @Override
            public void serverReponse(String reponse) {

            }

            @Override
            public void userlist(ArrayList<User> reponse) {

            }
        });
    }


   /** public  void sendFriendChatRoom(final String chatRoom) {

        if (!userLocalStore.getUserRegistrationId().isEmpty()) {


            Thread thread = new Thread() {
                @Override
                public void run() {
                    HttpURLConnection conn=null;
                    try {

                        String regid = userLocalStore.getUserRegistrationId();
                        String sendertname=userLocalStore.getLoggedInUser().getfullname();
                        ArrayList<Pair<String,String>> data=new ArrayList<>();


                        data.add(new Pair<String, String>("message", "New chat request from " +sendertname));
                        data.add(new Pair<String, String>("registrationReceiverIDs", userLocalStore.getUserPartnerRegId()));
                        data.add(new Pair<String, String>("receiver", userLocalStore.getLoggedInUser().email));
                        data.add(new Pair<String, String>("sender", sendertname));

                        data.add(new Pair<String, String>("registrationSenderIDs", regid));
                        data.add(new Pair<String, String>("title",sendertname +" invited you to chat." ));
                        data.add(new Pair<String, String>("chatRoom",chatRoom ));

                        data.add(new Pair<String, String>("apiKey", Config.FIREBASESERVER_KEY));

                        byte[] bytes = getData(data).getBytes("UTF-8");


                        URL url=new URL(Config.YOUR_SERVER_URL+ "FireBaseConnection.php");
                        conn=(HttpURLConnection)url.openConnection();
                        conn.setDoOutput(true);
                        conn.setUseCaches(false);
                        conn.setFixedLengthStreamingMode(bytes.length);
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type",
                                "application/x-www-form-urlencoded;charset=UTF-8");
                        // post the request
                        OutputStream out = conn.getOutputStream();
                        out.write(bytes);
                        out.close();

                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(conn.getInputStream()));
                        String inputLine;
                        StringBuffer reponse = new StringBuffer();

                        while ((inputLine = in.readLine()) != null) {
                            reponse.append(inputLine);
                        }
                        final String response =reponse.toString();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        if(conn!=null){
                            conn.disconnect();
                        }
                    }
                }
            };

            thread.start();

        }

    }

    **/
    private static String getData(ArrayList<Pair<String, String>> values) throws UnsupportedEncodingException {
        StringBuilder result=new StringBuilder();
        for(Pair<String,String> pair : values){

            if(result.length()!=0)

                result.append("&");
            result.append(URLEncoder.encode(pair.first, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.second, "UTF-8"));

        }
        return result.toString();
    }
    protected void sendSMS() {
        Log.i("Send SMS", "");
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);

        smsIntent.setData(Uri.parse("smsto:"));
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address"  , "+4917665916982");
        smsIntent.putExtra("sms_body"  , "Test ");

        try {
            startActivity(smsIntent);
            finish();
            Log.i("Finished sending SMS...", "");
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(NewCalendarActivty.this,
                    "SMS faild, please try again later.", Toast.LENGTH_SHORT).show();
        }
    }


    protected void sendSMSMessage() {



        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("+4917665916982", null, "message from android test", null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS faild, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onNavigationDrawersubItemSelected(int position) {
        switch (position){
            case 0:
                viewPager.setCurrentItem(0,true);
                break;
            case 1:
                viewPager.setCurrentItem(1,true);
                break;
            case 2:
                viewPager.setCurrentItem(2,true);
                break;
            case 3:
                viewPager.setCurrentItem(3, true);
                break;


            default:viewPager.setCurrentItem(0,true);

        }

    }




    private void getEvents(ArrayList<IncomingNotification> incomingNotifications){

        CalendarCollection.date_collection_arr=new ArrayList<>();
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
                String everymonth = jo_inside.getString("everymonth");
                String creationdatetime = jo_inside.getString("defaulttime");

                CalendarCollection  object =new CalendarCollection(titel,infotext,creator,creationTime,startingtime,endingtime,eventHash,category,alldayevent,everymonth,creationdatetime);
                object.incomingnotifictionid = incomingNotifications.get(i).id;
                CalendarCollection.date_collection_arr.add(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    private ArrayList<CalendarCollection> getCalendarEvents(ArrayList<IncomingNotification> incomingNotifications){

        ArrayList<CalendarCollection> a =new ArrayList<>();
        for (int i=0;i<incomingNotifications.size();i++){
            JSONObject jo_inside = null;
            if(incomingNotifications.get(i).type==1){
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
                    String everymonth = jo_inside.getString("everymonth");
                    String creationdatetime = jo_inside.getString("defaulttime");

                    CalendarCollection  object =new CalendarCollection(titel,infotext,creator,creationTime,startingtime,endingtime,eventHash,category,alldayevent,everymonth,creationdatetime);
                    object.incomingnotifictionid = incomingNotifications.get(i).id;
                    a.add(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
        return a;
    }

    void setEventinCalendar(CalendarCollection calendarCollection){

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd, HH:mm");
        formatter.setLenient(false);

        Date curDate = new Date();
        long curMillis = curDate.getTime();
        String curTime = formatter.format(curDate);
        String dgdh[]=curTime.split(",");


        String oldTime = calendarCollection.datetime +","+dgdh[1];
        Date oldDate = null;
        try {
            oldDate = formatter.parse(oldTime);
            long oldMillis = oldDate.getTime();
            switch (calendarCollection.category){
                case "Normal":
                    mCompactCalendarView.addEvent(new CalendarDayEvent(oldMillis,getResources().getColor(R.color.normal)),false);

                    break;
                case "Business":
                    mCompactCalendarView.addEvent(new CalendarDayEvent(oldMillis,getResources().getColor(R.color.business)),false);

                    break;
                case "Birthdays":
                    mCompactCalendarView.addEvent(new CalendarDayEvent(oldMillis,getResources().getColor(R.color.birthdays)),false);

                    break;
                case "Grocery":
                    mCompactCalendarView.addEvent(new CalendarDayEvent(oldMillis,getResources().getColor(R.color.grocery)),false);

                    break;
                case "Work Plans":
                    mCompactCalendarView.addEvent(new CalendarDayEvent(oldMillis,getResources().getColor(R.color.workPlans)),false);

                    break;
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
        }
        setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_logout) {
            DialogFragment dialogFragment=new DialogLogoutFragment();
            dialogFragment.setCancelable(false);
            dialogFragment.show(getSupportFragmentManager(), "LOGOUTNewCalendarActivityFRAGMENTSettings");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
       // d =setCurrentDate(new Date());
       // FragmentManager fragmentManager = getSupportFragmentManager();
       // fragmentManager.beginTransaction()
        //        .replace(R.id.container, new AddNewEventFragment())
        //        .commit();

        startActivity(new Intent(NewCalendarActivty.this, AddNewEventActivity.class));
    }


    @Override
    public void onBackPressed() {

        if((countout%2)==0){
            DialogFragment dialogFragment=new DialogLogoutFragment();
            dialogFragment.setCancelable(false);
            dialogFragment.show(getSupportFragmentManager(), "LOGOUTNewCalendarActivityFRAGMENT");
        }else {
            countout++;

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {

                public void run() {
                    countout =1;

                }

            }, 3000);

            Toast.makeText(getApplicationContext(), getString(R.string.PressSecondTimeToCloseApp),Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onYes() {
        progressBar.setCancelable(false);
        progressBar.setTitle("Logging out");
        progressBar.setMessage("in progress ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();
        if(auth.getCurrentUser()!=null){ //user log in
          DatabaseReference  data=databaseReference.child(auth.getCurrentUser().getUid())
                  .child(Config.FIREBASE_APP_URL_USERS_privateProfileInfo);
            data.child("status").setValue(0).addOnCompleteListener(NewCalendarActivty.this
            ,new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        auth.signOut();
                        FirebaseAuth.AuthStateListener authStateListener= new FirebaseAuth.AuthStateListener() {
                            @Override
                            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                                FirebaseUser user=firebaseAuth.getCurrentUser();
                                if(user==null){
                                    userLocalStore.clearUserData();
                                    Intent intent = new Intent(NewCalendarActivty.this, LoginScreenActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    progressBar.dismiss();
                                }else {
                                    if(progressBar!=null){
                                        progressBar.dismiss();
                                    }
                                }
                            }
                        };

                        auth.addAuthStateListener(authStateListener);
                    }else {
                        Toast.makeText(getApplicationContext(), "here"+task.getException().getMessage()
                                ,Toast.LENGTH_SHORT).show();
                        if(progressBar!=null){
                            progressBar.dismiss();
                        }
                    }
                }
            });

        }

    }



    private void showErrordialog(String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(message);
        alert.setPositiveButton("OK", null);
        alert.show();
    }

}
