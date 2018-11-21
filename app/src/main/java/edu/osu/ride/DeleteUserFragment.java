package edu.osu.ride;

import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class DeleteUserFragment extends DialogFragment implements View.OnClickListener {
    private Activity mActivity;
    private FirebaseAuth mAuth;

    public static DeleteUserFragment newInstance() {
        DeleteUserFragment frag = new DeleteUserFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        mActivity = getActivity();
        showDialog();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new AlertDialog.Builder(getActivity())
                .setTitle("Are you sure?")
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mAuth.getCurrentUser().delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(mActivity, "Profile deleted", Toast.LENGTH_SHORT).show();

                                                } else {
                                                    Toast.makeText(mActivity, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(mAuth.getCurrentUser().getUid()).removeValue();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }

                        }
                )
                .create();
    }
    void showDialog() {
        DialogFragment newFragment = DeleteUserFragment.newInstance();
        newFragment.show(getFragmentManager(), "dialog");
    }


    @Override
    public void onClick(View view) {

    }
}



