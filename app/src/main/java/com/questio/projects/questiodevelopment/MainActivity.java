package com.questio.projects.questiodevelopment;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;

import com.questio.projects.questiodevelopment.sections.SectionAvatar;
import com.questio.projects.questiodevelopment.sections.SectionCommunity;
import com.questio.projects.questiodevelopment.sections.SectionPrize;
import com.questio.projects.questiodevelopment.sections.SectionQuestmap;
import com.questio.projects.questiodevelopment.sections.SectionSearch;

import net.sourceforge.zbar.Symbol;

import zbar.scanner.ZBarConstants;
import zbar.scanner.ZBarScannerActivity;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    ViewPager mViewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Create the adapter that will return a fragment for each of the 5 primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        final ActionBar actionBar = getActionBar();

        BitmapDrawable background = new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.actionbar_wood));
        background.setTileModeX(Shader.TileMode.MIRROR);
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(background);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        }


        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (actionBar != null) {
                    actionBar.setSelectedNavigationItem(position);
                }
            }
        });
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            int ic_action_tab = 0;
            switch (i) {
                case 0:
                    ic_action_tab = R.drawable.ic_action_community;
                    break;
                case 1:
                    ic_action_tab = R.drawable.ic_action_searchquest;
                    break;
                case 2:
                    ic_action_tab = R.drawable.ic_action_questmap;
                    break;
                case 3:
                    ic_action_tab = R.drawable.ic_action_prize;
                    break;
                case 4:
                    ic_action_tab = R.drawable.ic_action_avatar;
                    break;
            }

            if (actionBar != null) {
                actionBar.addTab(actionBar.newTab().setIcon(ic_action_tab).setTabListener(this));
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


    // BEGIN QR SCANNER PART //

    private static final int ZBAR_SCANNER_REQUEST = 0;
    private static final int ZBAR_QR_SCANNER_REQUEST = 1;


    public void launchQRScanner(View v) {
        if (isCameraAvailable()) {
            Intent intent = new Intent(this, ZBarScannerActivity.class);
            intent.putExtra(ZBarConstants.SCAN_MODES, new int[]{Symbol.QRCODE});
            startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
        } else {
            Toast.makeText(this, "Rear Facing Camera Unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isCameraAvailable() {
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ZBAR_SCANNER_REQUEST:
            case ZBAR_QR_SCANNER_REQUEST:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "Scan Result = " + data.getStringExtra(ZBarConstants.SCAN_RESULT), Toast.LENGTH_SHORT).show();
                } else if (resultCode == RESULT_CANCELED && data != null) {
                    String error = data.getStringExtra(ZBarConstants.ERROR_INFO);
                    if (!TextUtils.isEmpty(error)) {
                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }


}
