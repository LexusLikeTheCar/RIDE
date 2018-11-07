package edu.osu.ride;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import edu.osu.ride.async.ResponseAggregatorAsyncTask;
import edu.osu.ride.model.bird.Bird;

public class RiderActivity extends FragmentActivity implements OnMyLocationButtonClickListener,
        OnClickListener, OnMapReadyCallback {

    private static final String UBER = "Uber";
    private static final String LYFT = "Lyft";
    private static final String BIRD = "Bird";
    private static final String LIME = "Lime";

    private GoogleMap mMap;
    private View mMapView;

    private List<Bird> mBirds;
    public void setBirds(List<Bird> birds) {
        mBirds = birds;
    }

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    private Button mFilterRidesAllButton;
    private Button mFilterRidesUberButton;
    private Button mFilterRidesLyftButton;
    private Button mFilterRidesBirdButton;
    private Button mFilterRidesLimeButton;
    private Button mFindRidesButton;
    private Button userIcon;
    private Boolean mAllFiltered = true;
    private Boolean mUberFiltered = false;
    private Boolean mLyftFiltered = false;
    private Boolean mBirdFiltered = false;
    private Boolean mLimeFiltered = false;

    public boolean mShowBirds;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 50000, 15, mLocationListener);
                    mMap.setMyLocationEnabled(true);
                    mMap.setOnMyLocationButtonClickListener(this);
                    Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    initializeMap(lastKnownLocation);
                }
            }
        }
    }

    public void initializeMap(Location location) {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));

        // Position "My Location" button in lower left corner
        if (mMapView != null && mMapView.findViewById((int) 1) != null) {
            final ViewGroup parent = (ViewGroup) mMapView.findViewById((int) 1);
            parent.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        View locationBtn = mMapView.findViewById((int) 2);

                        LayoutParams lp = new LayoutParams(locationBtn.getHeight(), locationBtn.getHeight());

                        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

                        lp.setMargins(30, 0, 0, 80);
                        locationBtn.setLayoutParams(lp);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
    }

    public void updateMap() {
        mMap.clear();

        if (mShowBirds) {
            List<LatLng> birdMarkers = getBirdMarkers(mBirds);

            if (birdMarkers.size() > 0) {
                for (LatLng m : birdMarkers) {
                    MarkerOptions birdMarkerOpts = new MarkerOptions().position(m);
                    mMap.addMarker(birdMarkerOpts)
                            .setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.bird_location));
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mMapView = mapFragment.getView();
        mapFragment.getMapAsync(this);

        mFilterRidesAllButton = findViewById(R.id.all_rides_filter);
        mFilterRidesAllButton.setOnClickListener(this);

        mFilterRidesUberButton = findViewById(R.id.uber_rides_filter);
        mFilterRidesUberButton.setOnClickListener(this);

        mFilterRidesLyftButton = findViewById(R.id.lyft_rides_filter);
        mFilterRidesLyftButton.setOnClickListener(this);

        mFilterRidesBirdButton = findViewById(R.id.bird_rides_filter);
        mFilterRidesBirdButton.setOnClickListener(this);

        mFilterRidesLimeButton = findViewById(R.id.lime_rides_filter);
        mFilterRidesLimeButton.setOnClickListener(this);

        mFindRidesButton = findViewById(R.id.find_rides);
        mFindRidesButton.setOnClickListener(this);
        userIcon = findViewById(R.id.user_icon);
        userIcon.setOnClickListener(this);
        mShowBirds = false;
    }

    public void responseAggregationFinished() {
        FragmentManager fm = getSupportFragmentManager();
        ChooseRideDialogFragment dialog = new ChooseRideDialogFragment();

        Bundle args = new Bundle();
        args.putBoolean("all", mAllFiltered);
        args.putBoolean("uber", mUberFiltered);
        args.putBoolean("lyft", mLyftFiltered);
        args.putBoolean("bird", mBirdFiltered);
        args.putBoolean("lime", mLimeFiltered);
        dialog.setArguments(args);
        dialog.show(fm, "tag");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateMap();
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            @Override
            public void onProviderEnabled(String provider) {}
            @Override
            public void onProviderDisabled(String provider) {}
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 15, mLocationListener);
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            initializeMap(lastKnownLocation);
        }
    }

    private List<LatLng> getBirdMarkers(List<Bird> birds) {
        List<LatLng> birdMarkers = new ArrayList<>();

        for (Bird bird : birds) {
            birdMarkers.add(new LatLng(bird.location.latitude, bird.location.longitude));
        }

        return birdMarkers;
    }

    private void toggleAll() {
        mAllFiltered = !mAllFiltered;
        mUberFiltered = false;
        mLyftFiltered = false;
        mBirdFiltered = false;
        mLimeFiltered = false;
        if (mAllFiltered) {
            mFilterRidesAllButton.setBackground(getDrawable(R.drawable.filtered_on));
            mFilterRidesUberButton.setBackground(getDrawable(R.drawable.filtered_off));
            mFilterRidesLyftButton.setBackground(getDrawable(R.drawable.filtered_off));
            mFilterRidesBirdButton.setBackground(getDrawable(R.drawable.filtered_off));
            mFilterRidesLimeButton.setBackground(getDrawable(R.drawable.filtered_off));
            mFilterRidesAllButton.setTextColor(getColor(R.color.colorPrimary));
            mFilterRidesUberButton.setTextColor(getColor(R.color.textSecondary));
            mFilterRidesLyftButton.setTextColor(getColor(R.color.textSecondary));
            mFilterRidesBirdButton.setTextColor(getColor(R.color.textSecondary));
            mFilterRidesLimeButton.setTextColor(getColor(R.color.textSecondary));
        } else {
            mFilterRidesAllButton.setTextColor(getColor(R.color.textSecondary));
            mFilterRidesAllButton.setBackground(getDrawable(R.drawable.filtered_off));
        }
    }

    private void toggleFilter(String service) {
        boolean serviceFiltered = false;
        Button serviceButton = null;

        switch (service) {
            case UBER:
                mUberFiltered = !mUberFiltered;
                serviceFiltered = mUberFiltered;
                serviceButton = mFilterRidesUberButton;
                break;
            case LYFT:
                mLyftFiltered = !mLyftFiltered;
                serviceFiltered = mLyftFiltered;
                serviceButton = mFilterRidesLyftButton;
                break;
            case BIRD:
                mBirdFiltered = !mBirdFiltered;
                serviceFiltered = mBirdFiltered;
                serviceButton = mFilterRidesBirdButton;
                break;
            case LIME:
                mLimeFiltered = !mLimeFiltered;
                serviceFiltered = mLimeFiltered;
                serviceButton = mFilterRidesLimeButton;
                break;
        }

        mAllFiltered = false;
        if (serviceFiltered) {
            mFilterRidesAllButton.setBackground(getDrawable(R.drawable.filtered_off));
            serviceButton.setBackground(getDrawable(R.drawable.filtered_on));
            mFilterRidesAllButton.setTextColor(getColor(R.color.textSecondary));
            serviceButton.setTextColor(getColor(R.color.colorPrimary));
        } else {
            serviceButton.setBackground(getDrawable(R.drawable.filtered_off));
            serviceButton.setTextColor(getColor(R.color.textSecondary));
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.all_rides_filter:
                toggleAll();
                break;
            case R.id.uber_rides_filter:
                toggleFilter(UBER);
                break;
            case R.id.lyft_rides_filter:
                toggleFilter(LYFT);
                break;
            case R.id.bird_rides_filter:
                toggleFilter(BIRD);
                break;
            case R.id.lime_rides_filter:
                toggleFilter(LIME);
                break;
            case R.id.find_rides:
                if (mAllFiltered) {
                    new ResponseAggregatorAsyncTask(RiderActivity.this).execute();
                } else {

                    new ResponseAggregatorAsyncTask(RiderActivity.this,
                        mBirdFiltered, mLimeFiltered, mUberFiltered, mLyftFiltered).execute();

                    //new ResponseAggregatorAsyncTask(RiderActivity.this).execute(); // TEMPORARY WORKAROUND
                }
                break;
            case R.id.user_icon:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
    }
}
