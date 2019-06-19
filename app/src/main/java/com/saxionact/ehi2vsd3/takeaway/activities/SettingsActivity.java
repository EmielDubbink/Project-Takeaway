package com.saxionact.ehi2vsd3.takeaway.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.saxionact.ehi2vsd3.takeaway.R;

/**
 * The Settings Activity
 *
 * @author Stef
 */

public class SettingsActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private Button btnNotificaties, btnCSV, btnSignOut;
    private GoogleSignInAccount account = AuthenticationActivity.account;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btnNotificaties = findViewById(R.id.btnNotificaties);
        //btnCSV = findViewById(R.id.btnCSV);
        btnSignOut = findViewById(R.id.btnUitloggen);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Currently disabled, because CSV doesn't work at the moment
//        // onClickListener to start activity to the CSVActivity
//        btnCSV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(SettingsActivity.this, CSVActivity.class);
//                startActivity(intent);
//            }
//        });

        Toast.makeText(getApplicationContext(),getString(R.string.logged_in)+ " " + account.getDisplayName(), Toast.LENGTH_SHORT).show();

        // onClickListener to log out
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(googleApiClient.isConnected()){
                    signOut();
                }
            }
        });

        // onClickListener to start activity to the NotificationsActivity
        btnNotificaties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });
    }

    // Signing out function
    private void signOut() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                        Auth.GoogleSignInApi.signOut(googleApiClient);
                        googleApiClient.disconnect();
                        googleApiClient.connect();
                        Toast.makeText(getApplicationContext(),R.string.logged_out, Toast.LENGTH_SHORT).show();

                        // Returns you back to the login activity(AuthenticationActivity)
                        Intent signOutIntent = new Intent(SettingsActivity.this, AuthenticationActivity.class);
                        startActivity(signOutIntent);
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("SignOutActivity", "onConnectionFailed:" + connectionResult);
    }
}
