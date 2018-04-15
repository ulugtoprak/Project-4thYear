package com.project.app.quickquery.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.app.quickquery.R;
import com.project.app.quickquery.models.AnswerModel;

import java.util.ArrayList;
import java.util.List;

public class FragmentChildAnswerAdapter extends RecyclerView.Adapter<FragmentChildAnswerAdapter.LikeList> {

    private List<AnswerModel> list = new ArrayList<>();

    FragmentChildAnswerAdapter(Context context) {
        Context context1 = context;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
    }

    public void addAll(List<AnswerModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LikeList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_answers, parent, false);
        return new LikeList(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull LikeList holder, int position) {
        int number = position + 1;
        holder.tvAnswer.setText("Ans " + number + ":-" + list.get(position).getAnswer());
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class LikeList extends RecyclerView.ViewHolder {
        TextView tvAnswer;

        LikeList(View itemView) {
            super(itemView);
            tvAnswer = itemView.findViewById(R.id.tvAnswer);
        }
    }

}
