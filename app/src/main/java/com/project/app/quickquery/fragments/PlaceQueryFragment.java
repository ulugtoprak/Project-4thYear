package com.project.app.quickquery.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.project.app.quickquery.ApplicationController;
import com.project.app.quickquery.R;
import com.project.app.quickquery.activities.MainActivity;
import com.project.app.quickquery.activities.PlaceSpeechActivity;
import com.project.app.quickquery.models.LocationModel;
import com.project.app.quickquery.models.PlaceAutoComplete;
import com.project.app.quickquery.models.PlacesSpeechModel;
import com.project.app.quickquery.models.QueryModel;
import com.project.app.quickquery.models.UserModel;
import com.project.app.quickquery.utils.Constants;
import com.project.app.quickquery.utils.PlaceListner;
import com.project.app.quickquery.utils.VolleyJSONRequest;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class PlaceQueryFragment extends Fragment implements PlaceListner, com.android.volley.Response.Listener<String>, com.android.volley.Response.ErrorListener {
    private DatabaseReference mDatabaseQueries;

    private EditText titleEditText, contentEditText;
    private TextView locationTextView;
    private LocationModel locationModel;
    private int PLACE_PICKER_REQUEST = 1;
    private final static int RESULT_OK = -1;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private DatabaseReference mDatabaseUsers;
    private String userId;
    UserModel userModel;
    private String getPlacesHit = "places_hit";
    private VolleyJSONRequest request;
    protected ImageView ivSpeech;
    private Handler handler;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_place_query, container, false);

        titleEditText = rootView.findViewById(R.id.title_edittext);
        contentEditText = rootView.findViewById(R.id.query_content_edittext);
        locationTextView = rootView.findViewById(R.id.location_textview);
        Button saveButton = rootView.findViewById(R.id.save_button);
        Button cancelButton = rootView.findViewById(R.id.cancel_button);
        Button selectLocationButton = rootView.findViewById(R.id.add_location_button);
        ivSpeech = rootView.findViewById(R.id.ivSpeech);
        FirebaseAuth auth;
        //auth = FirebaseAuth.getInstance();
        mDatabaseQueries = FirebaseDatabase.getInstance().getReference("queries");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");
        selectLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    startActivityForResult(builder.build(Objects.requireNonNull(getActivity())), PLACE_PICKER_REQUEST);
                } catch (Exception ex) {
                    Log.e("Exception", ex.getMessage());
                }
            }
        });

        ivSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validate the textbox.
                if (TextUtils.isEmpty(titleEditText.getText().toString())) {
                    Toast.makeText(getContext(), "Title is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(contentEditText.getText().toString())) {
                    Toast.makeText(getContext(), "Description is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(locationTextView.getText().toString())) {
                    Toast.makeText(getContext(), "Location is required", Toast.LENGTH_SHORT).show();
                    return;
                }
//                Save the data into the Firebase Database.
//                First get the userId
                String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                String key = mDatabaseQueries.push().getKey();
                final String message = titleEditText.getText().toString();
                final QueryModel query = new QueryModel(message, contentEditText.getText().toString(), userId, locationModel);
                mDatabaseQueries.child(key).setValue(query);
                titleEditText.setText("");
                contentEditText.setText("");
                locationTextView.setText("");

                Toast.makeText(getContext(), "Query posted successfully", Toast.LENGTH_SHORT).show();

                // Send push notification to the users who are in the location nearby
                ((MainActivity) Objects.requireNonNull(getActivity())).getUsers("There is a new query in your area!", "", locationModel);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleEditText.setText("");
                contentEditText.setText("");
                locationTextView.setText("");
            }
        });


        auth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");

        return rootView;
    }


    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }


    //This Activity will fire when user will select Location
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //User can only select maximum 3 locations
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(Objects.requireNonNull(getContext()), data);
                String toastMsg = String.format("Address: %s", place.getAddress());
                locationModel = new LocationModel(toastMsg, place.getLatLng().longitude, place.getLatLng().latitude);
                locationTextView.setText(toastMsg);
                Toast.makeText(getContext(), toastMsg, Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQ_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && null != data) {

                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                Intent intent = new Intent(getContext(), PlaceSpeechActivity.class);
                intent.putExtra("message", result.get(0));
                Objects.requireNonNull(getActivity()).startActivity(intent);
                Constants.placeListner = PlaceQueryFragment.this;
            }
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        mDatabaseUsers.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userModel = dataSnapshot.getValue(UserModel.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onPlaceSelectSpeech(final PlaceAutoComplete place, int position) {

        Runnable run = new Runnable() {


            @Override
            public void run() {

                // cancel all the previous requests in the queue to optimise your network calls during autocomplete search
                ApplicationController.volleyQueueInstance.cancelRequestInQueue(getPlacesHit);

                //build Get url of Place Autocomplete and hit the url to fetch result.
                request = new VolleyJSONRequest(com.android.volley.Request.Method.GET, "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + place.getPlaceID() + "&key=AIzaSyAZA9g-khAYq_rLKV6sPxM_05RLJL1YthA", null, null, PlaceQueryFragment.this, PlaceQueryFragment.this);

                //Give a tag to your request so that you can use this tag to cancel request later.
                request.setTag(getPlacesHit);

                ApplicationController.volleyQueueInstance.addToRequestQueue(request);

            }

        };

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        } else {
            handler = new Handler();
        }
        handler.postDelayed(run, 1000);

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e("Error", error.getMessage());
    }

    @Override
    public void onResponse(String response) {
        Gson gson = new Gson();
        PlacesSpeechModel predictions = gson.fromJson(response, PlacesSpeechModel.class);
        String toastMsg = String.format("Address: %s", predictions.result.formattedAddress);
        locationModel = new LocationModel(toastMsg, predictions.result.geometry.location.lng, predictions.result.geometry.location.lat);
        locationTextView.setText(toastMsg);
        Toast.makeText(getContext(), toastMsg, Toast.LENGTH_LONG).show();
    }
}

