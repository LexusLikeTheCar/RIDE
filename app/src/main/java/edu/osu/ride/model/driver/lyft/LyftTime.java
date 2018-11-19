package edu.osu.ride.model.driver.lyft;

import com.google.api.client.util.Key;

public class LyftTime {
    @Key
    public String display_name;

    @Key
    public String ride_type;

    @Key
    public int eta_seconds;

    @Key
    public boolean is_valid_estimate;
}
