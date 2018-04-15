package com.project.app.quickquery.models;

import java.io.Serializable;

public class VoteModel implements Serializable{
    public String userId;
    public int vote;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }
}
