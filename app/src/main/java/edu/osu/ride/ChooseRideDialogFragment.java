package edu.osu.ride;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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

        DecimalFormat df = new DecimalFormat("#0.00");

        if (mBirdFiltered) {
            optimalBird = (int)(getArguments().getDouble("birdDuration"));
            optimalBirdDest = (int)(getArguments().getDouble("birdDestination"));
            optimalBirdCost = df.format(getArguments().getDouble("birdCost"));
        }

        if (mLimeFiltered) {
            optimalLime = (int)(getArguments().getDouble("limeDuration"));
            optimalLimeDest = (int)(getArguments().getDouble("limeDestination"));
            optimalLimeCost = df.format(getArguments().getDouble("limeCost"));
        }

        if (mUberFiltered) {
            optimalUber = getArguments().getInt("uberDuration");
            optimalUberDest = getArguments().getInt("uberDestination");
            optimalUberCost = df.format(getArguments().getDouble("uberCost"));
        }

        if (mLyftFiltered) {
            optimalLyft = getArguments().getInt("lyftDuration");
            optimalLyftDest = getArguments().getInt("lyftDestination");
            optimalLyftCost = df.format(getArguments().getDouble("lyftCost"));
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
                getRiderActivity().deepLink(UBER_PACKAGE);
                break;
            case R.id.lyft_app:
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
}
