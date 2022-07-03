package com.ytspilot;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
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
import com.ytspilot.R;
import com.ytspilot.model.Trip;
import com.ytspilot.model.response.RequirementDriver;
import com.ytspilot.util.APIUtil;
import com.ytspilot.util.Constants;
import com.ytspilot.util.GPSTracker;
import com.ytspilot.util.NetworkHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BookingsListActivity extends BaseActivity {

    public static final String BOOKING_ID_JSON = "booking_id";
    public static final String BOOKING_FROM_JSON = "pickup_point";
    public static final String BOOKING_TO_JSON = "destination";
    public static final String BOOKING_FARE_JSON = "fixed_amount";
    private ListView bookingsList;
    private static String bookingsRequestURL = APIUtil.URL_API_HOST
            + APIUtil.URL_API_BOOKINGS_LIST;
    private static String API_USER_ID = "driver_id";
    public static final String BOOKING_ID_ARG = "booking_id";
    public static final String BOOKING_TRIP_TYPE = "trip_type";

    GPSTracker mGpsTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        bookingsList = (ListView) findViewById(R.id.lv_bookings);
        session = new SessionManager(this);
        mGson = new Gson();
        driver_id = session.getDriverID().get(SessionManager.KEY_ID);
        vehicle_id = session.getVehicleid();


        mGpsTracker = new GPSTracker(this);
        mGpsTracker.getLocation();

        if (NetworkHandler.isOnline(this)) {
            callReq_booking();
//            new BookingListTask().execute(bookingsRequestURL);
        } else {
            ShowToast("No internet available");
        }
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

//    class BookingListTask extends AsyncTask<String, Void, String> {
//        ProgressDialog loadingProgressDialog;
//
//        @Override
//        protected void onPreExecute() {
//            // TODO Auto-generated method stub
//            super.onPreExecute();
//            loadingProgressDialog = new ProgressDialog(BookingsListActivity.this);
//            loadingProgressDialog.setIndeterminate(true);
//            loadingProgressDialog.setMessage("Loading available bookings..");
//            loadingProgressDialog.show();
//        }
//
//        @Override
//        protected String doInBackground(String... urls) {
//            String requestUrl = urls[0];
//            ContentValues arguments = new ContentValues();
//            HashMap<String, String> userID = new SessionManager(
//                    getApplicationContext()).getDriverID();
////            String userIDWithoutPrefix = userID.get(SessionManager.KEY_ID);
////            userIDWithoutPrefix = userIDWithoutPrefix.substring(1, userIDWithoutPrefix.length());
//            arguments.put(API_USER_ID, userID.get(SessionManager.KEY_ID));
//            return NetworkHandler.getStringFromURL(requestUrl, arguments,
//                    HTTP_METHOD.POST);
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//
//            Log.i("result", result);
//            if (loadingProgressDialog != null && loadingProgressDialog.isShowing())
//                loadingProgressDialog.dismiss();
//            try {
//                JSONArray responseArray = new JSONArray(result);
//                if (responseArray.length() > 0) {
//                    BookingsListAdapter bookingsListAdapter = new BookingsListAdapter(responseArray);
//                    bookingsList.setAdapter(bookingsListAdapter);
//                } else {
//                    ShowToast("No booking available");
//                    callReq_driver();
//                }
//            } catch (JSONException exception) {
//                exception.printStackTrace();
//            }
//        }
//
//    }



    private class BookingsTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = ProgressDialog.show(
                    BookingsListActivity.this, "", "Please wait...");

            progress.setCancelable(false);

        }

        @Override
        protected String doInBackground(String... place) {

            ContentValues arguments = new ContentValues();
            arguments.put("driver_id", driver_id);
             String data="";

            try {
                // Fetching the data from web service in background
                data = NetworkHandler.getStringFromURL(bookingsRequestURL, arguments, NetworkHandler.HTTP_METHOD.GET);
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
               //JSONObject res = new JSONObject(response);
               JSONArray responseArray = new JSONArray(result);
               if (responseArray.length() > 0) {
                   BookingsListAdapter bookingsListAdapter = new BookingsListAdapter(responseArray);
                   bookingsList.setAdapter(bookingsListAdapter);
               } else {
                   // ShowToast("No booking avsailable");
                   callReq_driver();
               }
           } catch (JSONException exception) {
               exception.printStackTrace();
           }
       }

        }
    }
    public void callReq_booking() {

        new BookingsTask()
                .execute("");

    }

//    public void callReq_booking() {
//        RequestQueue queue = Volley.newRequestQueue(this);
//        progress = ProgressDialog.show(
//                this, "", "Please wait...");
//
//        progress.setCancelable(false);
//
//        StringRequest myReq = new StringRequest(Request.Method.POST,
//                bookingsRequestURL,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.e("result", response);
//                        progress.dismiss();
//                        try {
//                            //JSONObject res = new JSONObject(response);
//                            JSONArray responseArray = new JSONArray(response);
//                            if (responseArray.length() > 0) {
//                                BookingsListAdapter bookingsListAdapter = new BookingsListAdapter(responseArray);
//                                bookingsList.setAdapter(bookingsListAdapter);
//                            } else {
//                                // ShowToast("No booking avsailable");
//                                callReq_driver();
//                            }
//                        } catch (JSONException exception) {
//                            exception.printStackTrace();
//                        }
//
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
//
//                params.put("driver_id", "" + driver_id);
//
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


    public void callReq_driver() {
        RequestQueue queue = Volley.newRequestQueue(this);
        progress = ProgressDialog.show(
                this, "", "Please wait...");

        progress.setCancelable(false);

        StringRequest myReq = new StringRequest(Request.Method.POST,
                Constants.list_requirements_driver,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("result", response);
                        progress.dismiss();
                        try {
                            RequirementDriver mRequirementDriver = new RequirementDriver();
                            mRequirementDriver = mGson.fromJson(response, RequirementDriver.class);

                            if (mRequirementDriver.getStatus_code() == 1) {
                                if (mRequirementDriver.getTrips_array().size() > 0) {

//                                    AvalableTriptAdapter mAvalableTriptAdapter = new AvalableTriptAdapter(getApplicationContext(), R.layout.item_availabletrip, mRequirementDriver.getTrips_array());
//                                    bookingsList.setAdapter(mAvalableTriptAdapter);
                                }

                            } else {
                                ShowToast("No trip Available");
                            }
                            AvalableTriptAdapter mAvalableTriptAdapter = new AvalableTriptAdapter(getApplicationContext(), R.layout.item_availabletrip, mRequirementDriver.getTrips_array());
                            bookingsList.setAdapter(mAvalableTriptAdapter);

                        } catch (Exception e) {
                            Log.e("opps", "" + e);
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


    public class AvalableTriptAdapter extends ArrayAdapter<Trip> {

        private final LayoutInflater inflater;
        private final int itemLayout;


        public AvalableTriptAdapter(Context context, int itemLayout, List<Trip> mTripslist) {
            super(context, itemLayout, mTripslist);
            inflater = LayoutInflater.from(context);
            this.itemLayout = itemLayout;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(itemLayout, null);


                holder = new ViewHolder();
                holder.date_time = (TextView) convertView.findViewById(R.id.date_time);
                holder.status = (TextView) convertView.findViewById(R.id.status);
                holder.type_pkg = (TextView) convertView.findViewById(R.id.type_pkg);
                holder.from = (TextView) convertView.findViewById(R.id.from);
                holder.to = (TextView) convertView.findViewById(R.id.to);
                holder.btn_details = (Button) convertView.findViewById(R.id.btn_details);
                holder.btn_accepted = (Button) convertView.findViewById(R.id.btn_accepted);
                convertView.setTag(holder);
            }
            holder = (ViewHolder) convertView.getTag();

            final Trip mTrip = getItem(position);

            holder.date_time.setText(mTrip.getPickup_date() + " " + mTrip.getPickup_time());
            holder.status.setText(mTrip.getBid_status());
            if(mTrip.getPackage_value().length()>0) {
                holder.type_pkg.setText(mTrip.getTrip_type() + " (" + mTrip.getPackage_value() + ") ");
            }else{
                holder.type_pkg.setText(mTrip.getTrip_type()+"");
            }
            holder.from.setText(mTrip.getPickup_point() + "");
            holder.to.setText(mTrip.getDestination() + "");

            //Check is Quick Booking or Not
            if(mTrip.getIsQuickBooking().equals("0"))
            {
                holder.btn_details.setVisibility(View.VISIBLE);
                holder.btn_accepted.setText("Bid Now");
            }

            holder.btn_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            final ViewHolder finalHolder = holder;

            holder.btn_accepted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

             //       if (mTrip.getTrip_type().equalsIgnoreCase("LT")) {

                    if(mTrip.getIsQuickBooking().equals("0"))
                    {
                        Intent intent = new Intent(v.getContext(), BidActivity.class);
                        intent.putExtra(BookingsListActivity.BOOKING_ID_ARG, mTrip.getBooking_id());
                        intent.putExtra(BookingsListActivity.BOOKING_TRIP_TYPE, mTrip.getTrip_type());
                        startActivity(intent);
                    }
                    else {
                        callaccept_trip_driver(mTrip.getBooking_id(), "0");
                    }

            /*        } else {


                        AlertDialog.Builder alert = new AlertDialog.Builder(BookingsListActivity.this);
                        final EditText edittext = new EditText(getApplicationContext());
                        edittext.setTextColor(getResources().getColor(R.color.black));
                        alert.setMessage("Enter Your estimated permit");

                        alert.setView(edittext);

                        alert.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //What ever you want to do with the value

                                String YouEditTextValue = edittext.getText().toString();
                                if (YouEditTextValue.trim().length() > 0) {
                                    callaccept_trip_driver(mTrip.getBooking_id(), YouEditTextValue);

                                } else {
                                    ShowToast("PLease enter estimated permit");
                                }

                            }
                        });

                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // what ever you want to do with No option.
                            }
                        });

                        alert.show();
                    }*/

                }
            });

            return convertView;
        }


        class ViewHolder {

            TextView date_time;
            TextView status;
            TextView type_pkg;
            TextView from;
            TextView to;

            Button btn_details;
            Button btn_accepted;
            Button btn_cancel;


        }

    }


    public void callaccept_trip_driver(final String booklinid, final String estimated_permit) {
        RequestQueue queue = Volley.newRequestQueue(this);
        progress = ProgressDialog.show(
                this, "", "Please wait...");

        progress.setCancelable(false);

        StringRequest myReq = new StringRequest(Request.Method.POST,
                Constants.accept_trip_driver,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("result", response);
                        progress.dismiss();

                        try {

                            RequirementDriver mRequirementDriver = new RequirementDriver();
                            mRequirementDriver = mGson.fromJson(response, RequirementDriver.class);
                            if (mRequirementDriver.getStatus_code() == 1) {

                                ShowToast(mRequirementDriver.getStatus_message());
                                if (NetworkHandler.isOnline(BookingsListActivity.this)) {
                                    callReq_booking();
//                                    new BookingListTask().execute(bookingsRequestURL);
                                } else {
                                    Toast.makeText(BookingsListActivity.this, R.string.nointernet, Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                ShowToast("No trip Available");
                            }

                        } catch (Exception e) {
                            Log.e("opps", "" + e);
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
                params.put("booking_id", "" + booklinid);
                params.put("estimated_permit", "" + estimated_permit);

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

    private class CancelTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = ProgressDialog.show(
                    BookingsListActivity.this, "", "Please wait...");

            progress.setCancelable(false);

        }

        @Override
        protected String doInBackground(String... place) {

            ContentValues arguments = new ContentValues();

            arguments.put("driver_id", driver_id);
            arguments.put("booking_id", place[0]);
            String data="";

            try {
                // Fetching the data from web service in background
                data = NetworkHandler.getStringFromURL(Constants.cancel_trip_bydriver, arguments, NetworkHandler.HTTP_METHOD.GET);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progress.dismiss();
            if (NetworkHandler.isOnline(BookingsListActivity.this)) {
                callReq_booking();
//            new BookingListTask().execute(bookingsRequestURL);
            } else {
                ShowToast("No internet available");
            }

        }
    }
    public void callcancel_trip_driver(final String booklinid) {

        new CancelTask()
                .execute(booklinid);

    }

//    public void callcancel_trip_driver(final String booklinid) {
//        RequestQueue queue = Volley.newRequestQueue(this);
//        progress = ProgressDialog.show(
//                this, "", "Please wait...");
//
//        progress.setCancelable(false);
//
//        StringRequest myReq = new StringRequest(Request.Method.POST,
//                Constants.cancel_trip_bydriver,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//
//                        Log.e("result", response);
//                        progress.dismiss();
//                        if (NetworkHandler.isOnline(BookingsListActivity.this)) {
//                            callReq_booking();
////            new BookingListTask().execute(bookingsRequestURL);
//                        } else {
//                            ShowToast("No internet available");
//                        }
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
//                params.put("driver_id", "" + driver_id);
//                params.put("booking_id", "" + booklinid);
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


    public class BookingsListAdapter extends BaseAdapter {
        JSONArray bookingsArray;

        public BookingsListAdapter(JSONArray bookingsArray) {
            this.bookingsArray = bookingsArray;
        }

        @Override
        public int getCount() {
            return bookingsArray.length();
        }

        @Override
        public Object getItem(int position) {
            try {
                return bookingsArray.getJSONObject(position);
            } catch (JSONException JSONexception) {
                Log.d(BookingsListAdapter.class.getSimpleName(),
                        "getItem exception");
                JSONexception.printStackTrace();
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup container) {
            RowHolder rowHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(container.getContext()).inflate(
                        R.layout.row_bookings, container, false);
                rowHolder = new RowHolder();
                rowHolder.bookingID = (TextView) convertView
                        .findViewById(R.id.tv_row_booking_id);
                rowHolder.bookingFrom = (TextView) convertView
                        .findViewById(R.id.tv_row_booking_from);
                rowHolder.bookingTo = (TextView) convertView
                        .findViewById(R.id.tv_row_booking_to);
                rowHolder.bookingFare = (TextView) convertView
                        .findViewById(R.id.tv_row_booking_fare);
                rowHolder.name = (TextView) convertView
                        .findViewById(R.id.name);


                rowHolder.btn_cancel = (Button) convertView
                        .findViewById(R.id.btn_cancel);
                convertView.setTag(rowHolder);
            } else {
                rowHolder = (RowHolder) convertView.getTag();
            }
            try {
                final JSONObject bookingObject = bookingsArray
                        .getJSONObject(position);
                final String bookingID = bookingObject
                        .getString(BookingsListActivity.BOOKING_ID_JSON);
                rowHolder.bookingID.setText("Booking ID: " + bookingID);
                rowHolder.bookingFrom.setText("From: " + bookingObject
                        .getString(BookingsListActivity.BOOKING_FROM_JSON));
                rowHolder.bookingTo.setText("To: " + bookingObject
                        .getString(BookingsListActivity.BOOKING_TO_JSON));
                rowHolder.bookingFare
                        .setText("Fare: " + bookingObject.getString(BookingsListActivity.BOOKING_FARE_JSON));
                rowHolder.name
                        .setText("" + bookingObject.getString("customer_name"));


                rowHolder.btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callcancel_trip_driver(bookingID);
                    }
                });
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), Meter.class);
                        intent.putExtra(BookingsListActivity.BOOKING_ID_ARG,
                                bookingID);

                        try {
                            intent.putExtra("number", bookingObject.getString("phone_number"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        startActivity(intent);
                        finish();
                    }
                });
            } catch (Exception JSONexception) {
                JSONexception.printStackTrace();
            }
            return convertView;
        }

        class RowHolder {
            TextView bookingID;
            TextView bookingFrom;
            TextView bookingTo;
            TextView bookingFare;
            TextView name;
            Button btn_cancel;

        }

    }

}
