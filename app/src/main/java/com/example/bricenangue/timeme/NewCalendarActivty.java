package com.example.bricenangue.timeme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.CalendarDayEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by bricenangue on 27/02/16.
 */
public class NewCalendarActivty extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, View.OnClickListener,FragmentCategoryShopping.OnFragmentInteractionListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */

    ViewPager viewPager;
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private AppBarLayout mAppBarLayout;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy", /*Locale.getDefault()*/Locale.ENGLISH);

    private CompactCalendarView mCompactCalendarView;

    private boolean isExpanded = false;
    private float mCurrentRotation = 360.0f;
    private String datestr;
    private FloatingActionButton fab;
    private ListView lv_android;
    private AndroidListAdapter list_adapter;
    String d;
    ArrayList<CalendarCollection> allnewEvents=new ArrayList<>();
    private MySQLiteHelper mySQLiteHelper;



    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_sheet_act_test);

        mySQLiteHelper=new MySQLiteHelper(this);
        CalendarCollection.date_collection_arr=new ArrayList<>();
        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            if(extras.containsKey("events")){
                allnewEvents=extras.getParcelableArrayList("events");


            }
        }
       getEvents(mySQLiteHelper.getAllIncomingNotification());

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        Toolbar toolbar = (Toolbar) findViewById(R.id.ttoolbar);
        setSupportActionBar(toolbar);
        initViewPagerAndTabs();

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
       if(CalendarCollection.date_collection_arr.size()!=0){
            for (int i=0; i<CalendarCollection.date_collection_arr.size();i++){

        setEventinCalendar(CalendarCollection.date_collection_arr.get(i));


           }

        }



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
    }


    private void initViewPagerAndTabs() {

        viewPager = (ViewPager) findViewById(R.id.viewPager);

        ViewPagerAdapter pagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());

        pagerAdapter.addFragment(new FragmentAllEvents(),"All Events");
        pagerAdapter.addFragment(new FragmentMyEvent(),"MY Events");
        pagerAdapter.addFragment(new FragmentCategoryBusiness(),"Business meetings");
        pagerAdapter.addFragment(new FragmentCategoryBirthdays(),"Birthdays");
        pagerAdapter.addFragment(new FragmentCategoryShopping(),"Shopping");
        pagerAdapter.addFragment(new FragmentCategoryWorkPlan(),"Work Plans");

        viewPager.setAdapter(pagerAdapter);

        SlidingTabLayout tabLayout = (SlidingTabLayout) findViewById(R.id.tabLayout);
        tabLayout.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });
        tabLayout.setViewPager(viewPager);
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


       // viewPager.setCurrentItem(2,true);


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

                CalendarCollection.date_collection_arr.add(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
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
            mCompactCalendarView.addEvent(new CalendarDayEvent(oldMillis,getResources().getColor(R.color.warning_color)),false);


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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
       // d =setCurrentDate(new Date());
       // FragmentManager fragmentManager = getSupportFragmentManager();
       // fragmentManager.beginTransaction()
        //        .replace(R.id.container, new AddNewEventFragment())
        //        .commit();

        startActivity(new Intent(NewCalendarActivty.this,AddNewEventActivity.class));
    }


    private void returnedCalenderevent(CalendarCollection calendarCollection) {
        CalendarCollection.date_collection_arr.add(calendarCollection);
        setEventinCalendar(calendarCollection);
        Toast.makeText(getApplicationContext(),"new event",Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements  AdapterView.OnItemClickListener {
        private ListView lv_android;
        private AndroidListAdapter list_adapter;


        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getContext(), "You have event on this date: "+ CalendarCollection.date_collection_arr.get(position).date+
                    CalendarCollection.date_collection_arr.get(position).event_message , Toast.LENGTH_LONG).show();

        }


        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            CalendarCollection.date_collection_arr=new ArrayList<CalendarCollection>();
            CalendarCollection.date_collection_arr.add(new CalendarCollection("2016-04-01","John Birthday"));
            CalendarCollection.date_collection_arr.add(new CalendarCollection("2016-04-04","Client Meeting at 5 p.m."));
            CalendarCollection.date_collection_arr.add(new CalendarCollection("2016-03-06","A Small Party at my office"));
            CalendarCollection.date_collection_arr.add(new CalendarCollection("2016-05-02", "Marriage Anniversary"));
            CalendarCollection.date_collection_arr.add(new CalendarCollection("2016-04-11", "Live Event and Concert of sonu"));


            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView tv=(TextView)rootView.findViewById(R.id.section_label);
            lv_android = (ListView) rootView.findViewById(R.id.lvc_android);
            list_adapter=new AndroidListAdapter(getContext(),R.layout.list_item, CalendarCollection.date_collection_arr);
            lv_android.setAdapter(list_adapter);
            lv_android.setOnItemClickListener(this);



            return rootView;
        }




        private void returnedCalenderEvent(CalendarCollection calendarCollection) {
            CalendarCollection.date_collection_arr.add(calendarCollection);
            Toast.makeText(getContext(),"new event",Toast.LENGTH_SHORT).show();
            list_adapter.notifyDataSetChanged();
        }
    }
}
