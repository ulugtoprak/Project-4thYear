package com.project.app.quickquery.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.app.quickquery.ApplicationController;
import com.project.app.quickquery.R;
import com.project.app.quickquery.adapter.FragmentAnswerAdapter;
import com.project.app.quickquery.models.QueryModel;
import com.project.app.quickquery.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AnswersFragment extends Fragment {


    private DatabaseReference mDatabaseQueries;
    private TextView tvNoAnswer;
    private RecyclerView rvAnswers;
    private FragmentAnswerAdapter fragmentAnswerAdapter;
    private List<QueryModel> queries=new ArrayList<>();


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_answers, container, false);
        rvAnswers = rootView.findViewById(R.id.rvAnswers);
        tvNoAnswer=rootView.findViewById(R.id.tvNoAnswer);
        mDatabaseQueries = FirebaseDatabase.getInstance().getReference("queries");
        setAnsListAdapter();
        getQueries();
        return rootView;
    }

    public void setAnsListAdapter() {
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        rvAnswers.setLayoutManager(layoutManager);
        fragmentAnswerAdapter = new FragmentAnswerAdapter(getActivity());
        rvAnswers.setAdapter(fragmentAnswerAdapter);
        rvAnswers.setNestedScrollingEnabled(true);
    }

    public void getQueries() {
        mDatabaseQueries.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                queries.clear();
                Log.d("SnapShot Value ", dataSnapshot.getChildren().toString());
                for (DataSnapshot querySnapshot : dataSnapshot.getChildren()) {
                    QueryModel query = querySnapshot.getValue(QueryModel.class);
                    assert query != null;
                    query.setKey(querySnapshot.getKey());
                    //boolean isContained = false;
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
                            //isContained = true;
                            break;
                        }
                    }


                        if( query.getUserId().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                            queries.add(query);
                            Constants.queries = queries;
//                            if (queries.size() > 0) {
//                                Constants.queries = queries;
//                            }
                        }

                    if(Constants.queries!=null) {


                        fragmentAnswerAdapter.addAll(queries);
                        tvNoAnswer.setVisibility(View.GONE);
                        rvAnswers.setVisibility(View.VISIBLE);
                    }else {
                        tvNoAnswer.setVisibility(View.VISIBLE);
                        rvAnswers.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
