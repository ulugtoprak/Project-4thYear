package com.project.app.quickquery.models;
import java.util.HashMap;

public class QueryModel {
    private String key;
    private String query;
    private String title;
    private String userId;
    private LocationModel location;
    private HashMap<String, AnswerModel> answers = new HashMap<>();
    private HashMap<String, VoteModel> votes = new HashMap<>();

    public LocationModel getLocation() {
        return location;
    }

    public HashMap<String, VoteModel> getVotes() {
        return votes;
    }

    public HashMap<String,AnswerModel> getAnswers() {
        return answers;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public QueryModel()
    {
        //Cant declare hash map to votes without default constructor.
    }
    public QueryModel(String title, String query, String userId, LocationModel location)
    {
        this.title = title;
        this.query = query;
        this.userId = userId;
        this.location = location;
    }
    public String getUserId() {
        return userId;
    }

    public String getQuery() {
        return query;

    }


    public String getTitle() {
        return title;
    }

}
