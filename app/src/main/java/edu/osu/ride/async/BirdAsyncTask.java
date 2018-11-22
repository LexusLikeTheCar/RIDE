package edu.osu.ride.async;

import android.os.AsyncTask;

import java.util.List;

import edu.osu.ride.async.params.BirdTaskParams;
import edu.osu.ride.model.scooter.Scooter;
import edu.osu.ride.service.BirdService;
import edu.osu.ride.service.FirebaseService;

public class BirdAsyncTask extends AsyncTask<BirdTaskParams, Void, List<Scooter>> {

    public interface BirdResponse {
        void processFinish(List<Scooter> output);
    }

    private BirdResponse delegate;

    BirdAsyncTask(BirdResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected List<Scooter> doInBackground(BirdTaskParams... params) {
        try {
            String token;
            if (params[0].generateToken) {
                token = BirdService.generateToken();
                FirebaseService.setBirdToken(token);
            } else {
                token = params[0].token;
            }
            return BirdService.locationResponse(token, params[0].location);
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
