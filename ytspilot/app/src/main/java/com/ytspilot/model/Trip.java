package com.ytspilot.model;

/**
 * Created by Devani (sdevani92@gmail.com) on 9/7/2016.
 */
public class Trip {

    String auto_id = "";
    String booking_id = "";
    String trip_type = "";
    String pickup_point = "";
    String destination = "";
    String pickup_date = "";
    String pickup_time = "";
    String package_value = "";
    String end_date = "";
    String total_bids = "";
    String bid_status = "";
    String is_quick_booking = "";

    public String getAuto_id() {
        return auto_id;
    }

    public void setAuto_id(String auto_id) {
        this.auto_id = auto_id;
    }

    public String getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(String booking_id) {
        this.booking_id = booking_id;
    }

    public String getTrip_type() {
        return trip_type;
    }

    public void setTrip_type(String trip_type) {
        this.trip_type = trip_type;
    }

    public String getPickup_point() {
        return pickup_point;
    }

    public void setPickup_point(String pickup_point) {
        this.pickup_point = pickup_point;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getPickup_date() {
        return pickup_date;
    }

    public void setPickup_date(String pickup_date) {
        this.pickup_date = pickup_date;
    }

    public String getPickup_time() {
        return pickup_time;
    }

    public void setPickup_time(String pickup_time) {
        this.pickup_time = pickup_time;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getTotal_bids() {
        return total_bids;
    }

    public void setTotal_bids(String total_bids) {
        this.total_bids = total_bids;
    }

    public String getBid_status() {
        return bid_status;
    }

    public void setBid_status(String bid_status) {
        this.bid_status = bid_status;
    }

    public String getPackage_value() {
        return package_value;
    }

    public void setPackage_value(String package_value) {
        this.package_value = package_value;
    }

    public String getIsQuickBooking() {
        return is_quick_booking;
    }

    public void setIsQuickBooking(String is_quick_booking) {
        this.is_quick_booking = is_quick_booking;
    }
}
