package com.project.app.quickquery.models;

import java.io.Serializable;
import java.util.HashMap;

public class AnswerModel implements Serializable {
    private String queryId;
    private String title;
    private String answer;
    private HashMap<String, VoteModel> votes = new HashMap<>();
    private String userId;

    public AnswerModel() {
    }


    public AnswerModel(String queryId, String title, String answer, HashMap<String, VoteModel> votes, String userId) {
        this.queryId = queryId;
        this.title = title;
        this.answer = answer;
        this.votes = votes;
        this.userId = userId;
    }
    public String getUserId() {
        return userId;
    }
    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public HashMap<String, VoteModel> getVotes() {
        return votes;
    }

    public void setVotes(HashMap<String, VoteModel> votes) {
        this.votes = votes;
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }

    //    public AnswerModel() {
//        // Cant declare hash map to votes without default constructor.
//    }
//
//    public AnswerModel(String queryId, String title, String answer, HashMap<String, VoteModel> votes, String userId) {
//        this.queryId = queryId;
//        this.title = title;
//        this.answer = answer;
//        this.votes = votes;
//        this.userId = userId;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public String getAnswer() {
//        return answer;
//    }
//
//    public HashMap<String, VoteModel> getVotes() {
//        return votes;
//    }
//
//    public String getUserId() {
//        return userId;
//    }
//
//    public void setQueryId(String queryId) {
//        this.queryId = queryId;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public void setAnswer(String answer) {
//        this.answer = answer;
//    }
//
//    public void setVotes(HashMap<String, VoteModel> votes) {
//        this.votes = votes;
//    }
//
//    public void setUserId(String userId) {
//        this.userId = userId;
//    }
}
