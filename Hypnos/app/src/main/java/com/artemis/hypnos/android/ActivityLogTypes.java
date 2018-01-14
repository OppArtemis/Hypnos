package com.artemis.hypnos.android;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jf2lin on 01/08/2018.
 */

public class ActivityLogTypes {
    private String activityName;
    private String notes;
    private Constants.TodayTotalOutputOptions todayTotalOutputOptions;

    private Integer statesPossible;
    private List<String> stateNames;

    private boolean flagTimeEnd;
    private boolean flagTimePicker;

    private Integer stateCurrent;
    private DatabaseReference lastPath;
    private ActivityHandle lastEntry;

    ActivityLogTypes() {

    }

    ActivityLogTypes(String activityName, Integer statesPossible, List<String> stateNames,
                     Constants.TodayTotalOutputOptions todayTotalOutputOptions, String notes) {
        this.activityName = activityName;
        this.statesPossible = statesPossible;
        this.stateNames = stateNames;
        this.todayTotalOutputOptions = todayTotalOutputOptions;
        this.notes = notes;

        stateCurrent = 0;
        lastPath = null;
    }

    public void addLogEntry(ActivityHandle activityHandle, DatabaseReference path) {
        if (stateCurrent == 0) {
            // new entry
            path.setValue(activityHandle);
            lastEntry = activityHandle;
            lastPath = path;
        } else {
            // update entry
            lastEntry.setTimeEndMs(activityHandle.getTimeStartMs());
            lastPath.setValue(lastEntry);
        }

        incrementState();
    }

    public void removeLastEntry() {

    }

    public void incrementState() {
        stateCurrent++;

        if (stateCurrent > statesPossible-1) {
            stateCurrent = 0;
        }
    }

    public ActivityHandle getLastEntry() {
        return lastEntry;
    }

    public DatabaseReference getLastPath() {
        return lastPath;
    }

    public int getStateCurrent() {
        return stateCurrent;
    }

    public Integer getStatesPossible() {
        return statesPossible;
    }

    public String getActivityName() {
        return activityName;
    }

    public String getNotes() {
        return notes;
    }

    public List<String> getStateNames() {
        return stateNames;
    }

    public String currentStateName() {
        return stateNames.get(stateCurrent);
    }

    public boolean isFlagTimeEnd() {
        return flagTimeEnd;
    }

    public boolean isFlagTimePicker() {
        return flagTimePicker;
    }

    public Constants.TodayTotalOutputOptions getTodayTotalOutputOptions() {
        return todayTotalOutputOptions;
    }
}
