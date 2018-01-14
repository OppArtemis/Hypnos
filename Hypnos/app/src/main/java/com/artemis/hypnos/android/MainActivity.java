package com.artemis.hypnos.android;

import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

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

    private Button mBtnEatB;
    private TextView mTxtEatBLastReportedTime;
    private TextView mTxtEatBLastReportedPerson;
    private TextView mTxtEatBTotalToday;

    private Button mBtnEatS;
    private TextView mTxtEatSLastReportedTime;
    private TextView mTxtEatSLastReportedPerson;
    private TextView mTxtEatSTotalToday;

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
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        // set ui
        mBtnPoop = findViewById(R.id.btnPoop);
        mTxtPoopLastReportedTime = findViewById(R.id.txtPoopLastReportedTime);
        mTxtPoopLastReportedPerson = findViewById(R.id.txtPoopLastReportedPerson);
        mTxtPoopTotalToday = findViewById(R.id.txtPoopTotalToday);

        mBtnPee = findViewById(R.id.btnPee);
        mTxtPeeLastReportedTime = findViewById(R.id.txtPeeLastReportedTime);
        mTxtPeeLastReportedPerson = findViewById(R.id.txtPeeLastReportedPerson);
        mTxtPeeTotalToday = findViewById(R.id.txtPeeTotalToday);

        mBtnEatB = findViewById(R.id.btnEat);
        mTxtEatBLastReportedTime = findViewById(R.id.txtEatLastReportedTime);
        mTxtEatBLastReportedPerson = findViewById(R.id.txtEatLastReportedPerson);
        mTxtEatBTotalToday = findViewById(R.id.txtEatTotalToday);

        mBtnEatS = findViewById(R.id.btnEat2);
        mTxtEatSLastReportedTime = findViewById(R.id.txtEat2LastReportedTime);
        mTxtEatSLastReportedPerson = findViewById(R.id.txtEat2LastReportedPerson);
        mTxtEatSTotalToday = findViewById(R.id.txtEat2TotalToday);

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
        databaseHandle = new DatabaseHandle();
        databaseHandle.setContext(this);

        setHandles();

        mBtnWake.setVisibility(View.INVISIBLE);
        mBtnUndo.setVisibility(View.INVISIBLE);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver_UIRefresh,
                new IntentFilter(Constants.lbmUIRefresh));

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver_NewBabyProfile,
                new IntentFilter(Constants.lbmNewBabyProfileLoaded));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        //respond to menu item selection
        return true;
    }

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver_NewBabyProfile = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            getSupportActionBar().setTitle(databaseHandle.getBabyProfile().getBabyName());
        }
    };

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver_UIRefresh = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            String activityType = String.valueOf(message);

            setUI(activityType);
        }
    };

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver_UIRefresh);
        super.onDestroy();
    }

    public String milliToString(long latestTimestamp) {
        Calendar poopTime = Calendar.getInstance();
        poopTime.setTimeInMillis(latestTimestamp);
        return Constants.dateFormatDisplayLong.format(poopTime.getTime());
    }

//    public void setUIInit() {
//        mTxtBabyName.setText("Name: " + databaseHandle.getBabyProfile().getBabyName());
//        mTxtBabyDob.setText("DOB: " + databaseHandle.getBabyProfile().getBabyDOB());
//        mTxtNewDayTime.setText("Day Start: " + databaseHandle.getBabyProfile().getNewDayTimeString());
//        mTxtCurrentUser.setText("User: " + databaseHandle.getCurrentUser());
//    }

    public void setUI(String activityType) {
        setUICluster(mTxtPeeTotalToday, mTxtPeeLastReportedTime, mTxtPeeLastReportedPerson, databaseHandle.getBabyLogCounter().get(0));
        setUICluster(mTxtPoopTotalToday, mTxtPoopLastReportedTime, mTxtPoopLastReportedPerson, databaseHandle.getBabyLogCounter().get(1));
        setUICluster(mTxtEatBTotalToday, mTxtEatBLastReportedTime, mTxtEatBLastReportedPerson, databaseHandle.getBabyLogCounter().get(2));
        setUICluster(mTxtEatSTotalToday, mTxtEatSLastReportedTime, mTxtEatSLastReportedPerson, databaseHandle.getBabyLogCounter().get(3));
        setUICluster(mTxtSleepTotalToday, mTxtSleepLastReportedTime, mTxtSleepLastReportedPerson, databaseHandle.getBabyLogCounter().get(4));
//        setUICluster(mTxtWakeTotalToday, mTxtWakeLastReportedTime, mTxtWakeLastReportedPerson, databaseHandle.getBabyLogCounter().get(4));

        mTxtWakeTotalToday.setText(databaseHandle.getBabyLogCounter().get(4).getCurrentState());
        mTxtSleepList.setText(databaseHandle.getBabyLogCounter().get(4).getNotes());

//        // override the sleep ones
//        mTxtSleepTotalToday.setText(Constants.sleepTimeFormat(databaseHandle.sleepLengthMs));
//        mTxtWakeTotalToday.setText(databaseHandle.sleepState.toString());
//        mTxtSleepList.setText(databaseHandle.sleepList);
//
//        if (databaseHandle.sleepState == Constants.SleepWake.ASLEEP) {
//            mBtnSleep.setVisibility(View.INVISIBLE);
//            mBtnWake.setVisibility(View.VISIBLE);
//        } else {
//            mBtnSleep.setVisibility(View.VISIBLE);

//        }
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
                String activityType = "PEE";
                long timeMs = Calendar.getInstance().getTimeInMillis();
                addLogEntry(activityType, timeMs);
            }
        });

        mBtnPoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String activityType = "POOP";
                long timeMs = Calendar.getInstance().getTimeInMillis();
                addLogEntry(activityType, timeMs);
            }
        });

        mBtnEatB.setOnClickListener(new View.OnClickListener() {
            String activityType = "EAT B";

            @Override
            public void onClick(View view) {
                Calendar mCurrentTime = Calendar.getInstance();
                int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mCurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker
                        = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        Calendar mPickedTime = Calendar.getInstance();
                        mPickedTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                        mPickedTime.set(Calendar.MINUTE, selectedMinute);
                        mPickedTime.set(Calendar.SECOND, 0);
                        long timeMs = mPickedTime.getTimeInMillis();

                        addLogEntry(activityType, timeMs);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        mBtnEatS.setOnClickListener(new View.OnClickListener() {
            String activityType = "EAT S";

            @Override
            public void onClick(View view) {
                Calendar mCurrentTime = Calendar.getInstance();
                int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mCurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker
                        = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        Calendar mPickedTime = Calendar.getInstance();
                        mPickedTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                        mPickedTime.set(Calendar.MINUTE, selectedMinute);
                        mPickedTime.set(Calendar.SECOND, 0);
                        long timeMs = mPickedTime.getTimeInMillis();

                        addLogEntry(activityType, timeMs);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        mBtnSleep.setOnClickListener(new View.OnClickListener() {
            String activityType = "SLEEP";

            @Override
            public void onClick(View view) {
                Calendar mCurrentTime = Calendar.getInstance();
                int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mCurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker
                        = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        Calendar mPickedTime = Calendar.getInstance();
                        mPickedTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                        mPickedTime.set(Calendar.MINUTE, selectedMinute);
                        mPickedTime.set(Calendar.SECOND, 0);
                        long timeMs = mPickedTime.getTimeInMillis();

                        addLogEntry(activityType, timeMs);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

//        mBtnWake.setOnClickListener(new View.OnClickListener() {
//            String activityType = "SLEEP";
//
//            @Override
//            public void onClick(View view) {
//                Calendar mCurrentTime = Calendar.getInstance();
//                int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
//                int minute = mCurrentTime.get(Calendar.MINUTE);
//                TimePickerDialog mTimePicker
//                        = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
//                    @Override
//                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
//                        Calendar mPickedTime = Calendar.getInstance();
//                        mPickedTime.set(Calendar.HOUR_OF_DAY, selectedHour);
//                        mPickedTime.set(Calendar.MINUTE, selectedMinute);
//                        mPickedTime.set(Calendar.SECOND, 0);
//                        long timeMs = mPickedTime.getTimeInMillis();
//
//                        addLogEntry(activityType, timeMs);
//                    }
//                }, hour, minute, true);//Yes 24 hour time
//                mTimePicker.setTitle("Select Time");
//                mTimePicker.show();
//            }
//        });

        mBtnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeLogEntry();
            }
        });
    }

    private void addLogEntry(String activityType, long timeMs){
        databaseHandle.addLogEntry(activityType, timeMs);
    }

    private void removeLogEntry(){
        databaseHandle.removeLogEntry();
    }
}
