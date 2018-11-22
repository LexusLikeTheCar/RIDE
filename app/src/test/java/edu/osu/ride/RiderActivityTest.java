package edu.osu.ride;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

import edu.osu.ride.RiderActivity;
import edu.osu.ride.model.driver.Driver;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RiderActivityTest {

    @Test
    public void  getCarDestinationInfo_uber() {
        String service = "Uber";
        Driver car1 = new Driver("Uber",100, 500, 5);
        Driver car2 = new Driver("Uber",1000, 5000, 50);
        List<Driver> cars = new ArrayList<Driver>();
        cars.add(car1);
        cars.add(car2);

        RiderActivity riderActivity = new RiderActivity();
        riderActivity.getCarDestinationInfo(cars, service);

        assertEquals(car1, riderActivity.optimalUber);
    }

    @Test
    public void  getCarDestinationInfo_lyft() {
        String service = "Lyft";
        Driver car1 = new Driver("Lyft",200, 700, 7);
        Driver car2 = new Driver("Lyft",2000, 7000, 70);
        List<Driver> cars = new ArrayList<Driver>();
        cars.add(car1);
        cars.add(car2);

        RiderActivity riderActivity = new RiderActivity();
        riderActivity.getCarDestinationInfo(cars, service);

        assertEquals(car1, riderActivity.optimalLyft);
    }

    @Test
    public void  getScooterDestinationInfo_bird() {
        LatLng origin = new LatLng(40.0017, -83.0160);
        LatLng scooter = new LatLng(40.004, -83.0160);
        LatLng destination = new LatLng(40.008, -83.0160);
        String service = "Bird";

        RiderActivity riderActivity = new RiderActivity();
        riderActivity.getScooterDestinationInfo(origin, scooter, destination, service);

        assertEquals(3, riderActivity.optimalBird, 1e-15);
        assertEquals(4, riderActivity.optimalBirdDest, 1e-15);
        assertEquals(1.63, riderActivity.optimalBirdCost, 1e-15);
    }

    @Test
    public void  getScooterDestinationInfo_lime() {
        LatLng origin = new LatLng(20.0017, -23.0160);
        LatLng scooter = new LatLng(20.004, -23.0160);
        LatLng destination = new LatLng(20.008, -23.0160);
        String service = "Lime";

        RiderActivity riderActivity = new RiderActivity();
        riderActivity.getScooterDestinationInfo(origin, scooter, destination, service);

        assertEquals(3, riderActivity.optimalLime, 1e-15);
        assertEquals(4, riderActivity.optimalLimeDest, 1e-15);
        assertEquals(1.63, riderActivity.optimalLimeCost, 1e-15);
    }
}
