package edu.osu.ride;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import com.google.android.gms.common.api.Scope;
import com.lyft.lyftbutton.LyftButton;
import com.lyft.lyftbutton.RideParams;
import com.lyft.lyftbutton.RideTypeEnum;
import com.lyft.networking.ApiConfig;
import com.uber.sdk.android.core.auth.AccessTokenManager;
import com.uber.sdk.android.core.auth.LoginManager;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.core.auth.AccessTokenStorage;
import com.uber.sdk.rides.client.ServerTokenSession;
import com.uber.sdk.rides.client.SessionConfiguration;
import com.uber.sdk.android.core.Deeplink;
import com.uber.sdk.android.core.auth.AccessTokenManager;
import com.uber.sdk.android.core.auth.AuthenticationError;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestActivity;
import com.uber.sdk.android.rides.RideRequestActivityBehavior;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.android.rides.RideRequestButtonCallback;
import com.uber.sdk.android.rides.RideRequestViewError;
import com.uber.sdk.core.auth.AccessToken;
import com.uber.sdk.core.auth.AccessTokenStorage;
import com.uber.sdk.rides.client.error.ApiError;

import java.util.Arrays;

import static com.uber.sdk.android.core.utils.Preconditions.checkNotNull;
import static com.uber.sdk.android.core.utils.Preconditions.checkState;

public class ChooseRideDialogFragment extends DialogFragment implements View.OnClickListener {

    private Boolean mAllFiltered;
    private Boolean mUberFiltered;
    private Boolean mLyftFiltered;
    private Boolean mBirdFiltered;
    private Boolean mLimeFiltered;

    private int optimalBird;
    private int optimalBirdDest;
    private String optimalBirdCost;

    private int optimalLime;
    private int optimalLimeDest;
    private String optimalLimeCost;

    private Activity mActivity;
    private LinearLayout mOptimalUber;
    private LinearLayout mOptimalLyft;
    private LinearLayout mOptimalBird;
    private LinearLayout mOptimalLime;
    private static final String CLIENT_ID = "Z5wpBCpfdZu0HHWPkQ5Pf9Y3x1utTlRL";
    private static final String REDIRECT_URI = "https://www.uber.com/sign-in/";
    private static final String SERVER_TOKEN = "coaJmlyKfOzo23ScdhVfBCy4o6SNSDQ4zQke-2u-";

    private static final String DROPOFF_ADDR = "One Embarcadero Center, San Francisco";
    //private static final Double DROPOFF_LAT = 37.795079; // San Fran
    //private static final Double DROPOFF_LONG = -122.397805; // San Fran
    private static final Double DROPOFF_LAT = 40.0049976; // OSU
    private static final Double DROPOFF_LONG = -83.0077963; // OSU
    private static final String DROPOFF_NICK = "Embarcadero";
    private static final String ERROR_LOG_TAG = "UberSDK-SampleActivity";

    private static final String PICKUP_ADDR = "1455 Market Street, San Francisco";
    //private static final Double PICKUP_LAT = 37.775304; // San Fran
    //private static final Double PICKUP_LONG = -122.417522; // San Fran
    private static final Double PICKUP_LAT = 40.0017; // OSU
    private static final Double PICKUP_LONG = -83.0160; // OSU
    private static final String PICKUP_NICK = "Uber HQ";

    private com.uber.sdk.rides.client.SessionConfiguration configuration;
    private static final String TAG = "Rider Dialog Fragment";
    private static final String LYFT_PACKAGE = "me.lyft.android";
    private AccessTokenStorage accessTokenStorage;
    private LoginManager loginManager;
    private Button mShowLime;
    private Button mShowBird;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAllFiltered = getArguments().getBoolean("all");
        mUberFiltered = getArguments().getBoolean("uber");
        mLyftFiltered = getArguments().getBoolean("lyft");
        mBirdFiltered = getArguments().getBoolean("bird");
        mLimeFiltered = getArguments().getBoolean("lime");

        if (mAllFiltered) {
            mUberFiltered = true;
            mLyftFiltered = true;
            mBirdFiltered = true;
            mLimeFiltered = true;
        }

        if (mBirdFiltered) {
            optimalBird = (int)(getArguments().getDouble("birdDuration"));
            optimalBirdDest = (int)(getArguments().getDouble("birdDestination"));
            optimalBirdCost = Double.toString(getArguments().getDouble("birdCost"));
        }

        if (mLimeFiltered) {
            optimalLime = (int)(getArguments().getDouble("limeDuration"));
            optimalLimeDest = (int)(getArguments().getDouble("limeDestination"));
            optimalLimeCost = Double.toString(getArguments().getDouble("limeCost"));
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mActivity = getActivity();

        LayoutInflater inflater = LayoutInflater.from(mActivity);
        View v = inflater.inflate(R.layout.dialog_choose_ride, null, false);

        mOptimalUber = v.findViewById(R.id.optimal_uber);
        mOptimalLyft = v.findViewById(R.id.optimal_lyft);
        mOptimalBird = v.findViewById(R.id.optimal_bird);
        mOptimalLime = v.findViewById(R.id.optimal_lime);

        if(!mUberFiltered) {
            mOptimalUber.setVisibility(View.GONE);
        }
        if(!mLyftFiltered) {
            mOptimalLyft.setVisibility(View.GONE);
        }
        if(!mBirdFiltered) {
            mOptimalBird.setVisibility(View.GONE);
        }
        if(!mLimeFiltered) {
            mOptimalLime.setVisibility(View.GONE);
        }

        if(mBirdFiltered) {
            Calendar toBird = Calendar.getInstance();
            toBird.add(Calendar.HOUR, optimalBird/60);
            toBird.add(Calendar.MINUTE, optimalBird%60);
            Calendar toDest = Calendar.getInstance();
            toDest.add(Calendar.HOUR, optimalBirdDest/60);
            toDest.add(Calendar.MINUTE, optimalBirdDest%60);

            SimpleDateFormat localDateFormat = new SimpleDateFormat("KK:mm a");
            String toBirdTime = localDateFormat.format(toBird.getTime());
            String toDestTime = localDateFormat.format(toDest.getTime());

            TextView closestBird = v.findViewById(R.id.closest_bird);
            closestBird.setText("Closest scooter:" + toBirdTime);
            TextView durationBird = v.findViewById(R.id.duration_bird);
            durationBird.setText("Destination arrival: " + toDestTime);
            TextView costBird = v.findViewById(R.id.cost_bird);
            costBird.setText("Estimated Cost: $" + optimalBirdCost);
        }

        if(mLimeFiltered) {
            Calendar toLime = Calendar.getInstance();
            toLime.add(Calendar.HOUR, optimalLime/60);
            toLime.add(Calendar.MINUTE, optimalLime%60);
            Calendar toDest = Calendar.getInstance();
            toDest.add(Calendar.HOUR, optimalLimeDest/60);
            toDest.add(Calendar.MINUTE, optimalLimeDest%60);

            SimpleDateFormat localDateFormat = new SimpleDateFormat("KK:mm a");
            String toLimeTime = localDateFormat.format(toLime.getTime());
            String toDestTime = localDateFormat.format(toDest.getTime());

            TextView closestLime = v.findViewById(R.id.closest_lime);
            closestLime.setText("Closest scooter:" + toLimeTime);
            TextView durationLime = v.findViewById(R.id.duration_lime);
            durationLime.setText("Destination arrival: " + toDestTime);
            TextView costLime = v.findViewById(R.id.cost_lime);
            costLime.setText("Estimated Cost: $" + optimalLimeCost);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setView(v);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                removeDim();
            }
        });

        // Create the AlertDialog object and return it
        final AlertDialog dialog = builder.create();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        mShowBird = v.findViewById(R.id.show_birds);
        mShowBird.setOnClickListener(this);

        mShowLime = v.findViewById(R.id.show_limes);
        mShowLime.setOnClickListener(this);


        //TODO: ensure these work
        //uber
         configuration = new SessionConfiguration.Builder()
                .setClientId(CLIENT_ID)
                .setServerToken(SERVER_TOKEN).setRedirectUri(REDIRECT_URI).setEnvironment(SessionConfiguration.Environment.SANDBOX) //Useful for testing your app in the sandbox environment

                .build();
        validateConfiguration(configuration);


        accessTokenStorage = new AccessTokenManager(mActivity);
        //UberSdk.initialize(configuration);

        ServerTokenSession session = new ServerTokenSession(configuration);


        RideParameters rideParametersCheapestProduct = new RideParameters.Builder()
                .setPickupLocation(PICKUP_LAT, PICKUP_LONG, PICKUP_NICK, PICKUP_ADDR)
                .setDropoffLocation(DROPOFF_LAT, DROPOFF_LONG, DROPOFF_NICK, DROPOFF_ADDR)
                .build();

        // This button demonstrates deep-linking to the Uber app (default button behavior).
        RideRequestButton blackButton = (RideRequestButton) v.findViewById(R.id.uber_app);
        blackButton.setRideParameters(rideParametersCheapestProduct);
        blackButton.setSession(session);
        blackButton.loadRideInformation();

        //timeEstimateView = (TextView) findViewById(com.uber.sdk.android.rides.R.id.time_estimate); // ToDo: reference individual pieces of layout to fix UI


        //lyft
        ApiConfig apiConfig = new ApiConfig.Builder()
                .setClientId("y5_U06Wt1Uub")
                .setClientToken("/o+SE7Zb4/BVc63U5T6UEVHbB5xXTlU6Wyom69l2qI7aYP8z8/yCtDTQwkpuc8SbNJzpHpsbfa/Lf78KGDB5QEMTzonS8Ci1UYgnPGTuRJJQFquulZc9kqA=")
                .build();
        LyftButton lyftButton = (LyftButton) v.findViewById(R.id.lyft_app);
        lyftButton.setApiConfig(apiConfig);

        RideParams.Builder rideParamsBuilder = new RideParams.Builder()
                .setPickupLocation(PICKUP_LAT, PICKUP_LONG)
                .setDropoffLocation(DROPOFF_LAT, DROPOFF_LONG);
        rideParamsBuilder.setRideTypeEnum(RideTypeEnum.CLASSIC);

        lyftButton.setRideParams(rideParamsBuilder.build());
        lyftButton.load();

        v.findViewById(R.id.lyft_app).setOnClickListener(this);

        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.show_birds:
                getDialog().dismiss();
                removeDim();
                getRiderActivity().mShowBirds = true;
                getRiderActivity().updateMap();
                break;
            case R.id.show_limes:
                getDialog().dismiss();
                removeDim();
                getRiderActivity().mShowLimes = true;
                getRiderActivity().updateMap();
                break;
            case R.id.lyft_app:
                getDialog().dismiss();
                removeDim();
                deepLinkIntoLyft();
                break;
        }
    }

    public RiderActivity getRiderActivity() {
        return (RiderActivity) mActivity;
    }

    private void removeDim() {
        getActivity().findViewById(R.id.dim).setVisibility(View.GONE);
    }

    private void deepLinkIntoLyft() {
        if (isPackageInstalled(mActivity, LYFT_PACKAGE)) {
            //This intent will help you to launch if the package is already installed
            //ohio union is destination
            openLink(mActivity, "lyft://ridetype?id=lyft&pickup[latitude]=37.764728&pickup[longitude]=-122.422999&destination[latitude]=37.7763592&destination[longitude]=-122.4242038");

            Log.d(TAG, "Lyft is already installed on your phone.");
        } else {

            openLink(mActivity, "https://www.lyft.com/signup/SDKSIGNUP?clientId=y5_U06Wt1Uub&sdkName=android_direct");

            Log.d(TAG, "Lyft is not currently installed on your phone..");
        }
    }

    static void openLink(Activity activity, String link) {
        Intent playStoreIntent = new Intent(Intent.ACTION_VIEW);
        playStoreIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        playStoreIntent.setData(Uri.parse(link));
        activity.startActivity(playStoreIntent);
    }

    static boolean isPackageInstalled(Context context, String packageId) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageId, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            // ignored.
        }
        return false;
    }

    /**
     * Validates the local variables needed by the Uber SDK used in the Uber sample provided in their
     * SDK documentation
     *
     * @param configuration
     */
    private void validateConfiguration(com.uber.sdk.rides.client.SessionConfiguration configuration) {
        String nullError = "%s must not be null";
        String sampleError = "Please update your %s in the gradle.properties of the project before " +
                "using the Uber SDK Sample app. For a more secure storage location, " +
                "please investigate storing in your user home gradle.properties ";

        checkNotNull(configuration, String.format(nullError, "SessionConfiguration"));
        checkNotNull(configuration.getClientId(), String.format(nullError, "Client ID"));
        checkNotNull(configuration.getRedirectUri(), String.format(nullError, "Redirect URI"));
        checkNotNull(configuration.getServerToken(), String.format(nullError, "Server Token"));
        checkState(!configuration.getClientId().equals("insert_your_client_id_here"),
                String.format(sampleError, "Client ID"));
        checkState(!configuration.getRedirectUri().equals("insert_your_redirect_uri_here"),
                String.format(sampleError, "Redirect URI"));
        checkState(!configuration.getRedirectUri().equals("insert_your_server_token_here"),
                String.format(sampleError, "Server Token"));
    }
}
