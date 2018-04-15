package com.project.app.quickquery.utils;

import com.project.app.quickquery.models.AnswerModel;

public interface OnAnswerListner {

    void onAnswer(AnswerModel answerModel, int position);
}
