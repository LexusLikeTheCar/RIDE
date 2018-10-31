package edu.osu.ride;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.api.client.http.HttpResponseException;

import java.util.concurrent.ExecutionException;

public class TokenActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mGenerateTokenButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token);

        mGenerateTokenButton = findViewById(R.id.generate_token_button);
        mGenerateTokenButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.generate_token_button:
                Toast.makeText(this, "Button clicked", Toast.LENGTH_SHORT).show();
                String token = "";
                try {
                    token = new TokenOperation().execute("").get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                Log.i("TokenActivity", token);

                String locationResponse = "";
                if (!token.isEmpty()) {
                    try {
                        locationResponse = new BirdLocationOperation().execute(token).get();
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("LocationResponse", locationResponse);

                break;
        }
    }

    private static class TokenOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String token = "";

            try {
                try {
                    token = BirdService.generateToken();
                } catch (HttpResponseException e) {
                    System.err.println(e.getMessage());
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }

            return token;
        }

        @Override
        protected void onPostExecute(String result) {}

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    private static class BirdLocationOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String token = params[0];

            String locationResponse = "";

            try {
                try {
                    locationResponse = BirdService.locationResponse(token);
                } catch (HttpResponseException e) {
                    System.err.println(e.getMessage());
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }

            return locationResponse;
        }

        @Override
        protected void onPostExecute(String result) {}

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
