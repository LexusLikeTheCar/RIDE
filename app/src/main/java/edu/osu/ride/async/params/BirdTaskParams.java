package edu.osu.ride.async.params;

import android.location.Location;

public class BirdTaskParams {
    public String token;
    public Location location;
    public boolean generateToken;

    public BirdTaskParams(String token, Location location, boolean generateToken) {
        this.token = token;
        this.location = location;
        this.generateToken = generateToken;
    }
}
