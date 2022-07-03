package com.ytspilot;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressLint("NewApi")
public class DriverAttachment extends BaseActivity implements
		OnClickListener {

	EditText name, number, veh_number;
	Spinner type;
	CustomAutoCompleteTextView loginarea;
	ArrayList<String> issue_list = new ArrayList<String>();
	PlacesTask placesTask;
	ParserTask parserTask;

	private ProgressDialog dialog;
	AlertDialog.Builder alert;
	Button submit;

	SessionManager session;
	HashMap<String, String> user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver_attachment);
		ActionBar ab = getSupportActionBar();
		if (ab != null) {
			ab.setDisplayHomeAsUpEnabled(true);
		}
		session = new SessionManager(getApplicationContext());

		// = user.get(SessionManager.KEY_ID);

		issue_list.add("Select vehicle type");
		issue_list.add("Suv");
		issue_list.add("Hatchback ");
		issue_list.add("Sedan");

		name = (EditText) findViewById(R.id.name);
		number = (EditText) findViewById(R.id.mobile);
		veh_number = (EditText) findViewById(R.id.veh_number);

		loginarea = (CustomAutoCompleteTextView) findViewById(R.id.loginarea);

		type = (Spinner) findViewById(R.id.type);

		submit = (Button) findViewById(R.id.submit_attachment);

		submit.setOnClickListener(this);

		ArrayAdapter<String> cityadapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, issue_list);
		cityadapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		type.setAdapter(cityadapter);

		loginarea.setThreshold(1);

		loginarea.addTextChangedListener(new TextWatcher() {

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
	private class PlacesTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... place) {
			// For storing data from web service
			String data = "";

			// Obtain browser key from https://code.google.com/apis/console
			String key = "key=AIzaSyDl9tzf0lPHW0oWj7MnPFOX1pOXjVxKjJI";

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
			String parameters = input + "&" + types + "&" + sensor + "&" + key+ "&" + "components=country:IN";

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

	/** A class to parse the Google Places in JSON format */
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

			String[] from = new String[] { "description" };
			int[] to = new int[] { android.R.id.text1 };

			// Creating a SimpleAdapter for the AutoCompleteTextView
			SimpleAdapter adapter = new SimpleAdapter(DriverAttachment.this,
					result, android.R.layout.simple_list_item_1, from, to);

			// Setting the adapter
			loginarea.setAdapter(adapter);
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
			Log.d("Exception while", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	
	public class HttpAsyncTask extends AsyncTask<String, Void, String> {
		ContentValues arguments;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			arguments = new ContentValues();

			arguments.put("referral_driver_id",user
					.get(SessionManager.KEY_ID) );

			arguments.put("referred_driver_name",
					name.getText().toString().trim());

			arguments.put("phone_number", number
					.getText().toString().trim());

			arguments.put("vehicle_type", type
					.getSelectedItem().toString().trim());
			arguments.put("vehicle_number",
					veh_number.getText().toString().trim());
			arguments.put("login_area", loginarea
					.getText().toString().trim());
		}

		@Override
		protected String doInBackground(String... urls) {// Build JSON string\\

			HashMap<String, String> user = session.getDriverID();
		

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
					alert = new AlertDialog.Builder(DriverAttachment.this);
					alert.setTitle("Success!")

							.setMessage(
									"Referral attachment data has posted successfully")
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

					finish();
				} else {
					alert = new AlertDialog.Builder(DriverAttachment.this);
					alert.setTitle("Sorry!")

							.setMessage(
									"Referral attachment data has not posted successfully")
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

		if (name.getText().toString().length() == 0) {
			alert = new AlertDialog.Builder(DriverAttachment.this);
			alert.setTitle("Alert!")

					.setMessage("Please enter referral driver name")
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

		} else if (number.getText().toString().length() == 0) {
			alert = new AlertDialog.Builder(DriverAttachment.this);
			alert.setTitle("Alert!")

					.setMessage("Please enter referral driver mobile number")
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

		} else if (type.getSelectedItemPosition() == 0) {
			alert = new AlertDialog.Builder(DriverAttachment.this);
			alert.setTitle("Alert!")

					.setMessage("Please select vehicle type")
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

		} else if (veh_number.getText().toString().length() == 0) {
			alert = new AlertDialog.Builder(DriverAttachment.this);
			alert.setTitle("Alert!")

					.setMessage("Please enter vechicle number")
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

		} else if (loginarea.getText().toString().length() == 0) {
			alert = new AlertDialog.Builder(DriverAttachment.this);
			alert.setTitle("Alert!")

					.setMessage("Please enter login area")
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
			if(NetworkHandler.isOnline(this)) {
				dialog = ProgressDialog.show(DriverAttachment.this, "Request",
						"Processing your request. Please wait...", true);
				new HttpAsyncTask()
						.execute("https://www.yourtaxistand.com/myapps/driver_attachment");
			}else{
				Toast.makeText(DriverAttachment.this, R.string.nointernet,Toast.LENGTH_SHORT).show();

			}
		}

	}
}
