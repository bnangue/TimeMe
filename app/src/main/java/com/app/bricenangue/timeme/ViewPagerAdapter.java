package com.app.bricenangue.timeme;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by bricenangue on 16/02/16.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private  Context context;
    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created

    private final ArrayList<Fragment> fragmentList = new ArrayList<>();
    private final ArrayList<String> fragmentTitleList = new ArrayList<>();

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context=context;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        Fragment frag=null;
        switch (position){
            case 0:
                frag=new FragmentOverview();
                break;
            case 1:
                frag=new FragmentMyEvent();
                break;
            case 2:
                frag=new FragmentCategoryFinance();
                break;
            case 3:
                frag=new FragmentCategoryShopping();
                break;
        }
        return frag;
       // return  fragmentList.get(position);

    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        String title=" ";
        switch (position){
            case 0:
                title= context.getString(R.string.ViewPager_Fragment_FRAGMENTOVERVIEW);
                break;
            case 1:
                title=context.getString(R.string.ViewPager_Fragment_FRAGMENTEVENTS);
                break;
            case 2:
                title=context.getString(R.string.ViewPager_Fragment_FRAGMENTFINANCE);
                break;
            case 3:
                title=context.getString(R.string.ViewPager_Fragment_FRAGMENTSHOPPING);
                break;
        }

        return title;

        //return fragmentTitleList.get(position);
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return 4;
        //return fragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        fragmentList.add(fragment);
        fragmentTitleList.add(title);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Yet another bug in FragmentStatePagerAdapter that destroyItem is called on fragment that hasnt been added. Need to catch
        try {
            super.destroyItem(container, position, object);
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }
    }
    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }
}

