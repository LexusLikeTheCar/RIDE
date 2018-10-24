package edu.osu.ride;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity {
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mUsernameField;
    private Button mNextButton;
    private Button mdeleteButton;
    private ProgressBar mLoginProgressBar;
    private TextView mSettingTextView;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private String username;
    private String email;
    private String password;
    private String TAG = "Settings Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_settings);


        mEmailField = findViewById(R.id.editEmail);
        mPasswordField = findViewById(R.id.editPassword);
        mUsernameField = findViewById(R.id.editUsername);
        //mSettingTextView = findViewById(R.id.signup_text_view);
        mNextButton = findViewById(R.id.updateSettingsButton);

        mdeleteButton = findViewById(R.id.delete_Button);

        mLoginProgressBar = findViewById(R.id.settingProgressBar);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();


        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                username = dataSnapshot.child("username").getValue().toString();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });


    }


    public void editSettings() {

        email = mEmailField.getText().toString().trim();
        password = mPasswordField.getText().toString().trim();
        username = mUsernameField.getText().toString().trim();

        if (email != null) {

            FirebaseAuth.getInstance().getCurrentUser().updateEmail(email);

        } else if (password != null) {

            FirebaseAuth.getInstance().getCurrentUser().updatePassword(password);

        } else if (username != null) {

            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("username").setValue(username);

        }
    }


        public void onClick (View v){
            switch (v.getId()) {
                case R.id.delete_Button:
                    //turn into a modal??
                    Toast.makeText(SettingsActivity.this, "Are you sure?", Toast.LENGTH_SHORT);
                    FirebaseAuth.getInstance().getCurrentUser().delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User account deleted.");
                                    }
                                }
                            });
                    startActivity(new Intent(this, LoginActivity.class));
                    break;

                case R.id.updateSettingsButton:
                    editSettings();
                    Toast.makeText(SettingsActivity.this, "Profile updated", Toast.LENGTH_SHORT);
                    startActivity(new Intent(this, LoginActivity.class));
                    break;
            }
        }

    }

