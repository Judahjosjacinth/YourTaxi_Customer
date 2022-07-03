package com.ytspilot;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;

import com.ytspilot.R;
import com.ytspilot.util.NetworkHandler.HTTP_METHOD;
import com.ytspilot.util.NetworkHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@SuppressLint("NewApi")
public class LoginArea extends BaseActivity implements OnClickListener {

    private ProgressDialog dialog;
    AlertDialog.Builder alert;
    Button submit;

    PlacesTask placesTask;
    ParserTask parserTask;

    EditText date, time;
    CustomAutoCompleteTextView area;

    static final int DATE_DIALOG_ID = 999;
    static final int TIME_DIALOG_ID = 1000;

    private int hour;
    private int minute;
    private int year;
    private int month;
    private int day;

    String postdate;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_area);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        session = new SessionManager(getApplicationContext());

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        date = (EditText) findViewById(R.id.l_date);
        time = (EditText) findViewById(R.id.l_time);
        area = (CustomAutoCompleteTextView) findViewById(R.id.area);

        submit = (Button) findViewById(R.id.submit_login_area);

        submit.setOnClickListener(this);
        date.setOnClickListener(this);
        time.setOnClickListener(this);

        area.setThreshold(1);

        area.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                placesTask = new PlacesTask();
                placesTask.execute(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });
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

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListener, year, month,
                        day);
            case TIME_DIALOG_ID:
                // set time picker as current time
                return new TimePickerDialog(this, timePickerListener, hour, minute,
                        false);

        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            // set selected date into textview
            date.setText(new StringBuilder().append(day).append("-")
                    .append(month + 1).append("-").append(year).
                            append(""));

            postdate = new StringBuilder().append(year).append("-")
                    .append(month + 1).append("-").append(day).append("")
                    .toString();

        }
    };

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int selectedHour,
                              int selectedMinute) {
            hour = selectedHour;
            minute = selectedMinute;

            // set current time into textview
            time.setText(new StringBuilder().append(pad(hour)).append(":")
                    .append(pad(minute)));

            // set current time into timepicker

        }
    };

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    private class PlacesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... place) {
            // For storing data from web service
            String data = "";

            // Obtain browser key from https://code.google.com/apis/console

         //   String key = "key=AIzaSyDl9tzf0lPHW0oWj7MnPFOX1pOXjVxKjJI";
            String key = "key=AIzaSyAaI2BSdSs0X3M2L7XYvP1UU8BaINLfnLc";
            String input = "";

            try {
                input = "input=" + URLEncoder.encode(place[0], "utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            // place type to be searched
            String types = "types=geocode";

            // Sensor enabled
            String sensor = "sensor=false";

            // Building the parameters to the web service
            String parameters = input + "&" + types + "&" + sensor + "&" + key + "&" + "components=country:IN";

            // Output format
            String output = "json";

            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"
                    + output + "?" + parameters;
            try {

                // Fetching the data from web service in background
                data = NetworkHandler.getStringFromURL(url, null, HTTP_METHOD.GET);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Creating ParserTask
            parserTask = new ParserTask();

            // Starting Parsing the JSON string returned by Web Service
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends
            AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        @Override
        protected List<HashMap<String, String>> doInBackground(
                String... jsonData) {

            List<HashMap<String, String>> places = null;

            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try {
                jObject = new JSONObject(jsonData[0]);

                // Getting the parsed data as a List construct
                places = placeJsonParser.parse(jObject);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {

            String[] from = new String[]{"description"};
            int[] to = new int[]{android.R.id.text1};

            // Creating a SimpleAdapter for the AutoCompleteTextView
            SimpleAdapter adapter = new SimpleAdapter(LoginArea.this, result,
                    android.R.layout.simple_list_item_1, from, to);

            // Setting the adapter
            area.setAdapter(adapter);
        }
    }


    public class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @SuppressLint("SimpleDateFormat")
        @Override
        protected String doInBackground(String... urls) {// Build JSON string\\

            HashMap<String, String> user = session.getDriverID();
            String newstring = null;
            try {
                Date date_ = new SimpleDateFormat("yyyy-MM-dd")
                        .parse(postdate);
                System.out.println(date_);
                newstring = new SimpleDateFormat("yyyy-MM-dd")
                        .format(date_);
                System.out.println(newstring); // 2011-01-18
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            ContentValues arguments = new ContentValues();

            arguments.put("driver_id",
                    user.get(SessionManager.KEY_ID));

            arguments.put("login_area", area
                    .getText().toString().trim());

            arguments.put("pref_login_time", newstring.trim()
                    + " "
                    + time.getText().toString().trim() + ":00");
            return NetworkHandler.getStringFromURL(urls[0], arguments, HTTP_METHOD.POST);
        }

        @Override
        protected void onPostExecute(String result) {

            System.out.println("Result::::" + result);
            if (dialog.isShowing())
                dialog.dismiss();
            try {
                JSONObject res = new JSONObject(result);

                if (res.getString("status_code").equals("1")) {
                    alert = new AlertDialog.Builder(LoginArea.this);
                    alert.setTitle("Success!")

                            .setMessage(
                                    "Driver login area detail has been posted successfully")
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
                    alert = new AlertDialog.Builder(LoginArea.this);
                    alert.setTitle("Sorry!")

                            .setMessage(
                                    "Driver login area detail has not been posted successfully")
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

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == date) {
            showDialog(DATE_DIALOG_ID);
        } else if (v == time) {
            showDialog(TIME_DIALOG_ID);
        } else {

            if (date.getText().toString().length() == 0) {
                alert = new AlertDialog.Builder(LoginArea.this);
                alert.setTitle("Alert!")

                        .setMessage("Please enter referel driver id")
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub

                                    }
                                });

                AlertDialog build = alert.create();
                build.show();

            } else if (time.getText().toString().length() == 0) {
                alert = new AlertDialog.Builder(LoginArea.this);
                alert.setTitle("Alert!")

                        .setMessage("Please enter referel driver name")
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub

                                    }
                                });

                AlertDialog build = alert.create();
                build.show();

            } else if (area.getText().toString().length() == 0) {
                alert = new AlertDialog.Builder(LoginArea.this);
                alert.setTitle("Alert!")

                        .setMessage("Please enter referel driver mobile number")
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub

                                    }
                                });

                AlertDialog build = alert.create();
                build.show();

            } else {
                if (NetworkHandler.isOnline(this)) {
                    dialog = ProgressDialog.show(LoginArea.this,
                            "Requirement request",
                            "Processing your request. Please wait...", true);
                    new HttpAsyncTask()
                            .execute("https://www.yourtaxistand.com/myapps/driver_login");
                } else {
                    Toast.makeText(LoginArea.this, R.string.nointernet, Toast.LENGTH_SHORT).show();

                }
            }
        }
    }

}
