package com.artemis.hypnos.android;

/**
 * Main Activity, the entry point for the app, which
 * requires users to login.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import java.util.Arrays;

// Firebase UI libraries
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ErrorCodes;

// General Firebase libraries
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * MainLandingPage class that handles the entry point of the application,
 * which consists of:
 * - Request user to login
 * - Get the location from user (via location services)
 *
 * @author  Jonathan Lin & Jorge Quan
 * @since   2017-09-03
 */
public class MainLandingPage extends AppCompatActivity {

    private Button loginButton;
    private FirebaseAuth auth;

    // Choose an arbitrary request code value
    private static final int RC_SIGN_IN = 128;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            setContentView(R.layout.activity_mainlandingpage);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            loginButton = (Button)findViewById(R.id.login_button);
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivityForResult(AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                                    .setAvailableProviders(
                                            Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            });
        } else {
            loginUser();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // RC_SIGN_IN is the request code you passed into startActivityForResult
        if (requestCode == RC_SIGN_IN) {

            if(resultCode == RESULT_OK){
                loginUser();
            } else {

                IdpResponse response = IdpResponse.fromResultIntent(data);
                if (resultCode == RESULT_CANCELED) {
                    displayMessage(getString(R.string.signin_failed));
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    displayMessage(getString(R.string.no_network));
                }
            }

            return;
        }
        displayMessage(getString(R.string.unknown_response));
    }

    /**
     * This method starts a new "Activity" for users that are login.
     *
     */
    private void loginUser(){
        Intent loginIntent = new Intent(MainLandingPage.this, MainActivity.class);
        startActivity(loginIntent);
        finish();
    }

    /**
     * Helper method to display a message on screen.
     *
     * @param message This is the string to display.
     */
    private void displayMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}