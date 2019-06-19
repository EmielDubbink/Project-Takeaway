package com.saxionact.ehi2vsd3.takeaway.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.saxionact.ehi2vsd3.takeaway.R;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;


import com.saxionact.ehi2vsd3.takeaway.adapters.ProjectListAdapter;
import com.saxionact.ehi2vsd3.takeaway.databases.Database;
import com.saxionact.ehi2vsd3.takeaway.models.Project;

import java.util.ArrayList;
import java.util.List;


/**
 * In the ProjectOverviewActivity a user who is not an administrator can see the projects that are assigned to him/her
 *
 * @author Liam Schippers
 */

public class ProjectOverviewActivity extends AppCompatActivity {

    // Defining variables
    private ProjectListAdapter plAdapter;
    private List<Project> projects;
    private String userID;

    public static final String TAG = "ProjectOverviewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_overview);

        // Adding the layout items to their corresponding items in the code
        ListView lvProjects = findViewById(R.id.ProjectsListView);
        ImageView ivSettings = findViewById(R.id.ivSettings);
        FloatingActionButton fab = findViewById(R.id.fabAddProject);
        // Hiding the FloatingActionButton so that users can;t create new projects from this activity
        fab.hide();

        // Giving the settings button an onClickListener that will send you to the SettingsActivity when clicked on
        ivSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProjectOverviewActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        // This creates a channel for application notifications
        createNotificationChannel();

        // Getting the userID, from the user that is logged in, from the intent
        Intent intent = getIntent();
        userID = intent.getStringExtra("USER_ID");

        // Creating the list of projects and setting the adapeter
        projects = new ArrayList<>();
        plAdapter = new ProjectListAdapter(this, projects, userID);
        lvProjects.setAdapter(plAdapter);

        // Making sure that the user is able to use tha LongClick on the ListView
        lvProjects.setLongClickable(true);

        // Getting the projects from the database by using a custom made ValueEventListener => ProjectValueEventListener
        Database.getProjectsRef().addValueEventListener(new projectValueEventListener());

        // Giving the ListView an OnClickListener, when an item in the listview is pressed the HoursOverViewActivity is started
        lvProjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //notificationCall();
                //Creating the intent and giving it the userID of the user logged in, and giving it the project_id of the project that got selected
                Intent projectInfo = new Intent(ProjectOverviewActivity.this, HoursOverviewActivity.class);
                String project_id = projects.get(i).getProjectID();
                projectInfo.putExtra("PROJECT_ID", project_id);
                projectInfo.putExtra("USER_ID", userID);
                startActivity(projectInfo);
            }
        });
    }

    // When the onResume is called the projects will be retrieved from the database.
    @Override
    protected void onResume() {
        super.onResume();

        Database.getProjectsRef().addValueEventListener(new projectValueEventListener());

    }

    // onBackPressed will start an new activity
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    // Before you can deliver the notification on Android 8.0 and higher,
    // you must register your app's notification channel
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = TabbedAdministratorActivity.CHANNEL_NAME;
            String description = TabbedAdministratorActivity.CHANNEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(TabbedAdministratorActivity.CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * The ProjectValueEventListener retrieves all the projects from the database
     * The projects are retrieved and it will check whether he userID is assigned to the project
     * If the userID is assigned to the project the project will be added to the list of projects
     * The projects that the user is assigned to will be displayed on screen
     */
    private class projectValueEventListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            //Creating a Project object and clearing the list of projects
            Project project;
            projects.clear();

            //For each item that is in the database that has the project reference a dataSnapshot will be made
            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                //Each snapshot is converted to a Project Object
                project = snapshot.getValue(Project.class);
                if (project != null) {
                    //Check if there are users assigned to the project
                    if (project.getUsers() != null) {
                        //If there are users assigned to the project check whether the signed in user is assigned to the projects
                        if (project.getUsers().containsKey(userID)) {
                            //If the signed in user is assigned to the project add the project to the projects list
                            projects.add(project);
                        }
                    }
                }
            }
            //notify the adapter about the data retrieved from the databse
            plAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(TAG, "onCancelled", databaseError.toException());
        }
    }

}
