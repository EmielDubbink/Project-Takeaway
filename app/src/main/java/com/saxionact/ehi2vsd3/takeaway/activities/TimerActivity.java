package com.saxionact.ehi2vsd3.takeaway.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.NumberPicker;

import com.saxionact.ehi2vsd3.takeaway.R;
import com.saxionact.ehi2vsd3.takeaway.databases.Database;
import com.saxionact.ehi2vsd3.takeaway.dialogs.PauzeDialog;

import java.util.Calendar;
import java.util.Date;

/**
 * This activity keeps a timer that can be used to fill in you work time for that day.
 *
 * @author Vincnent Witten
 */
public class TimerActivity extends AppCompatActivity {

    //views instances
    private Chronometer chronometer;
    private Button btnStartStop;

    //project and user ID
    private String projectID;
    private String userID;

    //begin and end date
    private Date beginDate = null;
    private Date endDate = null;

    //variables to keep track of the timer
    private boolean isStart;
    private long timeWhenStopped = 0;

    /**
     * All the views and variables get initialized as well as the listeners.
     *
     * @author Vincent Witten
     *
     * @param savedInstanceState contains the savedInstanceState
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        Intent intent = getIntent();
        if (intent != null) {
            projectID =  intent.getStringExtra("PROJECT_ID");
            userID = intent.getStringExtra("USER_ID");
        }

        //find views
        chronometer = findViewById(R.id.chronometer);
        btnStartStop = findViewById(R.id.btnStartStop);

        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            /**
             * Sets the correct time for the chronometer.
             *
             * @author Vincent Witten
             *
             * @param chronometerChanged contains the chronometer that has changed.
             */
            @Override
            public void onChronometerTick(Chronometer chronometerChanged) {
                long time = SystemClock.elapsedRealtime() - chronometer.getBase();
                int h   = (int)(time /3600000);
                int m = (int)(time - h*3600000)/60000;
                int s= (int)(time - h*3600000- m*60000)/1000 ;
                String t = (h < 10 ? "0"+h: h)+":"+(m < 10 ? "0"+m: m)+":"+ (s < 10 ? "0"+s: s);
                chronometer.setText(t);
            }
        });
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.setText("00:00:00");

        btnStartStop.setOnClickListener(new View.OnClickListener() {
            /**
             * Calls the startStopChronometer method when clicked.
             *
             * @author Vincent Witten
             *
             * @param v contains the view that was clicked.
             */
            @Override
            public void onClick(View v) {
                startStopChronometer(v);
            }
        });
    }

    /**
     * Start and stops the timer.
     *
     * @author Vincent Witten
     * 
     * @param view contains the button view.
     */
    public void startStopChronometer(View view){
        if(isStart){
            endDate = Calendar.getInstance().getTime();
            timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
            chronometer.stop();
            isStart = false;
            ((Button)view).setText(R.string.start);

            Intent intent = new Intent(TimerActivity.this, AddingHoursActivity.class);
            intent.putExtra("BEGIN_DATE", beginDate);
            intent.putExtra("END_DATE", endDate);
            intent.putExtra("PROJECT_ID", projectID);
            intent.putExtra("USER_ID", userID);
            finish();
            startActivity(intent);
        }else{
            if (beginDate == null){
                beginDate = Calendar.getInstance().getTime();
            }
            chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
            chronometer.start();
            isStart = true;
            ((Button)view).setText(R.string.stop);
        }
    }
}
