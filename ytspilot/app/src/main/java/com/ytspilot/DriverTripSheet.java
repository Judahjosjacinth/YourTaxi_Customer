package com.ytspilot;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ytspilot.R;
import com.ytspilot.util.NetworkHandler.HTTP_METHOD;
import com.ytspilot.util.NetworkHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.ytspilot.R.id.skm;

import androidx.appcompat.app.ActionBar;

@SuppressLint("NewApi")
public class DriverTripSheet extends BaseActivity implements
        OnClickListener {

    static final int DATE_DIALOG_ID = 999;
    static final int TIME_DIALOG_ID = 1000;

    private int hour;
    private int minute;
    private int year;
    private int month;
    private int day;

    private int cyear;
    private int cmonth;
    private int cday;

    EditText date, time, startKM, endKM, bookingID, vts_fare, vts_sub,
            vts_bonus, act_fare, act_sub, act_bonus, customernumber;
    //    Spinner issue;
//    Spinner source;
    Button submit;

    CustomAutoCompleteTextView from, to;
    String newstring;
    private ProgressDialog dialog;
    AlertDialog.Builder alert;

    PlacesTask placesTask;
    ParserTask parserTask;
    String datetopass;
    RadioGroup payment;
    RadioGroup issue;
    String payment_wallet = "N";
    String issue_ = "No";

    TextView source;

    Button uber;
    Button ola;
    Button yts;
    Button personal;
    Button others;


//    ArrayList<String> trip_source = new ArrayList<String>();

    String trip_source = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_trip_sheet);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        cyear = c.get(Calendar.YEAR);
        cmonth = c.get(Calendar.MONTH);
        cday = c.get(Calendar.DAY_OF_MONTH);

        session = new SessionManager(this);
        mGson = new Gson();

        driver_id = session.getDriverID().get(SessionManager.KEY_ID);
        vehicle_id = session.getVehicleid();

//
//        trip_source.add("YTS");
//        trip_source.add("SMILESCABS");
//        trip_source.add("PERSONAL");
//        trip_source.add("OLA");
//        trip_source.add("UBER");
//        trip_source.add("OPERATOR");

        source = (TextView) findViewById(R.id.source);

        uber = (Button) findViewById(R.id.uber);
        ola = (Button) findViewById(R.id.ola);
        yts = (Button) findViewById(R.id.yts);
        personal = (Button) findViewById(R.id.personal);
        others = (Button) findViewById(R.id.others);

        uber.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                source.setText("UBER");
            }
        });
        ola.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                source.setText("OLA");

            }
        });
        yts.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                source.setText("YTS");
            }
        });
        personal.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                source.setText("PERSONAL");

            }
        });

        others.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                source.setText("OTHERS");

            }
        });

        date = (EditText) findViewById(R.id.date);
        time = (EditText) findViewById(R.id.time);
        from = (CustomAutoCompleteTextView) findViewById(R.id.from);
        to = (CustomAutoCompleteTextView) findViewById(R.id.to);
        startKM = (EditText) findViewById(skm);
        endKM = (EditText) findViewById(R.id.ekm);
        bookingID = (EditText) findViewById(R.id.bookid);
        vts_fare = (EditText) findViewById(R.id.v_fare);
        vts_sub = (EditText) findViewById(R.id.vt_subsidy);
        vts_bonus = (EditText) findViewById(R.id.vt_bonus);
        act_fare = (EditText) findViewById(R.id.act_fare);
        act_sub = (EditText) findViewById(R.id.act_subsidy);
        act_bonus = (EditText) findViewById(R.id.act_bonus);
        customernumber = (EditText) findViewById(R.id.cust_number);


//        issue = (Spinner) findViewById(issue);
//        source = (Spinner) findViewById(source);

        issue = (RadioGroup) findViewById(R.id.issue_rg);
        payment = (RadioGroup) findViewById(R.id.wallet);

//        ArrayAdapter<String> tripsourceadapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_spinner_item, trip_source);
//        tripsourceadapter
//                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        source.setAdapter(tripsourceadapter);


        customernumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
                        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 2);
                        //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                    } else {
                        Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                        pickContact.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                        startActivityForResult(pickContact, 1);
                    }
                }
            }
        });

//        customernumber.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
//                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 2);
//                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
//                } else {
//                    Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
//                    pickContact.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
//                    startActivityForResult(pickContact, 1);
//                }
//            }
//        });

        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(this);
        date.setOnClickListener(this);
        time.setOnClickListener(this);

        payment.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.yes) {
                    payment_wallet = "Y";
                } else {

                    payment_wallet = "N";
                }
            }

        });


        issue.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.issue_yes) {
                    issue_ = "Yes";
                } else {
                    issue_ = "No";
                }
            }

        });

        from.setThreshold(1);

        from.addTextChangedListener(new TextWatcher() {

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

        to.setThreshold(1);

        to.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                placesTask = new PlacesTask();
                placesTask.execute(s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case (1):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = DriverTripSheet.this.getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String number = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if(number!=null && !number.equalsIgnoreCase("")){
                            number=number.replaceAll("[\\D]", "");
                            customernumber.setText(number);
                        }
                    }
                }
                break;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                pickContact.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(pickContact, 1);
            } else {
                Toast.makeText(DriverTripSheet.this, "Until you grant the permission, we cannot display the names", Toast.LENGTH_SHORT).show();
            }
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
        @SuppressLint("SimpleDateFormat")
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            // set selected date into textview
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

            String currentdate = new StringBuilder().append(cday).append("/")
                    .append(cmonth + 1).append("/").append(cyear).append("")
                    .toString();

            String seelecteddat = new StringBuilder().append(day).append("/")
                    .append(month + 1).append("/").append(year).append("")
                    .toString();
            Date dates = new Date();
            try {
                dates = format.parse(currentdate);
                Date datee = format.parse(seelecteddat);


                System.out.println(getDaysDifference(datee, dates));
                int timedate = getDaysDifference(datee, dates);
                if ( timedate < 0 ) {

                    date.setText("");

                    Toast.makeText(DriverTripSheet.this,
                            "Date should not be future date",
                            Toast.LENGTH_SHORT).show();
                }else if(timedate >14){
                    date.setText("");

                    Toast.makeText(DriverTripSheet.this,
                            "Date should not be less then 2 week",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // set selected date itntao textview
                    date.setText(new StringBuilder().append(day).append("-")
                            .append(month + 1).append("-").append(year)
                            .append(""));
                    datetopass = new StringBuilder().append(year).append("-")
                            .append(month + 1).append("-").append(day)
                            .toString();

                }
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

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

    public class HttpAsyncTask extends AsyncTask<String, Void, String> {
        ContentValues arguments;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            arguments = new ContentValues();
            arguments.put("driver_id", driver_id);
            arguments.put("vehicle_id", vehicle_id);

            arguments.put("trip_date", newstring.trim());

            arguments.put("trip_time", time.getText().toString().trim());

            arguments.put("trip_from", from.getText().toString().trim());
            arguments.put("trip_to", to.getText().toString().trim());
            arguments.put("starting_km", startKM.getText().toString().trim());
            arguments.put("ending_km", endKM.getText().toString().trim());
            arguments.put("booking_id", bookingID.getText().toString().trim());

            arguments.put("vts_fare", vts_fare.getText().toString().trim());


            arguments.put("vts_subsidy", vts_sub.getText().toString().trim());
            arguments.put("vts_bonus", vts_bonus.getText().toString().trim());
            arguments.put("actual_fare", act_fare.getText().toString().trim());
            arguments
                    .put("actual_subsidy", act_sub.getText().toString().trim());

            arguments
                    .put("actual_bonus", act_bonus.getText().toString().trim());


            arguments.put("issue", issue_);
            arguments.put("trip_source", source.getText().toString().trim());
            arguments.put("customer_phone", customernumber.getText().toString()
                    .trim());
            arguments.put("is_wallet_payment", payment_wallet.trim());


        }

        @Override
        protected String doInBackground(String... urls) {// Build JSON string\\
            return NetworkHandler.getStringFromURL(urls[0], arguments,
                    HTTP_METHOD.POST);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            System.out.println("Result::::" + result);
            if (dialog.isShowing())
                dialog.dismiss();
            try {
                JSONObject res = new JSONObject(result);

                if (res.getString("status_code").equals("1")) {
                    alert = new AlertDialog.Builder(DriverTripSheet.this);
                    alert.setTitle("Success!")

                            .setMessage(
                                    "Driver trip sheet date has posted successfully")
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            // TODO Auto-generated method stub
                                            date.setText("");
                                            time.setText("");
                                            from.setText("");
                                            to.setText("");
                                            startKM.setText("");
                                            endKM.setText("");
                                            bookingID.setText("");
                                            vts_fare.setText("");
                                            vts_sub.setText("");
                                            vts_bonus.setText("");
                                            act_fare.setText("");
                                            act_sub.setText("");
                                            act_bonus.setText("");
                                            customernumber.setText("");

                                            finish();
                                        }
                                    });

                    AlertDialog build = alert.create();
                    build.show();
                } else {
                    alert = new AlertDialog.Builder(DriverTripSheet.this);
                    alert.setTitle("Sorry!")

                            .setMessage(
                                    "Driver trip sheet date has not posted successfully")
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

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        if (vts_sub.getText().toString().length() == 0) {
            vts_sub.setText("0");
        }
        if (vts_bonus.getText().toString().length() == 0) {
            vts_bonus.setText("0");
        }


        if (act_sub.getText().toString().length() == 0) {

            act_sub.setText("0");
        }
        if (act_bonus.getText().toString().length() == 0) {
            act_bonus.setText("0");
        }


        if (v == date) {
            showDialog(DATE_DIALOG_ID);
        } else if (v == time) {
            showDialog(TIME_DIALOG_ID);
        } else {

            if (source.getText().toString().trim().contains("Please select source")) {
                ShowToast("Please select source");
            } else if (date.getText().toString().length() == 0) {
                alert = new AlertDialog.Builder(DriverTripSheet.this);
                alert.setTitle("Alert!")

                        .setMessage("Please enter date")
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
                alert = new AlertDialog.Builder(DriverTripSheet.this);
                alert.setTitle("Alert!")

                        .setMessage("Please enter time")
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

            } else if (from.getText().toString().length() == 0) {
                alert = new AlertDialog.Builder(DriverTripSheet.this);
                alert.setTitle("Alert!")

                        .setMessage("Please enter starting point location")
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

            } else if (to.getText().toString().length() == 0) {
                alert = new AlertDialog.Builder(DriverTripSheet.this);
                alert.setTitle("Alert!")

                        .setMessage("Please enter destination location")
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

            } else if (date.getText().toString().length() == 0) {
                alert = new AlertDialog.Builder(DriverTripSheet.this);
                alert.setTitle("Alert!")

                        .setMessage("Please enter destination landmark")
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

            } else if (vts_fare.getText().toString().length() == 0) {
                alert = new AlertDialog.Builder(DriverTripSheet.this);
                alert.setTitle("Alert!")

                        .setMessage("Please enter VTS fare")
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

            } else if (act_fare.getText().toString().length() == 0) {
                    alert = new AlertDialog.Builder(DriverTripSheet.this);
                    alert.setTitle("Alert!")

                            .setMessage("Please enter actual fare")
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

                }  else {

                    System.out.println(datetopass);
                    String oldstring = datetopass;
                    try {
                        Date date_ = new SimpleDateFormat("yyyy-MM-dd")
                                .parse(oldstring);
                        System.out.println(date_);
                        newstring = new SimpleDateFormat("yyyy-MM-dd")
                                .format(date_);
                        System.out.println(newstring); // 2011-01-18
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                    String startkm = startKM.getText().toString().trim();
                    String endkm = endKM.getText().toString().trim();

                    int startkmint = 0;
                    int endkmint = 0;


                    try {
                        startkmint = Integer.parseInt(startkm);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    try {
                        endkmint = Integer.parseInt(endkm);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }


                    if (startkmint < endkmint) {

                        if (NetworkHandler.isOnline(this)) {
                            dialog = ProgressDialog.show(DriverTripSheet.this,
                                    "Requirement request",
                                    "Processing your request. Please wait...", true);
                            new HttpAsyncTask()
                                    .execute("https://www.yourtaxistand.com/myapps/driver_tripsheet");
                        } else {
                            Toast.makeText(DriverTripSheet.this, R.string.nointernet, Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        Toast.makeText(DriverTripSheet.this, "Start km not be more then End km", Toast.LENGTH_SHORT).show();
                    }
                }
        }
    }

    private class PlacesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... place) {
            // For storing data from web service
            String data = "";

            // Obtain browser key from https://code.google.com/apis/console
          //  String key = "key=AIzaSyDl9tzf0lPHW0oWj7MnPFOX1pOXjVxKjJI";
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
                data = downloadUrl(url);
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

            String[] from_ = new String[]{"description"};
            int[] to_ = new int[]{android.R.id.text1};

            // Creating a SimpleAdapter for the AutoCompleteTextView
            SimpleAdapter adapter = new SimpleAdapter(DriverTripSheet.this,
                    result, android.R.layout.simple_list_item_1, from_, to_);

            // Setting the adapter
            from.setAdapter(adapter);
            to.setAdapter(adapter);
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception while dwnld", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    public static int getDaysDifference(Date fromDate, Date toDate) {
        if (fromDate == null || toDate == null)
            return 0;

        return (int) ((toDate.getTime() - fromDate.getTime()) / (1000 * 60 * 60 * 24));
    }

}
