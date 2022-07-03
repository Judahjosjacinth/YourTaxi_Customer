package com.ytspilot;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;

import com.ytspilot.util.NetworkHandler.HTTP_METHOD;
import com.ytspilot.util.NetworkHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

@SuppressLint("NewApi")
public class PaymentIssueSheet extends BaseActivity implements
        OnClickListener {

    Spinner issue;
    EditText date;

    static final int DATE_DIALOG_ID = 999;
    private int year;
    private int month;
    private int day;

    Button submit;

    ArrayList<String> issue_list = new ArrayList<String>();

    private ProgressDialog dialog;
    AlertDialog.Builder alert;

    String postdate;

    private int cyear;
    private int cmonth;
    private int cday;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ytspilot.R.layout.activity_payment_issue_sheet);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        session = new SessionManager(getApplicationContext());

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        cyear = c.get(Calendar.YEAR);
        cmonth = c.get(Calendar.MONTH);
        cday = c.get(Calendar.DAY_OF_MONTH);


        issue_list.add("Scratch in Car");
        issue_list.add("Dent in Car");
        issue_list.add("Tube exploded");
        issue_list.add("Tire Cut");
        issue_list.add("Side View Mirror broke");
        issue_list.add("Other Damage");
        issue_list.add("Payment not receive");
        issue_list.add("Puncture");
        issue_list.add("Airconditioned not working");
        issue_list.add("Starting Issue");
        issue_list.add("Internet access issue");
        issue_list.add("Low credit of Mobile");


        date = (EditText) findViewById(com.ytspilot.R.id.date);
        issue = (Spinner) findViewById(com.ytspilot.R.id.issue);

        ArrayAdapter<String> cityadapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, issue_list);
        cityadapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        issue.setAdapter(cityadapter);

        date.setOnClickListener(this);
        submit = (Button) findViewById(com.ytspilot.R.id.submit);
        submit.setOnClickListener(this);

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


            SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy");

            String currentdate = new StringBuilder().append(cday).append("/")
                    .append(cmonth + 1).append("/").append(cyear).append("")
                    .toString();

            String seelecteddat = new StringBuilder().append(day).append("/")
                    .append(month + 1).append("/").append(year).append("")
                    .toString();

            Date dates;

            try {
                dates = format.parse(currentdate);
                Date datee = format.parse(seelecteddat);


                // set selected date itntao textview
                // set selected date into textview
                date.setText(new StringBuilder().append(day).append("-")
                        .append(month + 1).append("-").append(year)
                        .append(""));

                postdate = new StringBuilder().append(year).append("-")
                        .append(month + 1).append("-").append(day)
                        .append("").toString();

            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    };

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == date) {
            showDialog(DATE_DIALOG_ID);
        } else {
            if (date.getText().toString().length() == 0) {
                alert = new AlertDialog.Builder(PaymentIssueSheet.this);
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

            } else {


                if (NetworkHandler.isOnline(this)) {
                    dialog = ProgressDialog.show(PaymentIssueSheet.this,
                            "Requirement request",
                            "Processing your request. Please wait...", true);
                    new HttpAsyncTask()
                            .execute("https://www.yourtaxistand.com/myapps/other_issue");
                } else {
                    Toast.makeText(PaymentIssueSheet.this, com.ytspilot.R.string.nointernet, Toast.LENGTH_SHORT).show();
                }

            }
        }
    }


    public class HttpAsyncTask extends AsyncTask<String, Void, String> {

        String message;

        @SuppressLint("SimpleDateFormat")
        @Override
        protected String doInBackground(String... urls) {// Build JSON string\\

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
            HashMap<String, String> user = session.getDriverID();
            arguments.put("driver_id",
                    user.get(SessionManager.KEY_ID));

            arguments.put("issue_date", newstring.trim()
                    .trim() + " 00:00:00");

            if (issue.getSelectedItemPosition() == 0) {
                message = "Failed";
            } else {
                message = "Did not receive";
            }

            arguments.put("issue_type", issue
                    .getSelectedItem().toString().trim());
            arguments.put("message", message.trim());
            return NetworkHandler.getStringFromURL(urls[0], arguments, HTTP_METHOD.POST);
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
                    alert = new AlertDialog.Builder(PaymentIssueSheet.this);
                    alert.setTitle("Success!")

                            .setMessage(
                                    "General issue sheet date has posted successfully")
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            // TODO Auto-generated method stub
                                            finish();
                                        }
                                    });

                    AlertDialog build = alert.create();
                    build.show();
                } else {
                    alert = new AlertDialog.Builder(PaymentIssueSheet.this);
                    alert.setTitle("Sorry!")

                            .setMessage(
                                    "General issue sheet date has not posted successfully")
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

    public static int getDaysDifference(Date fromDate, Date toDate) {
        if (fromDate == null || toDate == null)
            return 0;

        return (int) ((toDate.getTime() - fromDate.getTime()) / (1000 * 60 * 60 * 24));
    }

}
