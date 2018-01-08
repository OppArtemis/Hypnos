package com.artemis.hypnos.android;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
    private UserProfile userProfile;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    DatabaseReference refBabyRootNode;
    DatabaseReference refUserRootNode;
    DatabaseReference refLogRootNode;
    ActivityCounterHandle poopCounter = new ActivityCounterHandle(Constants.ActivityType.POOP);
    ActivityCounterHandle peedCounter = new ActivityCounterHandle(Constants.ActivityType.PEE);
    ActivityCounterHandle eatCounter = new ActivityCounterHandle(Constants.ActivityType.EAT);
    ActivityCounterHandle sleepCounter = new ActivityCounterHandle(Constants.ActivityType.SLEEP);
    ActivityCounterHandle wakeCounter = new ActivityCounterHandle(Constants.ActivityType.WAKE);

    long sleepLengthMs = 0;
    Constants.SleepWake sleepState = Constants.SleepWake.AWAKE;
    String sleepList = "";

    DatabaseHandle(final BabyProfile babyProfile) {
        // connect to Firebase
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

//        userProfile = new UserProfile(mAuth.getCurrentUser());

        // todo // FIXME: 01/07/2018


        // search database for current user, insert if doesn't exist
        onLoadcheckUser();







        // set up the user profile
//        userProfile = new UserProfile(mAuth.getCurrentUser());
//        userProfile.connectedBabies = (babyProfile.getBabyId());

//
//        DatabaseReference usersRef = refLogRootNode.child(String.valueOf(nowMs));
//        usersRef.setValue(newEntryToAdd);

        // setup the basic nodes if missing TODO FIX
//        mDatabase.getReference().setValue(Constants.RootNodeNames.USER2.toString());

//        mDatabase.getReference().child("test").setValue("test");
//        mDatabase.getReference().child("test2").child("test2").setValue("test2");

//        mDatabase.getReference().child(Constants.RootNodeNames.USER2.toString()).child(user.getEmail()).setValue(userProfile);
//        mDatabase.getReference()(Constants.RootNodeNames.USER2.toString() + "/" + user.getEmail() + "/").setValue(userProfile);

//        // TODO load the current logged in user's profile
//
//        // create references to the user profile
//        refUserRootNode = mDatabase.getReference(Constants.RootNodeNames.USER2.toString() + "/" + user.getEmail() + "/");
//
//        // load baby and log data if exist
//        refBabyRootNode = mDatabase.getReference(Constants.RootNodeNames.BABY2.toString() + "/" + babyProfile.getBabyId() + "/");
//        refLogRootNode = mDatabase.getReference(Constants.RootNodeNames.LOG2.toString() + "/" + babyProfile.getBabyId() + "/");
////
////        addLogListener();
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

                addLogListener();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Handle possible errors.
            }
        });
    }


//    DatabaseHandle(BabyProfile babyProfile) {
//        this.babyProfile = babyProfile;
//
//        final FirebaseDatabase database = FirebaseDatabase.getInstance();
//        refBabyRootNode = database.getReference(Constants.RootNodeNames.BABY2.toString() + "/" + babyProfile.getBabyId() + "/");
//        refUserRootNode = database.getReference(Constants.RootNodeNames.USER2.toString() + "/" + user.getBabyId() + "/");
//        refLogRootNode = database.getReference(Constants.RootNodeNames.LOG2.toString() + "/" + babyProfile.getBabyId() + "/");
//
//        addLogListener();
//
//        mAuth = FirebaseAuth.getInstance();
//        FirebaseUser user = mAuth.getCurrentUser();
//        currentUser = user.getDisplayName();
//    }

    public BabyProfile getBabyProfile() { return babyProfile; }
    public String getCurrentUser() { return userProfile.getUserName(); }

    public void addBabyEntry() {
        DatabaseReference usersRef = refBabyRootNode;
        usersRef.setValue(babyProfile);
    }

    public void addUserEntry() {

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

    public void addLogEntry(Constants.ActivityType activityType, long nowMs) {
        ActivityHandle newEntryToAdd = new ActivityHandle(activityType, userProfile.getUserName(), nowMs);

//        DatabaseReference usersRef = refLogRootNode.push();
//        DatabaseReference usersRef = refLogRootNode.child(String.valueOf(nowMs));
        DatabaseReference usersRef = refLogRootNode.child(Constants.dateFormatDate.format(nowMs)).child(activityType.name());
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

            startInd--;

            if (startInd < 0) {
                startInd = 0;
            }

            if (sleepCounter.log.size() > wakeCounter.log.size()) {
                // currently asleep

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

                if (i > wakeCounter.log.size() - 1)
                    endTime = Calendar.getInstance().getTimeInMillis();
                else
                    endTime = wakeCounter.log.get(i).getTimeMs();

                long delta = endTime - startTime;
                sleepLengthMs = sleepLengthMs + delta;

                sleepList = sleepList +
                                Constants.dateFormatDisplayLong.format(startTime) +
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
