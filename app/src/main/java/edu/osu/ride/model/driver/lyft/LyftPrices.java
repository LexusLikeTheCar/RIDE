package edu.osu.ride.model.driver.lyft;

import com.google.api.client.util.Key;

import java.util.List;

public class LyftPrices {
    @Key
    public List<LyftPrice> cost_estimates;
}
