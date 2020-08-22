package com.example.pocketclash;

public class MyLocation {
    Double lat = 0d;
    Double lon = 0d;

    public MyLocation() {
    }

    public MyLocation(Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "MyLocation{" +
                "lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}