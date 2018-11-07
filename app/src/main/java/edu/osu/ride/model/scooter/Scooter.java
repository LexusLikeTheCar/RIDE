package edu.osu.ride.model.scooter;

import com.google.api.client.util.Key;

public class Scooter {
    @Key
    public String id;

    @Key
    public ScooterLocation location;

    @Key
    public int battery_level;
}

