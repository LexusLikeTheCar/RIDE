package edu.osu.ride.service;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.util.List;
import java.util.UUID;

import edu.osu.ride.model.scooter.Scooter;
import edu.osu.ride.model.scooter.Scooters;

public class LimeService {

    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private static final HttpRequestFactory requestFactory =
            HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                @Override
                public void initialize(HttpRequest request) {
                    request.setParser(new JsonObjectParser(JSON_FACTORY));
                }
            });

    public static List<Scooter> locationResponse() throws Exception {
        String locationVal = "{\"latitude\":40.001733,\"longitude\":-83.016041,\"altitude\":227,\"accuracy\":100,\"speed\":-1,\"heading\":-1}";

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(locationVal);

        GenericUrl limeLocationUrl = new GenericUrl("https://putsreq.com/fr0S8chPbKIQTpfBA7Bd");

        return requestFactory.buildGetRequest(limeLocationUrl)
                .setHeaders(headers)
                .execute()
                .parseAs(Scooters.class)
                .birds;
    }

}
