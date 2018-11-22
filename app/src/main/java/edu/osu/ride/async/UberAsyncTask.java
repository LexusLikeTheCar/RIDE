package edu.osu.ride.async;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import edu.osu.ride.model.driver.Driver;
import edu.osu.ride.model.driver.uber.UberPrice;
import edu.osu.ride.model.driver.uber.UberTime;
import edu.osu.ride.service.UberService;

public class UberAsyncTask extends AsyncTask<LatLng, Void, List<Driver>> {

    public interface UberResponse {
        void processFinish(List<Driver> output);
    }

    private UberResponse delegate;

    UberAsyncTask(UberResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected List<Driver> doInBackground(LatLng... latLngs) {
        try {
            return generateUbersList(latLngs[0], latLngs[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Driver> generateUbersList(LatLng origin, LatLng destination) throws Exception {
        List<Driver> ubers = new ArrayList<>();

        List<UberTime> arrivalTimeEstimates = UberService.getDriverArrivalTimeEstimates(origin);
        List<UberPrice> priceEstimates = UberService.getPriceEstimates(origin, destination);

        for (int i = 0; i < arrivalTimeEstimates.size(); i++) {
            UberTime arrivalTimeEstimate = arrivalTimeEstimates.get(i);

            for (int j = 0; j < priceEstimates.size(); j++) {
                if (priceEstimates.get(j).product_id.equals(arrivalTimeEstimate.product_id)) {
                    UberPrice priceEstimate = priceEstimates.get(j);
                    priceEstimates.remove(j);

                    Driver uber = new Driver(
                            priceEstimate.display_name,
                            arrivalTimeEstimate.estimate,
                            priceEstimate.duration,
                            (priceEstimate.low_estimate + priceEstimate.high_estimate) / 2
                    );

                    ubers.add(uber);
                }
            }
        }

        return ubers;
    }

    @Override
    protected void onPostExecute(List<Driver> uberResponse) {
        delegate.processFinish(uberResponse);
    }
}
