package com.artemis.hypnos.android;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by jf2lin on 10/21/2017.
 */

public class SetTime implements View.OnFocusChangeListener, TimePickerDialog.OnTimeSetListener {

//    private long timeMs;
//    private Calendar myCalendar;
//    private Context ctx;
//
//    public SetTime(Context ctx){
//        this.myCalendar = Calendar.getInstance();
//        this.ctx = ctx;
//    }
//
//    public long getTimeStartMs() {
//        return timeMs;
//    }
//
//    @Override
//    public void onFocusChange(View v, boolean hasFocus) {
//        // TODO Auto-generated method stub
//        if(hasFocus){
//            int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
//            int minute = myCalendar.get(Calendar.MINUTE);
//            new TimePickerDialog(ctx, this, hour, minute, true).show();
//        }
//    }
//
//    @Override
//    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//        // TODO Auto-generated method stub
//        Calendar newCal = Calendar.getInstance();
//        newCal.set(Calendar.HOUR_OF_DAY, hourOfDay); // new day is midnight
//        newCal.set(Calendar.MINUTE, minute); // new day is midnight
//        newCal.set(Calendar.SECOND, 0); // new day is midnight
//
////        myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay); // new day is midnight
////        myCalendar.set(Calendar.MINUTE, minute); // new day is midnight
////        myCalendar.set(Calendar.SECOND, 0); // new day is midnight
//
//        timeMs = newCal.getTimeInMillis();
//    }

    private Button editText;
    private Calendar myCalendar;
    private Context ctx;

    public SetTime(Button editText, Context ctx){
        this.editText = editText;
        this.editText.setOnFocusChangeListener(this);
        this.myCalendar = Calendar.getInstance();
        this.ctx = ctx;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        // TODO Auto-generated method stub
        if(hasFocus){
            int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
            int minute = myCalendar.get(Calendar.MINUTE);
            new TimePickerDialog(ctx, this, hour, minute, true).show();
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // TODO Auto-generated method stub
        this.editText.setText( hourOfDay + ":" + minute);
    }
}
