package com.artemis.hypnos.android;

/**
 * Class that performs the sign-in and sign-out actions.
 */

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

// Firebase libraries
import com.firebase.ui.auth.AuthUI;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

/**
 * SigninActivity class that handles specific activity for users who are signed in,
 * which consists of:
 * - Allow users to logout
 * - Allow users to view his/her profile information
 *
 * @author  Jonathan Lin & Jorge Quan
 * @since   2017-09-03
 */
public class SigninActivity extends AppCompatActivity {

    // For logging.
    private static final String TAG = SigninActivity.class.getSimpleName();

    // Stores the information of user name to be displayed.
    private TextView profileName;

    // Stores the information of location info to be displayed.
    private TextView locationInfo;

    private FirebaseAuth auth;

    // Stores User ID (unique key for database)
    private String mUserId;

    // --- Database ---
    // Stores the reference to the database
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() == null)
        {
            signOut();
        }

        // Store information in the database
        storeBasicInfoIntoDatabase();

        // Set up the profile page
        setContentView(R.layout.activity_signin);
        setTitle(getString(R.string.profile_title));
        displayLoginUserProfileName();

        // Button to logout
        Button logoutButton = findViewById(R.id.sign_out);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthUI.getInstance()
                        .signOut(SigninActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    signOut();
                                }else {
                                    displayMessage(getString(R.string.sign_out_error));
                                }
                            }
                        });
            }
        });
    }

    /**
     * This method starts a new "Activity" for users that are logout.
     *
     */
    private void signOut(){
        Intent signOutIntent = new Intent(this, MainActivity.class);
        startActivity(signOutIntent);
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

    /**
     * Helper method to set user_name on profile page.
     *
     */
    private void displayLoginUserProfileName(){
        FirebaseUser mUser = auth.getCurrentUser();
        profileName = findViewById(R.id.user_name);
        if(mUser != null){
            profileName.setText(TextUtils.isEmpty(mUser.getDisplayName())? "No name found" : mUser.getDisplayName());
        }
    }

    /**
     * Helper method store basic information onto database.
     *
     */
    private void storeBasicInfoIntoDatabase(){
        // Instantiate a reference to the database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // User ID acts as the key to the database
        mUserId = auth.getCurrentUser().getUid();

        Date currentTime = Calendar.getInstance().getTime();
        String userName = auth.getCurrentUser().getDisplayName();

        mDatabase.child("users").
                child(mUserId).
                child("Name").
                setValue(userName);
        mDatabase.child("users").
                child(mUserId).
                child("lastLoginTime").
                setValue(String.valueOf(currentTime));
    }
}
