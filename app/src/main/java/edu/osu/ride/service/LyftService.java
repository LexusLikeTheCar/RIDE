package edu.osu.ride.service;

import com.google.android.gms.maps.model.LatLng;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.json.JsonHttpContent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.osu.ride.model.driver.lyft.LyftPrice;
import edu.osu.ride.model.driver.lyft.LyftPrices;
import edu.osu.ride.model.driver.lyft.LyftTime;
import edu.osu.ride.model.driver.lyft.LyftTimes;
import edu.osu.ride.model.driver.lyft.LyftToken;

public class LyftService extends AbstractService {

    public static String generateToken() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAuthorization("Basic S1VDWjJqUEhUei1ZOmpvVFh3bXh6WV81b3NqWWh6SHFTU3FrVjVTZ3BpcFpo");

        Map<String, String> params = new HashMap<>();
        params.put("grant_type", "client_credentials");
        params.put("scope", "public");

        JsonHttpContent body = new JsonHttpContent(JSON_FACTORY, params);

        GenericUrl tokenUrl = new GenericUrl("https://api.lyft.com/oauth/token");

        return requestFactory.buildPostRequest(tokenUrl, body)
                .setHeaders(headers)
                .execute()
                .parseAs(LyftToken.class)
                .access_token;
    }

    public static List<LyftTime> getDriverArrivalTimeEstimates(String token, LatLng start) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAuthorization("Bearer " + token);

        GenericUrl url = new GenericUrl("https://api.lyft.com/v1/eta?"
                + "lat=" + start.latitude + "&lng=" + start.longitude);

        return requestFactory.buildGetRequest(url)
                .setHeaders(headers)
                .execute()
                .parseAs(LyftTimes.class)
                .eta_estimates;
    }

    public static List<LyftPrice> getPriceEstimates(String token, LatLng start, LatLng end) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAuthorization("Bearer " + token);

        GenericUrl url = new GenericUrl("https://api.lyft.com/v1/cost?"
                + "start_lat=" + start.latitude + "&start_lng=" + start.longitude
                + "&end_lat=" + end.latitude + "&end_lng=" + end.longitude);

        return requestFactory.buildGetRequest(url)
                .setHeaders(headers)
                .execute()
                .parseAs(LyftPrices.class)
                .cost_estimates;
    }

}
