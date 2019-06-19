package com.saxionact.ehi2vsd3.takeaway.activities;

import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import com.saxionact.ehi2vsd3.takeaway.R;
import com.saxionact.ehi2vsd3.takeaway.adapters.NotificationAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationActivity extends AppCompatActivity {

    public static List<Date> dates = new ArrayList<>();
    private ListView lvNotifications;
    private NotificationAdapter nAdapter;
    private final DateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' hh:mm a", Locale.UK);
    private DateFormat dayFormat = new SimpleDateFormat("EEEE");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        lvNotifications = findViewById(R.id.notificationListView);
        FloatingActionButton fabAddNotification = findViewById(R.id.fabAddNotification);

        nAdapter = new NotificationAdapter(this, dates);
        loadNotifications();

        lvNotifications.setAdapter(nAdapter);

        lvNotifications.setLongClickable(true);

        lvNotifications.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(NotificationActivity.this);
                View view1 = getLayoutInflater().inflate(R.layout.dialog_list_delete_button, null);
                Button deleteButton = view1.findViewById(R.id.btnDelete);
                builder.setView(view1);
                final AlertDialog dialog = builder.create();

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Date deleteDate = dates.get(i);
                        SharedPreferences prefs = getSharedPreferences("prefs", 0);
                        String datesInString = prefs.getString("dates", "");
                        String[] itemDates = datesInString.split(";");
                        for(int i = 0 ; i  < itemDates.length; i++){
                            Log.d("test", dateFormat.format(deleteDate));
                            Log.d("test", itemDates[i]);
                            if(itemDates[i].equals(dateFormat.format(deleteDate))){
                                dates.remove(i);
                            }
                        }
                        nAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });


                dialog.show();
                return true;
            }
        });

        fabAddNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotificationActivity.this, CreateNotificationActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadNotifications() {
        dates.clear();
        SharedPreferences prefs = getSharedPreferences("prefs", 0);
        String datesInString = prefs.getString("dates", "");
        String[] itemDates = datesInString.split(";");
        for (int i = 0; i < itemDates.length; i++){
            try {
                dates.add(dateFormat.parse(itemDates[i]));
                Log.d("Dates: ", "" +dates);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onResume(){
        super.onResume();
        nAdapter.notifyDataSetChanged();
    }
}
