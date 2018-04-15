package com.project.app.quickquery.models;

public class LocationModel {
    public String location;
    public double lon;
    public double lat;

    public LocationModel() {
    }

    public LocationModel(String location, double lon, double lat) {
        this.location = location;
        this.lon = lon;
        this.lat = lat;
    }

    public String getLocation() {
        return location;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

}
