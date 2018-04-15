package com.project.app.quickquery.utils;

import com.project.app.quickquery.models.PlaceAutoComplete;

public interface PlaceListner {

    void onPlaceSelectSpeech(PlaceAutoComplete placeAutoComplete, int position);
}
