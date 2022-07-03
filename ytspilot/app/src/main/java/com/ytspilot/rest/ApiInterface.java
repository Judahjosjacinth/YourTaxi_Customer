package com.ytspilot.rest;

import com.ytspilot.model.response.Bidding;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by hadialathas on 7/21/17.
 */

public interface ApiInterface {

    @POST("myapps/bid_trip_driver\n")
    @FormUrlEncoded
    Call<Bidding> sendBiddingLocal(@Field("driver_id") String driver_id,
                                   @Field("booking_id") String booking_id,
                                   @Field("vehicle_id") String vehicle_id,
                                   @Field("fixed_amount") String fixed_amount,
                                   @Field("additional_km") String additional_km,
                                   @Field("additional_hour") String additional_hour);

    @POST("myapps/bid_trip_driver\n")
    @FormUrlEncoded
    Call<Bidding> sendBiddingOutstation(@Field("driver_id") String driver_id,
                                        @Field("booking_id") String booking_id,
                                        @Field("vehicle_id") String vehicle_id,
                                        @Field("price_per_km") String price_per_km,
                                        @Field("estimated_permit") String estimated_permit,
                                        @Field("driver_bata") String driver_bata);

}
