package edu.osu.ride;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class RiderActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    private Button mFilterRidesAll;
    private Button mFilterRidesUber;
    private Button mFilterRidesLyft;
    private Button mFilterRidesBird;
    private Button mFilterRidesLime;
    private Button mFindRidesButton;

    private Boolean mAllFiltered = true;
    private Boolean mUberFiltered = false;
    private Boolean mLyftFiltered = false;
    private Boolean mBirdFiltered = false;
    private Boolean mLimeFiltered = false;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
                    Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    updateMap(lastKnownLocation);
                }
            }
        }
    }

    public void updateMap(Location location) {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
        mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFilterRidesAll = findViewById(R.id.all_rides_filter);
        mFilterRidesAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAllFiltered = !mAllFiltered;
                mUberFiltered = false;
                mLyftFiltered = false;
                mBirdFiltered = false;
                mLimeFiltered = false;
                if (mAllFiltered) {
                    mFilterRidesAll.setBackground(getDrawable(R.drawable.filtered_on));
                    mFilterRidesUber.setBackground(getDrawable(R.drawable.filtered_off));
                    mFilterRidesLyft.setBackground(getDrawable(R.drawable.filtered_off));
                    mFilterRidesBird.setBackground(getDrawable(R.drawable.filtered_off));
                    mFilterRidesLime.setBackground(getDrawable(R.drawable.filtered_off));
                    mFilterRidesAll.setTextColor(getColor(R.color.colorPrimary));
                    mFilterRidesUber.setTextColor(getColor(R.color.textSecondary));
                    mFilterRidesLyft.setTextColor(getColor(R.color.textSecondary));
                    mFilterRidesBird.setTextColor(getColor(R.color.textSecondary));
                    mFilterRidesLime.setTextColor(getColor(R.color.textSecondary));
                } else {
                    mFilterRidesAll.setTextColor(getColor(R.color.textSecondary));
                    mFilterRidesAll.setBackground(getDrawable(R.drawable.filtered_off));
                }
            }
        });

        mFilterRidesUber = findViewById(R.id.uber_rides_filter);
        mFilterRidesUber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUberFiltered = !mUberFiltered;
                mAllFiltered = false;
                if (mUberFiltered) {
                    mFilterRidesAll.setBackground(getDrawable(R.drawable.filtered_off));
                    mFilterRidesUber.setBackground(getDrawable(R.drawable.filtered_on));
                    mFilterRidesAll.setTextColor(getColor(R.color.textSecondary));
                    mFilterRidesUber.setTextColor(getColor(R.color.colorPrimary));
                } else {
                    mFilterRidesUber.setBackground(getDrawable(R.drawable.filtered_off));
                    mFilterRidesUber.setTextColor(getColor(R.color.textSecondary));
                }
            }
        });

        mFilterRidesLyft = findViewById(R.id.lyft_rides_filter);
        mFilterRidesLyft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLyftFiltered = !mLyftFiltered;
                mAllFiltered = false;
                if (mLyftFiltered) {
                    mFilterRidesAll.setBackground(getDrawable(R.drawable.filtered_off));
                    mFilterRidesLyft.setBackground(getDrawable(R.drawable.filtered_on));
                    mFilterRidesAll.setTextColor(getColor(R.color.textSecondary));
                    mFilterRidesLyft.setTextColor(getColor(R.color.colorPrimary));
                } else {
                    mFilterRidesLyft.setBackground(getDrawable(R.drawable.filtered_off));
                    mFilterRidesLyft.setTextColor(getColor(R.color.textSecondary));
                }
            }
        });

        mFilterRidesBird = findViewById(R.id.bird_rides_filter);
        mFilterRidesBird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBirdFiltered = !mBirdFiltered;
                mAllFiltered = false;
                if (mBirdFiltered) {
                    mFilterRidesAll.setBackground(getDrawable(R.drawable.filtered_off));
                    mFilterRidesBird.setBackground(getDrawable(R.drawable.filtered_on));
                    mFilterRidesAll.setTextColor(getColor(R.color.textSecondary));
                    mFilterRidesBird.setTextColor(getColor(R.color.colorPrimary));
                } else {
                    mFilterRidesBird.setBackground(getDrawable(R.drawable.filtered_off));
                    mFilterRidesBird.setTextColor(getColor(R.color.textSecondary));
                }
            }
        });

        mFilterRidesLime = findViewById(R.id.lime_rides_filter);
        mFilterRidesLime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLimeFiltered = !mLimeFiltered;
                mAllFiltered = false;
                if (mLimeFiltered) {
                    mFilterRidesAll.setBackground(getDrawable(R.drawable.filtered_off));
                    mFilterRidesLime.setBackground(getDrawable(R.drawable.filtered_on));
                    mFilterRidesAll.setTextColor(getColor(R.color.textSecondary));
                    mFilterRidesLime.setTextColor(getColor(R.color.colorPrimary));
                } else {
                    mFilterRidesLime.setBackground(getDrawable(R.drawable.filtered_off));
                    mFilterRidesLime.setTextColor(getColor(R.color.textSecondary));
                }
            }
        });

        mFindRidesButton = findViewById(R.id.find_rides);
        mFindRidesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                ChooseRideDialogFragment dialog = new ChooseRideDialogFragment();
                dialog.show(fm, "tag");
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateMap(location);
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
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
            Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (lastKnownLocation != null) {
                updateMap(lastKnownLocation);
            }
        }
    }
}
