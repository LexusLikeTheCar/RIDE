package edu.osu.ride.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class FirebaseService {
    public static void setBirdToken(String birdToken) {
        DatabaseReference ref = getUserDatabaseReference();

        ref.child("birdToken").setValue(birdToken);
        ref.child("birdTokenExpirationTimestamp").setValue(getTokenExpirationTime());
    }

    public static void setLyftToken(String lyftToken) {
        DatabaseReference ref = getUserDatabaseReference();

        ref.child("lyftToken").setValue(lyftToken);
        ref.child("lyftTokenExpirationTimestamp").setValue(getTokenExpirationTime());
    }

    private static DatabaseReference getUserDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    private static String getTokenExpirationTime() {
        long currentTime = new Date().getTime();
        return String.valueOf(currentTime + 86400000L);
    }
}
