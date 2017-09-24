package com.artemis.hypnos.android;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

/**
 * Created by jf2lin on 09/20/2017.
 */

public class BabyProfile {
    private String babyName = "";
    private Calendar babyDOB = Calendar.getInstance();
    private String babyId = "";
    private Calendar newDayTime = Calendar.getInstance();

    BabyProfile() {
        babyName = "Tobias Lin";
        babyDOB.set(2017, Calendar.MAY, 3, 0, 0, 0);
        newDayTime.set(newDayTime.get(Calendar.YEAR),
                newDayTime.get(Calendar.MONTH),
                newDayTime.get(Calendar.DAY_OF_MONTH),
                7, 0, 0); // new day is 7 am

        try {
            String hashString = babyName + ", " + Constants.dateFormat.format(babyDOB.getTime());

            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(hashString.getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

            babyId = sb.toString();
            System.out.println("Digest(in hex format):: " + babyId);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String getBabyName() {
        return babyName;
    }

    public String getBabyDOB() {
        return Constants.dateFormat.format(babyDOB.getTime());
    }

    public String getNewDayTimeString() {
        return Constants.dateFormat.format(newDayTime.getTime());
    }

    public long getNewDayTimeLong() {
        return newDayTime.getTimeInMillis();
    }

    public String getBabyId() {
        return babyId;
    }
}
