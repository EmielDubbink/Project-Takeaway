package com.saxionact.ehi2vsd3.takeaway.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.saxionact.ehi2vsd3.takeaway.R;
import com.saxionact.ehi2vsd3.takeaway.models.User;

import java.util.HashMap;
import java.util.List;

/**
 * The UserAssignAdapter is the adpater that is used to show users and be able to assign them to a project
 *
 * @author Liam Scippers
 */

public class UserAssignAdapter extends ArrayAdapter {

    private LayoutInflater inflater;
    private List<User> users;
    private HashMap<String, String> assignedUsers;

    public UserAssignAdapter(@NonNull Context context, @NonNull List<User> objects) {
        super(context, R.layout.list_item_assign_users, objects);

        inflater = LayoutInflater.from(context);
        users = objects;
        assignedUsers = new HashMap<>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_assign_users, parent, false);
            final ViewHolder holder = new ViewHolder();

            convertView.setTag(holder);

            holder.userName = convertView.findViewById(R.id.liauUserName);
            holder.checkBox = convertView.findViewById(R.id.checkBoxUser);

            final User user = users.get(position);

            if (user != null) {
                holder.userName.setText(user.getName());

                // When the chekcbox is clicked the corresponding user will be assigned or unassigned to the project
                holder.checkBox.setChecked(assignedUsers.containsKey(user.getUserID()));
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        assignUser(user, holder);
                    }
                });

                // Making sure that the whole view is clickable to assign or unassign a user
                holder.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        assignUser(user, holder);
                    }
                });

            }
        } else {
            // Get the ViewHolder that corresponds to the user
            final ViewHolder holder = (ViewHolder) convertView.getTag();

            final User user = users.get(position);

            if (user != null) {
                holder.userName.setText(user.getName());

                // When the chekcbox is clicked the corresponding user will be assigned or unassigned to the project
                holder.checkBox.setChecked(assignedUsers.containsKey(user.getUserID()));
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       assignUser(user, holder);
                    }
                });

                // Making sure that the whole view is clickable to assign or unassign a user
                holder.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        assignUser(user, holder);
                    }
                });
            }
        }
        return convertView;
    }

    // Class viewholder to hold the information for each seperate view inside the listview
    private class ViewHolder {
        TextView userName;
        CheckBox checkBox;
    }

    public HashMap<String, String> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(HashMap<String, String> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    // The method where a user will be assigned or unassigned to the project depending if the user was already assigned to the project
    private void assignUser(User user, ViewHolder holder) {
        if (!assignedUsers.containsKey(user.getUserID())) {
            //adding the user to the project's HashMap of assigned users
            assignedUsers.put(user.getUserID(), user.getUserID());
            holder.checkBox.setChecked(true);
        } else {
            //removing the user from the project;s HashMap of assigned users
            assignedUsers.remove(user.getUserID());
            holder.checkBox.setChecked(false);
        }
    }
}
