package com.artemis.hypnos.android;

import java.util.Calendar;

/**
 * Created by jf2lin on 09/20/2017.
 */

public class ActivityHandle implements Comparable<ActivityHandle> {
    private Constants.ActivityType activityType;
    private String user;
    private long timeMs;

    ActivityHandle() {

    }

    ActivityHandle(Constants.ActivityType activityType, String user, long time) {
        this.activityType = activityType;
        this.user = user;
        this.timeMs = time;
    }

    public Constants.ActivityType getActivityType() { return activityType; }

    public String getUser() { return user; }

    public long getTimeMs() { return timeMs; }

    public int compareTo(ActivityHandle anotherInstance) {
        long diff = this.getTimeMs() - anotherInstance.getTimeMs();
        return (int)diff;
    }
}
