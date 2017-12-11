package com.example.amank.locationtracker;

/**
 * Created by amank on 05-12-2017.
 */

public class Location {
    private String date;
    private String time;
    private String address;
    private Long createdAt;

    public Location(){}

    public Location(String date, String time, String address, Long createdAt) {
        this.date = date;
        this.time = time;
        this.address = address;
        this.createdAt = createdAt;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}
