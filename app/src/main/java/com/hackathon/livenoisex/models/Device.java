package com.hackathon.livenoisex.models;

import java.util.HashMap;
import java.util.Map;

public class Device {
    private double latitude;
    private double longitude;
    private int insensity;

    public Device(double latitude, double longitude, int insensity){
        this.latitude = latitude;
        this.longitude = longitude;
        this.insensity = insensity;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getInsensity() {
        return insensity;
    }

    public Map toMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("Insensity", insensity);
        map.put("Latitude", latitude);
        map.put("Longitude", longitude);
        return map;
    }
}
