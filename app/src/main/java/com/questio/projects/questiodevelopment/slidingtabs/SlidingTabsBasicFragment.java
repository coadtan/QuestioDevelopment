package com.questio.projects.questiodevelopment.slidingtabs;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.questio.projects.questiodevelopment.R;
import com.questio.projects.questiodevelopment.sections.SectionAvatar;
import com.questio.projects.questiodevelopment.sections.SectionCommunity;
import com.questio.projects.questiodevelopment.sections.SectionPrize;
import com.questio.projects.questiodevelopment.sections.SectionQuestmap;
import com.questio.projects.questiodevelopment.sections.SectionSearch;



public class SlidingTabsBasicFragment extends Fragment {

    private static final String LOG_TAG = SlidingTabsBasicFragment.class.getSimpleName();
    AppSectionsPagerAdapter mPagerAdapter;


    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPagerAdapter = new AppSectionsPagerAdapter(getFragmentManager());
    }

    /**
     * Inflates the {@link android.view.View} which will be displayed by this {@link android.support.v4.app.Fragment}, from the app's
     * resources.
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sample, container, false);

    }

    // BEGIN_INCLUDE (fragment_onviewcreated)


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(mPagerAdapter);
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);

    }


    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "text" + position;
        }


        @Override
        public Fragment getItem(int i) {
            Fragment fragment;
            switch (i) {
                case 0:
                    fragment = new SectionQuestmap();

                    break;
                case 1:
                    fragment = new SectionSearch();
                    break;
                case 2:
                    fragment = new SectionCommunity();
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
