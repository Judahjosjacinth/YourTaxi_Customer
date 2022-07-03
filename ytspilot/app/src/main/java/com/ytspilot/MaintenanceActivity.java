package com.ytspilot;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.ytspilot.fragments.FuelFragment;
import com.ytspilot.fragments.MaintenanceFragment;
import com.ytspilot.fragments.PenaltyFragment;
import com.ytspilot.fragments.Track_salaryFragment;

@SuppressLint("NewApi")
public class MaintenanceActivity extends BaseActivity {


    String driver_id = "";
    String vehicle_id = "";


    CustomPagerAdapter mCustomPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ytspilot.R.layout.activity_maintenance);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        session = new SessionManager(getApplicationContext());
        driver_id = session.getDriverID().get(SessionManager.KEY_ID);
        vehicle_id = session.getVehicleid();

        mGson = new Gson();


        mCustomPagerAdapter = new CustomPagerAdapter(this);

        mViewPager = (ViewPager) findViewById(com.ytspilot.R.id.pager);
        mViewPager.setAdapter(mCustomPagerAdapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                //do your action here.
                onBackPressed();
                break;

        }

        return true;
    }

    class CustomPagerAdapter extends FragmentPagerAdapter {

        Context mContext;

        public CustomPagerAdapter(Context context) {
            super(getSupportFragmentManager());
            mContext = context;
        }

        @Override
        public Fragment getItem(int position) {


            switch (position) {
                case 0:
                    return new MaintenanceFragment();
                case 1:
                    return new FuelFragment();
                case 2:
                    return new PenaltyFragment();
                case 3:
                    return new Track_salaryFragment();
                default:
                    return new MaintenanceFragment();

            }


        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
//            return "Page " + (position + 1);

            switch (position) {
                case 0:
                    return "Maintenance";

                case 1:
                    return "Fuel";
                case 2:
                    return "Penalty";
                case 3:
                    return "Track Salary / Bata";
                default:
                    return "Maintenance";
            }

        }
    }

}
