package com.artemis.hypnos.android;

import java.util.Calendar;

/**
 * Created by jf2lin on 01/16/2018.
 */

public class ActivityHandle_v1 {
    enum ActivityType { PEE, POOP, EAT, SLEEP, WAKE }
    private ActivityType activityType;
    private String user;
    private long timeMs;

    ActivityHandle_v1() {

    }

    ActivityHandle_v1(ActivityType activityType, String user, long time) {
        this.activityType = activityType;
        this.user = user;
        this.timeMs = time;
    }

    public ActivityType getActivityType() { return activityType; }

    public String getUser() { return user; }

    public long getTimeMs() { return timeMs; }

    public int compareTo(ActivityHandle_v1 anotherInstance) {
        long diff = this.getTimeMs() - anotherInstance.getTimeMs();
        return (int)diff;
    }
}
