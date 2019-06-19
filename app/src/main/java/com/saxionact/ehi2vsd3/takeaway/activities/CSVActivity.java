package com.saxionact.ehi2vsd3.takeaway.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.saxionact.ehi2vsd3.takeaway.R;
import com.saxionact.ehi2vsd3.takeaway.databases.Database;
import com.saxionact.ehi2vsd3.takeaway.models.WorkTime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * This activity can export a CSV file with an without a begin and enddate.
 *
 * @author Vincent Witten
 */
public class CSVActivity extends AppCompatActivity {

    //Delimiter used in CSV file
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";

    //CSV file header
    private static final String FILE_HEADER = "Project,Omschrijving,Minuten,Datum,Uur-ID,Gebruiker-ID";

    //all views instances
    private Button btnStartDate;
    private Button btnEndDate;
    private Button btnExporteren;

    //extra variables
    private String projectname;
    private Context context;
    private StringBuilder csvFile;
    private int arrayLength = 0;
    private boolean hasCheckDate = false;

    //our date format
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    /**
     * The onCreate method
     *
     * @author Vincent Witten
     *
     * @param savedInstanceState a savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_csv);

        //get the application context
        context = getApplicationContext();

        //set the views instances
        btnStartDate = findViewById(R.id.btnStartDate);
        btnEndDate = findViewById(R.id.btnEndDate);
        btnExporteren = findViewById(R.id.btnExporteren);

        //calls the export button
        btnExporteren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExportCSV();
            }
        });

        //calls the start date button
        btnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creates a dialog with a calender to pick a date
                new DatePickerDialog(CSVActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }

            //creates a new instance of a calender and attaches a onDataSetListener to it
            final Calendar myCalendar = Calendar.getInstance();
            final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    //sets the start date
                    // TODO Auto-generated method stub
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateLabel(btnStartDate, myCalendar);

                    //set hasCheckDate to true
                    hasCheckDate = true;
                }
            };
        });

        //calls the end date button
        btnEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creates a dialog with a calender to pick a date
                new DatePickerDialog(CSVActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }

            //creates a new instance of a calender and attaches a onDataSetListener to it
            final Calendar myCalendar = Calendar.getInstance();
            final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    //sets the end date
                    // TODO Auto-generated method stub
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateLabel(btnEndDate, myCalendar);

                    //set hasCheckDate to true
                    hasCheckDate = true;
                }
            };
        });
    }

    /**
     * This method creates the CSV string.
     *
     * @author Vincent Witten
     */
    private void ExportCSV() {
        //checks if you have given this app the permission to write to external storage
        int checkVal = context.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE");
        if (checkVal != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Turn on WRITE_EXTERNAL_STORAGE permission", Toast.LENGTH_LONG).show();
            return;
        }

        //makes new list for workTime
        final List<WorkTime> workTimeArrayList = new ArrayList<>();

        //gets the workTimes from the database
        Database.getWorkTimeRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    WorkTime workTime = snapshot.getValue(WorkTime.class);

                    //if hasCheckDates is true the workTimes will be filtered based on the dates
                    if (hasCheckDate) {
                        //create new date instances
                        Date after = new Date();
                        Date before = new Date();
                        Date check = new Date(workTime.getDate() * 1000L);

                        //tries to parse the dates
                        try {
                            after = sdf.parse(btnStartDate.getText().toString());
                            before = sdf.parse(btnEndDate.getText().toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Couldn't parse the dates", Toast.LENGTH_SHORT).show();
                        }

                        //if the date is with in range it will be added to to list
                        if (check.after(after) && check.before(before)){
                            workTimeArrayList.add(workTime);
                        }
                    } else {
                        //with no filter all will be added to the list
                        workTimeArrayList.add(workTime);
                    }
                }

                //creates the header for the CSV file
                csvFile = new StringBuilder();
                csvFile.append(FILE_HEADER);
                csvFile.append(NEW_LINE_SEPARATOR);

                //loops trough all the workTimes in the list
                for (final WorkTime workTime : workTimeArrayList) {
                    Database.getProjectsRef().child(workTime.getProjectID()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {

                                //adds the worktime to the CSV file
                                projectname = dataSnapshot.getValue().toString();
                                csvFile.append(projectname)
                                        .append(COMMA_DELIMITER)
                                        .append(String.valueOf(workTime.getDescription()))
                                        .append(COMMA_DELIMITER)
                                        .append(String.valueOf(workTime.getTime()))
                                        .append(COMMA_DELIMITER)
                                        .append(sdf.format(new Date(workTime.getDate() * 1000L)))
                                        .append(COMMA_DELIMITER)
                                        .append(String.valueOf(workTime.getWorktimeID()))
                                        .append(COMMA_DELIMITER)
                                        .append(String.valueOf(workTime.getUserID()))
                                        .append(COMMA_DELIMITER)
                                        .append(NEW_LINE_SEPARATOR);

                                //keeps track of the arraylist position
                                arrayLength++;
                            }

                            //if the array is done the file will be exported
                            if (arrayLength >= workTimeArrayList.size() ){
                                export();
                                arrayLength = 0;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }

                //if there are no worktimes in the filter display this message
                if (workTimeArrayList.size() == 0){
                    Toast.makeText(context, "There are no hours to be exported between these dates", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * sets the button text to the right date
     *
     * @author Vincent Witten
     *
     * @param button the button to change the text from
     * @param calendar the calender containing the right date
     */
    private void updateLabel(Button button, Calendar calendar) {
        button.setText(sdf.format(calendar.getTime()));
    }

    /**
     * exports the CSV string to a file
     *
     * @author Vincent Witten
     */
    private void export(){
        //get available number for file name
        int number = getPosition();

        //creates the file
        final File file = new File(Environment.getExternalStorageDirectory().getPath() + "/csv_" + number + ".csv");

        //creates a output stream and then streams the file to the external storage
        try {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(csvFile.toString());

            //closes the writer and stream

            myOutWriter.close();

            fOut.flush();
            fOut.close();

            Toast.makeText(this, "CSV created", Toast.LENGTH_LONG).show();

            Intent intentShareFile = new Intent(Intent.ACTION_SEND);

            //opens the chooser pop-up to choose share function
            if(file.exists()) {
                intentShareFile.setType("text/*");
                intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

                intentShareFile.putExtra(Intent.EXTRA_SUBJECT,"CSV");
                intentShareFile.putExtra(Intent.EXTRA_TEXT, "The exported CSV File");

                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                startActivity(Intent.createChooser(intentShareFile, "Share File"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("csvError", e.getMessage());
        }
    }


    /**
     * returns int with available position for file name
     *
     * @author Vincent Witten
     *
     * @return int
     */
    private int getPosition(){
        for (int i = 0; i < Integer.MAX_VALUE; i++){
            if (!new File(Environment.getExternalStorageDirectory().getPath() + "/csv_" + String.valueOf(i) + ".csv").exists()){
                return i;
            }
        }
        return 0;
    }
}