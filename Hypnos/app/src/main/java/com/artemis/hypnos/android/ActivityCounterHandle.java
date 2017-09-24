package com.artemis.hypnos.android;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by jf2lin on 09/20/2017.
 */

public class ActivityCounterHandle {
    Constants.ActivityType currentActivityType;
    String lastTimeOccured;
    String lastTimeReportedBy;
    String todayCounter; // # of times for poo/pee, length of time in [min] for sleep

    ActivityCounterHandle(Constants.ActivityType currentActivityType) {
        this.currentActivityType = currentActivityType;
    }
}
