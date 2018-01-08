package com.artemis.hypnos.android;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by jf2lin on 09/20/2017.
 */

public class BabyProfile {
    private String babyName;
    private Calendar babyDOB = Calendar.getInstance();
    private Calendar newDayTime = Calendar.getInstance();
    private int newDayHour;
    private int newDayMinute;
    private String babyId;
    private List<String> users = new ArrayList<>();

    BabyProfile() {
        generateId();
    }

    public static BabyProfile defaultTest() {
        BabyProfile tobias = new BabyProfile();
        tobias.babyName = "Tobias Lin";
        tobias.babyDOB.set(2017, Calendar.MAY, 3, 0, 0, 0);
        tobias.setNewDayTime(0, 0);
        tobias.generateId();

        tobias.addUsers("jlin815@gmail.com");
        tobias.addUsers("jellobaby@gmail.com");

        return tobias;
    }

    public void setNewDayTime(int hour, int minute) {
        this.newDayHour = hour;
        this.newDayMinute = minute;
        this.newDayTime.set(Calendar.HOUR_OF_DAY, newDayHour); // new day is midnight
        this.newDayTime.set(Calendar.MINUTE, minute); // new day is midnight
        this.newDayTime.set(Calendar.SECOND, 0); // new day is midnight
    }

    public void generateId() {
        String hashString = babyName + Constants.dateFormatLong.format(babyDOB.getTime());
        this.babyId = Constants.hashFunction(hashString);
    }

    public void addUsers(String userEmail) { users.add(userEmail); }

    public String getBabyName() {
        return babyName;
    }

    public String getBabyDOB() {
        return Constants.dateFormatLong.format(babyDOB.getTime());
    }

    public String getNewDayTimeString() { return Constants.dateFormatLong.format(newDayTime.getTime()); }

    public long getNewDayTimeLong() {
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
}
