package com.artemis.hypnos.android;

/**
 * Created by jf2lin on 09/20/2017.
 */

public class ActivityHandle implements Comparable<ActivityHandle> {
    private String activityType;
    private String reportedUser;

    private long timeStartMs;
    private long timeEndMs;
    private String notes;

    ActivityHandle() {

    }

    ActivityHandle(String activityType, String reportedUser, long time) {
        this.activityType = activityType;
        this.reportedUser = reportedUser;
        this.timeStartMs = time;
    }

    public String getActivityType() {
        return activityType;
    }

    public String getReportedUser() { return reportedUser; }

    public long getTimeStartMs() { return timeStartMs; }

    public long getTimeEndMs() {
        return timeEndMs;
    }

    public String getNotes() {
        return notes;
    }

    public int compareTo(ActivityHandle anotherInstance) {
        long diff = this.getTimeStartMs() - anotherInstance.getTimeStartMs();
        return (int)diff;
    }

    public void setTimeEndMs(long timeEndMs) {
        this.timeEndMs = timeEndMs;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
