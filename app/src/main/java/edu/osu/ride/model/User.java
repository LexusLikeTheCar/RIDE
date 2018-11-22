package edu.osu.ride.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String username;
    public String birdToken;
    public String birdTokenExpirationTimestamp;
    public String lyftToken;
    public String lyftTokenExpirationTimestamp;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username) {
        this.username = username;
    }
}
