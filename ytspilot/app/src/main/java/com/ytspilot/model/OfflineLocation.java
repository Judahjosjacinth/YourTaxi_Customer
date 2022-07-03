package com.ytspilot.model;

import com.orm.dsl.Table;

/**
 * Created by Devani (sdevani92@gmail.com) on 7/26/2016.
 */

@Table
public class OfflineLocation {
    Long id;
    String lat = "";
    String lng = "";
    String timestamp = "";

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getTimestemp() {
        return timestamp;
    }

    public void setTimestemp(String timestemp) {
        this.timestamp = timestemp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
