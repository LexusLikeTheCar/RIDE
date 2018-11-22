package edu.osu.ride.async.params;

import com.google.android.gms.maps.model.LatLng;

public class LyftTaskParams {
    public String token;
    public LatLng originLatLng;
    public LatLng destinationLatLng;
    public boolean generateToken;

    public LyftTaskParams(String token, LatLng originLatLng, LatLng destinationLatLng, boolean generateToken) {
        this.token = token;
        this.originLatLng = originLatLng;
        this.destinationLatLng = destinationLatLng;
        this.generateToken = generateToken;
    }
}
