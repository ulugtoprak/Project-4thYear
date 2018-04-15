package com.project.app.quickquery.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.app.quickquery.ApplicationController;
import com.project.app.quickquery.R;
import com.project.app.quickquery.activities.MainActivity;
import com.project.app.quickquery.adapter.AnswerAdapter;
import com.project.app.quickquery.models.AnswerModel;
import com.project.app.quickquery.models.LocationModel;
import com.project.app.quickquery.models.UserModel;
import com.project.app.quickquery.models.VoteModel;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class QueryDetailDialog extends Dialog implements OnAnswerListner {
    private Button upVoteBtn;
    private TextView userNameTextView;
    private MaterialRatingBar ratingBar;
    private AnswerAdapter answerAdapter;
    private boolean isVoted;
    private int votes;

    private DatabaseReference mDatabaseQueries, mDatabaseUsers;
    private UserModel myUser;
    private boolean isNotSend = false;
    private Activity activity;
    private LocationModel locationModel;

    @SuppressLint("SetTextI18n")
    public QueryDetailDialog(final Activity activity, final String title, final String query, final String userId, final String query_key, final ArrayList<AnswerModel> answerlist, ArrayList<VoteModel> voteList, final LocationModel locationModel) {
        super(activity);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_query_detail);
        this.activity = activity;
        this.locationModel = locationModel;
        upVoteBtn = findViewById(R.id.upvote_button);
        Button addEditButton = findViewById(R.id.add_edit_button);
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
        TextView queryContentTextView = findViewById(R.id.query_content_textview);

        ratingBar = findViewById(R.id.ratings);

        ListView answerListView = findViewById(R.id.answer_listview);
        answerAdapter = new AnswerAdapter(getContext(), answerlist);
        answerListView.setAdapter(answerAdapter);

        answerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayList<VoteModel> voteList = new ArrayList<>();
                for (Object o : answerlist.get(i).getVotes().entrySet()) {
                    Map.Entry votes = (Map.Entry) o;

                    VoteModel vote = (VoteModel) votes.getValue();
                    voteList.add(vote);
                    System.out.println("Vote User ID" + vote.userId);
                    System.out.println(vote.vote);
                }

                AnswerDetailDialog dialog = new AnswerDetailDialog(activity, answerlist.get(i).getTitle(),
                        answerlist.get(i).getAnswer(), query_key, answerlist.get(i).getUserId(), voteList, locationModel);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCancelable(false);
                dialog.show();
            }
        });

        FirebaseAuth auth = FirebaseAuth.getInstance();
        mDatabaseQueries = FirebaseDatabase.getInstance().getReference("queries").child(query_key);
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");

        final String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        isVoted = false;
        for (VoteModel vote : voteList) {
            if (uid.equals(vote.getUserId())) {
                //set the button text to remove vote.
                upVoteBtn.setText("Remove Vote");
                isVoted = true;
                break;
            }
        }

        mDatabaseUsers.child(userId).child("votes").addValueEventListener(new ValueEventListener() {
            @Override
            @SuppressWarnings("ConstantConditions")
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    votes = dataSnapshot.getValue(int.class);
                } catch (Exception e) {

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (uid.equals(userId)) {
            upVoteBtn.setVisibility(View.GONE);
            addEditButton.setText("Edit Query");
        }

        titleTextView.setText(title);
        queryContentTextView.setText(query);
        upVoteCountTextView.setText(String.valueOf(voteList.size()));

        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @SuppressWarnings("ConstantConditions")
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

        upVoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isNotSend = false;
                if (isVoted) {
                    mDatabaseQueries.child("votes").child(uid).removeValue();
                    upVoteBtn.setText("Vote");
                    isVoted = false;
                    votes = votes - 1;
                    Toast.makeText(getContext(), "Remove Vote sucessfully", Toast.LENGTH_SHORT).show();
                    myUser = ApplicationController.getInstance().getUserModel();

                    sendNotification(userId, "user " + myUser.userName + " removed previous up vote :(");
                } else {
                    VoteModel voteModel = new VoteModel();
                    voteModel.setUserId(uid);
                    voteModel.setVote(1);
                    upVoteBtn.setText("Remove Vote");
                    isVoted = true;
                    mDatabaseQueries.child("votes").child(uid).setValue(voteModel);
                    Toast.makeText(getContext(), "Vote successful", Toast.LENGTH_SHORT).show();
                    votes += 1;

                    myUser = ApplicationController.getInstance().getUserModel();
                    sendNotification(userId, "user " + myUser.userName + " up voted you :) ");
                }
                mDatabaseUsers.child(userId).child("votes").setValue(votes);


            }
        });

        addEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uid.equals(userId)) {
                    EditQueryDialog dialog = new EditQueryDialog(activity, title, query, query_key);
                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.setCancelable(false);
                    dialog.show();
                } else {
                    for (int i = 0; i < answerlist.size(); i++) {
                        if (answerlist.get(i).getUserId().equals(uid)) {
                            EditAnswerDialog dialog = new EditAnswerDialog(activity, answerlist.get(i).getTitle(), answerlist.get(i).getAnswer(), query_key, userId, locationModel, i, QueryDetailDialog.this);
                            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.setCancelable(false);
                            dialog.show();
                            return;
                        }
                    }
                    EditAnswerDialog dialog = new EditAnswerDialog(activity, "", "", query_key, userId, locationModel, 0, QueryDetailDialog.this);
                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.setCancelable(false);
                    dialog.show();
                }
            }
        });
    }


    private void sendNotification(final String uid, final String title) {

        DatabaseReference mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            @SuppressWarnings("ALL")
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    Map<String, UserModel> map = (Map<String, UserModel>) user.getValue();
                    if (user.getKey().equals(uid)) {
                        String token = String.valueOf(map.get("token"));
                        JSONArray jsonArray = new JSONArray();
                        jsonArray.put(token);
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("title", title);
                            jsonObject.put("message", "" + "");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (locationModel == null) {
                            locationModel = new LocationModel();
                        }

                        if (!isNotSend) {
                            isNotSend = true;

                            List<LocationModel> location = (List<LocationModel>) map.get("locations");
                            for (int i = 0; i < location.size(); i++) {

                                Map<String, List<LocationModel>> locations = (Map<String, List<LocationModel>>) location.get(i);
                                Double lat = Double.parseDouble(String.valueOf(locations.get("lat")));
                                Double lng = Double.parseDouble(String.valueOf(locations.get("lon")));
                                if (((MainActivity) activity).distance(lat,
                                        lng,
                                        locationModel.getLat(),
                                        locationModel.getLon()) < 100) {
                                    ((MainActivity) activity).sendMessage(jsonArray, ApplicationController.getInstance().getUserModel().userName + "", "", "Http:\\google.com", jsonObject.toString());
                                    break;

                                }
                            }

                        }


                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
//                        titleEditText.setText("");
//                        contentEditText.setText("");
//                        locationTextView.setText("");
            }
        });
    }

    @Override
    public void onAnswer(AnswerModel answerModel, int position) {
        if (position == 0) {
            if(answerAdapter.getAnswerList().size() == 0) {
                HashMap<String,VoteModel> vote =new HashMap<>();
                answerModel.setVotes(vote);
                answerAdapter.addAnswer(answerModel);
            }else {
                answerModel.setVotes(answerAdapter.getAnswerList().get(position).getVotes());
                answerAdapter.addAnswer(answerModel, position);
            }

        } else {
            answerModel = answerAdapter.getAnswerList().get(position);
            answerModel.setVotes(answerModel.getVotes());
            answerAdapter.addAnswer(answerModel, position);
        }

    }
}