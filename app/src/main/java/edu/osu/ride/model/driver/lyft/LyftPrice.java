package edu.osu.ride.model.driver.lyft;

import com.google.api.client.util.Key;

public class LyftPrice {
    @Key
    public String currency;

    @Key
    public String ride_type;

    @Key
    public String display_name;

    @Key
    public String primetime_percentage;

    @Key
    public String primetime_confirmation_token;

    @Key
    public String cost_token;

    @Key
    public String price_quote_id;

    @Key
    public String price_group_id;

    @Key
    public boolean is_valid_estimate;

    @Key
    public int estimated_duration_seconds;

    @Key
    public double estimated_distance_miles;

    @Key
    public int estimated_cost_cents_min;

    @Key
    public int estimated_cost_cents_max;

    @Key
    public boolean can_request_ride;
}
