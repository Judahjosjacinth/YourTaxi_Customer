package com.ytspilot.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ytspilot.SessionManager;
import com.ytspilot.util.Constants;
import com.ytspilot.util.GPSTracker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MyService_ extends Service {
    private Context ctx;
    GPSTracker mGpsTracker;
    Handler handler;
    SessionManager session;
    String driver_id = "";
    String vehicle_id = "";

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        ctx = this;
        session = new SessionManager(getApplicationContext());

        mGpsTracker = new GPSTracker(this);

        //pref = new Store_pref(ctx);
        Log.i("oncreate ", "service");
        //	timer = new Timer();

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 10 seconds
                handler.postDelayed(this, 1 * 60000);
                Log.i("new call after 5 MIN ", "service" + getCurrentTimeFormat() + " " + mGpsTracker.getLatitude() + "," + mGpsTracker.getLongitude());
                //   callSendMessage();
//                callSendMessageAPI();
            }
        }, 5000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        System.out.println("service stopped");
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }


    private String getCurrentTimeFormat() {
        String time = "";
        SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss a");
        Calendar c = Calendar.getInstance();
        time = df.format(c.getTime());

        return time;
    }

    public void callSendMessageAPI() {
        mGpsTracker = new GPSTracker(this);
        driver_id = session.getDriverID().get(SessionManager.KEY_ID);
        vehicle_id = session.getVehicleid();

        RequestQueue queue = Volley.newRequestQueue(ctx);

        StringRequest myReq = new StringRequest(Request.Method.POST,
                Constants.live_tracking,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("result ", "" + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("oops.. ", "" + error.getMessage());
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("driver_id", driver_id);
                params.put("vehicle_id", vehicle_id);
                params.put("vehicle_lat", mGpsTracker.getLatitude() + "");
                params.put("vehicle_lng", mGpsTracker.getLongitude() + "");
                params.put("booking_autoid", "0");

                mGpsTracker.removeUpdates();
                mGpsTracker.getLocation();
                return params;
            }
        };
        myReq.setShouldCache(false);
        myReq.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(myReq);
    }


    public void callSendMessage() {
        mGpsTracker = new GPSTracker(this);

        driver_id = "" + session.getDriverID().get(SessionManager.KEY_ID);
        vehicle_id = "" + session.getVehicleid();
        RequestQueue queue = Volley.newRequestQueue(ctx);

        StringRequest myReq = new StringRequest(Request.Method.POST,
                "http://demo.invicainfotech.com/tracker1/post.php",
                ReqSuccessListener(),
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progress.dismiss();
                        Log.e("oops.. ", "" + error.getMessage());
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("data", "" + getCurrentTimeFormat() + " " + mGpsTracker.getLatitude() + "," + mGpsTracker.getLongitude());
                params.put("data", getCurrentTimeFormat() + " " + driver_id + " " + vehicle_id + " " + mGpsTracker.getLatitude() + "," + mGpsTracker.getLongitude());
                mGpsTracker.removeUpdates();
                mGpsTracker.getLocation();
                return params;
            }

        };
        myReq.setShouldCache(false);
        myReq.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(myReq);

    }

    private Response.Listener<String> ReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //progress.dismiss();
                Log.e("result ", "" + response);

            }
        };

    }

}
