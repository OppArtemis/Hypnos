package com.artemis.hypnos.android;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Created by jf2lin on 09/20/2017.
 */

public class ActivityCounterHandle {
    ActivityLogTypes currentActivityType;
    List<ActivityHandle> log = new ArrayList<>();

    long totalToday; // # of times for poo/pee, length of reportedTimeMs in [min] for sleep
    int firstToday;

    String notes;

    ActivityCounterHandle(ActivityLogTypes currentActivityType) {
        this.currentActivityType = currentActivityType;
    }

    public void reset() {
        log = new ArrayList<>();
        totalToday = 0;
        firstToday = -1;
    }

    public void push(ActivityHandle newActivityHandle) {
        log.add(newActivityHandle);
        sortLog();
    }

    public void pop(int ind) {
        log.remove(ind);
    }

    public void findTotalToday(long todayStartTimeLong) {
        switch (currentActivityType.getTodayTotalOutputOptions()) {
            case COUNT:
                findTotalToday_Count(todayStartTimeLong);
                break;

            case DURATION:
                findTotalToday_Duration(todayStartTimeLong);
                break;
        }
    }

    public void findTotalToday_Count(long todayStartTimeLong) {
        long profileNewDayLimit = todayStartTimeLong;
        totalToday = 0;
        firstToday = -1;

        for (int i = 0; i < log.size(); i++) {
            ActivityHandle newPost = log.get(i);
            long currentLastReportedTime = newPost.getTimeStartMs();

            if (currentLastReportedTime > profileNewDayLimit) {
                if (totalToday == 0) {
                    firstToday = i;
                }

                totalToday++;
            }
        }
    }

    public void findTotalToday_Duration(long todayStartTimeLong) {
        long profileNewDayLimit = todayStartTimeLong;
        totalToday = 0;
        firstToday = -1;
        notes = "";

        for (int i = 0; i < log.size(); i++) {
            ActivityHandle newPost = log.get(i);

            // todo debugging
            String logPivot = Constants.dateFormatDisplayDebug.format(profileNewDayLimit);
            String logStart = Constants.dateFormatDisplayDebug.format(newPost.getTimeStartMs());
            String logEnd = Constants.dateFormatDisplayDebug.format(newPost.getTimeEndMs());

            long startTime = newPost.getTimeStartMs();
            long endTime = newPost.getTimeEndMs();

            if (newPost.getTimeEndMs() != 0 && newPost.getTimeEndMs() < profileNewDayLimit) {
                // anything that ends before profileNewDayLimit -> don't want
                continue;
            }
            else {
                if (newPost.getTimeEndMs() != 0 && newPost.getTimeEndMs() > profileNewDayLimit) {
                    // if start and end after profileNewDayLimit -> keep
//                    endTime = newPost.getTimeEndMs(); (default option)
                }
                else { // (newPost.getTimeEndMs() == 0)
                    endTime = Calendar.getInstance().getTimeInMillis();
                }
            }

            long delta = endTime - startTime;

            if (delta < 0) {
                // something is very wrong. the log entries is incorrect
                Log.d(Constants.appName, "Inproper date format in log.");
                continue;
            }

            totalToday = totalToday + delta;

            notes = notes +
                    Constants.dateFormatDisplayLong.format(startTime) +
                    " for " + Constants.sleepTimeFormat(delta) + "\n";
        }
    }

    public String getLastReportedTime() {
        if (log.size() == 0) {
            return "Never";
        } else {
            String output1 = Constants.dateFormatDisplayLong.format(log.get(log.size()-1).getTimeStartMs());
            long timeElapsed = Calendar.getInstance().getTimeInMillis() - log.get(log.size()-1).getTimeStartMs();
            String output2 = "(" + Constants.formatInterval(timeElapsed) + " ago)";
            return output1 + " " + output2;
//            return output1;
        }
    }

    public String getLastReportedPerson() {
        if (log.size() == 0) {
            return "";
        } else {
            return log.get(log.size()-1).getReportedUser();
        }
    }

    public String getTotalToday() {
        switch (currentActivityType.getTodayTotalOutputOptions()) {
            case COUNT:
                return String.valueOf(totalToday);

            case DURATION:
                return Constants.sleepTimeFormat(totalToday);

            default:
                return "";
        }
    }

    public void sortLog() {
        Collections.sort(log);
    }

    public String getNotes() {
        return notes;
    }

    public String getCurrentState() {
        return currentActivityType.currentStateName();
    }
}
