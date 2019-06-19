package com.saxionact.ehi2vsd3.takeaway.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.saxionact.ehi2vsd3.takeaway.R;
import com.saxionact.ehi2vsd3.takeaway.databases.Database;
import com.saxionact.ehi2vsd3.takeaway.models.Project;
import com.saxionact.ehi2vsd3.takeaway.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * The ProjectActivity is the activity that is started when using the LongClick on a project from the TabbedAdministratorActivity
 * From here an admin can choose to edit or delete the project
 *
 * @author Liam Schipers
 */

public class ProjectActivity extends AppCompatActivity {

    // Defining views and variables
    public static final String TAG = "ProjectActivity";

    private TextView tvProjectName;

    private String projectID;
    private Project project;

    private Button btnDeleteProject;
    private Button btnEditProject;

    private List<User> assignedUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        // Setting the size of the activty since it is shown as a dialog
        getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

        // Finding attributes from activity_project.xml
        tvProjectName = findViewById(R.id.ProjectNametv);
        btnDeleteProject = findViewById(R.id.btnDeleteProject);
        btnEditProject = findViewById(R.id.btnEditProject);

        // Creating the list containing the users that sre assigned to a project
        assignedUsers = new ArrayList<>();

        // Geting the intent
        Intent launchIntent = getIntent();
        if (launchIntent != null) {
            // Getting the ProjectID from the intent
            projectID = launchIntent.getStringExtra("PROJECT_ID");

            // Getting the Project that corresponds to the projectID
            Database.getProjectsRef().orderByChild("projectID").equalTo(projectID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // A new Project object is created
                    project = new Project();

                    // Get the project from the DataSnapshot and converting it to a Project object
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        project = snapshot.getValue(Project.class);
                    }

                    if (project != null) {
                        // Check if there are users assigned to the project
                        if (project.getUsers() != null) {
                            // Retrieve all the users from the database
                            Database.getUsersRef().addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // Clear the list containing assigned users
                                    assignedUsers.clear();

                                    // Create a user Object for all the DataSnapshots
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                        User user = snapshot.getValue(User.class);

                                        if (user != null) {
                                            //check whether the user is assigned to the project
                                            if (project.getUsers().containsKey(user.getUserID())){
                                                //If the user is assigned to the project add the user to the assignedUsers list
                                                assignedUsers.add(user);
                                            }
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                    // Show the ProjectName
                    tvProjectName.setText("Project Name: " + project.getName());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        // Adding an OnclickListener to the Edit Button
        btnEditProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //when pressed create an intent with containing the projectID and starting the AddProjectActivity where the user can edit the project
                Intent intent = new Intent(ProjectActivity.this, AddProjectActivity.class);
                intent.putExtra("PROJECT_ID", projectID);
                startActivity(intent);
            }
        });

        // Adding an OnClickListener to the Delete Button
        btnDeleteProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Retrieve the project corresponding to the projectID from the database
                Database.getProjectsRef().orderByChild("projectID").equalTo(project.getProjectID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    // Delete the project from the database
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                            Snapshot.getRef().removeValue();
                        }

                        // Finishing the activity
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled", databaseError.toException());
                    }
                });
            }
        });
    }
}