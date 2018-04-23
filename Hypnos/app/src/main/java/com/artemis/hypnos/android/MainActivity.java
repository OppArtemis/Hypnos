package com.artemis.hypnos.android;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
    private TextView mTxtPoopTotalToday;

    private Button mBtnPee;
    private TextView mTxtPeeLastReportedTime;
    private TextView mTxtPeeTotalToday;

    private Button mBtnEatB;
    private TextView mTxtEatBLastReportedTime;
    private TextView mTxtEatBTotalToday;

    private Button mBtnEatS;
    private TextView mTxtEatSLastReportedTime;
    private TextView mTxtEatSTotalToday;

    private Button mBtnSleep;
    private TextView mTxtSleepLastReportedTime;
    private TextView mTxtSleepTotalToday;

    private Button mBtnRefresh;

    private Button mBtnUndo;
    private TextView mTxtDetailsList;

    private static final int MENU_ITEM_ITEM1 = 1;
    private static final int MENU_ITEM_ITEM2 = 2;
    private static final int MENU_ITEM_ITEM3 = 3;

    private int detailToDisplay = -1;

    private long timeToDisplay = Calendar.getInstance().getTimeInMillis();
    
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
        mTxtPoopTotalToday = findViewById(R.id.txtPoopTotalToday);

        mBtnPee = findViewById(R.id.btnPee);
        mTxtPeeLastReportedTime = findViewById(R.id.txtPeeLastReportedTime);
        mTxtPeeTotalToday = findViewById(R.id.txtPeeTotalToday);

        mBtnEatB = findViewById(R.id.btnEat);
        mTxtEatBLastReportedTime = findViewById(R.id.txtEatLastReportedTime);
        mTxtEatBTotalToday = findViewById(R.id.txtEatTotalToday);

        mBtnEatS = findViewById(R.id.btnEat2);
        mTxtEatSLastReportedTime = findViewById(R.id.txtEat2LastReportedTime);
        mTxtEatSTotalToday = findViewById(R.id.txtEat2TotalToday);

        mBtnSleep = findViewById(R.id.btnSleep);
        mTxtSleepLastReportedTime = findViewById(R.id.txtSleepLastReportedTime);
        mTxtSleepTotalToday = findViewById(R.id.txtSleepTotalToday);

        mTxtDetailsList = findViewById(R.id.txtDetailsList);

        // todo get user login

        // load profile
        databaseHandle = new DatabaseHandle();
        databaseHandle.setContext(this);

        setHandles();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver_UIRefresh,
                new IntentFilter(Constants.lbmUIRefresh));

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver_NewBabyProfile,
                new IntentFilter(Constants.lbmNewBabyProfileLoaded));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

//        menu.add(Menu.NONE, MENU_ITEM_ITEM1, Menu.NONE, "Back");
//        menu.add(Menu.NONE, MENU_ITEM_ITEM2, Menu.NONE, "Forward");
//        menu.add(Menu.NONE, MENU_ITEM_ITEM3, Menu.NONE, "Today");
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        //respond to menu item selection
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refreshCurrentScreen();
                break;

            case R.id.action_undo:
                removeLogEntry();
                break;

            case R.id.action_back:
                timeToDisplay = timeToDisplay - 86400000;
                break;

            case R.id.action_forward:
                timeToDisplay = timeToDisplay + 86400000;
                break;
        }

        if (timeToDisplay > Calendar.getInstance().getTimeInMillis()) {
            setClockToToday();
        }

        setClockAndUpdateUI();

        return false;
    }

    public void setClockAndUpdateUI() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeToDisplay);
        calendar.add(Calendar.DATE, -1);

        String logPivot1 = Constants.dateFormatDisplayDebug.format(calendar.getTimeInMillis());
//        getSupportActionBar().setTitle(logPivot1);
        Snackbar.make(findViewById(android.R.id.content), logPivot1, Snackbar.LENGTH_LONG)
                .show();

        for (int i = 0; i < 5; i++) {
            databaseHandle.updateUI(calendar, databaseHandle.getBabyProfile().getLogTypes().get(i), false);
        }
    }

    public void setClockToToday() {
        timeToDisplay = Calendar.getInstance().getTimeInMillis();
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

    public void refreshCurrentScreen() {
        setClockToToday();
        setUI(null);
    }

    public void setUI(String activityType) {
        setUICluster(mTxtPeeTotalToday, mTxtPeeLastReportedTime, databaseHandle.getActivityClasses().get(0));
        setUICluster(mTxtPoopTotalToday, mTxtPoopLastReportedTime, databaseHandle.getActivityClasses().get(1));
        setUICluster(mTxtEatBTotalToday, mTxtEatBLastReportedTime, databaseHandle.getActivityClasses().get(2));
        setUICluster(mTxtEatSTotalToday, mTxtEatSLastReportedTime, databaseHandle.getActivityClasses().get(3));
        setUICluster(mTxtSleepTotalToday, mTxtSleepLastReportedTime, databaseHandle.getActivityClasses().get(4));

        if (detailToDisplay > -1) {
            String newText = databaseHandle.getActivityClasses().get(detailToDisplay).retrieveLogNotes();
            mTxtDetailsList.setText(newText);
        }
    }

    public void setUICluster(TextView totalToday, TextView reportedTime, ActivityClass activityClass) {
        String blurb = activityClass.retrieveLastReportedTime() + ", " + activityClass.retrieveLastReportedPerson();
        totalToday.setText(activityClass.retrieveLogTotalToday());
        reportedTime.setText(blurb);
//        reportedPerson.setText(counterHandle.retrieveLastReportedPerson());
    }

    public void timePickAddLogEntry(final String activityType) {
        final Calendar currentDate = Calendar.getInstance();
        final Calendar mCurrentTime = Calendar.getInstance();
        new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mCurrentTime.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mCurrentTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        mCurrentTime.set(Calendar.MINUTE, minute);
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();

        long timeMs = currentDate.getTimeInMillis();
        addLogEntry(activityType, timeMs);
    }

    public void timePickAddLogEntry2(final String activityType) {
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

    public void setHandles() {
        mTxtPeeLastReportedTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailToDisplay = 0;
                setUI(null);
            }
        });

        mTxtPoopLastReportedTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailToDisplay = 1;
                setUI(null);
            }
        });

        mTxtEatBLastReportedTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailToDisplay = 2;
                setUI(null);
            }
        });

        mTxtEatSLastReportedTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailToDisplay = 3;
                setUI(null);
            }
        });

        mTxtSleepLastReportedTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailToDisplay = 4;
                setUI(null);
            }
        });

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
                timePickAddLogEntry(activityType);
            }
        });

        mBtnEatS.setOnClickListener(new View.OnClickListener() {
            String activityType = "EAT S";

            @Override
            public void onClick(View view) {
                timePickAddLogEntry(activityType);
            }
        });

        mBtnSleep.setOnClickListener(new View.OnClickListener() {
            String activityType = "SLEEP";

            @Override
            public void onClick(View view) {
                timePickAddLogEntry(activityType);
            }
        });
    }

    private void addLogEntry(String activityType, long eventMs){
        long logMs = Calendar.getInstance().getTimeInMillis();
        databaseHandle.addLogEntry(activityType, logMs, eventMs);
    }

    private void removeLogEntry(){
        databaseHandle.removeLogEntry();
    }
}
