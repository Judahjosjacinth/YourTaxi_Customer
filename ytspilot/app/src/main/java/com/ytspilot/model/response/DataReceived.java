package com.ytspilot.model.response;

/**
 * Created by hadialathas on 7/16/17.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataReceived {

    @SerializedName("driver_id")
    @Expose
    private String driverId;
    @SerializedName("vehicle_id")
    @Expose
    private String vehicleId;

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

}
