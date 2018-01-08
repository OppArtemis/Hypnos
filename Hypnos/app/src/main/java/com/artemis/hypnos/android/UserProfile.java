package com.artemis.hypnos.android;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jf2lin on 10/04/2017.
 */

public class UserProfile {
    private String userId;
    private String userName;
    private String userEmail;
    private List<String> connectedBabies = new ArrayList<>();

    UserProfile() {
        // Default constructor required for calls to DataSnapshot.getValue(UserProfile.class)
    }

    UserProfile(FirebaseUser user) {
        this.userName = user.getDisplayName();
        this.userEmail = user.getEmail();
        generateId();
//        this.connectedBabies = new ArrayList<>();
    }

    public void generateId() {
        String hashString = userEmail;
        this.userId = Constants.hashFunction(hashString);
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public List<String> getConnectedBabies() {
        return connectedBabies;
    }

    public String getUserId() {
        return userId;
    }

    public void addBabies(String newBaby) {connectedBabies.add(newBaby);}
}
