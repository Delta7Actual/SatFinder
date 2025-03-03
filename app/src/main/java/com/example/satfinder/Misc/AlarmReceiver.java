package com.example.satfinder.Misc;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.satfinder.Activities.BrowserActivity;
import com.example.satfinder.R;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "alarm_channel";
    private static final int NOTIFICATION_ID = 1112;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "Setting alarm!");
        trigger(context);
    }

    private void createNotificationChannel(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Satellite Over Head",
                    NotificationManager.IMPORTANCE_HIGH
            );
            manager.createNotificationChannel(channel);
        }
    }

    private void trigger(Context context) {
        Intent notificationIntent = new Intent(context, BrowserActivity.class);

        // Specify FLAG_IMMUTABLE for PendingIntent
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_satellite_alt_24)
                .setContentTitle("Satellite Alarm Triggered")
                .setContentText("Your scheduled satellite event has occurred!")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            createNotificationChannel(context);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }
}