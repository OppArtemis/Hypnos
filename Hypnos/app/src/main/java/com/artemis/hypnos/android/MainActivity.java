package com.artemis.hypnos.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private BabyProfile babyProfile;
    private DatabaseHandle databaseHandle;

    public final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    private Button mBtnPooped;
    private TextView mTxtPoopedLast;
    private TextView mTxtPoopedToday;

    private Button mBtnPeed;

    private TextView mTxtBabyName;
    private TextView mTxtBabyDob;
    private TextView mTxtNewDayTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        babyProfile = new BabyProfile();
        databaseHandle = new DatabaseHandle(babyProfile);
        databaseHandle.setContext(this);

        mBtnPooped = (Button) findViewById(R.id.btnPooped);
        mTxtPoopedLast = (TextView) findViewById(R.id.txtLastPooped);
        mTxtPoopedToday = (TextView) findViewById(R.id.txtTodayPooped);

        mBtnPeed = (Button) findViewById(R.id.btnPeed);

        mTxtBabyName = (TextView) findViewById(R.id.txtBabyName);
        mTxtBabyDob = (TextView) findViewById(R.id.txtBabyDob);
        mTxtNewDayTime = (TextView) findViewById(R.id.txtNewDayTime);

        setHandles();
        setUI();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(Constants.lbmUIRefresh));
    }

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
//            String message = intent.getStringExtra("message");
//            Log.d("receiver", "Got message: " + message);
            setUI();
        }
    };

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    public String milliToString(long latestTimestamp) {
        Calendar poopTime = Calendar.getInstance();
        poopTime.setTimeInMillis(latestTimestamp);
        return dateFormat.format(poopTime.getTime());
    }

    public void setUI() {
        mTxtBabyName.setText(babyProfile.getBabyName());
        mTxtBabyDob.setText(babyProfile.getBabyDOB());
        mTxtNewDayTime.setText(babyProfile.getNewDayTimeString());

        mTxtPoopedLast.setText(databaseHandle.poopCounter.lastTimeOccured);
        mTxtPoopedToday.setText(databaseHandle.poopCounter.todayCounter);
    }

    public void setHandles() {
        mBtnPooped.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEntryPoop();
            }
        });

        mBtnPeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEntryPeed();
            }
        });
    }

    private void addEntryPoop(){
        databaseHandle.addEntry(babyProfile, Constants.ActivityType.POOPED);
    }

    private void addEntryPeed(){
        databaseHandle.addEntry(babyProfile, Constants.ActivityType.PEED);
    }
}
