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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.lyft.lyftbutton.LyftButton;
import com.lyft.lyftbutton.RideParams;
import com.lyft.lyftbutton.RideTypeEnum;
import com.lyft.networking.ApiConfig;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.rides.client.ServerTokenSession;
import com.uber.sdk.rides.client.SessionConfiguration;

import static com.uber.sdk.android.core.utils.Preconditions.checkNotNull;
import static com.uber.sdk.android.core.utils.Preconditions.checkState;

public class ChooseRideDialogFragment extends DialogFragment implements View.OnClickListener {

    private static final String UBER_PACKAGE = "com.ubercab";
    private static final String LYFT_PACKAGE = "me.lyft.android";

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

    private int optimalUber;
    private int optimalUberDest;
    private String optimalUberCost;

    private int optimalLyft;
    private int optimalLyftDest;
    private String optimalLyftCost;

    private Activity mActivity;
    private LinearLayout mOptimalUber;
    private LinearLayout mOptimalLyft;
    private LinearLayout mOptimalBird;
    private LinearLayout mOptimalLime;
    private static final String CLIENT_ID = "Z5wpBCpfdZu0HHWPkQ5Pf9Y3x1utTlRL";
    private static final String REDIRECT_URI = "https://www.uber.com/sign-in/";
    private static final String SERVER_TOKEN = "coaJmlyKfOzo23ScdhVfBCy4o6SNSDQ4zQke-2u-";

    private com.uber.sdk.rides.client.SessionConfiguration configuration;
    private static final String TAG = "Rider Dialog Fragment";

    private Button mShowLime;
    private Button mShowBird;
    private Button mUberApp;
    private Button mLyftApp;


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

        if (mUberFiltered) {
            optimalUber = getArguments().getInt("uberDuration");
            optimalUberDest = getArguments().getInt("uberDestination");
            optimalUberCost = Double.toString(getArguments().getDouble("uberCost"));
        }

        if (mLyftFiltered) {
            optimalLyft = getArguments().getInt("lyftDuration");
            optimalLyftDest = getArguments().getInt("lyftDestination");
            optimalLyftCost = Double.toString(getArguments().getDouble("lyftCost"));
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

        if (mBirdFiltered) {
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

        if(mUberFiltered) {
            Calendar toUber = Calendar.getInstance();
            toUber.add(Calendar.HOUR, optimalUber/3600);
            toUber.add(Calendar.MINUTE, (optimalUber%3600)/60);
            Calendar toDest = Calendar.getInstance();
            toDest.add(Calendar.HOUR, optimalUberDest/3600);
            toDest.add(Calendar.MINUTE, (optimalUberDest%3600)/60);

            SimpleDateFormat localDateFormat = new SimpleDateFormat("KK:mm a");
            String toUberTime = localDateFormat.format(toUber.getTime());
            String toDestTime = localDateFormat.format(toDest.getTime());

            TextView closestUber = v.findViewById(R.id.closest_uber);
            closestUber.setText("Driver arrival:" + toUberTime);
            TextView durationUber = v.findViewById(R.id.duration_uber);
            durationUber.setText("Destination arrival: " + toDestTime);
            TextView costUber = v.findViewById(R.id.cost_uber);
            costUber.setText("Estimated Cost: $" + optimalUberCost);
        }

        if(mLyftFiltered) {
            Calendar toLyft = Calendar.getInstance();
            toLyft.add(Calendar.HOUR, optimalLyft/3600);
            toLyft.add(Calendar.MINUTE, (optimalLyft%3600)/60);
            Calendar toDest = Calendar.getInstance();
            toDest.add(Calendar.HOUR, optimalLyftDest/3600);
            toDest.add(Calendar.MINUTE, (optimalLyftDest%3600)/60);

            SimpleDateFormat localDateFormat = new SimpleDateFormat("KK:mm a");
            String toLyftTime = localDateFormat.format(toLyft.getTime());
            String toDestTime = localDateFormat.format(toDest.getTime());

            TextView closestLyft = v.findViewById(R.id.closest_lyft);
            closestLyft.setText("Driver arrival:" + toLyftTime);
            TextView durationLyft = v.findViewById(R.id.duration_lyft);
            durationLyft.setText("Destination arrival: " + toDestTime);
            TextView costLyft = v.findViewById(R.id.cost_lyft);
            costLyft.setText("Estimated Cost: $" + optimalLyftCost);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setView(v);
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                removeDim();
                getRiderActivity().updateMap();
            }
        });

        // Create the AlertDialog object and return it
        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                final Button cancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                cancel.setTextColor(getResources().getColor(R.color.colorPrimary));
                cancel.setTextSize(20);
                LinearLayout.LayoutParams cancelParams = (LinearLayout.LayoutParams) cancel.getLayoutParams();
                cancelParams.width = ViewGroup.LayoutParams.MATCH_PARENT;;
                cancel.setLayoutParams(cancelParams);
            }
        });

        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        mShowBird = v.findViewById(R.id.show_birds);
        mShowBird.setOnClickListener(this);

        mShowLime = v.findViewById(R.id.show_limes);
        mShowLime.setOnClickListener(this);

        mUberApp = v.findViewById(R.id.uber_app);
        mUberApp.setOnClickListener(this);

        mLyftApp = v.findViewById(R.id.lyft_app);
        mLyftApp.setOnClickListener(this);

        Double dropoffLat = getRiderActivity().getDestination().getLatLng().latitude;
        Double dropoffLong = getRiderActivity().getDestination().getLatLng().longitude;

        Double pickupLat = getRiderActivity().getLastKnownLocation().getLatitude();
        Double pickupLong = getRiderActivity().getLastKnownLocation().getLongitude();


        //TODO: ensure these work
        //uber
         configuration = new SessionConfiguration.Builder()
                .setClientId(CLIENT_ID)
                .setServerToken(SERVER_TOKEN).setRedirectUri(REDIRECT_URI).setEnvironment(SessionConfiguration.Environment.SANDBOX) //Useful for testing your app in the sandbox environment

                .build();
        validateConfiguration(configuration);

        ServerTokenSession session = new ServerTokenSession(configuration);


        RideParameters rideParametersCheapestProduct = new RideParameters.Builder()
                .setPickupLocation(pickupLat, pickupLong, null, null)
                .setDropoffLocation(dropoffLat, dropoffLong, null, null)
                .build();

        // This button demonstrates deep-linking to the Uber app (default button behavior).
        /*RideRequestButton blackButton = (RideRequestButton) v.findViewById(R.id.uber_app);
        blackButton.setRideParameters(rideParametersCheapestProduct);
        blackButton.setSession(session);
        blackButton.loadRideInformation();*/

        //timeEstimateView = (TextView) findViewById(com.uber.sdk.android.rides.R.id.time_estimate); // ToDo: reference individual pieces of layout to fix UI


        //lyft
        ApiConfig apiConfig = new ApiConfig.Builder()
                .setClientId("y5_U06Wt1Uub")
                .setClientToken("/o+SE7Zb4/BVc63U5T6UEVHbB5xXTlU6Wyom69l2qI7aYP8z8/yCtDTQwkpuc8SbNJzpHpsbfa/Lf78KGDB5QEMTzonS8Ci1UYgnPGTuRJJQFquulZc9kqA=")
                .build();
        /*LyftButton lyftButton = (LyftButton) v.findViewById(R.id.lyft_app);
        lyftButton.setApiConfig(apiConfig);

        RideParams.Builder rideParamsBuilder = new RideParams.Builder()
                .setPickupLocation(pickupLat, pickupLong)
                .setDropoffLocation(dropoffLat, dropoffLong);
        rideParamsBuilder.setRideTypeEnum(RideTypeEnum.CLASSIC);

        lyftButton.setRideParams(rideParamsBuilder.build());
        lyftButton.load();

        v.findViewById(R.id.lyft_app).setOnClickListener(this);*/

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
            case R.id.uber_app:
                getDialog().dismiss();
                removeDim();
                getRiderActivity().deepLink(UBER_PACKAGE);
                break;
            case R.id.lyft_app:
                getDialog().dismiss();
                removeDim();
                getRiderActivity().deepLink(LYFT_PACKAGE);
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
