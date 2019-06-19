package com.saxionact.ehi2vsd3.takeaway.activities;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.saxionact.ehi2vsd3.takeaway.notification.NotificationReceiver;
import com.saxionact.ehi2vsd3.takeaway.R;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateNotificationActivity extends AppCompatActivity {

    private TextView tvTime, tvDay;
    private Calendar calendar, calendar1;
    private Button saveButton;

    private TimePickerDialog timePickerDialog;
    private DatePickerDialog datePickerDialog;

    private AlarmManager alarmManager;

    private Date date = new Date();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notification);

        SharedPreferences prefs = getSharedPreferences("prefs", 0);
        final SharedPreferences.Editor editor = prefs.edit();

        final DateFormat timeFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' hh:mm a", Locale.UK);
       // final DateFormat dayFormat = new SimpleDateFormat("EEEE");

        tvTime = findViewById(R.id.textViewNotificationTime);
        tvDay = findViewById(R.id.textViewNotificationDay);
        saveButton = findViewById(R.id.btn_saveNotification);

        saveButton.setEnabled(false);

        calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(System.currentTimeMillis());

        tvTime.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();

                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(CreateNotificationActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int nHour, int nMinute) {
                        tvTime.setText(checkTimeForMakeUp(nHour, nMinute));

                        date.setHours(nHour);
                        date.setMinutes(nMinute);
                        date.setSeconds(0);

                        calendar1.set(Calendar.HOUR_OF_DAY, nHour);
                        calendar1.set(Calendar.MINUTE, nMinute);
                    }
                },hour, minute, true);
                timePickerDialog.show();
            }

        });

        tvDay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();

                int days = calendar.get(Calendar.DAY_OF_MONTH);
                final int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                datePickerDialog = new DatePickerDialog(CreateNotificationActivity.this, R.style.DatePicker, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int nyear, int nmonth, int nday) {
                        date.setDate(nday);
                        date.setMonth(nmonth);
                        calendar1.set(Calendar.YEAR, nyear);
                        calendar1.set(Calendar.MONTH, nmonth);
                        calendar1.set(Calendar.DAY_OF_MONTH, nday);
                        Log.d("Test2",  String.valueOf(date));

                        if(String.valueOf(date).contains("Mon")){
                            tvDay.setText("Monday");
                        }
                        else if(String.valueOf(date).contains("Tue")){
                            tvDay.setText("Tuesday");
                        }
                        else if(String.valueOf(date).contains("Wed")){
                            tvDay.setText("Wednesday");
                        }
                        else if(String.valueOf(date).contains("Thu")){
                            tvDay.setText("Thursday");
                        }
                        else if(String.valueOf(date).contains("Fri")){
                            tvDay.setText("Friday");
                        }
                        else if(String.valueOf(date).contains("Sat")){
                            tvDay.setText("Saterday");
                        }
                        else if(String.valueOf(date).contains("Sun")){
                            tvDay.setText("Sunday");
                        }

                    }
                }, year, month, days);
                datePickerDialog.show();
                saveButton.setEnabled(true);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddAction();

                StringBuilder stringBuilder = new StringBuilder();
                for(Date d : NotificationActivity.dates){
                    stringBuilder.append(timeFormat.format(d));
                    stringBuilder.append(";");
                }

                String newDate = timeFormat.format(date);
                Log.d("test : ", newDate);
                stringBuilder.append(newDate);

                editor.putString("dates", stringBuilder.toString());
                editor.apply();

                Intent intent1 = new Intent(CreateNotificationActivity.this, NotificationActivity.class);
                startActivity(intent1);
                finish();
            }
        });


    }

    private String checkTimeForMakeUp(int hour, int minute)
    {
        String str = "";

        if(hour < 10)
        {
            str += "0" + hour;
        }

        else
        {
            str += hour;
        }

        str += ":";

        if(minute < 10)
        {
            str += "0" + minute;
        }

        else
        {
            str += minute;
        }

        return str;
    }

    public void AddAction(){

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("myAction", "mDoNotify");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), 1000 * 60 * 60 * 24 * 7,pendingIntent);

    }


}
