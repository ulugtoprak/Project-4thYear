package com.project.app.quickquery.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.app.quickquery.R;
import com.project.app.quickquery.models.AnswerModel;
import com.project.app.quickquery.models.QueryModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FragmentAnswerAdapter extends RecyclerView.Adapter<FragmentAnswerAdapter.LikeList> {

    private Context context;
    //private LayoutInflater layoutInflater;
    private List<QueryModel> list = new ArrayList<>();

    public FragmentAnswerAdapter(Context context) {
        this.context = context;
        //layoutInflater = LayoutInflater.from(context);
    }

    public void addAll(List<QueryModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LikeList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new LikeList(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull LikeList holder, @SuppressLint("RecyclerView") final int position) {

        int number = position + 1;
        HashMap<String, AnswerModel> ans = list.get(position).getAnswers();
        List<AnswerModel> answerList = new ArrayList<>(ans.values());
        holder.tvQuestion.setText("Query " + number + ": -" + list.get(position).getQuery());
        setAnsListAdapter(holder.rvAnswers, answerList);
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query applesQuery = ref.child("queries").orderByChild("title").equalTo(list.get(position).getTitle());

                applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                            appleSnapshot.getRef().removeValue();
                            list.remove(position);
                            notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("cancelled", "onCancelled", databaseError.toException());
                    }
                });
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class LikeList extends RecyclerView.ViewHolder {
        RecyclerView rvAnswers;
        TextView tvQuestion;
        ImageView ivDelete;

        LikeList(View itemView) {
            super(itemView);
            rvAnswers = itemView.findViewById(R.id.rvAnswers);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }

    private void setAnsListAdapter(RecyclerView rvAnswers, List<AnswerModel> answerList) {
        GridLayoutManager layoutManager = new GridLayoutManager(context, 1);
        rvAnswers.setLayoutManager(layoutManager);
        FragmentChildAnswerAdapter fragmentAnswerAdapter = new FragmentChildAnswerAdapter(context);
        rvAnswers.setAdapter(fragmentAnswerAdapter);
        rvAnswers.setNestedScrollingEnabled(true);
        fragmentAnswerAdapter.addAll(answerList);
    }

}
