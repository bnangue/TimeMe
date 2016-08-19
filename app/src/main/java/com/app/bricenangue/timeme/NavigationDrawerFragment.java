package com.app.bricenangue.timeme;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

/**
 * Created by bricenangue on 27/02/16.
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean isExpanded = false;
    private float mCurrentRotation = 360.0f;

    private DrawerLayout mDrawerLayout;
    private ExpandableListView mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    private UserLocalStore userLocalStore;
    int previousGroup;
    private ArrayList<Category> category_name=new ArrayList<>();

    private ArrayList<ArrayList<SubCategory>> subcategory_name=new ArrayList<>();
    private ArrayList<Integer> subCatCount = new ArrayList<Integer>();

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getCatData();
        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, true);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        userLocalStore=new UserLocalStore(getContext());

        String path=userLocalStore.getUserPicturePath();
        String uName =userLocalStore.getLoggedInUser().getfullname();
        View view =  inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);

        TextView userName = (TextView) view.findViewById(R.id.username);
        userName.setText(uName);
        CircularImageView profilePicture = (CircularImageView) view.findViewById(R.id.avatarfriend);
        if(path!=null){
            Bitmap bitmap=userLocalStore.loadImageFromStorage(path);
            if(bitmap!=null){

                profilePicture.setImageBitmap(bitmap);
            }
        }
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), OpenUserProfileActivity.class).putExtra("loggedInUser",userLocalStore.getLoggedInUser()));
            }
        });
        mDrawerListView = (ExpandableListView)view.findViewById(R.id.navlist);
        mDrawerListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                ImageView img=(ImageView)parent.getChildAt(groupPosition).findViewById(R.id.collapse_arrow);

                if (parent.isGroupExpanded(groupPosition)) {
                    parent.collapseGroup(groupPosition);

                } else {
                    if (groupPosition != previousGroup) {
                        parent.collapseGroup(previousGroup);
                    }
                    previousGroup = groupPosition;
                    parent.expandGroup(groupPosition);
                }

                parent.smoothScrollToPosition(groupPosition);

                if(groupPosition==0){
                    if (isExpanded) {
                        RotateAnimation anim = new RotateAnimation(mCurrentRotation, mCurrentRotation + 180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        mCurrentRotation = (mCurrentRotation + 180.0f) % 360.0f;
                        anim.setInterpolator(new LinearInterpolator());
                        anim.setFillAfter(true);
                        anim.setFillEnabled(true);
                        anim.setDuration(300);
                        img.startAnimation(anim);
                        isExpanded = false;
                    } else {
                        RotateAnimation anim = new RotateAnimation(mCurrentRotation, mCurrentRotation - 180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        mCurrentRotation = (mCurrentRotation - 180.0f) % 360.0f;
                        anim.setInterpolator(new LinearInterpolator());
                        anim.setFillAfter(true);
                        anim.setFillEnabled(true);
                        anim.setDuration(300);
                        img.startAnimation(anim);
                        isExpanded = true;
                    }
                }


                selectItem(groupPosition);
                return true;
            }
        });
        mDrawerListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                selectSubItem(childPosition);
                return false;
            }
        });

        mDrawerListView.setAdapter(new ExpandableAdapter(getActivity(),category_name,subcategory_name,subCatCount));

      //  mDrawerListView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1, android.R.id.text1, new  String[]{"Overview","All Events","MY Events","Business meetings","Birthdays","Shopping","Work Plans"}));
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        mDrawerListView.setFitsSystemWindows(true);
        return view;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        // Set the status bar color.
        // This only takes effect on Lollipop, or when using translucentStatusBar
        // on KitKat.
        mDrawerLayout.setStatusBarBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primary_dark));

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                toolbar,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null && position!=0) {

                mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    private void selectSubItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawersubItemSelected(position);
        }
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
        }
        getActivity().setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);

        //call when a subintem is selected
        void onNavigationDrawersubItemSelected(int position);
    }



    public void getCatData()
    {
        category_name.clear();
        Category categoryDetails = new Category();

        categoryDetails.setCatCode(10);
        categoryDetails.setCatName(getString(R.string.Categories_text));

        category_name.add(categoryDetails);

        categoryDetails = new Category();
        categoryDetails.setCatCode(20);
        categoryDetails.setCatName(getString(R.string.Overview_text));
        category_name.add(categoryDetails);

        categoryDetails = new Category();
        categoryDetails.setCatCode(30);
        categoryDetails.setCatName(getString(R.string.Invite_Partner_text));
        category_name.add(categoryDetails);

        categoryDetails = new Category();
        categoryDetails.setCatCode(40);
        categoryDetails.setCatName(getString(R.string.Settings_text));
        category_name.add(categoryDetails);


        categoryDetails = new Category();
        categoryDetails.setCatCode(50);
        categoryDetails.setCatName(getString(R.string.About_text));
        category_name.add(categoryDetails);



        //----Populate Sub Category Codes
        subcategory_name.clear();

        ArrayList<SubCategory> subCategoryMatches = new ArrayList<SubCategory>();

        SubCategory subCategoryMatch = new SubCategory();

        subCategoryMatch.setSubCatName(getString(R.string.Categories_Subtext_Overview));
        subCategoryMatches.add(subCategoryMatch);

        subCategoryMatch = new SubCategory();
        subCategoryMatch.setSubCatName(getString(R.string.Categories_Subtext_My_Events));
        subCategoryMatches.add(subCategoryMatch);

        subCategoryMatch = new SubCategory();
        subCategoryMatch.setSubCatName(getString(R.string.Categories_Subtext_My_Finances));
        subCategoryMatches.add(subCategoryMatch);

        subCategoryMatch = new SubCategory();
        subCategoryMatch.setSubCatName(getString(R.string.Categories_Subtext_My_Grocery));
        subCategoryMatches.add(subCategoryMatch);

        subcategory_name.add(subCategoryMatches);
        subCatCount.add(subCategoryMatches.size());
        //---




    }


}
