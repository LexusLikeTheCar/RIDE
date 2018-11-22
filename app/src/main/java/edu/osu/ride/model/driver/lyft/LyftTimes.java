package edu.osu.ride.model.driver.lyft;

import com.google.api.client.util.Key;

import java.util.List;

public class LyftTimes {
    @Key
    public List<LyftTime> eta_estimates;
}
