package com.ytspilot.model.response;

import com.ytspilot.model.Trip;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Devani (sdevani92@gmail.com) on 7/26/2016.
 */
public class RequirementDriver {
    int status_code = 0;
    String status_message = "";

    List<Trip> trips_array = new ArrayList<>();

    public int getStatus_code() {
        return status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }

    public String getStatus_message() {
        return status_message;
    }

    public void setStatus_message(String status_message) {
        this.status_message = status_message;
    }

    public List<Trip> getTrips_array() {
        return trips_array;
    }

    public void setTrips_array(List<Trip> trips_array) {
        this.trips_array = trips_array;
    }



}
