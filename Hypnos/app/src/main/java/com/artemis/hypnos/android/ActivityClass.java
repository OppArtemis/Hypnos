package com.artemis.hypnos.android;

import android.util.Log;

import com.google.api.client.util.Data;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Created by jf2lin on 01/08/2018.
 */

public class ActivityClass {
    private String activityName;
    private String notes;
    private Constants.TodayTotalOutputOptions todayTotalOutputOptions;

    private Integer statesPossible;
    private List<String> stateNames;
//    private List<Constants.ActivityStateTypes> stateTypes;

    private boolean flagTimePicker;


    List<ActivityHandle> logEntries = new ArrayList<>();
    List<DatabaseReference> logPaths = new ArrayList<>();

    long logTotalToday; // # of times for poo/pee, length of reportedTimeMs in [min] for sleep
    String logNotes;

    ActivityClass() {

    }

    ActivityClass(String activityName,
                  Integer statesPossible,
                  List<String> stateNames,
                  List<Constants.ActivityStateTypes> stateTypes,
                  Constants.TodayTotalOutputOptions todayTotalOutputOptions,
                  String notes) {
        this.activityName = activityName;
        this.statesPossible = statesPossible;
        this.stateNames = stateNames;
//        this.stateTypes = stateTypes;
        this.todayTotalOutputOptions = todayTotalOutputOptions;
        this.notes = notes;
    }

    public DatabaseReference addLogEntry(ActivityHandle activityHandle, DatabaseReference path) {
        if (logEntries.size() == 0) {
            // no existing entry, so this entry must be new
            return addNewEntry(activityHandle, path);
        } else if (statesPossible == 1) {
            // only new entries are possible, so this entry must be new
            return addNewEntry(activityHandle, path);
        } else if (statesPossible > 1 && checkState(retrieveLatestLogEntry()) == 1) {
            return addNewEntry(activityHandle, path);
        } else if (statesPossible > 1 && checkState(retrieveLatestLogEntry()) == 0) {
            // update entry
            return updateExistingEntry(activityHandle, path);
        }

        return null; // unaccounted case
    }

    public ActivityHandle retrieveLatestLogEntry() {
        return logEntries.get(logEntries.size() - 1);
    }

    public DatabaseReference retrieveLatestLogPath() {
        return logPaths.get(logPaths.size() - 1);
    }

    public DatabaseReference addNewEntry(ActivityHandle activityHandle, DatabaseReference path) {
//        logPaths.add(path);
//        logEntries.add(activityHandle);

        path.setValue(activityHandle);
        return path;
    }

    public DatabaseReference updateExistingEntry(ActivityHandle activityHandle, DatabaseReference path) {
        ActivityHandle lastEntry = retrieveLatestLogEntry();
        lastEntry.setTimeEndMs(activityHandle.getTimeStartMs());

        DatabaseReference lastPath = retrieveLatestLogPath();
        lastPath.setValue(lastEntry);

        return lastPath;
    }


    public void removeLastEntry() {

    }

    public void checkLastState(ActivityClass counter) {
        if (counter != null) {

        }
    }

    public Integer checkState(ActivityHandle activityHandle) {
        if (activityHandle.getTimeEndMs() == 0) {
            return 0;
        } else {
            return 1;
        }
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

    public boolean isFlagTimePicker() {
        return flagTimePicker;
    }

    public Constants.TodayTotalOutputOptions getTodayTotalOutputOptions() {
        return todayTotalOutputOptions;
    }

   // COUNTER FUNCTIONS

    public void logReset() {
        logEntries = new ArrayList<>();
        logTotalToday = 0;
    }

    public void logPush(ActivityHandle newActivityHandle, DatabaseReference newPath) {
        logEntries.add(newActivityHandle);
        logPaths.add(newPath);
//        sortLog();
    }

    public void logPop(int ind) {
        logEntries.remove(ind);
    }

    public void findTotalToday(long yesterdayStartTimeLong, long todayStartTimeLong, long tomorrowStartTimeLong) {
        switch (getTodayTotalOutputOptions()) {
            case COUNT:
                findTotalToday_Count(yesterdayStartTimeLong, todayStartTimeLong, tomorrowStartTimeLong);
                break;

            case DURATION:
                findTotalToday_Duration(yesterdayStartTimeLong, todayStartTimeLong, tomorrowStartTimeLong);
                break;
        }
    }

    public void findTotalToday_Count(long yesterdayStartTimeLong, long todayStartTimeLong, long tomorrowStartTimeLong) {
        logTotalToday = 0;

        for (int i = 0; i < logEntries.size(); i++) {
            ActivityHandle newPost = logEntries.get(i);
            long currentLastReportedTime = newPost.getTimeStartMs();

            if (currentLastReportedTime > todayStartTimeLong && currentLastReportedTime < tomorrowStartTimeLong) {

                logTotalToday++;
            }
        }
    }

    public void findTotalToday_Duration(long yesterdayStartTimeLong, long todayStartTimeLong, long tomorrowStartTimeLong) {
        long profileNewDayLimit = todayStartTimeLong;
        logTotalToday = 0;
        logNotes = "";

        for (int i = 0; i < logEntries.size(); i++) {
            ActivityHandle newPost = logEntries.get(i);

            // todo debugging
            String logPivot1 = Constants.dateFormatDisplayDebug.format(todayStartTimeLong);
            String logPivot2 = Constants.dateFormatDisplayDebug.format(tomorrowStartTimeLong);
            String logStart = Constants.dateFormatDisplayDebug.format(newPost.getTimeStartMs());
            String logEnd = Constants.dateFormatDisplayDebug.format(newPost.getTimeEndMs());

            long startTime = newPost.getTimeStartMs();
            long endTime = newPost.getTimeEndMs();

            if (endTime == 0) {
                endTime = Calendar.getInstance().getTimeInMillis();
            }

            // decide which times to keep
            if (startTime < yesterdayStartTimeLong) {
                // the entry is more than a day old. definitely don't keep
                continue;
            }
            else if (startTime < todayStartTimeLong) {
                if (endTime < todayStartTimeLong) {
                    // don't keep
                    continue;
                } else if (endTime > todayStartTimeLong &&
                        endTime < tomorrowStartTimeLong) {
                    // keep
                } else if (endTime > tomorrowStartTimeLong) {
                    // keep
                }
            } else if (startTime > todayStartTimeLong &&
                    startTime < tomorrowStartTimeLong) {
                if (endTime < todayStartTimeLong) {
                    // error
                    continue;
                } else if (endTime > todayStartTimeLong &&
                        endTime < tomorrowStartTimeLong) {
                    // keep
                } else if (endTime > tomorrowStartTimeLong) {
                    // keep
                }
            } else if (startTime > tomorrowStartTimeLong) {
                if (endTime < todayStartTimeLong) {
                    // error
                    continue;
                } else if (endTime > todayStartTimeLong &&
                        endTime < tomorrowStartTimeLong) {
                    //error
                    continue;
                } else if (endTime > tomorrowStartTimeLong) {
                    // don't keep
                    continue;
                }
            }

            long delta = endTime - startTime;

            if (delta < 0) {
                // something is very wrong. the logEntries entries is incorrect
                Log.d(Constants.appName, "Improper date format in logEntries.");
                continue;
            }

            logTotalToday = logTotalToday + delta;

            logNotes = logNotes +
                    Constants.dateFormatDisplayLong.format(startTime) +
                    " for " + Constants.sleepTimeFormat(delta) +
                    " (" + stateNames.get(checkState(newPost)) + ")" +
                    "\n";
        }
    }

    public String retrieveLastReportedTime() {
        if (logEntries.size() == 0) {
            return "Never";
        } else {
            String output1 = Constants.dateFormatDisplayLong.format(logEntries.get(logEntries.size()-1).getTimeStartMs());
            long timeElapsed = Calendar.getInstance().getTimeInMillis() - logEntries.get(logEntries.size()-1).getTimeStartMs();
            String output2 = "(" + Constants.formatInterval(timeElapsed) + " ago)";
            return output1 + " " + output2;
//            return output1;
        }
    }

    public String retrieveLastReportedPerson() {
        if (logEntries.size() == 0) {
            return "";
        } else {
            return logEntries.get(logEntries.size()-1).getReportedUser();
        }
    }

    public String retrieveLogTotalToday() {
        switch (getTodayTotalOutputOptions()) {
            case COUNT:
                return String.valueOf(logTotalToday);

            case DURATION:
                return Constants.sleepTimeFormat(logTotalToday);

            default:
                return "";
        }
    }

    public String retrieveLogNotes() {
        return logNotes;
    }

    public void sortLog() {
        Collections.sort(logEntries);
    }
}
