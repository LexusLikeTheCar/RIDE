package edu.osu.ride.service;

import android.location.Location;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.json.JsonHttpContent;

import org.apache.commons.lang3.RandomStringUtils;

import edu.osu.ride.model.scooter.Scooter;
import edu.osu.ride.model.scooter.BirdToken;
import edu.osu.ride.model.scooter.Scooters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BirdService extends AbstractService {

    public static String generateToken() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.put("Device-id", UUID.randomUUID().toString());
        headers.put("Platform", "android");

        Map<String, String> params = new HashMap<>();
        params.put("email", RandomStringUtils.randomAlphanumeric(10) + "@gmail.com");

        JsonHttpContent body = new JsonHttpContent(JSON_FACTORY, params);

        GenericUrl loginUrl = new GenericUrl("https://api.bird.co/user/login");

        return requestFactory.buildPostRequest(loginUrl, body)
                .setHeaders(headers)
                .execute()
                .parseAs(BirdToken.class)
                .token;
    }

    public static List<Scooter> locationResponse(String token, Location location) throws Exception {
        String locationVal = "{\"latitude\":" + location.getLatitude() + ",\"longitude\":" + location.getLongitude()
                + ",\"altitude\":" + location.getAltitude() + ",\"accuracy\":100,\"speed\":-1,\"heading\":-1}";

        HttpHeaders headers = new HttpHeaders();
        headers.setAuthorization("Bird " + token);
        headers.setLocation(locationVal);
        headers.put("Device-id", UUID.randomUUID().toString());
        headers.put("App-Version", "3.0.5");

        GenericUrl birdLocationUrl = new GenericUrl("https://api.bird.co/bird/nearby?latitude="
                + location.getLatitude() + "&longitude=" + location.getLongitude() + "&radius=1000");

        // NOTE: Comment out above URL and use this URL if testing after Bird's hours of operations
        // GenericUrl birdLocationUrl = new GenericUrl("https://putsreq.com/fr0S8chPbKIQTpfBA7Bd");

        return requestFactory.buildGetRequest(birdLocationUrl)
                .setHeaders(headers)
                .execute()
                .parseAs(Scooters.class)
                .birds;
    }

}
