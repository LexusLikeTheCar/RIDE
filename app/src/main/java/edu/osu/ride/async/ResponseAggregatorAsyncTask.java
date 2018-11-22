package edu.osu.ride.async;

import android.location.Location;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

import edu.osu.ride.R;
import edu.osu.ride.RiderActivity;
import edu.osu.ride.async.BirdAsyncTask.BirdResponse;
import edu.osu.ride.async.LimeAsyncTask.LimeResponse;
import edu.osu.ride.async.LyftAsyncTask.LyftResponse;
import edu.osu.ride.async.UberAsyncTask.UberResponse;
import edu.osu.ride.async.params.BirdTaskParams;
import edu.osu.ride.async.params.LyftTaskParams;
import edu.osu.ride.model.User;
import edu.osu.ride.model.driver.Driver;
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
        this(activity, true, true, true, true);
    }

    public ResponseAggregatorAsyncTask(RiderActivity activity, boolean birdRequest, boolean limeRequest,
                                       boolean uberRequest, boolean lyftRequest) {
        mActivity = activity;

        mDim = mActivity.findViewById(R.id.dim);
        mFindRidesProgressBar = mActivity.findViewById(R.id.find_rides_progress_bar);

        mBirdDone = !birdRequest;
        mLimeDone = !limeRequest;
        mUberDone = !uberRequest;
        mLyftDone = !lyftRequest;
    }

    @Override
    protected Void doInBackground(Void... ignore) {
        Location origin = mActivity.getLastKnownLocation();
        LatLng originLatLng = new LatLng(origin.getLatitude(), origin.getLongitude());
        LatLng destinationLatLng = mActivity.getDestination().getLatLng();

        User user = mActivity.getUser();

        if (!mBirdDone) {
            boolean generateToken = user.birdToken == null ||
                    user.birdTokenExpirationTimestamp == null ||
                    new Date(Long.valueOf(user.birdTokenExpirationTimestamp)).before(new Date());

            BirdTaskParams params = new BirdTaskParams(user.birdToken, origin, generateToken);

            new BirdAsyncTask(new BirdResponse() {
                @Override
                public void processFinish(List<Scooter> birds) {
                    mBirdDone = true;
                    mActivity.setBirds(birds);
                    checkAllResponses();
                }
            }).execute(params);
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

        if (!mUberDone) {
            new UberAsyncTask(new UberResponse() {
                @Override
                public void processFinish(List<Driver> ubers) {
                    mUberDone = true;
                    mActivity.setUbers(ubers);
                    checkAllResponses();
                }
            }).execute(originLatLng, destinationLatLng);
        }

        if (!mLyftDone) {
            boolean generateToken = user.lyftToken == null ||
                    user.lyftTokenExpirationTimestamp == null ||
                    new Date(Long.valueOf(user.lyftTokenExpirationTimestamp)).before(new Date());

            LyftTaskParams params = new LyftTaskParams(user.lyftToken, originLatLng, destinationLatLng, generateToken);

            new LyftAsyncTask(new LyftResponse() {
                @Override
                public void processFinish(List<Driver> lyfts) {
                    mLyftDone = true;
                    mActivity.setLyfts(lyfts);
                    checkAllResponses();
                }
            }).execute(params);
        }

        checkAllResponses();

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
            mActivity.launchRideOptionsDialog();
        }
    }

}
