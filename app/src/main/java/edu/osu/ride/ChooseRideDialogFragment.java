package edu.osu.ride;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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

public class ChooseRideDialogFragment extends DialogFragment implements View.OnClickListener {

    private Boolean mAllFiltered;
    private Boolean mUberFiltered;
    private Boolean mLyftFiltered;
    private Boolean mBirdFiltered;
    private Boolean mLimeFiltered;

    private int optimalBird;
    private int optimalBirdDest;
    private String optimalBirdCost;

    private Activity mActivity;
    private LinearLayout mOptimalUber;
    private LinearLayout mOptimalLyft;
    private LinearLayout mOptimalBird;
    private LinearLayout mOptimalLime;

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

            //d.setTime(optimalBird);
            TextView closestBird = v.findViewById(R.id.closest_bird);
            closestBird.setText("Closest scooter:" + toBirdTime);
            TextView durationBird = v.findViewById(R.id.duration_bird);
            durationBird.setText("Destination arrival: " + toDestTime);
            TextView costBird = v.findViewById(R.id.cost_bird);
            costBird.setText("Estimated Cost: $" + optimalBirdCost);
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
        }
    }

    public RiderActivity getRiderActivity() {
        return (RiderActivity) mActivity;
    }

    private void removeDim() {
        getActivity().findViewById(R.id.dim).setVisibility(View.GONE);
    }
}
