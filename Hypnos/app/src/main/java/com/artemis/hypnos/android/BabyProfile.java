package com.artemis.hypnos.android;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by jf2lin on 09/20/2017.
 */

public class BabyProfile {
    private String babyId;
    private String babyName;
    private long babyDOB;
    private int newDayHour;
    private int newDayMinute;

    private List<String> users = new ArrayList<>();

    private List<ActivityLogTypes> logTypes = new ArrayList<>();

    BabyProfile() {

    }

    public static BabyProfile defaultTest() {
        BabyProfile tobias = new BabyProfile();
        tobias.babyName = "Tobias Lin";

        Calendar dob = Calendar.getInstance();
        dob.set(2017, Calendar.MAY, 3, 0, 0, 0);
        dob.set(Calendar.MILLISECOND, 0);
        tobias.babyDOB = dob.getTimeInMillis();

        tobias.setNewDayTime(0, 0);

        tobias.generateId();

        tobias.addUsers("jlin815@gmail.com");
        tobias.addUsers("jellobaby@gmail.com");

        tobias.addLogTypes(new ActivityLogTypes("PEE",   1, Arrays.asList(""),    ""));
        tobias.addLogTypes(new ActivityLogTypes("POOP",  1, Arrays.asList(""),    ""));
        tobias.addLogTypes(new ActivityLogTypes("EAT B", 1, Arrays.asList(""),    "Breastmilk"));
        tobias.addLogTypes(new ActivityLogTypes("EAT S", 1, Arrays.asList(""),    "Solids"));
        tobias.addLogTypes(new ActivityLogTypes("SLEEP", 2, Arrays.asList("AWAKE", "ASLEEP"), ""));

        return tobias;
    }

    public void addLogTypes(ActivityLogTypes activityLogTypes) {
        logTypes.add(activityLogTypes);
    }

    public void setNewDayTime(int hour, int minute) {
        this.newDayHour = hour;
        this.newDayMinute = minute;
    }

    public void generateId() {
        String hashString = babyName + Long.toString(babyDOB);
        this.babyId = Constants.hashFunction(hashString);
    }

    public void addUsers(String userEmail) { users.add(userEmail); }

    public String getBabyName() {
        return babyName;
    }

    public long getBabyDOB() {
        return babyDOB;
    }

    public String babyDOBString() {
        return Constants.dateFormatLong.format(babyDOB);
    }

//    public String getNewDayTimeString() { return Constants.dateFormatLong.format(newDayTime.getTime()); }

    public long newDayTimeLong() {
        Calendar newDayTime = Calendar.getInstance();
        newDayTime.set(Calendar.HOUR_OF_DAY, newDayHour); // new day is midnight
        newDayTime.set(Calendar.MINUTE, newDayMinute); // new day is midnight
        newDayTime.set(Calendar.SECOND, 0); // new day is midnight

        return newDayTime.getTimeInMillis();
    }

    public List<String> getUsers() { return users; }

    public String getBabyId() {
        return babyId;
    }

    public int getNewDayHour() {
        return newDayHour;
    }

    public int getNewDayMinute() {
        return newDayMinute;
    }

    public List<ActivityLogTypes> getLogTypes() {
        return logTypes;
    }
}
