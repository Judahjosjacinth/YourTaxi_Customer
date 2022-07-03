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

import com.ytspilot.R;
import com.ytspilot.util.NetworkHandler.HTTP_METHOD;
import com.ytspilot.util.NetworkHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

@SuppressLint("NewApi")
public class Leave extends BaseActivity implements OnClickListener {

	EditText from, to;
	Button submit;
	Spinner reason;

	private ProgressDialog dialog;
	AlertDialog.Builder alert;

	public static String id;

	static final int DATE_DIALOG_ID = 999;
	static final int DATE_DIALOG_ID1 = 1000;

	private int year;
	private int month;
	private int day;

	String frompost, topost;
	ArrayList<String> issue_list = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leave);

		ActionBar ab = getSupportActionBar();
		if (ab != null) {
			ab.setDisplayHomeAsUpEnabled(true);
		}

		issue_list.add("Select leave reason");
		issue_list.add("Not well");
		issue_list.add("Personal work");
		issue_list.add("Personal trip");
		issue_list.add("FC");
		issue_list.add("Service");
		issue_list.add("Lease of vehicle over");
		issue_list.add("No driver");

		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);

		from = (EditText) findViewById(R.id.from_l);
		to = (EditText) findViewById(R.id.to_l);
		reason = (Spinner) findViewById(R.id.reason);

		ArrayAdapter<String> cityadapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, issue_list);
		cityadapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		reason.setAdapter(cityadapter);

		submit = (Button) findViewById(R.id.submit_l);

		submit.setOnClickListener(this);
		from.setOnClickListener(this);
		to.setOnClickListener(this);
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
			return new DatePickerDialog(this, datePickerListenerfrom, year,
					month, day);
		case DATE_DIALOG_ID1:
			// set time picker as current time
			return new DatePickerDialog(this, datePickerListenerto, year,
					month, day);

		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener datePickerListenerfrom = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		@SuppressLint("SimpleDateFormat")
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;

			// set selected date itntao textview
			from.setText(new StringBuilder().append(day).append("-")
					.append(month + 1).append("-").append(year).append(""));
			frompost = new StringBuilder().append(year).append("-")
					.append(month + 1).append("-").append(day).toString();

		}
	};

	private DatePickerDialog.OnDateSetListener datePickerListenerto = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		@SuppressLint("SimpleDateFormat")
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;

			// set selected date itntao textview
			to.setText(new StringBuilder().append(day).append("-")
					.append(month + 1).append("-").append(year).append(""));
			topost = new StringBuilder().append(year).append("-")
					.append(month + 1).append("-").append(day).toString();

		}
	};

	public class HttpAsyncTask extends AsyncTask<String, Void, String> {
		@SuppressLint("SimpleDateFormat")
		@Override
		protected String doInBackground(String... urls) {// Build JSON string\\

			String from = null, to = null;
			try {
				Date date_from = new SimpleDateFormat("yyyy-MM-dd")
						.parse(frompost);
				Date date_to = new SimpleDateFormat("yyyy-MM-dd").parse(topost);
				System.out.println(date_from);
				from = new SimpleDateFormat("yyyy-MM-dd").format(date_from);
				to = new SimpleDateFormat("yyyy-MM-dd").format(date_to);
				System.out.println(from); // 2011-01-18
				System.out.println(to); // 2011-01-18
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ContentValues arguments = new ContentValues();
			arguments.put("driver_id", LoginActivity.id);

			arguments.put("from_date", from.trim());
			arguments.put("to_date", to.trim());
			arguments.put("reason", reason.getSelectedItem().toString().trim());
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

					alert = new AlertDialog.Builder(Leave.this);
					alert.setTitle("Alert!")

							.setMessage(
									"Leave request has been posted sucessfully.")
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
					alert = new AlertDialog.Builder(Leave.this);
					alert.setTitle("Sorry!")

							.setMessage(
									"Leave request not posted sucessfully.Please try again.")
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

		if (v == from) {
			showDialog(DATE_DIALOG_ID);
		} else if (v == to) {
			showDialog(DATE_DIALOG_ID1);
		} else {
			// TODO Auto-generated method stub
			if (from.getText().toString().length() == 0) {
				alert = new AlertDialog.Builder(Leave.this);
				alert.setTitle("Alert!")

						.setMessage("Please enter from date")
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
				alert = new AlertDialog.Builder(Leave.this);
				alert.setTitle("Alert!")

						.setMessage("Please enter to date")
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

			} else if (reason.getSelectedItemPosition() == 0) {
				alert = new AlertDialog.Builder(Leave.this);
				alert.setTitle("Alert!")

						.setMessage("Please select reason")
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

				if(NetworkHandler.isOnline(this)){
				dialog = ProgressDialog.show(Leave.this, "Leave request",
						"Processing your request. Please wait...", true);
				new HttpAsyncTask()
						.execute("https://www.yourtaxistand.com/myapps/driver_leave");
					}else{
					Toast.makeText(Leave.this, R.string.nointernet,Toast.LENGTH_SHORT).show();

				}
			}
		}
	}

}
