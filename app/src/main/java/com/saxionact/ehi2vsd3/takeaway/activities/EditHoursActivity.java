package com.saxionact.ehi2vsd3.takeaway.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.saxionact.ehi2vsd3.takeaway.R;
import com.saxionact.ehi2vsd3.takeaway.databases.Database;
import com.saxionact.ehi2vsd3.takeaway.dialogs.PauzeDialog;
import com.saxionact.ehi2vsd3.takeaway.models.WorkTime;

import java.util.Calendar;
import java.util.Date;

/**
 * EditHoursActivity for edit hours (WorkTimes) on projects.
 *
 * @author Boris Oortwijn
 */
public class EditHoursActivity extends AppCompatActivity {

    // View containers and variables
    private TextView tv_datePicker;
    private TextView tv_beginTime;
    private TextView tv_endTime;
    private TextView tv_description;

    private Button btn_saveWorkedHours;

    private Calendar calendar;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    private Date beginDate = new Date();
    private Date endDate = new Date();

    private String projectID;
    private String userID;
    private String description;
    private String worktimeID;
    
    private boolean descriptionFilled = true;

    private int time; // in minutes rounded by 5

    private long date;

    // Dialog views
    private NumberPicker npHours;
    private NumberPicker npMinutes;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_hours);

        // Get intent and set the variables of in the created or edited state, like setting the
        // date of creation, or setting the beginning hours and minutes.
        Intent intent = getIntent();
        if (intent != null) {
            worktimeID = intent.getStringExtra("WORKTIME_ID");
            projectID = intent.getStringExtra("PROJECT_ID");
            userID = intent.getStringExtra("USER_ID");
            description = intent.getStringExtra("DESCRIPTION");
            beginDate.setTime(intent.getLongExtra("BEGIN_DATE", 0));
            endDate.setTime(intent.getLongExtra("END_DATE", 0));
        }

        // Finding views from activity_adding_hours.xml
        tv_datePicker = findViewById(R.id.tv_datePicker);
        tv_beginTime = findViewById(R.id.tv_beginTime);
        tv_endTime = findViewById(R.id.tv_endTime);
        tv_description = findViewById(R.id.tv_description);

        btn_saveWorkedHours = findViewById(R.id.btn_saveWorkedHours);
        btn_saveWorkedHours.setVisibility(View.VISIBLE);

        tv_datePicker.setText(beginDate.getDate() + " - " + (beginDate.getMonth() + 1) + " - " + (beginDate.getYear() + 1900));
        tv_beginTime.setText(checkTimeForMakeUp(beginDate.getHours(), beginDate.getMinutes()));
        tv_endTime.setText(checkTimeForMakeUp(endDate.getHours(), endDate.getMinutes()));
        tv_description.setText(description);

        /**
         * OnClickListener for adding a date.
         */
        tv_datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();

                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                /**
                 * Date picker dialog for adding a date.
                 * (And for setting the text from the TextView).
                 */
                datePickerDialog = new DatePickerDialog(EditHoursActivity.this, R.style.DatePicker, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int nYear, int nMonth, int nDay) {
                        tv_datePicker.setText(nDay + " - " + (nMonth + 1) + " - " + nYear);

                        if (beginDate == null) {
                            beginDate = new Date();
                        }
                        beginDate.setYear(nYear - 1900);
                        beginDate.setMonth(nMonth);
                        beginDate.setDate(nDay);

                        if (endDate == null) {
                            endDate = new Date();
                        }
                        endDate.setYear(nYear - 1900);
                        endDate.setMonth(nMonth);
                        endDate.setDate(nDay);

                        checkDate();
                    }
                }, year, month, day);

                datePickerDialog.show();
            }
        });

        /**
         * OnClickListener for adding a time (beginning time).
         */
        tv_beginTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();

                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                /**
                 * Time picker dialog for adding a time (hours and minutes).
                 * (And for setting the text from the TextView, also a check).
                 */
                timePickerDialog = new TimePickerDialog(EditHoursActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int nHour, int nMinute) {
                        tv_beginTime.setText(checkTimeForMakeUp(nHour, nMinute));

                        if (beginDate == null) {
                            beginDate = new Date();
                        }
                        beginDate.setHours(nHour);
                        beginDate.setMinutes(nMinute);
                        beginDate.setSeconds(0);

                        checkDate();
                    }
                }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        /**
         * OnClickListener for adding a time (ending time).
         */
        tv_endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();

                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                /**
                 * Time picker dialog for adding a time (hours and minutes).
                 * (And for setting the text from the TextView, also a check).
                 */
                timePickerDialog = new TimePickerDialog(EditHoursActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int nHour, int nMinute) {
                        tv_endTime.setText(checkTimeForMakeUp(nHour, nMinute));

                        if (endDate == null) {
                            endDate = new Date();
                        }

                        endDate.setHours(nHour);
                        endDate.setMinutes(nMinute);
                        endDate.setSeconds(0);

                        checkDate();
                    }
                }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        /**
         *  Text change listener for description to check if save button can be displayed.
         */
        tv_description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                description = tv_description.getText().toString();

                if (description.length() > 0) {
                    descriptionFilled = true;
                } else {
                    descriptionFilled = false;
                }
                if (beginDate == null) {
                    beginDate = new Date();
                }

                if (endDate == null) {
                    endDate = new Date();
                }

                checkDate();
            }
        });

        /**
         * Button to send the data from the AddingHoursActivity to the database.
         * Button will only be visible when all fields are filled, and the ending date (time)
         * is later than the beginning date (time).
         */
        btn_saveWorkedHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PauzeDialog pauzeDialog = new PauzeDialog();
                pauzeDialog.showNow(getSupportFragmentManager(), "pause");
                pauzeDialog.getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                npHours = pauzeDialog.getDialog().findViewById(R.id.npHours);
                npMinutes = pauzeDialog.getDialog().findViewById(R.id.npMinutes);
                btnAdd = pauzeDialog.getDialog().findViewById(R.id.btnAdd);

                description = tv_description.getText().toString();

                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String[] hoursArray = npHours.getDisplayedValues();
                        String[] minutesArray = npMinutes.getDisplayedValues();
                        int hours;
                        int minutes;
                        try {
                            hours = Integer.valueOf(hoursArray[npHours.getValue()]);
                        } catch (NumberFormatException nfe) {
                            hours = 0;
                        }

                        try {
                            minutes = Integer.valueOf(minutesArray[npMinutes.getValue()]);
                        } catch (NumberFormatException nfe) {
                            minutes = 0;
                        }

                        if((((((endDate.getTime() - beginDate.getTime()) / 1000) / 60) ) <= (hours * 60) + minutes)){
                            Toast.makeText(EditHoursActivity.this, "You cant have more pause time then working time", Toast.LENGTH_LONG).show();
                            pauzeDialog.dismiss();
                            return;
                        }

                        // Function to set the time (in minutes)
                        setTimeAndDate((hours * 60) + minutes);

                        // Creating new WorkTime class, to save the attributes
                        WorkTime workTime = new WorkTime(worktimeID, projectID, userID, description, time, date, beginDate.getTime(), endDate.getTime());
                        Database.getWorkTimeRef().child(workTime.getWorktimeID()).setValue(workTime);
                        Toast.makeText(EditHoursActivity.this, "Hours edited!", Toast.LENGTH_SHORT).show();

                        // Create intent and open the HoursOverviewActivity to the right project
                        Intent projectInfo = new Intent(EditHoursActivity.this, HoursOverviewActivity.class);
                        projectInfo.putExtra("PROJECT_ID", projectID);
                        projectInfo.putExtra("USER_ID", userID);

                        // Finish the activity
                        finish();
                        startActivity(projectInfo);
                    }
                });
            }
        });

        if (beginDate == null) {
            beginDate = new Date();
        }

        if (endDate == null) {
            endDate = new Date();
        }
    }

    /**
     * Simple function for markup for the time picker dialog. This is because when you would have
     * filled in from 08:00 the textview would show 8:0. So this is just a little function for
     * text markup in the time picker dialogs.
     *
     * @param hour
     * @param minute
     * @return
     */
    private String checkTimeForMakeUp(int hour, int minute) {
        String str = "";

        if (hour < 10) {
            str += "0" + hour;
        } else {
            str += hour;
        }

        str += ":";

        if (minute < 10) {
            str += "0" + minute;
        } else {
            str += minute;
        }

        return str;
    }

    /**
     *  Function to set the time to minutes (rounded by 5), and set the beginning date to set the
     *  date of creation.
     *
     * @param pauze amount of minutes subtracted from the worked minutes
     */
    private void setTimeAndDate(int pauze) {
        this.time = 5*(Math.round((((((endDate.getTime() - beginDate.getTime()) / 1000) / 60) ) - pauze)/5));
        this.date = beginDate.getTime();
    }

    /**
     * Simple function to check if the save hours button can be displayed or not.
     */
    private void checkDate() {
        if (endDate.getTime() <= beginDate.getTime()) {
            btn_saveWorkedHours.setVisibility(View.GONE);
        } else if (!descriptionFilled) {
            btn_saveWorkedHours.setVisibility(View.GONE);
        } else {
            btn_saveWorkedHours.setVisibility(View.VISIBLE);
        }
    }
}
