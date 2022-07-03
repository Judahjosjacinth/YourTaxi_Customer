package com.ytspilot;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ytspilot.model.response.Bidding;
import com.ytspilot.rest.ApiClient;
import com.ytspilot.rest.ApiInterface;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.android.volley.VolleyLog.TAG;


public class BidActivity extends Activity {


    @BindView(com.ytspilot.R.id.tv_bookingid_number)
    TextView tvBookingIDNumber;

    @BindView(com.ytspilot.R.id.tv_triptype_details)
    TextView tvTripType;

    @BindView(com.ytspilot.R.id.layout_local)
    LinearLayout layoutLocal;

    @BindView(com.ytspilot.R.id.layout_outstation)
    LinearLayout layoutOutstation;

    @BindView(com.ytspilot.R.id.et_min_amount)
    EditText etMinAmount;

    @BindView(com.ytspilot.R.id.et_additional_km)
    EditText etAdditonalKM;

    @BindView(com.ytspilot.R.id.et_additional_hour)
    EditText etAdditionalHour;

    @BindView(com.ytspilot.R.id.et_driverbatta)
    EditText etDriverBatta;

    @BindView(com.ytspilot.R.id.et_estimated_permit)
    EditText etEstimatedPermit;

    @BindView(com.ytspilot.R.id.et_price_per_km)
    EditText etPricePerKm;

    @BindView(com.ytspilot.R.id.btn_bid)
    Button btnBid;

    private ApiInterface apiService;
    private String tripType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ytspilot.R.layout.activity_bid);
        ButterKnife.bind(this);

        apiService = ApiClient.getClient().create(ApiInterface.class);

        Intent intent = getIntent();

        if(intent.hasExtra(BookingsListActivity.BOOKING_ID_ARG))
        {
            tvBookingIDNumber.setText(intent.getStringExtra(BookingsListActivity.BOOKING_ID_ARG));
        }

        if(intent.hasExtra(BookingsListActivity.BOOKING_TRIP_TYPE))
        {
            tripType = intent.getStringExtra(BookingsListActivity.BOOKING_TRIP_TYPE);

            if (tripType.equalsIgnoreCase("LT"))
            {
                tripType = "Local";
                layoutLocal.setVisibility(View.VISIBLE);
                layoutOutstation.setVisibility(View.GONE);
            }
            else
            {
                tripType = "Outstation";
                layoutLocal.setVisibility(View.GONE);
                layoutOutstation.setVisibility(View.VISIBLE);
            }
            tvTripType.setText(tripType);
        }

    }

    @OnClick (com.ytspilot.R.id.btn_bid)
    public void submit(){

        SessionManager session = new SessionManager(this);

        String bookingId = tvBookingIDNumber.getText().toString();
        String driverId = session.getDriverID().get(SessionManager.KEY_ID);
        String vehicleId = session.getVehicleid();

        if (tripType.equalsIgnoreCase("LT")) {
            String minAmount = etMinAmount.getText().toString();
            String additionalKM = etAdditonalKM.getText().toString();
            String additionalHour = etAdditionalHour.getText().toString();
            sendBidding(driverId, bookingId, vehicleId, minAmount, additionalKM, additionalHour );
        } else {
            String pricePerKm = etPricePerKm.getText().toString();
            String estimatedPermit = etEstimatedPermit.getText().toString();
            String driverBatta = etDriverBatta.getText().toString();
            sendBidding(driverId, bookingId, vehicleId, pricePerKm, estimatedPermit, driverBatta );

        }


    }


    public void sendBidding(String driverId, String bookingId, String vehicleId, String additionalParameter1,
                             String additionalParameter2, String additionalParameter3) {
        if (tripType.equalsIgnoreCase("LT")) {
            apiService.sendBiddingLocal(driverId, bookingId, vehicleId, additionalParameter1, additionalParameter2, additionalParameter3).enqueue(new Callback<Bidding>() {
                @Override
                public void onResponse(Call<Bidding> call, Response<Bidding> response) {

                    if (response.isSuccessful()) {
                        showResponse(response.body().getStatusMessage());
                        Log.i(TAG, "post submitted to API." + response.message().toString());
                    }
                }

                @Override
                public void onFailure(Call<Bidding> call, Throwable t) {
                    Log.e(TAG, "Unable to submit post to API.");
                }
            });
        } else {
            apiService.sendBiddingOutstation(driverId, bookingId, vehicleId, additionalParameter1, additionalParameter2, additionalParameter3).enqueue(new Callback<Bidding>() {
                @Override
                public void onResponse(Call<Bidding> call, Response<Bidding> response) {

                    if (response.isSuccessful()) {
                        showResponse(response.body().getStatusMessage());
                        Log.i(TAG, "post submitted to API." + response.message().toString());
                    }
                }

                @Override
                public void onFailure(Call<Bidding> call, Throwable t) {
                    Log.e(TAG, "Unable to submit post to API.");
                }
            });

        }
    }

    public void showResponse(String response) {

        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(response)
                .setTitle(com.ytspilot.R.string.bidding);

        builder.setCancelable(true);

        builder.setPositiveButton(com.ytspilot.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                finish();
            }
        });

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
