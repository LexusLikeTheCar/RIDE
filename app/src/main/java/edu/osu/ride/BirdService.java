package edu.osu.ride;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BirdService {

    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private static final HttpRequestFactory requestFactory =
            HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                @Override
                public void initialize(HttpRequest request) {
                    request.setParser(new JsonObjectParser(JSON_FACTORY));
                }
            });

    public static class BirdToken {
        @Key
        public String token;
    }

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

    public static String locationResponse(String token) throws Exception {
        String locationVal = "{\"latitude\":40.001733,\"longitude\":-83.016041,\"altitude\":227,\"accuracy\":100,\"speed\":-1,\"heading\":-1}";

        HttpHeaders headers = new HttpHeaders();
        headers.setAuthorization("Bird " + token);
        headers.setLocation(locationVal);
        headers.put("Device-id", UUID.randomUUID().toString());
        headers.put("App-Version", "3.0.5");

        GenericUrl birdLocationUrl = new GenericUrl("https://api.bird.co/bird/nearby?latitude=40.001733&longitude=-83.016041&radius=1000");

        return requestFactory.buildGetRequest(birdLocationUrl)
                .setHeaders(headers)
                .execute()
                .parseAsString();
    }

}
