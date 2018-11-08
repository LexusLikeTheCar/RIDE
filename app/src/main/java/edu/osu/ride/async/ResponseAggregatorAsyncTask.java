package edu.osu.ride.async;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import java.util.List;

import edu.osu.ride.R;
import edu.osu.ride.RiderActivity;
import edu.osu.ride.async.BirdAsyncTask.BirdResponse;
import edu.osu.ride.async.LimeAsyncTask.LimeResponse;
import edu.osu.ride.model.scooter.Scooter;

public class ResponseAggregatorAsyncTask extends AsyncTask<Void, Void, Void> {

    private final RiderActivity mActivity;

    private final View mDim;
    private final ProgressBar mFindRidesProgressBar;

    private boolean mBirdDone;
    private boolean mLimeDone;
    private boolean mUberDone;
    private boolean mLyftDone;

    public ResponseAggregatorAsyncTask(RiderActivity activity) {
        // TODO: Need to make this work with all booleans; will look like call below
        // this(activity, true, true, true, true);

        this(activity, true, true, false, false);
    }

    public ResponseAggregatorAsyncTask(RiderActivity activity, boolean birdRequest, boolean limeRequest,
                                       boolean uberRequest, boolean lyftRequest) {
        mActivity = activity;

        mDim = mActivity.findViewById(R.id.dim);
        mFindRidesProgressBar = mActivity.findViewById(R.id.find_rides_progress_bar);

        mBirdDone = !birdRequest;
        mLimeDone = !limeRequest;
        mUberDone = true;
        mLyftDone = true;
    }

    @Override
    protected Void doInBackground(Void... ignore) {
        if (!mBirdDone) {
            new BirdAsyncTask(new BirdResponse() {
                @Override
                public void processFinish(List<Scooter> birds) {
                    mBirdDone = true;
                    mActivity.setBirds(birds);
                    checkAllResponses();
                }
            }).execute();
        }

        if (!mLimeDone) {
            new LimeAsyncTask(new LimeResponse() {
                @Override
                public void processFinish(List<Scooter> limes) {
                    mLimeDone = true;
                    mActivity.setLimes(limes);
                    checkAllResponses();
                }
            }).execute();
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        mDim.setVisibility(View.VISIBLE);
        mFindRidesProgressBar.setVisibility(View.VISIBLE);
    }

    private void checkAllResponses() {
        if (mBirdDone && mLimeDone && mUberDone && mLyftDone) {
            mFindRidesProgressBar.setVisibility(View.GONE);
            mActivity.responseAggregationFinished();
        }
    }

}
