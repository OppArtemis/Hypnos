package com.artemis.hypnos.android;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.api.client.util.Data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
    private UserProfile userProfile;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    DatabaseReference refBabyRootNode;
    DatabaseReference refUserRootNode;
    DatabaseReference refLogRootNode;

    List<DatabaseReference> refLogSubrootNodes;
    List<DatabaseReference> refLogSessionLog;
    List<ActivityCounterHandle> babyLogCounter;

    long sleepLengthMs = 0;
    String sleepList = "";

    DatabaseHandle() {
        // connect to Firebase
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // search database for current user, insert if doesn't exist
        onLoadcheckUser();
    }

    public void onLoadcheckUser() {
        final DatabaseReference userRoot = mDatabase.getReference().child(Constants.RootNodeNames.USER2.toString());

        userRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUser user = mAuth.getCurrentUser();
                UserProfile newUserProfileToCheck = new UserProfile(user);
                String currUserId = newUserProfileToCheck.getUserId();

                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    UserProfile newPost = child.getValue(UserProfile.class);

                    if (newPost.getUserId().equals(currUserId)) {
                        userProfile = newPost;
                        break;
                    }
                }

                if (userProfile == null) {
                    userProfile = new UserProfile(mAuth.getCurrentUser());
                    BabyProfile newBabyProfile = BabyProfile.defaultTest();
                    userProfile.addBabies(newBabyProfile.getBabyId());
                    userRoot.child(userProfile.getUserId()).setValue(userProfile);
                }

                refUserRootNode = mDatabase.getReference()
                        .child(Constants.RootNodeNames.USER2.toString())
                        .child(userProfile.getUserId());

                onLoadcheckBaby();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Handle possible errors.
            }
        });
    }

    public void onLoadcheckBaby() {
        // search database for current baby, insert if doesn't exist
        final DatabaseReference babyRoot = mDatabase.getReference().child(Constants.RootNodeNames.BABY2.toString());
        babyRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String currBabyId = userProfile.getConnectedBabies().get(0);

                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    BabyProfile newPost = child.getValue(BabyProfile.class);
                    if (newPost.getBabyId().equals(currBabyId)) {
                        babyProfile = newPost;
                    }
                }

                if (babyProfile == null) {
                    babyProfile = BabyProfile.defaultTest(); // todo fix=
                    babyRoot.child(babyProfile.getBabyId()).setValue(babyProfile);
                }

                refBabyRootNode = mDatabase.getReference(Constants.RootNodeNames.BABY2.toString())
                        .child(babyProfile.getBabyId());

                refLogRootNode = mDatabase.getReference(Constants.RootNodeNames.LOG2.toString())
                        .child(babyProfile.getBabyId());

                refLogSubrootNodes = new ArrayList<>();
                refLogSessionLog = new ArrayList<>();
                babyLogCounter = new ArrayList<>();
                for (int i = 0; i < babyProfile.getLogTypes().size(); i++) {
                    ActivityLogTypes currLogType = babyProfile.getLogTypes().get(i);
                    String currLogName = currLogType.getActivityName().toString();

                    DatabaseReference currRef = mDatabase.getReference(Constants.RootNodeNames.LOG2.toString())
                            .child(babyProfile.getBabyId())
                            .child(currLogName);
                    refLogSubrootNodes.add(currRef);

                    ActivityCounterHandle currCounter = new ActivityCounterHandle(currLogType);
                    babyLogCounter.add(currCounter);

                    addLogListener(babyProfile.getLogTypes().get(i), currRef, currCounter);
                }

                lbmNewBabyProfileLoaded();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Handle possible errors.
            }
        });
    }

    public BabyProfile getBabyProfile() { return babyProfile; }

    public String getCurrentUser() { return userProfile.getUserName(); }

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

    public void addLogEntry(String activityType, long nowMs) {
        ActivityHandle newEntryToAdd = new ActivityHandle(activityType, userProfile.getUserName(), nowMs);
        DatabaseReference pathToAdd = refLogRootNode.child(activityType).child(Long.toString(nowMs));

        // check the state for this activity. if the activity is switching to state '0', then make
        // a new entry. otherwise, find the existing one and update it
        for (int i = 0; i < babyProfile.getLogTypes().size(); i++) {
            if (babyProfile.getLogTypes().get(i).getActivityName().equals(activityType.toString())) {
                babyProfile.getLogTypes().get(i).addLogEntry(newEntryToAdd, pathToAdd); // write the log
//                refBabyRootNode.setValue(babyProfile);
                // update baby state information
//                refLogSessionLog.add();
                break;
            }
        }
    }

    public void addLogListener(final ActivityLogTypes logType, final DatabaseReference logSubnode, final ActivityCounterHandle counter) {
        // Attach a listener to read the data at our posts reference
        logSubnode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                onDataChangeFirebaseEvent(dataSnapshot, logType, counter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void onDataChangeFirebaseEvent(DataSnapshot dataSnapshot, ActivityLogTypes logType, ActivityCounterHandle counter) {
        counter.reset();

        for (DataSnapshot child: dataSnapshot.getChildren()) {
            ActivityHandle newPost = child.getValue(ActivityHandle.class);
            counter.push(newPost);
        }

        long profileNewDayLimit = babyProfile.newDayTimeLong();
        counter.findTotalToday(profileNewDayLimit);

        lbmUpdateUI(logType.getActivityName());
    }

    public List<ActivityCounterHandle> getBabyLogCounter() {
        return babyLogCounter;
    }

    private void lbmNewUserProfileLoaded() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent(Constants.lbmNewUserProfileLoaded);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    private void lbmNewBabyProfileLoaded() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent(Constants.lbmNewBabyProfileLoaded);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    private void lbmUpdateUI(String activityType) {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent(Constants.lbmUIRefresh);
        // You can also include some extra data.
        intent.putExtra("message", activityType);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }
}
