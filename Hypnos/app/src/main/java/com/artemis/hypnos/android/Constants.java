package com.artemis.hypnos.android;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by jf2lin on 09/20/2017.
 */

public class Constants {
    public final static String appName = "hyp";

    enum ActivityOptionalParams { DURATION, NOTES }
    enum RootNodeNames {BABY2, LOG2, USER2}
    enum TodayTotalOutputOptions { COUNT, DURATION }

    public final static String RootNodeBaby = "BABY2";
    public final static String RootNodeLog = "LOG2";
    public final static String RootNodeUser = "USER2";

    public final static SimpleDateFormat dateFormatDate = new SimpleDateFormat("yyyy-MM-dd");
    public final static SimpleDateFormat dateFormatLong = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public final static SimpleDateFormat dateFormatDisplayLong = new SimpleDateFormat("MMM dd HH:mm");
    public final static SimpleDateFormat dateFormatDisplayShort = new SimpleDateFormat("HH:mm");
    public final static SimpleDateFormat dateFormatDisplayDebug = new SimpleDateFormat("yyyy MMM dd HH:mm:ss.SSS");

    public final static String lbmNewBabyProfileLoaded = "lbm_newbabyprofileloaded";
    public final static String lbmNewUserProfileLoaded = "lbm_newuserprofileloaded";
    public final static String lbmUIRefresh = "lbm_uirefresh";

    public static String sleepTimeFormat(long sleepLengthMs) {
        String out;

        Calendar newTime = Calendar.getInstance();
        newTime.set(1970, Calendar.JANUARY, 1, 0, 0, 0);
        long baseTime = newTime.getTimeInMillis() + sleepLengthMs;
        out = Constants.dateFormatDisplayShort.format(baseTime);

        return out;
    }

    public static String hashFunction(String hashString) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(hashString.getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String formatInterval(final long l)
    {
        final long hr = TimeUnit.MILLISECONDS.toHours(l);
        final long min = TimeUnit.MILLISECONDS.toMinutes(l - TimeUnit.HOURS.toMillis(hr));
        return String.format("%02d:%02d", hr, min);
    }
}
