package com.artemis.hypnos.android;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.auth.ActionCodeResult;
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
        final DatabaseReference userRoot = mDatabase.getReference().child(Constants.RootNodeUser.toString());

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
                        .child(Constants.RootNodeUser.toString())
                        .child(userProfile.getUserId());

                onLoadcheckBaby();

//                dataMigration1();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Handle possible errors.
            }
        });
    }

    public void onLoadcheckBaby() {
        // search database for current baby, insert if doesn't exist
        final DatabaseReference babyRoot = mDatabase.getReference().child(Constants.RootNodeBaby.toString());
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

                refBabyRootNode = mDatabase.getReference(Constants.RootNodeBaby.toString())
                        .child(babyProfile.getBabyId());

                refLogRootNode = mDatabase.getReference(Constants.RootNodeLog.toString())
                        .child(babyProfile.getBabyId());

                refLogSubrootNodes = new ArrayList<>();
                refLogSessionLog = new ArrayList<>();
                for (int i = 0; i < babyProfile.getLogTypes().size(); i++) {
                    ActivityClass currLogType = babyProfile.getLogTypes().get(i);
                    String currLogName = currLogType.getActivityName().toString();

                    DatabaseReference currRef = mDatabase.getReference(Constants.RootNodeLog.toString())
                            .child(babyProfile.getBabyId())
                            .child(currLogName);
                    refLogSubrootNodes.add(currRef);

                    addLogListener(babyProfile.getLogTypes().get(i), currRef);
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
        if (refLogSessionLog.size() > 0) {
            DatabaseReference lastPath = refLogSessionLog.get(refLogSessionLog.size() - 1);
            refLogSessionLog.remove(refLogSessionLog.size() - 1);
            lastPath.removeValue();
        }
    }

    public void addLogEntry(String activityType, long currentMs, long logMs) {
        ActivityHandle newEntryToAdd = new ActivityHandle(activityType, userProfile.returnFirstName(), logMs); // which may be different than the one saved
        DatabaseReference pathToAdd = refLogRootNode.child(activityType).child(Long.toString(currentMs)); // log entry is the current one

        // check the state for this activity. if the activity is switching to state '0', then make
        // a new entry. otherwise, find the existing one and update it
        for (int i = 0; i < babyProfile.getLogTypes().size(); i++) {
            if (babyProfile.getLogTypes().get(i).getActivityName().equals(activityType.toString())) {
                DatabaseReference newPath = babyProfile.getLogTypes().get(i).addLogEntry(newEntryToAdd, pathToAdd); // write the logEntries

                // update baby state information
                refLogSessionLog.add(newPath);
                break;
            }
        }
    }

    public void addLogListener(final ActivityClass logType, final DatabaseReference logSubnode) {
        // Attach a listener to read the data at our posts reference
        logSubnode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                onDataChangeFirebaseEvent(dataSnapshot, logType);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void onDataChangeFirebaseEvent(DataSnapshot dataSnapshot, ActivityClass logType) {
        logType.logReset();

        for (DataSnapshot child: dataSnapshot.getChildren()) {
            ActivityHandle newPost = child.getValue(ActivityHandle.class);
            DatabaseReference newPath = child.getRef();
            logType.logPush(newPost, newPath);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        updateUI(calendar, logType, true);
    }

    public void updateUI(Calendar today, ActivityClass logType, boolean isToday) {
        long timePoint_Old = babyProfile.newDayTimeLong(today) - 86400000;
        long timePoint_Start = babyProfile.newDayTimeLong(today);
        long timePoint_End = babyProfile.newDayTimeLong(today) + 86400000;

        if (isToday) { // if today is the timepoint of interest, then don't have an upper bound
            timePoint_End = timePoint_End + 86400000;
        }

        logType.findTotalToday(timePoint_Old, timePoint_Start, timePoint_End);

        // update baby state
        refBabyRootNode.setValue(babyProfile);

        // update UI
        lbmUpdateUI(logType.getActivityName());
    }

    public List<ActivityClass> getActivityClasses() {
        return babyProfile.getLogTypes();
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

    public void dataMigration1() {
        DatabaseReference refLogRootNodeOld = mDatabase.getReference("LOG/bbe385bff3d7586e3789556841029ece");
        DatabaseReference refLogRootNodeNew = refLogRootNode;

        // load all the old logEntries entries
        refLogRootNodeOld.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    ActivityHandle_v1 oldPost = child.getValue(ActivityHandle_v1.class);

                    switch (oldPost.getActivityType()) {
                        case PEE:
                            addLogEntry(oldPost.getActivityType().toString(), oldPost.getTimeMs(), oldPost.getTimeMs());
                            break;

                        case POOP:
                            addLogEntry(oldPost.getActivityType().toString(), oldPost.getTimeMs(), oldPost.getTimeMs());
                            break;

                        case EAT:
                            addLogEntry("EAT B", oldPost.getTimeMs(), oldPost.getTimeMs());
                            break;

                        case SLEEP:
                            addLogEntry(oldPost.getActivityType().toString(), oldPost.getTimeMs(), oldPost.getTimeMs());
                            break;

                        case WAKE:
                            addLogEntry("SLEEP", oldPost.getTimeMs(), oldPost.getTimeMs());
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
}
