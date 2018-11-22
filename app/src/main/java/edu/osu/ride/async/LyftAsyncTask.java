package edu.osu.ride.async;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import edu.osu.ride.async.params.LyftTaskParams;
import edu.osu.ride.model.driver.Driver;
import edu.osu.ride.model.driver.lyft.LyftPrice;
import edu.osu.ride.model.driver.lyft.LyftTime;
import edu.osu.ride.service.FirebaseService;
import edu.osu.ride.service.LyftService;

public class LyftAsyncTask extends AsyncTask<LyftTaskParams, Void, List<Driver>> {

    public interface LyftResponse {
        void processFinish(List<Driver> output);
    }

    private LyftResponse delegate;

    LyftAsyncTask(LyftResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected List<Driver> doInBackground(LyftTaskParams... params) {
        try {
            String token;
            if (params[0].generateToken) {
                token = LyftService.generateToken();
                FirebaseService.setLyftToken(token);
            } else {
                token = params[0].token;
            }
            return generateLyftsList(token, params[0].originLatLng, params[0].destinationLatLng);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Driver> generateLyftsList(String token, LatLng origin, LatLng destination) throws Exception {
        List<Driver> lyfts = new ArrayList<>();

        List<LyftTime> arrivalTimeEstimates = LyftService.getDriverArrivalTimeEstimates(token, origin);
        List<LyftPrice> priceEstimates = LyftService.getPriceEstimates(token, origin, destination);

        for (int i = 0; i < arrivalTimeEstimates.size(); i++) {
            LyftTime arrivalTimeEstimate = arrivalTimeEstimates.get(i);

            for (int j = 0; j < priceEstimates.size(); j++) {
                if (priceEstimates.get(j).ride_type.equals(arrivalTimeEstimate.ride_type)) {
                    LyftPrice priceEstimate = priceEstimates.get(j);
                    priceEstimates.remove(j);

                    Driver lyft = new Driver(
                            priceEstimate.display_name,
                            arrivalTimeEstimate.eta_seconds,
                            priceEstimate.estimated_duration_seconds,
                            (priceEstimate.estimated_cost_cents_min + priceEstimate.estimated_cost_cents_max) / 200
                    );

                    lyfts.add(lyft);
                }
            }
        }

        return lyfts;
    }

    @Override
    protected void onPostExecute(List<Driver> lyftResponse) {
        delegate.processFinish(lyftResponse);
    }
}
