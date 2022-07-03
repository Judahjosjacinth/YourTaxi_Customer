package com.ytspilot;

import android.app.ActivityManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.ytspilot.service.MyService;
import com.ytspilot.service.MyService_;
import com.ytspilot.util.Store_pref;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Devani (sdevani92@gmail.com) on 7/26/2016.
 */
public class BaseActivity extends AppCompatActivity {
    Gson mGson;
    Dialog progress;
    SessionManager session;
    Store_pref mStore_pref;
    PendingIntent pendingIntent;


    String driver_id = "";
    String vehicle_id = "";

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        mGson = new Gson();
    }

    public void ShowDatepicker(final EditText et) {
        Calendar c = Calendar.getInstance();

        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.DAY_OF_YEAR, -7);

        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                et.setText("" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DATE));
        dialog.getDatePicker().setMaxDate(new Date().getTime());
        dialog.getDatePicker().setMinDate(maxDate.getTime().getTime());
        dialog.show();
    }


    public Boolean ValidateEt(EditText et) {

        return ValidateEt(et, 0);
    }

    public Boolean ValidateEt(EditText et, int length) {

        if (et != null) {
            String s = et.getText().toString().trim();
            if (s.length() > length) {
                return true;
            } else {
                return false;
            }

        } else {
            return false;
        }

    }


    public void ShowToast(String message) {
        if (message.length() > 0)
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected void resetEdittext(View view) {
        if (view instanceof ViewGroup
                && ((ViewGroup) view).getChildCount() != 0) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                resetEdittext(((ViewGroup) view).getChildAt(i));
            }
        } else {

            if (view instanceof EditText) {
                ((EditText) view).setText("");
            }

        }
    }



    public boolean checkLocationPermission() {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }


    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permission denied plz enable from setting", Toast.LENGTH_LONG).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void stopMyservice() {
        Intent trollIntent_ = new Intent(getApplicationContext(),
                MyService.class);
        if (isMyServiceRunning(MyService_.class)) {
            stopService(trollIntent_);
        }
    }

}
