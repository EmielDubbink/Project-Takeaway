package com.saxionact.ehi2vsd3.takeaway.adapters;

import android.app.Notification;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.saxionact.ehi2vsd3.takeaway.R;

import java.util.Date;
import java.util.List;

/**
 * NotificatonAdapter to make an overview of the created notifications 
 *
 * @author Stef Gortemaker
 */
public class NotificationAdapter extends ArrayAdapter<Date> {

    // Definging the variables
    private LayoutInflater inflater;

    private List<Date> dates;

    public NotificationAdapter(@NonNull Context context, @NonNull List<Date> objects) {
        super(context, R.layout.list_item_notification, objects);

        dates = objects;
        inflater = LayoutInflater.from(getContext());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_notification, parent, false);
        }

        Date date = getItem(position);

        if (date != null) {
            TextView tvDagGedeelte = convertView.findViewById(R.id.tvDagen);
            TextView tvNotifationTime = convertView.findViewById(R.id.tvNotificationTime);

            int hours = date.getHours();
            int minutes = date.getMinutes();

            tvNotifationTime.setText(checkTimeForMakeUp(hours, minutes));
            tvDagGedeelte.setText(checkDay(String.valueOf(date)));
        }

        return convertView;
    }

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


    private String checkDay(String i) {
        String day = "";
        if(i.contains("Mon")){
            day = "Monday";
        }
        else if(i.contains("Tue")){
            day = "Tuesday";
        }
        else if(i.contains("Wed")){
            day = "Wednesday";
        }
        else if(i.contains("Thu")){
            day = "Thursday";
        }
        else if(i.contains("Fri")){
            day = "Friday";
        }
        else if(i.contains("Sat")){
            day = "Saterday";
        }
        else if(i.contains("Sun")){
            day = "Sunday";
        }
        return day;
    }
}
