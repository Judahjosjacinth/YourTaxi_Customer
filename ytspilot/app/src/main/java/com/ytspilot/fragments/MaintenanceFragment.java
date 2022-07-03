package com.ytspilot.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class MaintenanceFragment extends BaseFragment {
    //    maintenance

    ScrollView mantenance_layout;

    EditText m_form_et;
    EditText m_odometer_reading;
    EditText m_workshop_name;
    EditText m_vehicle_in;
    EditText m_vehicle_out;
    EditText m_bill_number;
    EditText m_bill_amount;
    EditText m_bill_date;
    Button submit_maintenance;


    public MaintenanceFragment() {
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


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_maintenance, container, false);

        session = new SessionManager(getActivity());
        mGson = new Gson();


        driver_id = session.getDriverID().get(SessionManager.KEY_ID);
        vehicle_id = session.getVehicleid();

        mantenance_layout = (ScrollView) rootView.findViewById(R.id.mantenance_layout);


        m_form_et = (EditText) rootView.findViewById(R.id.m_form_et);
        m_odometer_reading = (EditText) rootView.findViewById(R.id.m_odometer_reading);
        m_workshop_name = (EditText) rootView.findViewById(R.id.m_workshop_name);
        m_vehicle_in = (EditText) rootView.findViewById(R.id.m_vehicle_in);
        m_vehicle_out = (EditText) rootView.findViewById(R.id.m_vehicle_out);
        m_bill_number = (EditText) rootView.findViewById(R.id.m_bill_number);
        m_bill_amount = (EditText) rootView.findViewById(R.id.m_bill_amount);
        m_bill_date = (EditText) rootView.findViewById(R.id.m_bill_date);
        submit_maintenance = (Button) rootView.findViewById(R.id.submit_maintenance);


        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Calendar c = Calendar.getInstance();

        m_form_et.setText(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DATE));
        m_form_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDatepicker(m_form_et);
            }
        });
        m_vehicle_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDatepicker(m_vehicle_in);
            }
        });
        m_vehicle_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDatepicker(m_vehicle_out);
            }
        });
        m_bill_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDatepicker(m_bill_date);
            }
        });

        submit_maintenance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String indate = "0" + m_vehicle_in.getText().toString().trim();
                String outdate = "0" + m_vehicle_out.getText().toString().trim();

                indate = indate.replaceAll("-", "");
                outdate = outdate.replaceAll("-", "");

                int INdate = Integer.parseInt(indate);
                int OUTdate = Integer.parseInt(outdate);

                Boolean isOutisless = false;

                if (INdate > OUTdate) {
                    isOutisless = true;
                }


                if (!ValidateEt(m_form_et)) {
                    ShowToast("Please Select from date");
                } else if (!ValidateEt(m_odometer_reading)) {
                    ShowToast("Please enter odometer reading");
                } else if (!ValidateEt(m_workshop_name, 2)) {
                    ShowToast("Please enter proper workshop name");
                } else if (!ValidateEt(m_vehicle_in)) {
                    ShowToast("Please select vehicle in date");
                } else if (!ValidateEt(m_vehicle_out)) {
                    ShowToast("Please select vehicle out date");
                } else if (isOutisless) {
                    ShowToast("Please select vehicle out ♪date less then in date");
                } else if (!ValidateEt(m_bill_number)) {
                    ShowToast("Please Bill number");
                } else if (!ValidateEt(m_bill_amount)) {
                    ShowToast("Please Bill amount");
                } else if (!ValidateEt(m_bill_date)) {
                    ShowToast("Please Bill date");
                } else {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("driver_id", "" + driver_id);
                    params.put("vehicle_id", "" + vehicle_id);
                    params.put("chosen_date", "" + m_form_et.getText().toString().trim());
                    params.put("odometer_reading", "" + m_odometer_reading.getText().toString());
                    params.put("workshop_name", "" + m_workshop_name.getText().toString());
                    params.put("vehicle_in", "" + m_vehicle_in.getText().toString().trim());
                    params.put("vechicle_out", "" + m_vehicle_out.getText().toString().trim());
                    params.put("bill_number", "" + m_bill_number.getText().toString().trim());
                    params.put("bill_amount", "" + m_bill_amount.getText().toString().trim());
                    params.put("bill_date", "" + m_bill_date.getText().toString().trim());
                    callMaintence(params);
                }

            }
        });

    }


    public void callMaintence(final Map<String, String> params) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        progress = ProgressDialog.show(
                getActivity(), "", "Please wait...");

        progress.setCancelable(false);

        StringRequest myReq = new StringRequest(Request.Method.POST,
                Constants.driver_maintenance,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("result", response);
                        progress.dismiss();
                        try {
                            Maintenance maintenance = mGson.fromJson("" + response, Maintenance.class);

                            if (maintenance.getStatus_code() == 1) {
                                ShowToast(maintenance.getStatus_message());
                                resetEdittext(mantenance_layout);
                                getActivity().finish();
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
