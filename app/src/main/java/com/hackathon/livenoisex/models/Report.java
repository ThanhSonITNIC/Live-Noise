package com.hackathon.livenoisex.models;

import java.util.HashMap;
import java.util.Map;

public class Report {
    private String phone;

    private String url;
    private String description;
    private int decibel;
    private double latitude;
    private double longtitude;

    public Report() {
    }

    public Report(String phone, String url, String description, int decibel, double latitude, double longtitude) {
        this.phone = phone;
        this.url = url;
        this.description = description;
        this.decibel = decibel;
        this.latitude = latitude;
        this.longtitude = longtitude;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDecibel() {
        return decibel;
    }

    public void setDecibel(int decibel) {
        this.decibel = decibel;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public Map toMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("phone", phone);
        map.put("description", description);
        map.put("url", url);
        map.put("decibel", decibel);
        map.put("latitude", latitude);
        map.put("longtitude", longtitude);
        return map;
    }
}
