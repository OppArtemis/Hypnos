package com.artemis.hypnos.android;

import java.text.SimpleDateFormat;

/**
 * Created by jf2lin on 09/20/2017.
 */

public class Constants {
    enum ActivityType { PEE, POOP, SLEEP, WAKE }
    enum SleepWake { ASLEEP, AWAKE }
    enum RootNodeNames { BABY, LOG }

    public final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public final static String lbmUIRefresh = "lbm_ui_refresh";
}
