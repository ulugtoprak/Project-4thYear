package com.project.app.quickquery.models;

import java.util.ArrayList;

public class UserModel {
    public String userName;
    private String phoneNumber;
    public String gender;
    private String birthDate;
    private int votes;
    public String token;
    private ArrayList<LocationModel> locations;

    public ArrayList<LocationModel> getLocations() {
        if(locations == null){
            locations = new ArrayList<>();
        }
        return locations;
    }

    public UserModel() {

        // Default constructor required for calls to DataSnapshot.getValue(UserModel.class)
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public int getVotes() {
        return votes;
    }

    public String getUserName() {
        return userName;
    }

    public UserModel(String userName, String phoneNumber, String gender, String birthDate, int votes, ArrayList<LocationModel> locations, String token) {
        this.userName = userName;
        this.token = token;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.birthDate = birthDate;
        this.locations = locations;
        this.votes = votes;
    }
}
