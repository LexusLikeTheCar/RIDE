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

public class ChooseRideDialogFragment extends DialogFragment implements View.OnClickListener {

    private Boolean mAllFiltered;
    private Boolean mUberFiltered;
    private Boolean mLyftFiltered;
    private Boolean mBirdFiltered;
    private Boolean mLimeFiltered;

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
