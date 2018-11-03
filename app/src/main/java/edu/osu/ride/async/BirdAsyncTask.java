package edu.osu.ride.async;

import android.os.AsyncTask;

import java.util.List;

import edu.osu.ride.model.bird.Bird;
import edu.osu.ride.service.BirdService;

public class BirdAsyncTask extends AsyncTask<Void, Void, List<Bird>> {

    public interface BirdResponse {
        void processFinish(List<Bird> output);
    }

    private BirdResponse delegate;

    BirdAsyncTask(BirdResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected List<Bird> doInBackground(Void... ignored) {
        try {
            String token = BirdService.generateToken();
            return BirdService.locationResponse(token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Bird> birdResponse) {
        delegate.processFinish(birdResponse);
    }
}
