package com.ytspilot.model.response;

/**
 * Created by Devani (sdevani92@gmail.com) on 28/09/2016.
 */
public class Endtrip {
    int status_code = 0;
    String status_message = "";
    String booking_id = "";
    String start_km = "0";
    String end_km = "0";
    double total_kms = 0;
    double extra_km = 0;
    String base_fare = "";
    String service_charge = "0";
    String rate_per_km = "0";
    String minimum_distance = "0";
    String minimum_fare = "0";

    double extra_distance_fare = 0;
    double extra_time_fare = 0;
    double extra_charge_total = 0;

    double total_time = 0;
    double total_fare = 0;

    String package_hours = "";
    String package_distance = "";
    String package_rate = "";
    String package_rate_additional_km = "";
    String package_rate_additional_hour = "";

    String trip_type= "";
    String driver_bata= "";

    public String getRate_per_km() {
        return rate_per_km;
    }

    public void setRate_per_km(String rate_per_km) {
        this.rate_per_km = rate_per_km;
    }

    public String getMinimum_distance() {
        return minimum_distance;
    }

    public void setMinimum_distance(String minimum_distance) {
        this.minimum_distance = minimum_distance;
    }

    public String getMinimum_fare() {
        return minimum_fare;
    }

    public void setMinimum_fare(String minimum_fare) {
        this.minimum_fare = minimum_fare;
    }

    public String getTrip_type() {
        return trip_type;
    }

    public String getDriver_bata() {
        return driver_bata;
    }

    public void setDriver_bata(String driver_bata) {
        this.driver_bata = driver_bata;
    }

    public void setTrip_type(String trip_type) {
        this.trip_type = trip_type;
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

    public String getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(String booking_id) {
        this.booking_id = booking_id;
    }

    public void setStatus_message(String status_message) {
        this.status_message = status_message;
    }

    public String getStart_km() {
        return start_km;
    }

    public void setStart_km(String start_km) {
        this.start_km = start_km;
    }

    public String getEnd_km() {
        return end_km;
    }

    public void setEnd_km(String end_km) {
        this.end_km = end_km;
    }

    public double getTotal_kms() {
        return total_kms;
    }

    public void setTotal_kms(double total_kms) {
        this.total_kms = total_kms;
    }

    public double getExtra_km() {
        return extra_km;
    }

    public void setExtra_km(double extra_km) {
        this.extra_km = extra_km;
    }

    public String getBase_fare() {
        return base_fare;
    }

    public void setBase_fare(String base_fare) {
        this.base_fare = base_fare;
    }


    public String getService_charge() {
        return service_charge;
    }

    public void setService_charge(String service_charge) {
        this.service_charge = service_charge;
    }


    public double getExtra_distance_fare() {
        return extra_distance_fare;
    }

    public void setExtra_distance_fare(double extra_distance_fare) {
        this.extra_distance_fare = extra_distance_fare;
    }

    public double getExtra_time_fare() {
        return extra_time_fare;
    }

    public void setExtra_time_fare(double extra_time_fare) {
        this.extra_time_fare = extra_time_fare;
    }

    public double getExtra_charge_total() {
        return extra_charge_total;
    }

    public void setExtra_charge_total(double extra_charge_total) {
        this.extra_charge_total = extra_charge_total;
    }

    public double getTotal_time() {
        return total_time;
    }

    public void setTotal_time(double total_time) {
        this.total_time = total_time;
    }

    public String getPackage_hours() {
        return package_hours;
    }

    public void setPackage_hours(String package_hours) {
        this.package_hours = package_hours;
    }

    public String getPackage_distance() {
        return package_distance;
    }

    public void setPackage_distance(String package_distance) {
        this.package_distance = package_distance;
    }

    public String getPackage_rate() {
        return package_rate;
    }

    public void setPackage_rate(String package_rate) {
        this.package_rate = package_rate;
    }

    public String getPackage_rate_additional_km() {
        return package_rate_additional_km;
    }

    public void setPackage_rate_additional_km(String package_rate_additional_km) {
        this.package_rate_additional_km = package_rate_additional_km;
    }

    public String getPackage_rate_additional_hour() {
        return package_rate_additional_hour;
    }

    public void setPackage_rate_additional_hour(String package_rate_additional_hour) {
        this.package_rate_additional_hour = package_rate_additional_hour;
    }

    public double getTotal_fare() {
        return total_fare;
    }

    public void setTotal_fare(double total_fare) {
        this.total_fare = total_fare;
    }
}
