package com.artemis.hypnos.android;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private String babyName = "";
    private Calendar babyDOB = Calendar.getInstance();
    private String babyId = "";
    private Calendar newDayTime = Calendar.getInstance();

    public final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    private Button mBtnPooped;
    private TextView mTxtPoopedLast;
    private TextView mTxtPoopedToday;

    private TextView mTxtBabyName;
    private TextView mTxtBabyDob;
    private TextView mTxtNewDayTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        babyName = "Tobias Lin";
        babyDOB.set(2017, Calendar.MAY, 3, 0, 0, 0);
        newDayTime.set(newDayTime.get(Calendar.YEAR),
                newDayTime.get(Calendar.MONTH),
                newDayTime.get(Calendar.DAY_OF_MONTH),
                7, 0, 0); // new day is 7 am
        try {
            String hashString = babyName + ", " + dateFormat.format(babyDOB.getTime());

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

        // connect to firebase
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mBtnPooped = (Button) findViewById(R.id.btnPooped);
        mTxtPoopedLast = (TextView) findViewById(R.id.txtLastPooped);
        mTxtPoopedToday = (TextView) findViewById(R.id.txtTodayPooped);

        mTxtBabyName = (TextView) findViewById(R.id.txtBabyName);
        mTxtBabyDob = (TextView) findViewById(R.id.txtBabyDob);
        mTxtNewDayTime = (TextView) findViewById(R.id.txtNewDayTime);

        setHandles();
        setUI();





        // Get a reference to our posts
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refTimePooped = database.getReference("baby/" + babyId + "/TimePooped");

        // Attach a listener to read the data at our posts reference
        refTimePooped.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long latestTimestamp = 0;
                int counterToday = 0;

                long currentLimit = newDayTime.getTimeInMillis();

                for (DataSnapshot child: dataSnapshot.getChildren()) {
//                    Log.d("User key", child.getKey());
//                    Log.d("User ref", child.getRef().toString());
//                    Log.d("User val", child.getValue().toString());

                    long currTimeStamp = Long.parseLong((String) child.getValue());

                    if (currTimeStamp > latestTimestamp) {
                        latestTimestamp = currTimeStamp;
                    }

                    if (currTimeStamp > currentLimit) {
                        counterToday++;
                    }
                }

                Calendar poopTime = Calendar.getInstance();
                poopTime.setTimeInMillis(latestTimestamp);
                if (latestTimestamp == 0) {
                    mTxtPoopedLast.setText("Never pooped");
                } else {
                    mTxtPoopedLast.setText(dateFormat.format(poopTime.getTime()));
                }
                    mTxtPoopedToday.setText(String.valueOf(counterToday));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public String milliToString(long latestTimestamp) {
        Calendar poopTime = Calendar.getInstance();
        poopTime.setTimeInMillis(latestTimestamp);
        return dateFormat.format(poopTime.getTime());
    }

    public void setUI() {
        mTxtBabyName.setText(babyName);
        mTxtBabyDob.setText(dateFormat.format(babyDOB.getTime()));
        mTxtNewDayTime.setText(dateFormat.format(newDayTime.getTime()));
    }

    public void setHandles() {
        mBtnPooped.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEntryPoop();
            }
        });
    }

    private void addEntryPoop(){
//        Date currentTime = Calendar.getInstance().getTime();
        long currentTimeMs = Calendar.getInstance().getTimeInMillis();

        mDatabase.child("baby").
                child(babyId).
                child("TimePooped").
                push().
                setValue(String.valueOf(currentTimeMs));
    }

    private void getLastPoop() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("baby/" + babyId + "saving-data/fireblog/posts");
    }
}
