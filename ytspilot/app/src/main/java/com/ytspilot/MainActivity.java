package com.ytspilot;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ytspilot.service.MyService;
import com.ytspilot.util.Store_pref;

@SuppressLint("NewApi")
public class MainActivity extends BaseActivity implements OnClickListener {

    Button free, trip, issue, att, login, leave, meter, logout, booking;

    Button btn_maintenance;
    SessionManager session;

    //    private JobManager mJobManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(getApplicationContext());
        btn_maintenance = (Button) findViewById(R.id.btn_maintenance);
        free = (Button) findViewById(R.id.free);
        trip = (Button) findViewById(R.id.tripsheet);
        issue = (Button) findViewById(R.id.issue);
        att = (Button) findViewById(R.id.d_attachment);
        login = (Button) findViewById(R.id.driver_login);
        leave = (Button) findViewById(R.id.driver_leave);
        meter = (Button) findViewById(R.id.meter);
        logout = (Button) findViewById(R.id.logout);
        booking = (Button) findViewById(R.id.booking);

        free.setOnClickListener(this);
        trip.setOnClickListener(this);
        issue.setOnClickListener(this);
        att.setOnClickListener(this);
        login.setOnClickListener(this);
        leave.setOnClickListener(this);
        meter.setOnClickListener(this);
        logout.setOnClickListener(this);
        booking.setOnClickListener(this);

        btn_maintenance.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MaintenanceActivity.class);
                startActivity(i);
            }
        });

        if (checkLocationPermission()) {
            startService_alm();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == att) {
            Intent i = new Intent(MainActivity.this, DriverAttachment.class);
            startActivity(i);
        } else if (v == login) {
            Intent i = new Intent(MainActivity.this, LoginArea.class);
            startActivity(i);
        } else if (v == trip) {
            Intent i = new Intent(MainActivity.this, DriverTripSheet.class);
            startActivity(i);
        } else if (v == issue) {
            Intent i = new Intent(MainActivity.this, PaymentIssueSheet.class);
            startActivity(i);
        } else if (v == leave) {
            Intent i = new Intent(MainActivity.this, Leave.class);
            startActivity(i);
        } else if (v == meter) {
            Intent i = new Intent(MainActivity.this, Meter.class);
            startActivity(i);
        } else if (v == logout) {
            stopMyservice();
            mStore_pref = new Store_pref(this);
            mStore_pref.removeAll();
            finish();
            LoginActivity.islogin = false;
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } else if (v == free) {
            Intent i = new Intent(MainActivity.this, DriverFree.class);
            startActivity(i);
        } else if (v == booking) {
            Intent intent = new Intent(MainActivity.this,
                    BookingsListActivity.class);
            startActivity(intent);
        }

    }


    public void startService_alm() {

        Intent myIntent = new Intent(MainActivity.this, MyService.class);
        pendingIntent = PendingIntent.getService(this, 0, myIntent,
                0);
        startService(myIntent);

    }
}
