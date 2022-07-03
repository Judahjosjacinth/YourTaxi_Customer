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


public class PenaltyFragment extends BaseFragment {
    ScrollView penalty_layout;
    EditText p_odometer_reading;
    Spinner p_offence_details;
    EditText p_bill_number;
    EditText p_bill_amount;
    EditText p_bill_date;
    Button submit_penalty;

    ArrayList<String> offtype = new ArrayList<String>();

    public PenaltyFragment() {
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
        offtype.clear();
        offtype.add("Traffic signal jump");
        offtype.add("Overspeed");
        offtype.add("Over loaded");
        offtype.add("One way");
        offtype.add("Toll");
        offtype.add("Others");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_penalty, container, false);
        session = new SessionManager(getActivity());
        mGson = new Gson();

        driver_id = session.getDriverID().get(SessionManager.KEY_ID);
        vehicle_id = session.getVehicleid();
        penalty_layout = (ScrollView) rootView.findViewById(R.id.penalty_layout);

        p_odometer_reading = (EditText) rootView.findViewById(R.id.p_odometer_reading);
        p_offence_details = (Spinner) rootView.findViewById(R.id.p_offence_details);
        p_bill_number = (EditText) rootView.findViewById(R.id.p_bill_number);
        p_bill_amount = (EditText) rootView.findViewById(R.id.p_bill_amount);
        p_bill_date = (EditText) rootView.findViewById(R.id.p_bill_date);
        submit_penalty = (Button) rootView.findViewById(R.id.submit_penalty);

        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ArrayAdapter<String> salarytypeAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, offtype);
        salarytypeAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        p_offence_details.setAdapter(salarytypeAdapter);


        p_bill_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDatepicker(p_bill_date);
            }
        });

        submit_penalty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ValidateEt(p_odometer_reading)) {
                    ShowToast("Please enter odometer reading");
                } else
//                if (!ValidateEt(p_offence_details)) {
//                    ShowToast("Please enter offence details");
//                } else
                    if (!ValidateEt(p_bill_number)) {
                        ShowToast("Please enter bill number");
                    } else if (!ValidateEt(p_bill_amount)) {
                        ShowToast("Please Bill amount");
                    } else if (!ValidateEt(p_bill_date)) {
                        ShowToast("Please Bill date");
                    } else {
                        Map<String, String> params = new HashMap<String, String>();

                        params.put("driver_id", "" + driver_id);
                        params.put("vehicle_id", "" + vehicle_id);
                        params.put("odometer_reading", "" + p_odometer_reading.getText().toString());
                        params.put("offence_details", "" + p_offence_details.getSelectedItem().toString().trim());
                        params.put("bill_number", "" + p_bill_number.getText().toString().trim());
                        params.put("bill_amount", "" + p_bill_amount.getText().toString().trim());
                        params.put("bill_date", "" + p_bill_date.getText().toString());
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
                Constants.driver_penalty,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("result", response);

                        progress.dismiss();

                        try {
                            Maintenance maintenance = mGson.fromJson("" + response, Maintenance.class);

                            if (maintenance.getStatus_code() == 1) {
                                ShowToast(maintenance.getStatus_message());
                                resetEdittext(penalty_layout);
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
