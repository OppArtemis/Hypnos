package com.artemis.hypnos.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jf2lin on 09/20/2017.
 */

public class ActivityCounterHandle {
    String currentActivityType;
    List<ActivityHandle> log = new ArrayList<>();

    int totalToday; // # of times for poo/pee, length of reportedTimeMs in [min] for sleep
    int firstToday;

    ActivityCounterHandle(String currentActivityType) {
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

    public String getLastReportedTime() {
        if (log.size() == 0) {
            return "Never";
        } else {
            return Constants.dateFormatDisplayLong.format(log.get(log.size()-1).getTimeStartMs());
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
        return String.valueOf(totalToday);
    }

    public void sortLog() {
        Collections.sort(log);
    }
}
