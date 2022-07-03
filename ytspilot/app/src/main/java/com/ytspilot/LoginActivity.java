package com.ytspilot;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ytspilot.R;
import com.ytspilot.util.NetworkHandler.HTTP_METHOD;
import com.ytspilot.model.Vehicle;
import com.ytspilot.model.response.Login;
import com.ytspilot.util.NetworkHandler;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

@SuppressLint("NewApi")
public class LoginActivity extends BaseActivity implements
        OnClickListener, OnCheckedChangeListener {

    EditText driverid, password;
    Button submit;

    private ProgressDialog dialog;
    AlertDialog.Builder alert;

    public static String id;
    public static boolean islogin = false;
    CheckBox remember;

    TextView signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mGson = new Gson();

        session = new SessionManager(getApplicationContext());

        driverid = (EditText) findViewById(R.id.driverid);
        password = (EditText) findViewById(R.id.pass);
        remember = (CheckBox) findViewById(R.id.remember);
        remember.setOnCheckedChangeListener(this);

        submit = (Button) findViewById(R.id.login);
        submit.setOnClickListener(this);
        signup = (TextView) findViewById(R.id.signup);
        signup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, "sales@yourtaxistand.com");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Interested in Joining" + " Yourtaxistand.com");
                intent.putExtra(Intent.EXTRA_TEXT, "Name: " + "\n" + "Phone Number: " + "\n" + "# of Vehicles owned");
                startActivity(intent);
            }
        });
    }

    public class HttpAsyncTask extends AsyncTask<String, Void, String> {
        ContentValues arguments;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            arguments = new ContentValues();
            //D000002
            //9884406994
            arguments.put("driver_id", driverid.getText().toString().trim());
            arguments.put("password", password.getText().toString());
        }

        @Override
        protected String doInBackground(String... urls) {

            return NetworkHandler.getStringFromURL(urls[0], arguments, HTTP_METHOD.POST);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            System.out.println("Result::::" + result);
            if (dialog.isShowing())
                dialog.dismiss();
            try {

                Login mLogin = mGson.fromJson("" + result, Login.class);


                if (mLogin.getStatus_code() == 1) {
                    id = driverid.getText().toString().trim();
                    session.createLoginSession(driverid.getText().toString(),
                            password.getText().toString());
                    session.driverID(driverid.getText().toString().trim());

                    selectVehicle(mLogin.getVehicle_list());


                } else {
                    alert = new AlertDialog.Builder(LoginActivity.this);
                    alert.setTitle("Sorry!")

                            .setMessage("Invalid user name or password")
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
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (driverid.getText().toString().length() == 0) {
            alert = new AlertDialog.Builder(LoginActivity.this);
            alert.setTitle("Alert!")

                    .setMessage("Please enter driver id")
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

        } else if (password.getText().toString().length() == 0) {
            alert = new AlertDialog.Builder(LoginActivity.this);
            alert.setTitle("Alert!")

                    .setMessage("Please enter driver mobile number")
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

            if (remember.isChecked()) {
                session.createLoginSession(driverid.getText().toString(),
                        password.getText().toString());

                HashMap<String, String> user = session.getUserDetails();

                // name
                String name = user.get(SessionManager.KEY_NAME);

                // email
                String email = user.get(SessionManager.KEY_EMAIL);
                System.out.println(name + email);
            } else {
                session.createLoginSession("", "");
            }

            if (NetworkHandler.isOnline(this)) {
                try {
                    dialog = ProgressDialog.show(LoginActivity.this, "Login request",
                            "Processing your request. Please wait...", true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                //Check Application Version.
//                PackageManager manager = getApplicationContext().getPackageManager();
//                PackageInfo info = null;
//                try {
//                    info = manager.getPackageInfo(getApplicationContext().getPackageName(), 0);
//                } catch (PackageManager.NameNotFoundException e) {
//                    e.printStackTrace();
//                }
//                String appVersion = info.versionName.trim();
//
//                //Check PlayStore Version
//                VersionChecker versionChecker = new VersionChecker();
//                String latestVersion = "";
//                try {
//                    latestVersion = versionChecker.execute().get();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }

                //Compare Current App Version with Playstore Version
//                int result = compareVersionNames(appVersion, latestVersion);
                int result = 1;
//                Log.d("APP Version", appVersion);
//                Log.d("Latest Version", latestVersion);
//                Log.d("RESULT", Integer.toString(result));

                if(result == 1) {
                    new HttpAsyncTask()
                            .execute("https://www.yourtaxistand.com/myapps/validate_driver");
                }
                else
                {
                    if (dialog.isShowing())
                        dialog.dismiss();
                    alert = new AlertDialog.Builder(LoginActivity.this);
                    alert.setTitle("Alert!")
                            .setMessage("You're using the old app, please update now to continue.")
                            .setCancelable(false)
                            .setPositiveButton("Update Now",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {

                                            //goto Play Store
                                            Intent i = new Intent(Intent.ACTION_VIEW);
                                            i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.ytspilot&hl=en"));
                                            startActivity(i);

                                        }
                                    });

                    AlertDialog build = alert.create();
                    build.show();
                }

            } else {
                Toast.makeText(LoginActivity.this, R.string.nointernet, Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // TODO Auto-generated method stub

        if (isChecked) {
            if (driverid.getText().toString().length() == 0) {
                Toast.makeText(getApplicationContext(),
                        "Please enter driver id", Toast.LENGTH_SHORT).show();
                remember.setChecked(false);
            } else if (password.getText().toString().length() == 0) {
                Toast.makeText(getApplicationContext(),
                        "Please enter mobile number", Toast.LENGTH_SHORT)
                        .show();
                remember.setChecked(false);
            }
        }

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        HashMap<String, String> user = session.getUserDetails();
        // name
        String name = user.get(SessionManager.KEY_NAME);
        // email
        String email = user.get(SessionManager.KEY_EMAIL);
        driverid.setText(name);
        password.setText(email);
    }


    public void selectVehicle(final List<Vehicle> mVehicleList) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(LoginActivity.this);
        builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Select One Vehicle");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                LoginActivity.this,
                android.R.layout.select_dialog_singlechoice);

        for (Vehicle mVehicle : mVehicleList) {
            arrayAdapter.add(mVehicle.getVehicle());
        }


        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        session.vehicleid(mVehicleList.get(which).getId());

                        AlertDialog.Builder builderInner = new AlertDialog.Builder(
                                LoginActivity.this);
                        builderInner.setMessage(strName);
                        builderInner.setTitle("Your Selected Vehicle is");
                        builderInner.setPositiveButton(
                                "Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        dialog.dismiss();


                                        Intent i = new Intent(LoginActivity.this,
                                                MainActivity.class);
                                        startActivity(i);
                                        LoginActivity.islogin = true;
                                        finish();
                                    }
                                });
                        builderInner.show();
                    }
                });
        builderSingle.show();
    }

    public int compareVersionNames(String oldVersionName, String newVersionName) {
        int res = 0;

        String[] oldNumbers = oldVersionName.split("\\.");
        String[] newNumbers = newVersionName.split("\\.");

        // To avoid IndexOutOfBounds
        int maxIndex = Math.min(oldNumbers.length, newNumbers.length);

        for (int i = 0; i < maxIndex; i ++) {
            int oldVersionPart = Integer.valueOf(oldNumbers[i]);
            int newVersionPart = Integer.valueOf(newNumbers[i]);

            if (oldVersionPart < newVersionPart) {
                res = -1;
                break;
            } else if (oldVersionPart >= newVersionPart) {
                res = 1;
                break;
            }
        }

        /*
        // If versions are the same so far, but they have different length...
        if (res == 0 && oldNumbers.length != newNumbers.length) {
            res = (oldNumbers.length > newNumbers.length)?1:-1;
        }
        */
        return res;
    }

}
