package edu.osu.ride.model.driver.uber;

import com.google.api.client.util.Key;

public class UberPrice {
    @Key
    public String product_id;

    @Key
    public String display_name;

    @Key
    public double distance;

    @Key
    public int high_estimate;

    @Key
    public int low_estimate;

    @Key
    public int duration;

    @Key
    public String estimate;
}
