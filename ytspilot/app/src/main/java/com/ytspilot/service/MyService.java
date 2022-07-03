package com.ytspilot.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.orm.SugarRecord;
import com.ytspilot.SessionManager;
import com.ytspilot.model.OfflineLocation;
import com.ytspilot.util.Constants;
import com.ytspilot.util.NetworkHandler;
import com.ytspilot.util.Store_pref;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyService extends Service {
    private static final String TAG = "Mytracker";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 120000;
    private static final float LOCATION_DISTANCE = 100f;

    SessionManager session;
    String driver_id = "";
    String vehicle_id = "";

    Store_pref mStore_pref;
    Context context;
    Gson gson;

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            Log.e(TAG, "onLocationChanged accuracy: " + location.getAccuracy() + " " + location.getProvider());

            mLastLocation.set(location);

            OfflineLocation offlineLocation = new OfflineLocation();
            offlineLocation.setLat("" + location.getLatitude());
            offlineLocation.setLng("" + location.getLongitude());
            offlineLocation.setTimestemp("" + System.currentTimeMillis());
            SugarRecord.saveInTx(offlineLocation);

            if (NetworkHandler.isOnline(context)) {
                //callSendMessageAPI(location);
                callSendMessageAPI();
            }

        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        session = new SessionManager(getApplicationContext());
        context = this;
        mStore_pref = new Store_pref(this);
        gson = new Gson();

        try {

            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }


            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }


    private String getCurrentTimeFormat() {
        String time = "";
        SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss a");
        Calendar c = Calendar.getInstance();
        time = df.format(c.getTime());

        return time;
    }


    public void callSendMessageAPI() {

        List<OfflineLocation> mOfflineLocations = SugarRecord.listAll(OfflineLocation.class);
        final String lat_lng_data = gson.toJson(mOfflineLocations);
        Log.i("lat_lng_data", "" + lat_lng_data);

        driver_id = session.getDriverID().get(SessionManager.KEY_ID);
        vehicle_id = session.getVehicleid();

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest myReq = new StringRequest(Request.Method.POST,
                Constants.live_tracking,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("result ", "" + response);

                        SugarRecord.deleteAll(OfflineLocation.class);

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
                params.put("lat_lng_data", "" + lat_lng_data);
                params.put("booking_autoid", "" + mStore_pref.getAutoid());
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
}