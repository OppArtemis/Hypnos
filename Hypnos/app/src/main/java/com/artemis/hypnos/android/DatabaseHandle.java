package com.artemis.hypnos.android;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private BabyProfile babyProfile;
    private String userName = "Jonathan Lin";

    DatabaseReference refLogRootNode;
    ActivityCounterHandle poopCounter = new ActivityCounterHandle(Constants.ActivityType.POOPED);

    DatabaseHandle(BabyProfile babyProfile) {
        this.babyProfile = babyProfile;

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        refLogRootNode = database.getReference(Constants.RootNodeNames.LOG.toString() + "/" + babyProfile.getBabyId() + "/");

        addListeners(Constants.ActivityType.POOPED);
    }

    public void addEntry(BabyProfile babyProfile, Constants.ActivityType activityType) {
        ActivityHandle newEntryToAdd = new ActivityHandle(userName, Calendar.getInstance());

        DatabaseReference usersRef = refLogRootNode.child(activityType.toString()).push();
        usersRef.setValue(newEntryToAdd);
    }

    public void addListeners(Constants.ActivityType activityType) {
        // Get a reference to our posts
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refTimePooped = refLogRootNode.child(activityType.toString());

        // Attach a listener to read the data at our posts reference
        refTimePooped.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               onDataChangeFirebaseEvent(dataSnapshot, Constants.ActivityType.POOPED);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void onDataChangeFirebaseEvent(DataSnapshot dataSnapshot, Constants.ActivityType activityType) {
        long latestTimestamp = 0;
        String latestReported = "";
        int counterToday = 0;

        long currentLimit = babyProfile.getNewDayTimeLong();

        for (DataSnapshot child: dataSnapshot.getChildren()) {
            ActivityHandle newPost = child.getValue(ActivityHandle.class);
//                    Log.d("User key", child.getKey());
//                    Log.d("User ref", child.getRef().toString());
//                    Log.d("User val", child.getValue().toString());

            long currTimeStamp = Long.parseLong(newPost.time);

            if (currTimeStamp > latestTimestamp) {
                latestTimestamp = currTimeStamp;
                latestReported = newPost.user;
            }

            if (currTimeStamp > currentLimit) {
                counterToday++;
            }
        }

        Calendar poopTime = Calendar.getInstance();
        poopTime.setTimeInMillis(latestTimestamp);
        if (latestTimestamp == 0) {
            poopCounter.lastTimeOccured = "Never pooped";
        } else {
            poopCounter.lastTimeOccured = Constants.dateFormat.format(poopTime.getTime());
            poopCounter.lastTimeReportedBy = latestReported;
        }
        poopCounter.todayCounter = String.valueOf(counterToday);

        lbmUpdateUI();
    }

    private void lbmUpdateUI() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent(Constants.lbmUIRefresh);
        // You can also include some extra data.
//        intent.putExtra("message", "This is my message!");
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }
}
