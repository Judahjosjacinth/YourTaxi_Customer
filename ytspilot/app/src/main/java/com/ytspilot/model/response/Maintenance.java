package com.ytspilot.model.response;

/**
 * Created by Devani (sdevani92@gmail.com) on 7/26/2016.
 */
public class Maintenance {
    int status_code = 0;
    String status_message = "";

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
}
