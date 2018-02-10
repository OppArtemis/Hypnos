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

    private List<ActivityClass> logTypes = new ArrayList<>();

    BabyProfile() {

    }

    BabyProfile(String babyName, Calendar dob) {
        this.babyName = babyName;
        this.babyDOB = dob.getTimeInMillis();

        setNewDayTime(0, 0);
        generateId();
    }

    public static BabyProfile defaultTest() {
        Calendar dob = Calendar.getInstance();
        dob.set(2017, Calendar.MAY, 3, 0, 0, 0);
        dob.set(Calendar.MILLISECOND, 0);

        BabyProfile tobias = new BabyProfile("Tobias Lin", dob);

        tobias.addUsers(new UserProfile("Jonathan Lin", "jlin815@gmail.com"));
        tobias.addUsers(new UserProfile("Joanna Wu", "jellobaby@gmail.com"));

//        ActivityClass(String activityName,
//                Integer statesPossible,
//                List<String> stateNames,
//                List<Constants.ActivityStateTypes> stateTypes,
//                Constants.TodayTotalOutputOptions todayTotalOutputOptions,
//                String notes)

        tobias.addLogTypes(new ActivityClass("PEE", 1, Arrays.asList(""),
                Arrays.asList(Constants.ActivityStateTypes.TIME_START),
                Constants.TodayTotalOutputOptions.COUNT, ""));
        tobias.addLogTypes(new ActivityClass("POOP", 1, Arrays.asList(""),
                Arrays.asList(Constants.ActivityStateTypes.TIME_START),
                Constants.TodayTotalOutputOptions.COUNT, ""));
        tobias.addLogTypes(new ActivityClass("EAT B", 1, Arrays.asList(""),
                Arrays.asList(Constants.ActivityStateTypes.TIME_START),
                Constants.TodayTotalOutputOptions.COUNT, "Breastmilk"));
        tobias.addLogTypes(new ActivityClass("EAT S", 1, Arrays.asList(""),
                Arrays.asList(Constants.ActivityStateTypes.TIME_START),
                Constants.TodayTotalOutputOptions.COUNT, "Solids"));
        tobias.addLogTypes(new ActivityClass("SLEEP", 2, Arrays.asList("ASLEEP", "AWAKE"),
                Arrays.asList(Constants.ActivityStateTypes.TIME_START, Constants.ActivityStateTypes.TIME_END),
                Constants.TodayTotalOutputOptions.DURATION, ""));
        return tobias;
    }

    public void addLogTypes(ActivityClass activityClass) {
        logTypes.add(activityClass);
    }

    public void setNewDayTime(int hour, int minute) {
        this.newDayHour = hour;
        this.newDayMinute = minute;
    }

    public void generateId() {
        String hashString = babyName + Long.toString(babyDOB);
        this.babyId = Constants.hashFunction(hashString);
    }

    public void addUsers(UserProfile userProfile) {
        users.add(userProfile.getUserId());
    }

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

    public long newDayTimeLong(Calendar newDayTime) {
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

    public List<ActivityClass> getLogTypes() {
        return logTypes;
    }
}
