package com.ytspilot.model.response;

/**
 * Created by Devani (sdevani92@gmail.com) on 7/26/2016.
 */
public class UpdateTripStatus {
    int status_code = 0;
    String status_message = "";

    status data_received = new status();

    public status getData_received() {
        return data_received;
    }

    public void setData_received(status data_received) {
        this.data_received = data_received;
    }

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


    public class status {
        public String driver_status = "";

        public String getDriver_status() {
            return driver_status;
        }

        public void setDriver_status(String driver_status) {
            this.driver_status = driver_status;
        }
    }

}
