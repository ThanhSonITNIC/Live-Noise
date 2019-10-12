package com.hackathon.livenoisex.models;

import java.util.HashMap;
import java.util.Map;

public class Device {
    private double latitude;
    private double longtitude;
    private int insensity;

    public Device() {
    }

    public Device(double latitude, double longtitude, int insensity){
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.insensity = insensity;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public int getInsensity() {
        return insensity;
    }

    public Map toMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("insensity", insensity);
        map.put("latitude", latitude);
        map.put("longtitude", longtitude);
        return map;
    }
}
