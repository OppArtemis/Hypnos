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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private DatabaseHandle databaseHandle;

    private TextView mTxtBabyName;
    private TextView mTxtBabyDob;
    private TextView mTxtNewDayTime;
    private TextView mTxtCurrentUser;

    private Button mBtnPoop;
    private TextView mTxtPoopLastReportedTime;
    private TextView mTxtPoopLastReportedPerson;
    private TextView mTxtPoopTotalToday;

    private Button mBtnPee;
    private TextView mTxtPeeLastReportedTime;
    private TextView mTxtPeeLastReportedPerson;
    private TextView mTxtPeeTotalToday;

    private Button mBtnSleep;
    private TextView mTxtSleepLastReportedTime;
    private TextView mTxtSleepLastReportedPerson;
    private TextView mTxtSleepTotalToday;

    private Button mBtnWake;
    private TextView mTxtWakeLastReportedTime;
    private TextView mTxtWakeLastReportedPerson;
    private TextView mTxtWakeTotalToday;


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

        BabyProfile babyProfile = new BabyProfile();
        databaseHandle = new DatabaseHandle(babyProfile);
        databaseHandle.setContext(this);

        mTxtBabyName = (TextView) findViewById(R.id.txtBabyName);
        mTxtBabyDob = (TextView) findViewById(R.id.txtBabyDob);
        mTxtNewDayTime = (TextView) findViewById(R.id.txtNewDayTime);
        mTxtCurrentUser = (TextView) findViewById(R.id.txtCurrentUser);

        mBtnPoop = (Button) findViewById(R.id.btnPoop);
        mTxtPoopLastReportedTime = (TextView) findViewById(R.id.txtPoopLastReportedTime);
        mTxtPoopLastReportedPerson = (TextView) findViewById(R.id.txtPoopLastReportedPerson);
        mTxtPoopTotalToday = (TextView) findViewById(R.id.txtPoopTotalToday);

        mBtnPee = (Button) findViewById(R.id.btnPee);
        mTxtPeeLastReportedTime = (TextView) findViewById(R.id.txtPeeLastReportedTime);
        mTxtPeeLastReportedPerson = (TextView) findViewById(R.id.txtPeeLastReportedPerson);
        mTxtPeeTotalToday = (TextView) findViewById(R.id.txtPeeTotalToday);

        mBtnSleep = (Button) findViewById(R.id.btnSleep);
        mTxtSleepLastReportedTime = (TextView) findViewById(R.id.txtSleepLastReportedTime);
        mTxtSleepLastReportedPerson = (TextView) findViewById(R.id.txtSleepLastReportedPerson);
        mTxtSleepTotalToday = (TextView) findViewById(R.id.txtSleepTotalToday);

        mBtnWake = (Button) findViewById(R.id.btnWake);
        mTxtWakeLastReportedTime = (TextView) findViewById(R.id.txtWakeLastReportedTime);
        mTxtWakeLastReportedPerson = (TextView) findViewById(R.id.txtWakeLastReportedPerson);
        mTxtWakeTotalToday = (TextView) findViewById(R.id.txtWakeTotalToday);

        setHandles();
        setUIInit();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(Constants.lbmUIRefresh));
    }

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            Constants.ActivityType activityType = Constants.ActivityType.valueOf(message);

            setUI(activityType);
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
        return Constants.dateFormat.format(poopTime.getTime());
    }

    public void setUIInit() {
        mTxtBabyName.setText("Name: " + databaseHandle.getBabyProfile().getBabyName());
        mTxtBabyDob.setText("DOB: " + databaseHandle.getBabyProfile().getBabyDOB());
        mTxtNewDayTime.setText("Day Start: " + databaseHandle.getBabyProfile().getNewDayTimeString());
        mTxtCurrentUser.setText("User: " + databaseHandle.getCurrentUser());
    }

    public void setUI(Constants.ActivityType activityType) {
        setUICluster(mTxtPeeTotalToday, mTxtPeeLastReportedTime, mTxtPeeLastReportedPerson, databaseHandle.peedCounter);
        setUICluster(mTxtPoopTotalToday, mTxtPoopLastReportedTime, mTxtPoopLastReportedPerson, databaseHandle.poopCounter);
        setUICluster(mTxtSleepTotalToday, mTxtSleepLastReportedTime, mTxtSleepLastReportedPerson, databaseHandle.sleepCounter);
        setUICluster(mTxtWakeTotalToday, mTxtWakeLastReportedTime, mTxtWakeLastReportedPerson, databaseHandle.wakeCounter);

        // override the sleep ones
        mTxtSleepTotalToday.setText(sleepTimeFormat(databaseHandle.sleepLengthMs));
        mTxtWakeTotalToday.setText(databaseHandle.sleepState.toString());

        if (databaseHandle.sleepState == Constants.SleepWake.ASLEEP) {
            mBtnSleep.setVisibility(View.INVISIBLE);
            mBtnWake.setVisibility(View.VISIBLE);
        } else {
            mBtnSleep.setVisibility(View.VISIBLE);
            mBtnWake.setVisibility(View.INVISIBLE);
        }
    }

    public String sleepTimeFormat(long sleepLengthMs) {
        String out;

        if (sleepLengthMs < 1000) {
            out = sleepLengthMs + " ms";
        } else if (sleepLengthMs < 1000*60) {
            out = sleepLengthMs / 1000 + " s";
        }
        else if (sleepLengthMs < 1000*60*60){
            out = sleepLengthMs / (1000*60) + " min";
        } else {
            out = sleepLengthMs / (1000*60*60) + " hr";
        }

        return out;
    }

    public void setUICluster(TextView totalToday, TextView reportedTime, TextView reportedPerson,
                             ActivityCounterHandle counterHandle) {
        totalToday.setText(counterHandle.getTotalToday());
        reportedTime.setText(counterHandle.getLastReportedTime());
        reportedPerson.setText(counterHandle.getLastReportedPerson());
    }

    public void setHandles() {
        mBtnPee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEntry(Constants.ActivityType.PEE);
            }
        });

        mBtnPoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEntry(Constants.ActivityType.POOP);
            }
        });

        mBtnSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEntry(Constants.ActivityType.SLEEP);
            }
        });

        mBtnWake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEntry(Constants.ActivityType.WAKE);
            }
        });
    }

    private void addEntry(Constants.ActivityType activityType){
        databaseHandle.addEntry(activityType);
    }
}
