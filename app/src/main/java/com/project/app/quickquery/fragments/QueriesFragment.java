package com.project.app.quickquery.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.app.quickquery.ApplicationController;
import com.project.app.quickquery.R;
import com.project.app.quickquery.adapter.QueryAdapter;
import com.project.app.quickquery.dialogs.QueryDetailDialog;
import com.project.app.quickquery.models.AnswerModel;
import com.project.app.quickquery.models.QueryModel;
import com.project.app.quickquery.models.VoteModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class QueriesFragment extends Fragment {
    //Initialize Firebase
    //Initialize UI
    private ListView query_list;
    private List<QueryModel> queries;
    private QueryAdapter query_adapter;
    private DatabaseReference mDatabaseQueries;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_queries, container, false);
        mDatabaseQueries = FirebaseDatabase.getInstance().getReference("queries");
        queries = new ArrayList<>();
        query_list = rootView.findViewById(R.id.query_list_view);

        query_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayList<AnswerModel> answerList = new ArrayList<>();
                ArrayList<VoteModel> voteList = new ArrayList<>();

                for (Object o : queries.get(i).getAnswers().entrySet()) {
                    Map.Entry answers = (Map.Entry) o;
                    System.out.println("Key: " + answers.getKey() + " & Value: " + answers.getValue());

                    AnswerModel answer = (AnswerModel) answers.getValue();
                    answerList.add(answer);
                }

                for (Object o : queries.get(i).getVotes().entrySet()) {
                    Map.Entry votes = (Map.Entry) o;

                    VoteModel vote = (VoteModel) votes.getValue();
                    voteList.add(vote);
                    System.out.println("Vote User ID" + vote.userId);
                    System.out.println(vote.vote);
                }

                QueryDetailDialog dialog = new QueryDetailDialog(getActivity(), queries.get(i).getTitle(),
                        queries.get(i).getQuery(), queries.get(i).getUserId(), queries.get(i).getKey(),
                        answerList, voteList,queries.get(i).getLocation());
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCancelable(false);
                dialog.show();
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mDatabaseQueries.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                queries.clear();
                Log.d("SnapShot Value ", dataSnapshot.getChildren().toString());
                for (DataSnapshot querySnapshot : dataSnapshot.getChildren()) {
                    QueryModel query = querySnapshot.getValue(QueryModel.class);
                    assert query != null;
                    query.setKey(querySnapshot.getKey());
                    boolean isContained = false;
                    for (int i = 0; i < ApplicationController.getInstance().getUserModel().getLocations().size(); i++) {
                        float[] distance = new float[2];
                        if (query.getLocation() == null) {
                            continue;
                        }
                        Location.distanceBetween(query.getLocation().getLat(),
                                query.getLocation().getLon(),
                                ApplicationController.getInstance().getUserModel().getLocations().get(i).getLat(),
                                ApplicationController.getInstance().getUserModel().getLocations().get(i).getLon(),
                                distance);
                        if (distance[0] < 5000) {
                            isContained = true;
                            break;
                        }
                    }
                    if (isContained)
                        queries.add(query);
                }
//                query_list.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 118 * queries.size()));
                query_adapter = new QueryAdapter(getContext(), queries);
                query_list.setAdapter(query_adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



}
