package edu.osu.ride.model.driver.uber;

import com.google.api.client.util.Key;

public class UberTime {
    @Key
    public String product_id;

    @Key
    public String display_name;

    @Key
    public int estimate;
}
