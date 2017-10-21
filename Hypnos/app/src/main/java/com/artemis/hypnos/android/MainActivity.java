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

//    private TextView mTxtBabyName;
//    private TextView mTxtBabyDob;
//    private TextView mTxtNewDayTime;
//    private TextView mTxtCurrentUser;

    private Button mBtnPoop;
    private TextView mTxtPoopLastReportedTime;
    private TextView mTxtPoopLastReportedPerson;
    private TextView mTxtPoopTotalToday;

    private Button mBtnPee;
    private TextView mTxtPeeLastReportedTime;
    private TextView mTxtPeeLastReportedPerson;
    private TextView mTxtPeeTotalToday;

    private Button mBtnEat;
    private TextView mTxtEatLastReportedTime;
    private TextView mTxtEatLastReportedPerson;
    private TextView mTxtEatTotalToday;

    private Button mBtnSleep;
    private TextView mTxtSleepLastReportedTime;
    private TextView mTxtSleepLastReportedPerson;
    private TextView mTxtSleepTotalToday;

    private Button mBtnWake;
    private TextView mTxtWakeLastReportedTime;
    private TextView mTxtWakeLastReportedPerson;
    private TextView mTxtWakeTotalToday;

    private Button mBtnUndo;
    private TextView mTxtSleepList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // set ui
//        mTxtBabyName = findViewById(R.id.txtBabyName);
//        mTxtBabyDob = findViewById(R.id.txtBabyDob);
//        mTxtNewDayTime = findViewById(R.id.txtNewDayTime);
//        mTxtCurrentUser = findViewById(R.id.txtCurrentUser);

        mBtnPoop = findViewById(R.id.btnPoop);
        mTxtPoopLastReportedTime = findViewById(R.id.txtPoopLastReportedTime);
        mTxtPoopLastReportedPerson = findViewById(R.id.txtPoopLastReportedPerson);
        mTxtPoopTotalToday = findViewById(R.id.txtPoopTotalToday);

        mBtnPee = findViewById(R.id.btnPee);
        mTxtPeeLastReportedTime = findViewById(R.id.txtPeeLastReportedTime);
        mTxtPeeLastReportedPerson = findViewById(R.id.txtPeeLastReportedPerson);
        mTxtPeeTotalToday = findViewById(R.id.txtPeeTotalToday);

        mBtnEat = findViewById(R.id.btnEat);
        mTxtEatLastReportedTime = findViewById(R.id.txtEatLastReportedTime);
        mTxtEatLastReportedPerson = findViewById(R.id.txtEatLastReportedPerson);
        mTxtEatTotalToday = findViewById(R.id.txtEatTotalToday);

        mBtnSleep = findViewById(R.id.btnSleep);
        mTxtSleepLastReportedTime = findViewById(R.id.txtSleepLastReportedTime);
        mTxtSleepLastReportedPerson = findViewById(R.id.txtSleepLastReportedPerson);
        mTxtSleepTotalToday = findViewById(R.id.txtSleepTotalToday);

        mBtnWake = findViewById(R.id.btnWake);
        mTxtWakeLastReportedTime = findViewById(R.id.txtWakeLastReportedTime);
        mTxtWakeLastReportedPerson = findViewById(R.id.txtWakeLastReportedPerson);
        mTxtWakeTotalToday = findViewById(R.id.txtWakeTotalToday);

        mBtnUndo = findViewById(R.id.btnUndo);
        mTxtSleepList = findViewById(R.id.txtSleepList);
        
        // todo get user login

        // load profile
        BabyProfile babyProfile = BabyProfile.defaultTest();
        databaseHandle = new DatabaseHandle(babyProfile);
        databaseHandle.setContext(this);

        databaseHandle.addBabyEntry();

        getSupportActionBar().setTitle(babyProfile.getBabyName());

        setHandles();
//        setUIInit();

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
        return Constants.dateFormatLong.format(poopTime.getTime());
    }

//    public void setUIInit() {
//        mTxtBabyName.setText("Name: " + databaseHandle.getBabyProfile().getBabyName());
//        mTxtBabyDob.setText("DOB: " + databaseHandle.getBabyProfile().getBabyDOB());
//        mTxtNewDayTime.setText("Day Start: " + databaseHandle.getBabyProfile().getNewDayTimeString());
//        mTxtCurrentUser.setText("User: " + databaseHandle.getCurrentUser());
//    }

    public void setUI(Constants.ActivityType activityType) {
        setUICluster(mTxtPeeTotalToday, mTxtPeeLastReportedTime, mTxtPeeLastReportedPerson, databaseHandle.peedCounter);
        setUICluster(mTxtPoopTotalToday, mTxtPoopLastReportedTime, mTxtPoopLastReportedPerson, databaseHandle.poopCounter);
        setUICluster(mTxtEatTotalToday, mTxtEatLastReportedTime, mTxtEatLastReportedPerson, databaseHandle.eatCounter);
        setUICluster(mTxtSleepTotalToday, mTxtSleepLastReportedTime, mTxtSleepLastReportedPerson, databaseHandle.sleepCounter);
        setUICluster(mTxtWakeTotalToday, mTxtWakeLastReportedTime, mTxtWakeLastReportedPerson, databaseHandle.wakeCounter);

        // override the sleep ones
        mTxtSleepTotalToday.setText(Constants.sleepTimeFormat(databaseHandle.sleepLengthMs));
        mTxtWakeTotalToday.setText(databaseHandle.sleepState.toString());
        mTxtSleepList.setText(databaseHandle.sleepList);

        if (databaseHandle.sleepState == Constants.SleepWake.ASLEEP) {
            mBtnSleep.setVisibility(View.INVISIBLE);
            mBtnWake.setVisibility(View.VISIBLE);
        } else {
            mBtnSleep.setVisibility(View.VISIBLE);
            mBtnWake.setVisibility(View.INVISIBLE);
        }
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
                addLogEntry(Constants.ActivityType.PEE);
            }
        });

        mBtnPoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLogEntry(Constants.ActivityType.POOP);
            }
        });

        mBtnEat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLogEntry(Constants.ActivityType.EAT);
            }
        });

        mBtnSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLogEntry(Constants.ActivityType.SLEEP);
            }
        });

        mBtnWake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLogEntry(Constants.ActivityType.WAKE);
            }
        });

        mBtnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeLogEntry();
            }
        });
    }

    private void addLogEntry(Constants.ActivityType activityType){
        databaseHandle.addLogEntry(activityType);
    }

    private void removeLogEntry(){
        databaseHandle.removeLogEntry();
    }
}
