package com.saxionact.ehi2vsd3.takeaway.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.saxionact.ehi2vsd3.takeaway.R;
import com.saxionact.ehi2vsd3.takeaway.adapters.UserAssignAdapter;
import com.saxionact.ehi2vsd3.takeaway.databases.Database;
import com.saxionact.ehi2vsd3.takeaway.models.Project;
import com.saxionact.ehi2vsd3.takeaway.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The AddProjectActivity is the activty where an admin can add a brand new project or edit an already existing project
 *
 * @author Liam Schipers
 */

public class AddProjectActivity extends AppCompatActivity {

    /**
     * Defining Variables
     */
    private Button btnSaveProject, btnDiscardProject;
    private EditText etProjectName;
    private ListView userList;
    private Switch sIsFinished;
    private UserAssignAdapter userAssignAdapter;
    private List<User> users;

    private String projectId;

    private Project project;

    private HashMap<String, String> assignedUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        // Finding attributes from activity_add_project.xml
        btnSaveProject = findViewById(R.id.btnAPAaddProjects);
        btnDiscardProject = findViewById(R.id.btnAPAdiscard);
        userList = findViewById(R.id.assignUserList);
        etProjectName = findViewById(R.id.etProjectNameAAP);
        sIsFinished = findViewById(R.id.sFinishedProject);

        assignedUsers = new HashMap<>();

        users = new ArrayList<>();

        //Getting all the users from the database
        Database.getUsersRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users.clear();
                User user;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    user = snapshot.getValue(User.class);
                    if (user != null) {
                        //check whether the user is still active
                        if (user.getActive()) {
                            //if the user is still active add the user to the users list
                            users.add(snapshot.getValue(User.class));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Creating the userAssignAdapter
        userAssignAdapter = new UserAssignAdapter(this, users);

        Intent launchIntent = getIntent();
        // Check whether the user is going to edit an already existing project or add a brand new project
        if (launchIntent.getStringExtra("PROJECT_ID") != null) {
            //If an already existing project is being edited get the projectId from the intent
            projectId = launchIntent.getStringExtra("PROJECT_ID");

            btnDiscardProject.setText(R.string.discard_changes);

            // Retrieve the corresponding project from the database
            Database.getProjectsRef().orderByChild("projectID").equalTo(projectId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    project = new Project();

                    // Make a prject object from the DataSnapshot retrieved from the database
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        project = snapshot.getValue(Project.class);
                    }

                    if (project != null) {
                        etProjectName.setText(project.getName());
                        sIsFinished.setChecked(project.getDone());

                        if (project.getUsers() != null) {
                            //Filter out which users are assigned to the project by checking if the userId is in the HashMap containing the assigned userIDs of the project
                            for (int i = 0; i < users.size(); i++) {
                                if (project.getUsers().containsKey(users.get(i).getUserID())) {
                                    assignedUsers.put(users.get(i).getUserID(), users.get(i).getUserID());
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            // Giving the list of assignedUsers to the userAssignAdapter
            userAssignAdapter.setAssignedUsers(assignedUsers);

            // Give the save button a OnClickListener
            btnSaveProject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = etProjectName.getText().toString();
                    // Getting the HashMap of assignedUsers
                    assignedUsers = userAssignAdapter.getAssignedUsers();

                    // Setting the attributes of the project
                    project.setName(name);
                    project.setUsers(assignedUsers);
                    project.setDone(sIsFinished.isChecked());

                    // Updating the Project in the database
                    Database.getProjectsRef().child(project.getProjectID()).setValue(project);

                    // Finishing the activity
                    finish();
                }
            });

        } else {
            // Creating a brand new project
            btnDiscardProject.setText(R.string.discard_project);
            sIsFinished.setVisibility(View.INVISIBLE);

            btnSaveProject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = etProjectName.getText().toString();
                    // Getting the HasMap of assignedUsers
                    assignedUsers = userAssignAdapter.getAssignedUsers();

                    // Creating the object for the new Project  with the coreespondeing attributes
                    Project newproject = new Project(Database.getProjectsRef().push().getKey(), name, false, assignedUsers);

                    // Creating a new project in the databse with the corresponding attributes
                    Database.getProjectsRef().child(newproject.getProjectID()).setValue(newproject);

                    // Finishing the activity
                    finish();
                }
            });
        }
        // Setting the adapter
        userList.setAdapter(userAssignAdapter);

        // Adding an OnClickListener to the discard button that will finish the activity when pressed
        btnDiscardProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // onBackPressed just finishes the activity
    @Override
    public void onBackPressed() {
        finish();
    }
}
