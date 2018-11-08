package edu.osu.ride;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
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
import edu.osu.ride.model.scooter.Scooter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class RiderActivity extends FragmentActivity implements OnMyLocationButtonClickListener,
        OnClickListener, OnMapReadyCallback {

    private static final String UBER = "Uber";
    private static final String LYFT = "Lyft";
    private static final String BIRD = "Bird";
    private static final String LIME = "Lime";
    private static final String BIRD_PACKAGE = "co.bird.android";
    private static final String LIME_PACKAGE = "com.limebike";
    private static final String TAG = "RiderActivity";

    private Location lastKnownLocation;

    private GoogleMap mMap;
    private View mMapView;

    private List<Scooter> mBirds;

    public void setBirds(List<Scooter> birds) {
        mBirds = birds;
    }

    private double optimalBird = Double.MAX_VALUE;
    private double optimalBirdDest = Double.MAX_VALUE;
    private double optimalBirdCost = Double.MAX_VALUE;

    private List<Scooter> mLimes;

    public void setLimes(List<Scooter> limes) {
        mLimes = limes;
    }

    private double optimalLime = Double.MAX_VALUE;
    private double optimalLimeDest = Double.MAX_VALUE;
    private double optimalLimeCost = Double.MAX_VALUE;

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    private Button mFilterRidesAllButton;
    private Button mFilterRidesUberButton;
    private Button mFilterRidesLyftButton;
    private Button mFilterRidesBirdButton;
    private Button mFilterRidesLimeButton;
    private Button mFindRidesButton;
    private Button mUserIcon;
    private Button mOpenBirdAppButton;
    private Button mOpenLimeAppButton;
    private Button mRideOptionsButton;

    private Boolean mAllFiltered = true;
    private Boolean mUberFiltered = false;
    private Boolean mLyftFiltered = false;
    private Boolean mBirdFiltered = false;
    private Boolean mLimeFiltered = false;

    public boolean mShowBirds;
    public boolean mShowLimes;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 50000, 15, mLocationListener);
                    mMap.setMyLocationEnabled(true);
                    mMap.setOnMyLocationButtonClickListener(this);
                    lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
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

        if (mShowBirds || mShowLimes) {
            List<LatLng> markers = getScooterMarkers(mShowBirds ? mBirds : mLimes);
            int markerIconId = mShowBirds ? R.mipmap.bird_location : R.mipmap.lime_location;

            if (markers.size() > 0) {
                for (LatLng m : markers) {
                    MarkerOptions markerOpts = new MarkerOptions().position(m);
                    mMap.addMarker(markerOpts)
                            .setIcon(BitmapDescriptorFactory.fromResource(markerIconId));
                }
            }
            mFindRidesButton.setVisibility(GONE);
            findViewById(R.id.filters).setVisibility(GONE);
            findViewById(R.id.destination).setVisibility(GONE);
            mRideOptionsButton.setVisibility(VISIBLE);
            Button openScooterAppBtn = mShowBirds ? mOpenBirdAppButton : mOpenLimeAppButton;
            openScooterAppBtn.setVisibility(VISIBLE);
        } else {
            mFindRidesButton.setVisibility(VISIBLE);
            findViewById(R.id.filters).setVisibility(VISIBLE);
            findViewById(R.id.destination).setVisibility(VISIBLE);
            mRideOptionsButton.setVisibility(GONE);
            mOpenBirdAppButton.setVisibility(GONE);
            mOpenLimeAppButton.setVisibility(GONE);
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

        mUserIcon = findViewById(R.id.user_icon);
        mUserIcon.setOnClickListener(this);

        mOpenBirdAppButton = findViewById(R.id.open_bird);
        mOpenBirdAppButton.setOnClickListener(this);

        mOpenLimeAppButton = findViewById(R.id.open_lime);
        mOpenLimeAppButton.setOnClickListener(this);

        mRideOptionsButton = findViewById(R.id.ride_options);
        mRideOptionsButton.setOnClickListener(this);

        mShowBirds = false;
    }

    public void launchRideOptionsDialog() {
        LatLng origin = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

        if (mAllFiltered || mBirdFiltered) {
            optimalBirdDest = Double.MAX_VALUE;
            List<LatLng> birdMarkers = getScooterMarkers(mBirds);
            if (birdMarkers.size() > 0) {
                for (LatLng bird : birdMarkers) {
                    getDestinationInfo(origin, bird, "Bird");
                }
            }
        }

        if (mAllFiltered || mLimeFiltered) {
            optimalLimeDest = Double.MAX_VALUE;
            List<LatLng> limeMarkers = getScooterMarkers(mLimes);
            if (limeMarkers.size() > 0) {
                for (LatLng lime : limeMarkers) {
                    getDestinationInfo(origin, lime, "Lime");
                }
            }
        }

        FragmentManager fm = getSupportFragmentManager();
        ChooseRideDialogFragment dialog = new ChooseRideDialogFragment();

        Bundle args = new Bundle();
        args.putBoolean("all", mAllFiltered);
        args.putBoolean("uber", mUberFiltered);
        args.putBoolean("lyft", mLyftFiltered);
        args.putBoolean("bird", mBirdFiltered);
        args.putBoolean("lime", mLimeFiltered);
        if (mAllFiltered || mBirdFiltered) {
            args.putDouble("birdDuration", optimalBird);
            args.putDouble("birdDestination", optimalBirdDest);
            args.putDouble("birdCost", optimalBirdCost);
        }
        if (mAllFiltered || mLimeFiltered) {
            args.putDouble("limeDuration", optimalLime);
            args.putDouble("limeDestination", optimalLimeDest);
            args.putDouble("limeCost", optimalLimeCost);
        }
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
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 15, mLocationListener);
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            initializeMap(lastKnownLocation);
        }
    }

    private List<LatLng> getScooterMarkers(List<Scooter> scooters) {
        List<LatLng> markers = new ArrayList<>();

        for (Scooter scooter : scooters) {
            markers.add(new LatLng(scooter.location.latitude, scooter.location.longitude));
        }

        return markers;
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
                launchResponseAggregatorTask();
                break;
            case R.id.user_icon:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.open_bird:
                deepLink(BIRD_PACKAGE);
                break;
            case R.id.open_lime:
                deepLink(LIME_PACKAGE);
                break;
            case R.id.ride_options:
                mShowLimes = false;
                mShowBirds = false;
                updateMap();
                launchResponseAggregatorTask();
                break;
        }
    }

    private void deepLink(String packageName) {
        if (isPackageInstalled(this, packageName)) {
            Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
            startActivity(intent);
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(
                    "https://play.google.com/store/apps/details?id=" + packageName));
            intent.setPackage("com.android.vending");
            startActivity(intent);
        }
    }

    private boolean isPackageInstalled(Context context, String packageId) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageId, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            // ignored.
        }
        return false;
    }

    private void getDestinationInfo(LatLng origin, LatLng scooter, String service) {
        final double earthRadius = 3961; // mi
        final double averageWalkingSpeed = 3.1; // mph
        final double averageScooterSpeed = 15; // mph
        final LatLng destination = new LatLng(40.0049976, -83.0077963);

        double dLon = (scooter.longitude - origin.longitude) * (Math.PI / 180); // Radians
        double dLat = (scooter.latitude - origin.latitude) * (Math.PI / 180); // Radians
        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.cos(origin.latitude) * Math.cos(scooter.latitude) * Math.pow((Math.sin(dLon / 2)), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distanceBird = earthRadius * c; // mi
        double durationBird = distanceBird / averageWalkingSpeed * 60; // min

        System.out.println("bird: " + distanceBird + "mi, " + durationBird + "min");

        dLon = (destination.longitude - scooter.longitude) * (Math.PI / 180); // Radians
        dLat = (destination.latitude - scooter.latitude) * (Math.PI / 180); // Radians
        a = Math.pow(Math.sin(dLat / 2), 2) + Math.cos(scooter.latitude) * Math.cos(destination.latitude) * Math.pow((Math.sin(dLon / 2)), 2);
        c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distanceDest = earthRadius * c; // mi
        double durationDest = distanceDest / averageScooterSpeed * 60 + durationBird; // min
        double cost = Math.round((1 + 0.15 * durationDest) * 100.00) / 100.00;

        System.out.println("dest: " + (distanceDest + distanceBird) + "mi, " + durationDest + "min");

        if ((durationDest < optimalBirdDest) && (service == "Bird")) {
            optimalBird = Math.round(durationBird);
            optimalBirdDest = Math.round(durationDest);
            optimalBirdCost = cost;
        }

        if ((durationDest < optimalLimeDest) && (service == "Lime")) {
            optimalLime = Math.round(durationBird);
            optimalLimeDest = Math.round(durationDest);
            optimalLimeCost = cost;
        }
    }

    private void launchResponseAggregatorTask() {
        if (mAllFiltered) {
            new ResponseAggregatorAsyncTask(RiderActivity.this).execute();
        } else {
            new ResponseAggregatorAsyncTask(RiderActivity.this,
                    mBirdFiltered, mLimeFiltered, mUberFiltered, mLyftFiltered).execute();
        }
    }
}
