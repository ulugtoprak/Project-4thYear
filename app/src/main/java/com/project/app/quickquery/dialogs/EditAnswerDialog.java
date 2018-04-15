package com.project.app.quickquery.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.project.app.quickquery.ApplicationController;
import com.project.app.quickquery.R;
import com.project.app.quickquery.activities.MainActivity;
import com.project.app.quickquery.models.AnswerModel;
import com.project.app.quickquery.models.LocationModel;
import com.project.app.quickquery.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.app.quickquery.utils.OnAnswerListner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Objects;

class EditAnswerDialog extends Dialog {
    private EditText titleEditText, contentEditText;

    private DatabaseReference mDatabaseAnswers;
    private FirebaseAuth auth;
    private boolean isNotSend = false;
    private Activity activity;
    private LocationModel locationModel;

    EditAnswerDialog(final Activity activity, String title, String query, final String query_key, final String userId, LocationModel locationModel, final int position, final OnAnswerListner onAnswerListner) {
        super(activity);
        this.activity = activity;
        this.locationModel = locationModel;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_edit_answer);
        titleEditText = findViewById(R.id.title_edittext);
        titleEditText.setText(title);

        contentEditText = findViewById(R.id.answer_content_edittext);
        contentEditText.setText(query);

        auth = FirebaseAuth.getInstance();
        mDatabaseAnswers = FirebaseDatabase.getInstance().getReference("queries").child(query_key).child("answers").child(Objects.requireNonNull(auth.getCurrentUser()).getUid());

        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(titleEditText.getText())) {
                    Toast.makeText(getContext(), "Answer title is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(contentEditText.getText())) {
                    Toast.makeText(getContext(), "Answer description is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                mDatabaseAnswers.child("title").setValue(titleEditText.getText().toString());
                mDatabaseAnswers.child("answer").setValue(contentEditText.getText().toString());
                mDatabaseAnswers.child("userId").setValue(auth.getCurrentUser().getUid());
                Toast.makeText(getContext(), "Answer updated successfully", Toast.LENGTH_SHORT).show();
                AnswerModel answerModel=new AnswerModel(query_key,titleEditText.getText().toString(),contentEditText.getText().toString(),null,auth.getCurrentUser().getUid());

                onAnswerListner.onAnswer(answerModel,position);
                sendNotification(userId, "user " + ApplicationController.getInstance().getUserModel().userName + " answered your query");
                dismiss();


            }
        });
        Button closeButton = findViewById(R.id.cancel_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
    @SuppressWarnings("ALL")
    private void sendNotification(final String uid, final String title) {
        isNotSend = false;

        DatabaseReference mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("Yse", "r");
                for (DataSnapshot user : dataSnapshot.getChildren()) {

                    Map<String, UserModel> map = (Map<String, UserModel>) user.getValue();
                    if (user.getKey().equals(uid)) {


                        String token = String.valueOf(map.get("token"));
                        JSONArray jsonArray = new JSONArray();
                        jsonArray.put(token);
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("title", title);
                            jsonObject.put("message", "");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (locationModel == null) {
                            locationModel = new LocationModel();
                        }

                        List<LocationModel> location = (List<LocationModel>) map.get("locations");
                        for (int i = 0; i < location.size(); i++) {

                            Map<String, List<LocationModel>> locations = (Map<String, List<LocationModel>>) location.get(i);
                            Double lat = Double.parseDouble(String.valueOf(locations.get("lat")));
                            Double lng = Double.parseDouble(String.valueOf(locations.get("lon")));
                            if (((MainActivity) activity).distance(lat,
                                    lng,
                                    locationModel.getLat(),
                                    locationModel.getLon()) < 100) {
                                if (!isNotSend) {
                                    ((MainActivity) activity).sendMessage(jsonArray, ApplicationController.getInstance().getUserModel().userName + "", "", "Http:\\google.com", jsonObject.toString());
                                }

                                break;

                            }
                        }
                        isNotSend = true;

                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}