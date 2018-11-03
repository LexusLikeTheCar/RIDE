package edu.osu.ride;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import edu.osu.ride.model.User;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEmailField;
    private EditText mUsernameField;
    private EditText mPasswordField;
    private EditText mConfirmPasswordField;
    private Button mSignUpButton;
    private TextView mLoginTextView;
    private ProgressBar mRegistrationProgressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mEmailField = findViewById(R.id.email_edit_text);
        mUsernameField = findViewById(R.id.username_edit_text);
        mPasswordField = findViewById(R.id.password_edit_text);
        mConfirmPasswordField = findViewById(R.id.confirm_password_edit_text);

        mSignUpButton = findViewById(R.id.sign_up_button);
        mSignUpButton.setOnClickListener(this);

        mLoginTextView = findViewById(R.id.login_text_view);
        mLoginTextView.setOnClickListener(this);

        mRegistrationProgressBar = findViewById(R.id.registration_progress_bar);

        mAuth = FirebaseAuth.getInstance();
    }

    private void registerUser() {
        String email = mEmailField.getText().toString().trim();
        String password = mPasswordField.getText().toString().trim();
        final String username = mUsernameField.getText().toString().trim();
        String confirmPassword = mConfirmPasswordField.getText().toString().trim();

        if (email.isEmpty()) {
            mEmailField.setError("Email is required");
            mEmailField.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailField.setError("Please enter a valid email");
            mEmailField.requestFocus();
            return;
        }

        if (username.isEmpty()) {
            mUsernameField.setError("Username is required");
            mUsernameField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            mPasswordField.setError("Password is required");
            mPasswordField.requestFocus();
            return;
        }

        if (password.length() < 6) {
            mPasswordField.setError("Password must be at least 6 characters");
            mPasswordField.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            mConfirmPasswordField.setError("Passwords do not match");
            mConfirmPasswordField.requestFocus();
            return;
        }

        mRegistrationProgressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    User user = new User(username);

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, "User registration successful", Toast.LENGTH_SHORT).show();
                                mAuth.signOut();
                                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                            } else {
                                Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

                mRegistrationProgressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_up_button:
                registerUser();
                break;
            case R.id.login_text_view:
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }
}
