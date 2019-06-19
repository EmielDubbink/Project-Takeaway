package com.saxionact.ehi2vsd3.takeaway.activities;

import android.annotation.SuppressLint;
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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * AddingHoursActivity for adding hours on projects.
 *
 * @author Boris Oortwijn
 * @author Vincent Witten
 */
public class AddingHoursActivity extends AppCompatActivity {

    /**
     * Defining variables
     */
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

    private boolean dateFilled = false;
    private boolean beginDateFilled = false;
    private boolean endDateFilled = false;
    private boolean descriptionFilled = false;

    private int time; // In minutes rounded by 5

    private long date;

    //dialog views
    private NumberPicker npHours;
    private NumberPicker npMinutes;
    private Button btnAdd;

    /**
     * onCreate is called when this activity is started. From the previous activity we get the
     * project id and the user id.
     *
     * @param savedInstanceState
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_hours);

        // Finding attributes from activity_adding_hours.xml
        tv_datePicker = findViewById(R.id.tv_datePicker);
        tv_beginTime = findViewById(R.id.tv_beginTime);
        tv_endTime = findViewById(R.id.tv_endTime);

        // Get intent from calling activity
        Intent intent = getIntent();
        // Just to be sure that the app doesn't crash when intent is null
        if (intent != null) {
            projectID = intent.getStringExtra("PROJECT_ID");
            userID = intent.getStringExtra("USER_ID");
            beginDate = (Date) intent.getSerializableExtra("BEGIN_DATE");
            endDate = (Date) intent.getSerializableExtra("END_DATE");

            // Setting hours if beginDate =! null
            if (beginDate != null) {
                beginDate.setHours(beginDate.getHours() + 1);
                endDate.setHours(endDate.getHours() + 1);

                @SuppressLint("SimpleDateFormat") SimpleDateFormat mdyFormat = new SimpleDateFormat("dd - MM - yyyy");
                String date = mdyFormat.format(beginDate.getTime());

                tv_datePicker.setText(date);
                tv_beginTime.setText(checkTimeForMakeUp(beginDate.getHours(), beginDate.getMinutes()));
                tv_endTime.setText(checkTimeForMakeUp(endDate.getHours(), endDate.getMinutes()));

                dateFilled = true;
                beginDateFilled = true;
                endDateFilled = true;
            }
        }

        tv_datePicker = findViewById(R.id.tv_datePicker);
        tv_beginTime = findViewById(R.id.tv_beginTime);
        tv_endTime = findViewById(R.id.tv_endTime);
        tv_description = findViewById(R.id.tv_description);

        btn_saveWorkedHours = findViewById(R.id.btn_saveWorkedHours);
        btn_saveWorkedHours.setVisibility(View.GONE);

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
                datePickerDialog = new DatePickerDialog(AddingHoursActivity.this, R.style.DatePicker, new DatePickerDialog.OnDateSetListener() {
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

                        dateFilled = true;
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
                timePickerDialog = new TimePickerDialog(AddingHoursActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int nHour, int nMinute) {
                        tv_beginTime.setText(checkTimeForMakeUp(nHour, nMinute));

                        if (beginDate == null) {
                            beginDate = new Date();
                        }
                        beginDate.setHours(nHour);
                        beginDate.setMinutes(nMinute);
                        beginDate.setSeconds(0);

                        beginDateFilled = true;
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
                timePickerDialog = new TimePickerDialog(AddingHoursActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int nHour, int nMinute) {
                        tv_endTime.setText(checkTimeForMakeUp(nHour, nMinute));

                        if (endDate == null) {
                            endDate = new Date();
                        }

                        endDate.setHours(nHour);
                        endDate.setMinutes(nMinute);
                        endDate.setSeconds(0);

                        endDateFilled = true;
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
                            Toast.makeText(AddingHoursActivity.this, "You cant have more pause time then working time", Toast.LENGTH_LONG).show();
                            pauzeDialog.dismiss();
                            return;
                        }

                        setTimeAndDate((hours * 60) + minutes);

                        WorkTime workTime = new WorkTime(Database.getWorkTimeRef().push().getKey(), projectID, userID, description, time, date, beginDate.getTime(), endDate.getTime());
                        Database.getWorkTimeRef().child(workTime.getWorktimeID()).setValue(workTime);
                        Toast.makeText(AddingHoursActivity.this, "Hours saved!", Toast.LENGTH_SHORT).show();

                        Intent projectInfo = new Intent(AddingHoursActivity.this, HoursOverviewActivity.class);
                        projectInfo.putExtra("PROJECT_ID", projectID);
                        projectInfo.putExtra("USER_ID", userID);
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
        } else if (!dateFilled || !beginDateFilled || !endDateFilled || !descriptionFilled) {
            btn_saveWorkedHours.setVisibility(View.GONE);
        } else {
            btn_saveWorkedHours.setVisibility(View.VISIBLE);
        }
    }
}