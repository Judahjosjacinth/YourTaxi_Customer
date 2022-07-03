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


public class FuelFragment extends BaseFragment {

    ScrollView fuel_layout;
    EditText f_from_date;
    EditText f_odometer_reading;
    EditText f_liters;
    EditText f_bill_amount;
    EditText f_bill_date;
    Button submit_fuel;


    public FuelFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_fuel, container, false);
        session = new SessionManager(getActivity());
        mGson = new Gson();

        driver_id = session.getDriverID().get(SessionManager.KEY_ID);
        vehicle_id = session.getVehicleid();
        fuel_layout = (ScrollView) rootView.findViewById(R.id.fuel_layout);

        f_from_date = (EditText) rootView.findViewById(R.id.f_from_date);
        f_odometer_reading = (EditText) rootView.findViewById(R.id.f_odometer_reading);
        f_liters = (EditText) rootView.findViewById(R.id.f_liters);
        f_bill_amount = (EditText) rootView.findViewById(R.id.f_bill_amount);
        f_bill_date = (EditText) rootView.findViewById(R.id.f_bill_date);
        submit_fuel = (Button) rootView.findViewById(R.id.submit_fuel);


        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        f_from_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDatepicker(f_from_date);
            }
        });

        f_bill_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDatepicker(f_bill_date);
            }
        });


        Calendar c = Calendar.getInstance();

        f_from_date.setText(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DATE));

        submit_fuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ValidateEt(f_from_date)) {
                    ShowToast("Please Select from date");
                } else if (!ValidateEt(f_odometer_reading)) {
                    ShowToast("Please enter odometer reading");
                } else if (!ValidateEt(f_liters)) {
                    ShowToast("Please enter Litre filled");
                } else if (!ValidateEt(f_bill_amount)) {
                    ShowToast("Please Bill amount");
                } else if (!ValidateEt(f_bill_date)) {
                    ShowToast("Please Bill date");
                } else {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("driver_id", "" + driver_id);
                    params.put("vehicle_id", "" + vehicle_id);
                    params.put("chosen_date", "" + f_from_date.getText().toString().trim());
                    params.put("odometer_reading", "" + f_odometer_reading.getText().toString().trim());
                    params.put("litres_filled", "" + f_liters.getText().toString().trim());
                    params.put("bill_amount", "" + f_bill_amount.getText().toString().trim());
                    params.put("bill_date", "" + f_bill_date.getText().toString().trim());
                    callFuelMaintence(params);
                }

            }
        });

    }


    public void callFuelMaintence(final Map<String, String> params) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        progress = ProgressDialog.show(
                getActivity(), "", "Please wait...");

        progress.setCancelable(false);

        StringRequest myReq = new StringRequest(Request.Method.POST,
                Constants.driver_fuel,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("result", response);

                        progress.dismiss();

                        try {
                            Maintenance maintenance = mGson.fromJson("" + response, Maintenance.class);
                            if (maintenance.getStatus_code() == 1) {
                                ShowToast(maintenance.getStatus_message());
                                resetEdittext(fuel_layout);
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
