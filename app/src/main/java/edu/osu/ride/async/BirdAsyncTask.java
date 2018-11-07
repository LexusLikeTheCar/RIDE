package edu.osu.ride.async;

import android.os.AsyncTask;

import java.util.List;

import edu.osu.ride.model.scooter.Scooter;
import edu.osu.ride.service.BirdService;

public class BirdAsyncTask extends AsyncTask<Void, Void, List<Scooter>> {

    public interface BirdResponse {
        void processFinish(List<Scooter> output);
    }

    private BirdResponse delegate;

    BirdAsyncTask(BirdResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected List<Scooter> doInBackground(Void... ignored) {
        try {
            String token = BirdService.generateToken();
            return BirdService.locationResponse(token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Scooter> birdResponse) {
        delegate.processFinish(birdResponse);
    }
}
