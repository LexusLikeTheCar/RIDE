package edu.osu.ride.service;

import com.google.android.gms.maps.model.LatLng;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;

import java.util.List;

import edu.osu.ride.model.driver.uber.UberPrice;
import edu.osu.ride.model.driver.uber.UberPrices;
import edu.osu.ride.model.driver.uber.UberTime;
import edu.osu.ride.model.driver.uber.UberTimes;

public class UberService extends AbstractService {

    public static List<UberTime> getDriverArrivalTimeEstimates(LatLng start) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAuthorization("Token 19Mxvfp3En_PT__C7z47nizd2zR7kUzr_5uPmK1k");
        headers.put("Accept-Language", "en-US");

        GenericUrl url = new GenericUrl("https://api.uber.com/v1.2/estimates/time?"
                + "start_latitude=" + start.latitude
                + "&start_longitude=" + start.longitude);

        return requestFactory.buildGetRequest(url)
                .setHeaders(headers)
                .execute()
                .parseAs(UberTimes.class)
                .times;
    }

    public static List<UberPrice> getPriceEstimates(LatLng start, LatLng end) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAuthorization("Token 19Mxvfp3En_PT__C7z47nizd2zR7kUzr_5uPmK1k");
        headers.put("Accept-Language", "en-US");

        GenericUrl url = new GenericUrl("https://api.uber.com/v1.2/estimates/price?"
                + "start_latitude=" + start.latitude
                + "&start_longitude=" + start.longitude
                + "&end_latitude=" + end.latitude
                + "&end_longitude=" + end.longitude);

        return requestFactory.buildGetRequest(url)
                .setHeaders(headers)
                .execute()
                .parseAs(UberPrices.class)
                .prices;
    }

}
