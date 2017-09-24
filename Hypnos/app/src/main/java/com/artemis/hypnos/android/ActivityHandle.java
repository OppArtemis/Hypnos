package com.artemis.hypnos.android;

import java.util.Calendar;

/**
 * Created by jf2lin on 09/20/2017.
 */

public class ActivityHandle {
    public String user;
    public String time;

    ActivityHandle() {

    }

    ActivityHandle(String user, Calendar time) {
        this.user = user;
        this.time = String.valueOf(time.getTimeInMillis());
    }
}
