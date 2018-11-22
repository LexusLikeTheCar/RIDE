package edu.osu.ride.service;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;

import java.util.List;

import edu.osu.ride.model.scooter.Scooter;
import edu.osu.ride.model.scooter.Scooters;

public class LimeService extends AbstractService {

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
