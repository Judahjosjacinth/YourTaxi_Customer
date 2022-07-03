package com.ytspilot.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;

import androidx.annotation.Nullable;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.ytspilot.R;
import com.ytspilot.SessionManager;
import com.ytspilot.model.response.Maintenance;
import com.ytspilot.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Track_salaryFragment extends BaseFragment {
    ScrollView salary_layout;
    EditText from_date;
    EditText to_date;
    Spinner spiiner_amountttype;
    EditText amount_paid;
    Button submit_salary;

    ArrayList<String> salarytype = new ArrayList<String>();

    public Track_salaryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    // TODO: Rename and change types and number of parameters
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        salarytype.clear();
        salarytype.add("Per diem");
        salarytype.add("Salary");
        salarytype.add("Overtime");
        salarytype.add("Salary Advance");
        salarytype.add("Forecasted salary");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_trace_salary, container, false);
        session = new SessionManager(getActivity());
        mGson = new Gson();


        driver_id = session.getDriverID().get(SessionManager.KEY_ID);
        vehicle_id = session.getVehicleid();
        salary_layout = (ScrollView) rootView.findViewById(R.id.salary_layout);

        from_date = (EditText) rootView.findViewById(R.id.from_date);
        to_date = (EditText) rootView.findViewById(R.id.to_date);
        spiiner_amountttype = (Spinner) rootView.findViewById(R.id.spiiner_amountttype);
        amount_paid = (EditText) rootView.findViewById(R.id.amount_paid);
        submit_salary = (Button) rootView.findViewById(R.id.submit_salary);


        ArrayAdapter<String> salarytypeAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, salarytype);
        salarytypeAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiiner_amountttype.setAdapter(salarytypeAdapter);


        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        from_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDatepicker(from_date);
            }
        });
        to_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDatepicker(to_date);
            }
        });

        submit_salary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String indate = "0" + from_date.getText().toString().trim();
                String outdate = "0" + to_date.getText().toString().trim();

                indate = indate.replaceAll("-", "");
                outdate = outdate.replaceAll("-", "");

                int INdate = Integer.parseInt(indate);
                int OUTdate = Integer.parseInt(outdate);

                Boolean isOutisless = false;

                if (INdate > OUTdate) {
                    isOutisless = true;
                }



                if (!ValidateEt(from_date)) {
                    ShowToast("Please enter from date");
                } else if (!ValidateEt(to_date)) {
                    ShowToast("Please enter to date");
                } if (isOutisless) {
                    ShowToast("Please select vehicle from date less then to date");
                }  else if (!ValidateEt(amount_paid)) {
                    ShowToast("Please enter amount paid");
                } else {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("driver_id", "" + driver_id);
                    params.put("vehicle_id", "" + vehicle_id);
                    params.put("from_date", "" + from_date.getText().toString());
                    params.put("to_date", "" + to_date.getText().toString());
                    params.put("amount_type", "" + spiiner_amountttype.getSelectedItem().toString().trim());
                    params.put("amount_paid", "" + amount_paid.getText().toString().trim());
                    callPenaltyMaintence(params);
                }
            }
        });

    }

    public void callPenaltyMaintence(final Map<String, String> params) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        progress = ProgressDialog.show(
                getActivity(), "", "Please wait...");

        progress.setCancelable(false);

        StringRequest myReq = new StringRequest(Request.Method.POST,
                Constants.driver_salary,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("result", response);

                        progress.dismiss();

                        try {
                            Maintenance maintenance = mGson.fromJson("" + response, Maintenance.class);

                            if (maintenance.getStatus_code() == 1) {
                                ShowToast(maintenance.getStatus_message());
                                resetEdittext(salary_layout);
                            } else {
                                ShowToast("Error in update data.");
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
