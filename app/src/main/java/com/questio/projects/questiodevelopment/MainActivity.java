package com.questio.projects.questiodevelopment;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    int testSlack = 0;
    ViewPager mViewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.

        final ActionBar actionBar = getActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        actionBar.setHomeButtonEnabled(false);

        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            if (i == 0) {
//               actionBar.addTab(actionBar.newTab().setIcon(android.R.drawable.ic_menu_call).setTabListener(this));
                actionBar.addTab(actionBar.newTab().setIcon(R.drawable.ic_action_community).setTabListener(this));
            } else if (i == 1) {

                actionBar.addTab(actionBar.newTab().setIcon(R.drawable.ic_action_searchquest).setTabListener(this));
            } else if (i == 2) {
//                String uri = "@drawable/ic_launcher";
//                int imageResource = getResources().getIdentifier(uri, null, getPackageName());
//                Drawable res = getResources().getDrawable(imageResource);
//                actionBar.addTab(
//                        actionBar.newTab()
//                                .setIcon(res)
//                                .setTabListener(this));
//              actionBar.addTab(actionBar.newTab().setIcon(R.drawable.ic_action_questmap).setTabListener(this));
                actionBar.addTab(actionBar.newTab().setIcon(R.drawable.ic_action_questmap).setTabListener(this));
            } else if (i == 3) {

                actionBar.addTab(actionBar.newTab().setIcon(R.drawable.ic_action_prize).setTabListener(this));

            } else {

                actionBar.addTab(actionBar.newTab().setIcon(R.drawable.ic_action_avatar).setTabListener(this));
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }


    /**
     * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int i) {
            Fragment fragment;
            switch (i) {
                case 0:
                    fragment = new SectionCommunity();
                    break;
                case 1:
                    fragment = new SectionSearch();
                    break;
                case 2:
                    fragment = new SectionQuestmap();
                    break;
                case 3:
                    fragment = new SectionPrize();
                    break;
                default:
                    fragment = new SectionAvatar();
                    break;
            }


            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;


        }

        @Override
        public int getCount() {
            return 5;
        }


    }

}
