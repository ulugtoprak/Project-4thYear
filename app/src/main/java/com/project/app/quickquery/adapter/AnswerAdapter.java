package com.project.app.quickquery.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.project.app.quickquery.R;
import com.project.app.quickquery.models.AnswerModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AnswerAdapter extends BaseAdapter {
    private Context context;
    private List<AnswerModel> answers;
    private DatabaseReference mDatabaseUsers;

    public AnswerAdapter(Context context, List<AnswerModel> answers) {
        this.context = context;
        this.answers = answers;
    }


    public List<AnswerModel> getAnswerList() {
        return answers;
    }

    public void addAnswer(AnswerModel answerModel) {
        answers.add(answerModel);
        notifyDataSetChanged();
    }

    public void addAnswer(AnswerModel answerModel,int position) {
        answers.set(position,answerModel);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return answers.size();
    }

    @Override
    public Object getItem(int position) {
        return answers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View answerView, ViewGroup parent) {
        if (answerView == null) {
            answerView = LayoutInflater.from(context).inflate(R.layout.answer_listview_item, parent, false);
        }
        TextView answer_title = answerView.findViewById(R.id.title_textview);
        final AnswerModel answerModel = (AnswerModel) this.getItem(position);
        answer_title.setText(answerModel.getTitle());

        final TextView username_title = answerView.findViewById(R.id.username_textview);
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users").child(answerModel.getUserId()).child("userName");
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                username_title.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        int rowColorEven = ContextCompat.getColor(context, R.color.table_data_row_even);
        int rowColorOdd = ContextCompat.getColor(context, R.color.table_data_row_odd);
        if (position % 2 == 0) {
            answerView.setBackgroundColor(rowColorEven);
        } else {
            answerView.setBackgroundColor(rowColorOdd);
        }
        return answerView;
    }
}
