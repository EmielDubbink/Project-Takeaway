package com.saxionact.ehi2vsd3.takeaway.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.saxionact.ehi2vsd3.takeaway.R;
import com.saxionact.ehi2vsd3.takeaway.models.User;

import java.util.List;

/**
 * UserOverviewAdapter to set the list items in the ListView from TabbedAdministratorActivity
 *
 * @author Boris Oortwijn
 * @author Liam Schippers
 */
public class UserOverviewAdapter extends ArrayAdapter<User> {

    // Defining variables
    private LayoutInflater layoutInflater;

    private List<User> users;

    public UserOverviewAdapter(@NonNull Context context, @NonNull List<User> objects) {
        super(context, R.layout.list_item_overview_users, objects);

        users = objects;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null)
            convertView = layoutInflater.inflate(R.layout.list_item_overview_users, parent, false);

        TextView tvUserName = convertView.findViewById(R.id.tvUserName);

        final User user = users.get(position);

        // If statement to set background color to grey if user is not active
        if (!user.getActive()) {
            convertView.setBackgroundResource(R.drawable.shape_ltgrey_item_background);
            tvUserName.setText(user.getName() + " - Not Active");
        } else {
            convertView.setBackgroundResource(R.drawable.shape_white_item_background);
            tvUserName.setText(user.getName());
        }
      
        return convertView;
    }
}
