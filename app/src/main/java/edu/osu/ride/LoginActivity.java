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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mLoginButton;
    private TextView mSignupTextView;
    private ProgressBar mLoginProgressBar;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailField = findViewById(R.id.email_edit_text);
        mPasswordField = findViewById(R.id.password_edit_text);

        mLoginButton = findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(this);

        mSignupTextView = findViewById(R.id.signup_text_view);
        mSignupTextView.setOnClickListener(this);

        mLoginProgressBar = findViewById(R.id.login_progress_bar);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        if (mCurrentUser != null) {
            // TODO: Signed in, launch the Rider activity
            startActivity(new Intent(this, SignUpActivity.class));
        }
    }

    public void signIn() {
        String email = mEmailField.getText().toString().trim();
        String password = mPasswordField.getText().toString().trim();

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

        if (password.isEmpty()) {
            mPasswordField.setError("Password is required");
            mPasswordField.requestFocus();
            return;
        }

        mLoginProgressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // TODO: redirect to Rider activity
                    startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                } else {
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
                mLoginProgressBar.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                signIn();
                break;
            case R.id.signup_text_view:
                startActivity(new Intent(this, SignUpActivity.class));
                break;
        }
    }
}
