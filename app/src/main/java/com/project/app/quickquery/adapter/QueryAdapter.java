package com.project.app.quickquery.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.project.app.quickquery.R;
import com.project.app.quickquery.models.QueryModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.*;

public class QueryAdapter extends BaseAdapter {
    private Context context;
    private List<QueryModel> queries;

    public QueryAdapter(Context context, List<QueryModel> queries)
    {
        this.context = context;
        this.queries = queries;
    }
    @Override
    public int getCount() {
        return queries.size();
    }

    @Override
    public Object getItem(int position) {
        return queries.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View queryView, ViewGroup parent) {
        if (queryView == null)
        {
            queryView = LayoutInflater.from(context).inflate(R.layout.query_listview_item,parent,false);
        }
        final TextView query_title = queryView.findViewById(R.id.title_textview);
        final TextView query_username = queryView.findViewById(R.id.username_textview);
        final QueryModel queryModel =(QueryModel) this.getItem(position);
        query_title.setText(queryModel.getTitle());

        DatabaseReference mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users").child(queryModel.getUserId()).child("userName");
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                query_username.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Log.d("Votes",queryModel.getVotes().toString());

        int rowColorEven = ContextCompat.getColor(context, R.color.table_data_row_even);
        int rowColorOdd = ContextCompat.getColor(context, R.color.table_data_row_odd);
        if (position % 2 == 0) {
            queryView.setBackgroundColor(rowColorEven);
        } else {
            queryView.setBackgroundColor(rowColorOdd);
        }
        return queryView;
    }
}
