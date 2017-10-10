package com.artemis.hypnos.android;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by jf2lin on 09/20/2017.
 */

public class Constants {
    enum ActivityType { PEE, POOP, EAT, SLEEP, WAKE }
    enum SleepWake { ASLEEP, AWAKE }
    enum RootNodeNames { BABY, LOG, USER }

    public final static SimpleDateFormat dateFormatLong = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public final static SimpleDateFormat dateFormatShort = new SimpleDateFormat("HH:mm:ss");

    public final static String lbmUIRefresh = "lbm_ui_refresh";

    public static String sleepTimeFormat(long sleepLengthMs) {
        String out;

        Calendar newTime = Calendar.getInstance();
        newTime.set(1970, Calendar.JANUARY, 1, 0, 0, 0);
        long baseTime = newTime.getTimeInMillis() + sleepLengthMs;
        out = Constants.dateFormatShort.format(baseTime);

        return out;
    }
}
