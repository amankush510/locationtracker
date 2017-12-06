package com.example.amank.locationtracker;

/**
 * Created by amank on 05-12-2017.
 */

public class Location {
    private String time;
    private String address;

    public Location(){}

    public Location(String time, String address) {
        this.time = time;
        this.address = address;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
