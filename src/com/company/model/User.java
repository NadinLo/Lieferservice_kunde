package com.company.model;

import java.sql.Time;

public class User {
    private int orderNo;
    private String name;
    private String address;
    private String ZIP;
    private String location;
    private int locationId;


    public User(String name, String address, String ZIP, String location) {
        this.name = name;
        this.address = address;
        this.ZIP = ZIP;
        this.location = location;

    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getLocation() {
        return location;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    public void setMinutesToDeliver(Time minutesToDeliver) {
    }
}
