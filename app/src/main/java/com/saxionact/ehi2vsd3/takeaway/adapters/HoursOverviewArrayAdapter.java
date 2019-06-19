package com.saxionact.ehi2vsd3.takeaway.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.saxionact.ehi2vsd3.takeaway.models.WorkTime;
import com.saxionact.ehi2vsd3.takeaway.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * This adapter puts all relevant registered hours into the listview in the HoursOverviewActivity
 *
 * @author Emiel Dubbink
 */

public class HoursOverviewArrayAdapter extends ArrayAdapter<WorkTime> {

    List<WorkTime> workTimes;
    LayoutInflater inflater;

    public HoursOverviewArrayAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        workTimes = objects;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            convertView = inflater.inflate(R.layout.list_item_overzicht_uren, parent, false);
        }

        TextView tvDate = convertView.findViewById(R.id.tvDatum);
        TextView tvUren = convertView.findViewById(R.id.tvUren);
        TextView tvDescription = convertView.findViewById(R.id.tvDescription);

        WorkTime workTime = workTimes.get(position);

        int workHours = workTime.getTime() / 60;
        int workMin = workTime.getTime() % 60;

        Date realDate = new Date();
        realDate.setTime(workTime.getDate());

        @SuppressLint("SimpleDateFormat") SimpleDateFormat mdyFormat = new SimpleDateFormat("dd-MM-yyyy");
        String dmy = mdyFormat.format(realDate);

        tvDate.setText(dmy);
        tvUren.setText(String.valueOf(workHours + " uur " + workMin + " min"));
        tvDescription.setText(workTime.getDescription());

        return convertView;
    }
}
