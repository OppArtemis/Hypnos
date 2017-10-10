package com.artemis.hypnos.android;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

/**
 * Facilitates all interaction with Firebase
 *
 * Created by jf2lin on 09/20/2017.
 */

public class DatabaseHandle {
    private Context mContext;
    public Context getContext() {
        return mContext;
    }
    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    private BabyProfile babyProfile;
    private String currentUser = "Joanna Wu";

    DatabaseReference refBabyRootNode;
    DatabaseReference refLogRootNode;
    ActivityCounterHandle poopCounter = new ActivityCounterHandle(Constants.ActivityType.POOP);
    ActivityCounterHandle peedCounter = new ActivityCounterHandle(Constants.ActivityType.PEE);
    ActivityCounterHandle eatCounter = new ActivityCounterHandle(Constants.ActivityType.EAT);
    ActivityCounterHandle sleepCounter = new ActivityCounterHandle(Constants.ActivityType.SLEEP);
    ActivityCounterHandle wakeCounter = new ActivityCounterHandle(Constants.ActivityType.WAKE);

    long sleepLengthMs = 0;
    Constants.SleepWake sleepState = Constants.SleepWake.AWAKE;
    String sleepList = "";

    DatabaseHandle(BabyProfile babyProfile) {
        this.babyProfile = babyProfile;

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        refBabyRootNode = database.getReference(Constants.RootNodeNames.BABY.toString() + "/" + babyProfile.getBabyId() + "/");
        refLogRootNode = database.getReference(Constants.RootNodeNames.LOG.toString() + "/" + babyProfile.getBabyId() + "/");

        addLogListener();
    }

    public BabyProfile getBabyProfile() { return babyProfile; }
    public String getCurrentUser() { return currentUser; }

    public void addBabyEntry() {
        DatabaseReference usersRef = refBabyRootNode;
        usersRef.setValue(babyProfile);
    }

    public void removeLogEntry() {
        Query lastQuery = refLogRootNode.limitToLast(1);

        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                    firstChild.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Handle possible errors.
            }
        });
    }

    public void addLogEntry(Constants.ActivityType activityType) {
        long nowMs = Calendar.getInstance().getTimeInMillis();
        ActivityHandle newEntryToAdd = new ActivityHandle(activityType, currentUser, nowMs);

//        DatabaseReference usersRef = refLogRootNode.push();
        DatabaseReference usersRef = refLogRootNode.child(String.valueOf(nowMs));
        usersRef.setValue(newEntryToAdd);
    }

    public void addLogListener() {
        // Get a reference to our posts
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference logEntries = refLogRootNode;

        // Attach a listener to read the data at our posts reference
        logEntries.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                   onDataChangeFirebaseEvent(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

//        logEntries.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
//                Post newPost = dataSnapshot.getValue(Post.class);
//                System.out.println("Author: " + newPost.author);
//                System.out.println("Title: " + newPost.title);
//                System.out.println("Previous Post ID: " + prevChildKey);
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {}
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {}
//        });
    }

    public void onDataChangeFirebaseEvent(DataSnapshot dataSnapshot) {
        long latestLastReportedTime = 0;
        String latestLastReportedPerson = "";
        int counterTotalToday = 0;

        peedCounter.reset();
        poopCounter.reset();
        eatCounter.reset();
        sleepCounter.reset();
        wakeCounter.reset();

        for (DataSnapshot child: dataSnapshot.getChildren()) {
            ActivityHandle newPost = child.getValue(ActivityHandle.class);
            ActivityCounterHandle activityCounterHandle = returnHandle(newPost.getActivityType());
            activityCounterHandle.push(newPost);
        }

//        peedCounter.sortLog();
//        poopCounter.sortLog();
//        sleepCounter.sortLog();
//        wakeCounter.sortLog();

        long profileNewDayLimit = babyProfile.getNewDayTimeLong();
        peedCounter.findTotalToday(profileNewDayLimit);
        poopCounter.findTotalToday(profileNewDayLimit);
        eatCounter.findTotalToday(profileNewDayLimit);
        sleepCounter.findTotalToday(profileNewDayLimit);
        wakeCounter.findTotalToday(profileNewDayLimit);

        // perform sleep calculations
        calcSleepState();

        lbmUpdateUI(Constants.ActivityType.POOP);
    }

    public void calcSleepState() {
        if (sleepCounter.log.size() == wakeCounter.log.size()) {
            sleepState = Constants.SleepWake.AWAKE;
            calcSleepLength();
        } else if (sleepCounter.log.size() > wakeCounter.log.size()) {
            // one entry in sleep log
            sleepState = Constants.SleepWake.ASLEEP;
            calcSleepLength();
        } else {
            // there's more entries in wake than sleep. might not be done loading yet
        }
    }

    public void calcSleepLength() {
        sleepLengthMs = 0; // ms -> s -> m
        sleepList = "";
        if (sleepCounter.firstToday > -1 || sleepCounter.log.size() > wakeCounter.log.size()) {
            int startInd = sleepCounter.firstToday;

            if (sleepCounter.log.size() > wakeCounter.log.size()) {
                // currently asleep
                startInd--;

                if (sleepCounter.firstToday < 0)
                    startInd = sleepCounter.log.size() - 1;
            }

            for (int i = startInd; i < sleepCounter.log.size(); i++) {
                long startTime;
                long endTime;

                if (sleepCounter.log.get(i).getTimeMs() < babyProfile.getNewDayTimeLong())
                    startTime = babyProfile.getNewDayTimeLong();
                else
                    startTime = sleepCounter.log.get(i).getTimeMs();

                if (i > wakeCounter.log.size())
                    endTime = Calendar.getInstance().getTimeInMillis();
                else
                    endTime = wakeCounter.log.get(i).getTimeMs();

                long delta = endTime - startTime;
                sleepLengthMs = sleepLengthMs + delta;

                sleepList = sleepList +
                                Constants.dateFormatLong.format(startTime) +
                               " for " + Constants.sleepTimeFormat(delta) + "\n";
            }
        }
    }

    public ActivityCounterHandle returnHandle(Constants.ActivityType activityType) {
        switch (activityType) {
            case PEE:
                return peedCounter;

            case POOP:
                return poopCounter;

            case EAT:
                return eatCounter;

            case SLEEP:
                return sleepCounter;

            case WAKE:
                return wakeCounter;

            default:
                return null;
        }
    }

    private void lbmUpdateUI(Constants.ActivityType activityType) {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent(Constants.lbmUIRefresh);
        // You can also include some extra data.
        intent.putExtra("message", activityType.toString());
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }
}
