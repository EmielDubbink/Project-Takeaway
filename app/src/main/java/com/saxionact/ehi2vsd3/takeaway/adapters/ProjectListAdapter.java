package com.saxionact.ehi2vsd3.takeaway.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.saxionact.ehi2vsd3.takeaway.R;
import com.saxionact.ehi2vsd3.takeaway.activities.AddProjectActivity;
import com.saxionact.ehi2vsd3.takeaway.databases.Database;
import com.saxionact.ehi2vsd3.takeaway.models.Project;
import com.saxionact.ehi2vsd3.takeaway.models.User;


import java.util.List;

/**
 * The ProjectListAdapter is the adapter that is used to show the projects in a listview
 *
 * @author Liam Schippers
 */

public class ProjectListAdapter extends ArrayAdapter<Project> {

    // Defining the variables
    private LayoutInflater layoutInflater;

    private List<Project> projects;
    private User user;

    public ProjectListAdapter(@NonNull Context context, @NonNull List<Project> objects, String userId) {
        super(context, R.layout.list_item_project, objects);

        projects = objects;
        layoutInflater = LayoutInflater.from(context);

        // Retrieving the User that is logged in from the database
        Database.getUsersRef().orderByChild("userID").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    user = snapshot.getValue(User.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null)
            convertView = layoutInflater.inflate(R.layout.list_item_project, parent, false);

        final Project project = projects.get(position);

        if (project != null) {

            TextView projectID = convertView.findViewById(R.id.tvProjectID);
            Button btnEditProject = convertView.findViewById(R.id.btnEditProject);
            projectID.setText(project.getName());

            // If the user is not an admin the user will not be able to see or click the editProjct Button
            if (!user.getAdministrator()) {
                btnEditProject.setVisibility(View.GONE);
            }

            // If the btnEditProject is pressed the user will be send to the AddProjectActivity where the user can edit the project
            btnEditProject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), AddProjectActivity.class);
                    intent.putExtra("PROJECT_ID", project.getProjectID());
                    getContext().startActivity(intent);
                }
            });
        }

        return convertView;
    }
}
