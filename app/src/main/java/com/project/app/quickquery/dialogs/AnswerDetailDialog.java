package com.project.app.quickquery.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.project.app.quickquery.ApplicationController;
import com.project.app.quickquery.R;
import com.project.app.quickquery.activities.MainActivity;
import com.project.app.quickquery.models.LocationModel;
import com.project.app.quickquery.models.UserModel;
import com.project.app.quickquery.models.VoteModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

@SuppressWarnings("ALL")
class AnswerDetailDialog extends Dialog {
    private Button upVoteButton;
    private TextView userNameTextView;
    private MaterialRatingBar ratingBar;

    private boolean isVoted;
    private int votes;

    private DatabaseReference mDatabaseAnswers, mDatabaseUsers;
    private UserModel currentUser;
    private boolean isNotSend = false;
    private Activity activity;
    private LocationModel locationModel;

    @SuppressLint("SetTextI18n")
    AnswerDetailDialog(final Activity activity, final String title, final String answer, final String queryKey, final String userId, ArrayList<VoteModel> voteList, LocationModel locationModel) {
        super(activity);
        this.activity = activity;
        this.locationModel = locationModel;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_answer_detail);

        upVoteButton = findViewById(R.id.upvote_button);
        Button closeButton = findViewById(R.id.cancel_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        TextView titleTextView = findViewById(R.id.title_textview);
        userNameTextView = findViewById(R.id.username_textview);
        TextView upVoteCountTextView = findViewById(R.id.upvote_count_textview);
        TextView answerContentTextView = findViewById(R.id.answer_content_textview);

        ratingBar = findViewById(R.id.ratings);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        mDatabaseAnswers = FirebaseDatabase.getInstance().getReference("queries").child(queryKey).child("answers");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");

        final String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        isVoted = false;
        for (VoteModel vote : voteList) {
            if (uid.equals(vote.getUserId())) {
                //set the button text to remove vote.
                upVoteButton.setText("Remove Vote");
                isVoted = true;
                break;
            }
        }

        mDatabaseUsers.child(userId).child("votes").addValueEventListener(new ValueEventListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                votes = dataSnapshot.getValue(int.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (uid.equals(userId)) {
            upVoteButton.setVisibility(View.GONE);
        }

        titleTextView.setText(title);
        answerContentTextView.setText(answer);
        upVoteCountTextView.setText(String.valueOf(voteList.size()));

        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userNameTextView.setText(dataSnapshot.child(userId).child("userName").getValue(String.class));
                int voteCount = dataSnapshot.child(userId).child("votes").getValue(int.class);
                if (voteCount < 5) {
                    ratingBar.setRating(0.0f);
                } else if (voteCount >= 5 && voteCount < 10) {
                    ratingBar.setRating(0.5f);
                } else if (voteCount >= 10 && voteCount < 15) {
                    ratingBar.setRating(1.0f);
                } else if (voteCount >= 15 && voteCount < 20) {
                    ratingBar.setRating(1.5f);
                } else if (voteCount >= 20 && voteCount < 25) {
                    ratingBar.setRating(2.0f);
                } else if (voteCount >= 25 && voteCount < 30) {
                    ratingBar.setRating(2.5f);
                } else if (voteCount >= 30 && voteCount < 35) {
                    ratingBar.setRating(3.0f);
                } else if (voteCount >= 35 && voteCount < 40) {
                    ratingBar.setRating(3.5f);
                } else if (voteCount >= 40 && voteCount < 45) {
                    ratingBar.setRating(4.0f);
                } else if (voteCount >= 45 && voteCount < 50) {
                    ratingBar.setRating(4.5f);
                } else if (voteCount >= 50) {
                    ratingBar.setRating(5.0f);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        upVoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentUser = ApplicationController.getInstance().getUserModel();
                if (isVoted) {
                    mDatabaseAnswers.child(userId).child("votes").child(uid).removeValue();
                    upVoteButton.setText("Vote");
                    isVoted = false;
                    votes = votes - 1;
                    sendNotification(userId, "user " + currentUser.userName + " removed upvote :(", "");

                } else {
                    VoteModel voteModel = new VoteModel();
                    voteModel.setUserId(uid);
                    voteModel.setVote(1);
                    upVoteButton.setText("Remove Vote");
                    isVoted = true;
                    mDatabaseAnswers.child(userId).child("votes").child(uid).setValue(voteModel);
                    votes += 1;
                    sendNotification(userId, "user " + currentUser.userName + " upvoted you :)", "");
                }
                mDatabaseUsers.child(userId).child("votes").setValue(votes);

            }
        });
    }
    public void sendNotification(final String uid, final String title, final String message) {
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
                            jsonObject.put("message", message + "");
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