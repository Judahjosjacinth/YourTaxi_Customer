package com.ytspilot;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ytspilot.MyLocation.LocationResult;
import com.ytspilot.R;
import com.ytspilot.util.NetworkHandler.HTTP_METHOD;
import com.ytspilot.model.response.CurrentTripDetails;
import com.ytspilot.model.response.Endtrip;
import com.ytspilot.model.response.TripStatusDetails;
import com.ytspilot.model.response.UpdateTripStatus;
import com.ytspilot.util.Constants;
import com.ytspilot.util.GPSTracker;
import com.ytspilot.util.NetworkHandler;
import com.ytspilot.util.Store_pref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@SuppressLint("NewApi")
public class Meter extends BaseActivity {

    String lat, longt;
    private ProgressDialog dialog;
    AlertDialog.Builder alert;
    EditText skm, ekm, bid;
    TextView tekm;
    TextView tv_bookingid;
    TextView tv_startkm;

    int starttripkm = 0;

    String number = "";

    String trip_status = "";

    SessionManager session;
    //    String autoid;
    String bookingId = null;
    Button metter_on_off;


    GPSTracker mGpsTracker;
    Location mLocation;
    Button call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        mGpsTracker = new GPSTracker(this);
        mLocation = mGpsTracker.getLocation();
        skm = (EditText) findViewById(R.id.skm);
        ekm = (EditText) findViewById(R.id.ekm);
        bid = (EditText) findViewById(R.id.bid);
        tekm = (TextView) findViewById(R.id.tekm);
        tv_bookingid = (TextView) findViewById(R.id.tv_bookingid);
        tv_startkm = (TextView) findViewById(R.id.tv_startkm);
        metter_on_off = (Button) findViewById(R.id.metter_on_off);

        call = (Button) findViewById(R.id.call);

        lat = "";
        longt = "";

        Intent myIntent = getIntent();
        bookingId = myIntent
                .getStringExtra(BookingsListActivity.BOOKING_ID_ARG);

        number = myIntent.getStringExtra("number");

        if (bookingId != null) {
            bid.setText(bookingId);
            bid.setEnabled(false);
        } else {
            bid.setEnabled(true);
        }

        mStore_pref = new Store_pref(this);
        if (mStore_pref.getisTrip()) {
            ekm.setVisibility(View.VISIBLE);
            tekm.setVisibility(View.VISIBLE);
            tv_startkm.setVisibility(View.GONE);
            skm.setVisibility(View.GONE);
        } else {
            tv_startkm.setVisibility(View.VISIBLE);
            skm.setVisibility(View.VISIBLE);
            ekm.setVisibility(View.GONE);
            tekm.setVisibility(View.GONE);
        }

        session = new SessionManager(getApplicationContext());

        mGson = new Gson();
        getTripDetailsStatus();
        getTripDetails();
//        if (mStore_pref.getisTrip()) {
//            metter_on_off.setText("Turn off meter");
//        } else {
////            metter_on_off.setText("Turn on meter");
//
//            StatusSet();
//        }


        driver_id = session.getDriverID().get(SessionManager.KEY_ID);
        vehicle_id = session.getVehicleid();


        metter_on_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (metter_on_off.getText().toString().trim()) {
                    case "Confirmed with customer":
                        updateStatus("Confirmed with customer");
                        break;
                    case "To Customer point":
                        updateStatus("To Customer point");

                        break;
                    case "At customer point":
                        updateStatus("At customer point");

                        break;

                    case "Turn on meter":
                        if (bid.getText().toString().length() == 0) {
                            alert = new AlertDialog.Builder(Meter.this);
                            alert.setTitle("Alert!")
                                    .setMessage("Booking id should not be empty")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", null);
                            AlertDialog build = alert.create();
                            build.show();

                        } else if (skm.getText().toString().length() == 0) {

                            alert = new AlertDialog.Builder(Meter.this);
                            alert.setTitle("Alert!")

                                    .setMessage("Starting km should not be empty")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", null);

                            AlertDialog build = alert.create();
                            build.show();
                        } else {
                            ekm.setVisibility(View.VISIBLE);
                            tekm.setVisibility(View.VISIBLE);
                            skm.setFocusableInTouchMode(false);
                            bid.setFocusableInTouchMode(false);

                            starttripkm = Integer.parseInt("0" + skm.getText().toString().trim());
                            if (NetworkHandler.isOnline(Meter.this)) {
                                dialog = ProgressDialog
                                        .show(Meter.this,
                                                "Request",
                                                "Processing your request. Please wait...",
                                                true);
                                new HttpAsyncTask()
                                        .execute("https://www.yourtaxistand.com/myapps/starttrip");
                            } else {
                                Toast.makeText(Meter.this, R.string.nointernet, Toast.LENGTH_SHORT).show();

                            }
                        }


                        break;
                    case "Turn off meter":
                        int endkm = Integer.parseInt("0" + ekm.getText().toString().trim());

                        if (endkm > starttripkm || endkm == 0) {

                            if (NetworkHandler.isOnline(Meter.this)) {
                                dialog = ProgressDialog.show(Meter.this, "Request",
                                        "Processing your request. Please wait...", true);
                                new HttpAsyncTask_End()
                                        .execute("https://www.yourtaxistand.com/myapps/endtrip");
                            } else {
                                Toast.makeText(Meter.this, R.string.nointernet, Toast.LENGTH_SHORT).show();

                            }

                        } else {
                            ShowToast("End km must be greater than start km.");
                        }

                        break;
                }


            }
        });


        LocationResult locationResult = new LocationResult() {
            @Override
            public void gotLocation(Location location) {
                // Got the location!
                try {
                    lat = String.valueOf(location.getLatitude());
                    longt = String.valueOf(location.getLongitude());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null));
                startActivity(intent);
            }
        });


        MyLocation myLocation = new MyLocation();
        myLocation.getLocation(this, locationResult);
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


    public class HttpAsyncTask extends AsyncTask<String, Void, String> {
        HashMap<String, String> user = session.getDriverID();
        ContentValues arguments = new ContentValues();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            arguments.put("driver_id", user
                    .get(SessionManager.KEY_ID));
            arguments.put("booking_id", bid
                    .getText().toString().trim());
            arguments.put("vehicle_id", vehicle_id);
            arguments.put("start_km", skm.getText()
                    .toString().trim());
            arguments.put("start_lat", "" + lat.trim());
            arguments.put("start_long", "" + longt.trim());
        }

        @Override
        protected String doInBackground(String... urls) {
            return NetworkHandler.getStringFromURL(urls[0], arguments, HTTP_METHOD.POST);
        }

        @Override
        protected void onPostExecute(String result) {
            if (dialog.isShowing())
                dialog.dismiss();
            try {
                JSONObject res = new JSONObject(result);

                if (res.getString("status_code").equals("1")) {
                    mStore_pref.setTrip(true);

                    mStore_pref.setAutoid(res.getInt("autoid"));
                    alert = new AlertDialog.Builder(Meter.this);
                    if (mStore_pref.getisTrip()) {
                        ekm.setVisibility(View.VISIBLE);
                        tekm.setVisibility(View.VISIBLE);
                        tv_startkm.setVisibility(View.GONE);
                        skm.setVisibility(View.GONE);
                    } else {
                        tv_startkm.setVisibility(View.VISIBLE);
                        skm.setVisibility(View.VISIBLE);
                        ekm.setVisibility(View.GONE);
                        tekm.setVisibility(View.GONE);
                    }

                    if (mStore_pref.getisTrip()) {
                        metter_on_off.setText("Turn off meter");
                    } else {
                        metter_on_off.setText("Turn on meter");
                    }
                    alert.setTitle("Success!")
                            .setMessage("Meter started successfully")
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            // TODO Auto-generated method stub
                                        }
                                    });

                    AlertDialog build = alert.create();
                    build.show();
                } else {
                    alert = new AlertDialog.Builder(Meter.this);
                    alert.setTitle("Sorry!")

                            .setMessage(
                                    "Meter not started successfully.Try again")
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            // TODO Auto-generated method stub

                                        }
                                    });

                    AlertDialog build = alert.create();
                    build.show();

                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    public class HttpAsyncTask_End extends AsyncTask<String, Void, String> {
        HashMap<String, String> user = session.getDriverID();
        ContentValues arguments = new ContentValues();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            Log.e("id",mStore_pref.getAutoid()+"");
            arguments.put("id", mStore_pref.getAutoid());
            arguments.put("end_km", ekm.getText()
                    .toString().trim());
//            arguments.put("id", "78");


//            arguments.put("end_km", "500");

            arguments.put("end_lat", "" + lat.trim());
            arguments.put("end_long", "" + longt.trim());
        }

        @Override
        protected String doInBackground(String... urls) {
            return NetworkHandler.getStringFromURL(urls[0], arguments, HTTP_METHOD.POST);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {


            Log.e("end trip", result);

            if (dialog.isShowing())
                dialog.dismiss();
            try {

                Endtrip mEndtrip = mGson.fromJson(result, Endtrip.class);

                if (mEndtrip.getStatus_code() == 1) {
                    mStore_pref.setTrip(false);

                    skm.setFocusableInTouchMode(true);
                    bid.setFocusableInTouchMode(true);
                    skm.setText("");
                    bid.setText("");
                    ekm.setText("");
                    mStore_pref.setAutoid(0);
                    mStore_pref.setTrip(false);

                    if (mStore_pref.getisTrip()) {
                        ekm.setVisibility(View.VISIBLE);
                        tekm.setVisibility(View.VISIBLE);
                        tv_startkm.setVisibility(View.GONE);
                        skm.setVisibility(View.GONE);
                    } else {
                        tv_startkm.setVisibility(View.VISIBLE);
                        skm.setVisibility(View.VISIBLE);
                        ekm.setVisibility(View.GONE);
                        tekm.setVisibility(View.GONE);
                    }

                    if (mEndtrip.getTrip_type().length() > 0) {
                        showtotalos(mEndtrip);
                    } else {
                        showtotallocal(mEndtrip);
                    }


//                    alert = new AlertDialog.Builder(Meter.this);

//                    alert.setTitle("Success!")
//
//                            .setMessage("Meter stopped successfully")
//                            .setCancelable(false)
//                            .setPositiveButton("OK",
//                                    new DialogInterface.OnClickListener() {
//
//                                        @Override
//                                        public void onClick(
//                                                DialogInterface dialog,
//                                                int which) {
//
//
//                                        }
//                                    });
//
//                    AlertDialog build = alert.create();
//                    build.show();
                } else {
                    alert = new AlertDialog.Builder(Meter.this);
                    alert.setTitle("Sorry!")

                            .setMessage("Meter not stopped successfully")
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            // TODO Auto-generated method stub
                                        }
                                    });

                    AlertDialog build = alert.create();
                    build.show();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    private class TripDetailTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
                    progress = ProgressDialog.show(
                Meter.this, "", "Please wait...");
        }

        @Override
        protected String doInBackground(String... place) {

            ContentValues arguments = new ContentValues();
            arguments.put("autoid", mStore_pref.getAutoid() + "");

            String data="";
            try {
                // Fetching the data from web service in background
                data = NetworkHandler.getStringFromURL(  Constants.fetchtrip, arguments, NetworkHandler.HTTP_METHOD.GET);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

              progress.dismiss();
            if(result!=null && !result.equalsIgnoreCase("")){
                                        try {
                           // CurrentTripDetails mCurrentTripDetails = mGson.fromJson("" + result, CurrentTripDetails.class);

                                            JSONObject jsonObject= new JSONObject(result);
                                            int code=jsonObject.getInt("status_code");
                                            String startkm=jsonObject.getString("start_km");
                                         //   String
                           // if (mCurrentTripDetails.getStatus_code() == 1) {
                              if (code == 1) {
                                starttripkm = Integer.parseInt(startkm);

//                                trip_status = mCurrentTripDetails.getDriver_status();
//                                switch (trip_status) {
//                                    case "Confirmed with customer":
//                                        mStore_pref.setStatus(1);
//                                        break;
//                                    case "To Customer point":
//                                        mStore_pref.setStatus(2);
//
//                                        break;
//                                    case "At customer point":
//                                        mStore_pref.setStatus(3);
//
//                                        break;
//
//                                    case "Turn on meter":
//                                        mStore_pref.setStatus(4);
//
//
//                                        break;
//                                    case "Turn off meter":
//
//                                        mStore_pref.setStatus(0);
//
//
//                                        break;
//
//                                }
//
//                                StatusSet();
                            } else {
//                                mStore_pref.setStatus(0);
//                                call.setVisibility(View.VISIBLE);
//                                StatusSet();

                            }

                        } catch (Exception e) {
                            Log.e("opps", "" + e);
                        }
            }

        }
    }
    public void getTripDetails() {

        new TripDetailTask().execute("");

    }

//    public void getTripDetails() {
//        RequestQueue queue = Volley.newRequestQueue(this);
//        progress = ProgressDialog.show(
//                this, "", "Please wait...");
//
//        progress.setCancelable(false);
//
//        StringRequest myReq = new StringRequest(Request.Method.POST,
//                Constants.fetchtrip,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//
//                        Log.e("result", response);
//                        progress.dismiss();
//
//                        try {
//                            CurrentTripDetails mCurrentTripDetails = mGson.fromJson("" + response, CurrentTripDetails.class);
//
//                            if (mCurrentTripDetails.getStatus_code() == 1) {
//                                starttripkm = Integer.parseInt(mCurrentTripDetails.getStart_km());
//
////                                trip_status = mCurrentTripDetails.getDriver_status();
////                                switch (trip_status) {
////                                    case "Confirmed with customer":
////                                        mStore_pref.setStatus(1);
////                                        break;
////                                    case "To Customer point":
////                                        mStore_pref.setStatus(2);
////
////                                        break;
////                                    case "At customer point":
////                                        mStore_pref.setStatus(3);
////
////                                        break;
////
////                                    case "Turn on meter":
////                                        mStore_pref.setStatus(4);
////
////
////                                        break;
////                                    case "Turn off meter":
////
////                                        mStore_pref.setStatus(0);
////
////
////                                        break;
////
////                                }
////
////                                StatusSet();
//                            } else {
////                                mStore_pref.setStatus(0);
////                                call.setVisibility(View.VISIBLE);
////                                StatusSet();
//
//                            }
//
//                        } catch (Exception e) {
//                            Log.e("opps", "" + e);
//                        }
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        //progress.dismiss();
//                        Log.e("oops.. ", "" + error.getMessage());
//                        progress.dismiss();
//                    }
//                }) {
//
//            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("id", "" + mStore_pref.getAutoid());
//                int sutod= mStore_pref.getAutoid();
//                Log.e("autoid", mStore_pref.getAutoid() + "");
//                return params;
//            }
//
//        };
//        myReq.setShouldCache(false);
//        myReq.setRetryPolicy(new DefaultRetryPolicy(
//                10000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        queue.add(myReq);
//
//    }


    private class TripDetailStatusTask extends AsyncTask<String, Void, String> {



        @Override
        protected String doInBackground(String... place) {

            ContentValues arguments = new ContentValues();
            arguments.put("booking_id", bookingId);
            String data="";
            try {
                // Fetching the data from web service in background
                data = NetworkHandler.getStringFromURL( Constants.fetchtrip_status, arguments, NetworkHandler.HTTP_METHOD.GET);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
          //  progress.dismiss();
            if(result!=null && !result.equalsIgnoreCase("")){
                try {
                    TripStatusDetails mTripStatusDetails = mGson.fromJson("" + result, TripStatusDetails.class);


                    trip_status = mTripStatusDetails.getDriver_status();
                    switch (trip_status) {
                        case "Confirmed with customer":
                            mStore_pref.setStatus(1);
                            break;
                        case "To Customer point":
                            mStore_pref.setStatus(2);

                            break;
                        case "At customer point":
                            mStore_pref.setStatus(3);

                            break;

                        case "Turn on meter":
                            mStore_pref.setStatus(4);


                            break;
                        case "Turn off meter":

                            mStore_pref.setStatus(0);


                            break;

                        default:
                            mStore_pref.setStatus(1);
                            break;
                    }

                    StatusSet();


                } catch (Exception e) {
                    Log.e("opps", "" + e);
                }
            }

        }
    }
    public void getTripDetailsStatus() {

        new TripDetailStatusTask().execute("");

    }

//    public void getTripDetailsStatus() {
//        RequestQueue queue = Volley.newRequestQueue(this);
//
//        StringRequest myReq = new StringRequest(Request.Method.POST,
//                Constants.fetchtrip_status,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//
//                        Log.e("result", response);
//
//
//                        try {
//                            TripStatusDetails mTripStatusDetails = mGson.fromJson("" + response, TripStatusDetails.class);
//
//
//                            trip_status = mTripStatusDetails.getDriver_status();
//                            switch (trip_status) {
//                                case "Confirmed with customer":
//                                    mStore_pref.setStatus(1);
//                                    break;
//                                case "To Customer point":
//                                    mStore_pref.setStatus(2);
//
//                                    break;
//                                case "At customer point":
//                                    mStore_pref.setStatus(3);
//
//                                    break;
//
//                                case "Turn on meter":
//                                    mStore_pref.setStatus(4);
//
//
//                                    break;
//                                case "Turn off meter":
//
//                                    mStore_pref.setStatus(0);
//
//
//                                    break;
//
//                                default:
//                                    mStore_pref.setStatus(1);
//                                    break;
//                            }
//
//                            StatusSet();
//
//
//                        } catch (Exception e) {
//                            Log.e("opps", "" + e);
//                        }
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        //progress.dismiss();
//                        Log.e("oops.. ", "" + error.getMessage());
//                    }
//                }) {
//
//            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("booking_id", "" + bookingId);
//                Log.e("booking_id", bookingId + "");
//                return params;
//            }
//
//        };
//        myReq.setShouldCache(false);
//        myReq.setRetryPolicy(new DefaultRetryPolicy(
//                10000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        queue.add(myReq);
//
//    }


    public void showtotallocal(Endtrip mEndtrip) {

        final Dialog dialog = new Dialog(Meter.this); // Context, this, etc.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_fare_total);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        TextView status = (TextView) dialog.findViewById(R.id.tv_status);
        TextView tv_bookingid = (TextView) dialog.findViewById(R.id.tv_bookingid);
        TextView tv_startkm = (TextView) dialog.findViewById(R.id.tv_startkm);
        TextView tv_endkm = (TextView) dialog.findViewById(R.id.tv_endkm);
        TextView tv_totalkm = (TextView) dialog.findViewById(R.id.tv_totalkm);
        TextView tv_service_charge = (TextView) dialog.findViewById(R.id.tv_service_charge);
        TextView total_fare = (TextView) dialog.findViewById(R.id.total_fare);
        Button okaybutton = (Button) dialog.findViewById(R.id.okaybutton);


        status.setText("" + mEndtrip.getStatus_message());
        tv_bookingid.setText("" + mEndtrip.getBooking_id());
        tv_startkm.setText("" + mEndtrip.getStart_km());
        tv_endkm.setText("" + mEndtrip.getEnd_km());
//        tv_package_hours.setText("" + mEndtrip.getPackage_hours());
        tv_totalkm.setText("" + mEndtrip.getTotal_kms());
//        tv_extrakm.setText("" + mEndtrip.getExtra_km());
//        tv_basefare.setText("" + mEndtrip.getBase_fare());
        tv_service_charge.setText("" + mEndtrip.getService_charge());
//        tv_extra_distance_fare.setText("" + mEndtrip.getExtra_distance_fare());


//        tv_extra_time_charge.setText("" + mEndtrip.getExtra_time_fare());
//        tv_extra_charge_total.setText("" + mEndtrip.getExtra_charge_total());
//        tv_package_distance.setText("" + mEndtrip.getPackage_distance());
//        tv_package_rate.setText("" + mEndtrip.getPackage_rate());
//        tv_package_rate_additional_km.setText("" + mEndtrip.getPackage_rate_additional_km());
//        tv_package_rate_additional_hour.setText("" + mEndtrip.getPackage_rate_additional_hour());
//
//        total_time.setText("" + mEndtrip.getTotal_time());
        total_fare.setText("" + mEndtrip.getTotal_fare());

        okaybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dialog.show();
    }

    public void showtotalos(Endtrip mEndtrip) {

        final Dialog dialog = new Dialog(Meter.this); // Context, this, etc.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_fare_totalos);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        TextView status = (TextView) dialog.findViewById(R.id.tv_status);
        TextView tv_bookingid = (TextView) dialog.findViewById(R.id.tv_bookingid);

        TextView tv_minimalkm = (TextView) dialog.findViewById(R.id.tv_minimalkm);
        TextView tv_minimalkmtitle = (TextView) dialog.findViewById(R.id.tv_title_minimalkm);
        TextView tv_minimalfare = (TextView) dialog.findViewById(R.id.tv_minimalfare);
        TextView tv_driverbatta = (TextView) dialog.findViewById(R.id.tv_driverbatta);
        TextView tv_triptypetitle = (TextView) dialog.findViewById(R.id.tv_title_triptype);
        TextView tv_triptype = (TextView) dialog.findViewById(R.id.tv_triptype);
        TextView tv_additional_km_rate_per_km = (TextView) dialog.findViewById(R.id.tv_additional_km_rate_per_km);
        TextView tv_startkm = (TextView) dialog.findViewById(R.id.tv_startkm);
        TextView tv_endkm = (TextView) dialog.findViewById(R.id.tv_endkm);
        TextView tv_totalkm = (TextView) dialog.findViewById(R.id.tv_totalkm);
        TextView tv_driver_batta = (TextView) dialog.findViewById(R.id.tv_driverbatta);
        TextView tv_service_charge = (TextView) dialog.findViewById(R.id.tv_service_charge);

        TextView total_fare = (TextView) dialog.findViewById(R.id.total_fare);
        Button okaybutton = (Button) dialog.findViewById(R.id.okaybutton);


        status.setText("" + mEndtrip.getStatus_message());


        tv_driver_batta.setText("" + mEndtrip.getDriver_bata());
        tv_triptype.setText("" + mEndtrip.getTrip_type());
        tv_additional_km_rate_per_km.setText("" + mEndtrip.getRate_per_km());
        tv_minimalkm.setText("" + mEndtrip.getMinimum_distance());
        tv_minimalfare.setText("" + mEndtrip.getMinimum_fare());

        tv_bookingid.setText("" + mEndtrip.getBooking_id());

        tv_startkm.setText("" + mEndtrip.getStart_km());
        tv_endkm.setText("" + mEndtrip.getEnd_km());

        tv_totalkm.setText("" + mEndtrip.getTotal_kms());

        tv_service_charge.setText("" + mEndtrip.getService_charge());


        total_fare.setText("" + mEndtrip.getTotal_fare());


        if(mEndtrip.getTrip_type().equalsIgnoreCase("LT")) {
            tv_minimalkmtitle.setText("Package");
            tv_triptypetitle.setText("Additional Hr");

            tv_minimalkm.setText("" + mEndtrip.getPackage_rate());
            tv_triptypetitle.setText("" + mEndtrip.getPackage_rate_additional_hour());

        }
        okaybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dialog.show();
    }


    public void StatusSet() {

        switch (mStore_pref.getStatus()) {
            case 0:
                metter_on_off.setText("Confirmed with customer");

                call.setVisibility(View.VISIBLE);

                tv_startkm.setVisibility(View.GONE);
                skm.setVisibility(View.GONE);


                break;
            case 1:
                metter_on_off.setText("To Customer point");
                call.setVisibility(View.VISIBLE);

                tv_startkm.setVisibility(View.GONE);
                skm.setVisibility(View.GONE);
                ekm.setVisibility(View.GONE);
                tekm.setVisibility(View.GONE);

                break;
            case 2:
                metter_on_off.setText("At customer point");
                call.setVisibility(View.VISIBLE);


                tv_startkm.setVisibility(View.GONE);
                skm.setVisibility(View.GONE);
                ekm.setVisibility(View.GONE);
                tekm.setVisibility(View.GONE);

                break;

            case 3:
                metter_on_off.setText("Turn on meter");
                call.setVisibility(View.GONE);

                tv_startkm.setVisibility(View.VISIBLE);
                skm.setVisibility(View.VISIBLE);
                ekm.setVisibility(View.GONE);
                tekm.setVisibility(View.GONE);

                break;
            case 4:
                metter_on_off.setText("Turn off meter");
                call.setVisibility(View.GONE);


                tv_startkm.setVisibility(View.GONE);
                skm.setVisibility(View.GONE);
                ekm.setVisibility(View.VISIBLE);
                tekm.setVisibility(View.VISIBLE);

                break;

        }
    }

//
//
//
//
//Meter on or Customer cancelled ( End trip)
//    • If cancelled , then reason for cancellation should be selected from the below
//    • Customer cancelled
//    • Customer could not be contacted
//    • Vehicle breakdown

//    • Meter off
//    • Post Meter off , you should receive a response  from backend team and display the fare ( This feature is now available)
//    "

// update status

    public void updateStatus(final String status) {

        mGpsTracker.getLocation();
        RequestQueue queue = Volley.newRequestQueue(this);
        progress = ProgressDialog.show(
                this, "", "Please wait...");

        progress.setCancelable(false);

        StringRequest myReq = new StringRequest(Request.Method.POST,
                Constants.driver_status,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("result update ", response);
                        progress.dismiss();


                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int statusCode = jsonObject.getInt("status_code");


                         //   UpdateTripStatus mUpdateTripStatus = mGson.fromJson("" + response, UpdateTripStatus.class);

                       //     if (mUpdateTripStatus.getStatus_code() == 1) {
                            if (statusCode== 1) {
                                switch (metter_on_off.getText().toString().trim()) {
                                    case "Confirmed with customer":
                                        mStore_pref.setStatus(1);
                                        break;
                                    case "To Customer point":
                                        mStore_pref.setStatus(2);


                                        break;
                                    case "At customer point":
                                        mStore_pref.setStatus(3);


                                        break;

                                    case "Turn on meter":

                                        mStore_pref.setStatus(4);

                                        break;
                                    case "Turn off meter":
                                        mStore_pref.setStatus(0);


                                        break;
                                }


                                StatusSet();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progress.dismiss();
                        Log.e("oops.. ", "" + error.getMessage());
                        progress.dismiss();
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("driver_id", "" + driver_id);
                params.put("vehicle_id", "" + vehicle_id);
                params.put("booking_id", "" + bookingId);
                params.put("vehicle_lat", "" + mGpsTracker.getLatitude());
                params.put("vehicle_lng", "" + mGpsTracker.getLongitude());
                params.put("driver_status", "" + status);
                return params;
            }
        };
        myReq.setShouldCache(false);
        myReq.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(myReq);

    }


}
