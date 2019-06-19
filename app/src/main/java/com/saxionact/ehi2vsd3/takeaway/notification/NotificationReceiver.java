package com.saxionact.ehi2vsd3.takeaway.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.saxionact.ehi2vsd3.takeaway.R;
import com.saxionact.ehi2vsd3.takeaway.activities.AuthenticationActivity;
import com.saxionact.ehi2vsd3.takeaway.activities.ProjectOverviewActivity;

import static com.saxionact.ehi2vsd3.takeaway.activities.TabbedAdministratorActivity.CHANNEL_ID;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
            createNotification(context, "Takeaway urenverantwoording", "Vergeet niet je uren in te vullen!","Notification");
    }

    public void createNotification(Context context, String title, String msg, String msgNotification){
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, AuthenticationActivity.class), PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.takeaway)
                .setContentTitle(title)
                .setContentText(msg)
                .setTicker(msgNotification)
                .setVibrate(new long[] {1000})
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        nBuilder.setContentIntent(pendingIntent);

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, nBuilder.build());
    }


}
