package com.ytspilot.model.response;

import com.ytspilot.model.Vehicle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Devani (sdevani92@gmail.com) on 7/26/2016.
 */
public class Login {

    int status_code = 0;
    String status_message = "Login Successful";
    int driver_id = 0;
    List<Vehicle> vehicle_list = new ArrayList<>();

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

    public int getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(int driver_id) {
        this.driver_id = driver_id;
    }

    public List<Vehicle> getVehicle_list() {
        return vehicle_list;
    }

    public void setVehicle_list(List<Vehicle> vehicle_list) {
        this.vehicle_list = vehicle_list;
    }
}
