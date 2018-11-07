package edu.osu.ride.async;

import android.os.AsyncTask;

import java.util.List;

import edu.osu.ride.model.scooter.Scooter;
import edu.osu.ride.service.LimeService;

public class LimeAsyncTask extends AsyncTask<Void, Void, List<Scooter>> {

    public interface LimeResponse {
        void processFinish(List<Scooter> output);
    }

    private LimeResponse delegate;

    LimeAsyncTask(LimeResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected List<Scooter> doInBackground(Void... ignored) {
        try {
            return LimeService.locationResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Scooter> limeResponse) {
        delegate.processFinish(limeResponse);
    }
}
