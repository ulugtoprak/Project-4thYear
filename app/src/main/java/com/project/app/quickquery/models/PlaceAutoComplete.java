package com.project.app.quickquery.models;

public class PlaceAutoComplete {

    private String place_id;
    private String description;

    public PlaceAutoComplete(String place_id, String description) {
        this.place_id = place_id;
        this.description = description;
    }

    public String getPlaceDesc() {
        return description;
    }

    public void setPlaceDesc(String placeDesc) {
        description = placeDesc;
    }

    public String getPlaceID() {
        return place_id;
    }

    public void setPlaceID(String placeID) {
        place_id = placeID;
    }

}
