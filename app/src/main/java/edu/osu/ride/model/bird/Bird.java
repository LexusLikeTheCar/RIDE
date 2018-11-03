package edu.osu.ride.model.bird;

import com.google.api.client.util.Key;

public class Bird {
    @Key
    public String id;

    @Key
    public BirdLocation location;

    @Key
    public int battery_level;
}

