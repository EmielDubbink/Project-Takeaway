package com.saxionact.ehi2vsd3.takeaway.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.saxionact.ehi2vsd3.takeaway.R;
import com.saxionact.ehi2vsd3.takeaway.adapters.HoursOverviewArrayAdapter;
import com.saxionact.ehi2vsd3.takeaway.databases.Database;
import com.saxionact.ehi2vsd3.takeaway.models.Project;
import com.saxionact.ehi2vsd3.takeaway.models.WorkTime;

import java.util.ArrayList;

/**
 * The HoursOverviewActivity class is the activity class where the user can view registered hours.
 * @author Emiel Dubbink
 * @author Liam Schippers
 */
public class HoursOverviewActivity extends AppCompatActivity {

    // Defining views and variables
    private ImageView ivSettings;
    private ListView listView;
    private Button btnTimer;
    private ArrayList<WorkTime> workTimes;
    private HoursOverviewArrayAdapter hoursOverviewArrayAdapter;
    private FloatingActionButton fab;
    public static final String TAG = "HoursOverviewActivity";

    private String projectID;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hours_overview);

        // Finding attributes from activity_hours_overview.xml
        ivSettings = findViewById(R.id.ivSettings);
        listView = findViewById(R.id.lvUrenOverzicht);
        btnTimer = findViewById(R.id.btnTimer);
        fab = findViewById(R.id.fab);

        // onClickListener for settings button, to create an new activity.
        ivSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HoursOverviewActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        // Setting adapter for the ListView (worked hours)
        workTimes = new ArrayList<>();
        hoursOverviewArrayAdapter = new HoursOverviewArrayAdapter(this, 0, workTimes);
        listView.setAdapter(hoursOverviewArrayAdapter);

        // Get information from calling activity and database.
        // Setting value event listeners, so when data has been edited or added in the database, the
        // activity will update the list.
        Intent intent = getIntent();
        if (intent != null) {
            //Checking whether the registerd hours of a project needs to be seen or the registerd hours of a user needs to be seen
            if (intent.getStringExtra("PROJECT_ID") != null && intent.getStringExtra("USER_ID") != null) {
                //the registerd hours the selected project need to be seen
                projectID = intent.getStringExtra("PROJECT_ID");
                userID = intent.getStringExtra("USER_ID");
                //check whether the user is an admin or not
                if (intent.getBooleanExtra("ADMIN", false)){
                    //if the user is an admin get all the registered hours for the selected project
                    Database.getWorkTimeRef().orderByChild("projectID").equalTo(projectID).addValueEventListener(new MyValueEventListener());
                } else {
                    //if the user is not an admin only get the hours that user registered for the selected project
                    Database.getWorkTimeRef().orderByChild("projectID").equalTo(projectID).addValueEventListener(new MyOverviewHoursEventListener());
                }

            } else if (intent.getStringExtra("userId") != null){
                //the registerd hours the selected user need to be seen
                userID = intent.getStringExtra("userId");
                btnTimer.setVisibility(View.GONE);
                fab.hide();
                Database.getWorkTimeRef().orderByChild("userID").equalTo(userID).addValueEventListener(new MyValueEventListener());
            }
        } else {
            //For if there is a function where someone would like to see all of his/her rgistered hours
            Database.getWorkTimeRef().addValueEventListener(new  MyValueEventListener());
        }

        // onClickListener to start the TimerActivity
        btnTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HoursOverviewActivity.this, TimerActivity.class);
                intent.putExtra("PROJECT_ID", projectID);
                intent.putExtra("USER_ID", userID);
                startActivity(intent);
            }
        });

        // onClickListener to start the AddingHoursActivity
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HoursOverviewActivity.this, AddingHoursActivity.class);
                intent.putExtra("PROJECT_ID", projectID);
                intent.putExtra("USER_ID", userID);
                finish();
                startActivity(intent);

            }
        });

        // onItemClickListener to edit or delete worked hours
        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                // Show dialog with delete and modify
                final AlertDialog.Builder builder = new AlertDialog.Builder(HoursOverviewActivity.this);
                View view1 = getLayoutInflater().inflate(R.layout.dialog_list_buttons, null);
                Button modifyButton = view1.findViewById(R.id.btnModify);
                Button deleteButton = view1.findViewById(R.id.btnDelete);

                builder.setView(view1);
                final AlertDialog dialog = builder.create();

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        WorkTime workTime = workTimes.get(i);
                        Query deleteQuery = Database.getWorkTimeRef().orderByChild("worktimeID").equalTo(workTime.getWorktimeID());

                        deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                                    appleSnapshot.getRef().removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "onCancelled", databaseError.toException());
                            }
                        });
                        dialog.dismiss();
                    }
                });

                modifyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WorkTime workTime = workTimes.get(i);

                        Intent intent = new Intent(HoursOverviewActivity.this, EditHoursActivity.class);
                        intent.putExtra("WORKTIME_ID", workTime.getWorktimeID());
                        intent.putExtra("PROJECT_ID", workTime.getProjectID());
                        intent.putExtra("USER_ID", workTime.getUserID());
                        intent.putExtra("DESCRIPTION", workTime.getDescription());
                        intent.putExtra("BEGIN_DATE", workTime.getBeginDate());
                        intent.putExtra("END_DATE", workTime.getEndDate());

                        finish();
                        startActivity(intent);
                    }
                });

                dialog.show();
                return true;
            }
        });
    }

    /**
     * The MyOverviewHoursEventListener will only return the hours that the logged in user, who is not an admin registered for the selected project
     */

    public class MyOverviewHoursEventListener implements ValueEventListener {
        /**
         * when the data in the database is changed, clear the current list, and fill it in with the updated database info
         * @param dataSnapshot
         */
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            workTimes.clear();

            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                WorkTime workTime = snapshot.getValue(WorkTime.class);

                //filter out the hours that the logged in user has registered for the seleted project
                if (workTime.getUserID().equals(userID)){
                    workTimes.add(snapshot.getValue(WorkTime.class));
                }
            }

            hoursOverviewArrayAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(TAG, "onCancelled", databaseError.toException());
        }
    }

    /**
     * The MyValueEventListener will return all the hours that are registered for the selected project
     */
    public class MyValueEventListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            workTimes.clear();

            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                workTimes.add(snapshot.getValue(WorkTime.class));
            }
            hoursOverviewArrayAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(TAG, "onCancelled", databaseError.toException());
        }
    }
}