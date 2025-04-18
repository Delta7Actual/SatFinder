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

/**
 * Broadcast receiver for handling alarm notifications.
 */
public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";
    private static final String CHANNEL_ID = "alarm_channel";
    private static final int NOTIFICATION_ID = 1112;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            Log.e(TAG, "Received null intent — aborting.");
            return;
        }

        Log.d(TAG, "Alarm triggered! Preparing notification...");
        showNotification(context);
    }

    private void createNotificationChannel(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null || manager.getNotificationChannel(CHANNEL_ID) != null) {
            return; // Channel already exists
        }

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Satellite Alerts",
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription("Notifies when a satellite is overhead.");
        manager.createNotificationChannel(channel);
    }

    private void showNotification(Context context) {
        createNotificationChannel(context);

        Intent notificationIntent = new Intent(context, BrowserActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_satellite_alt_24)
                .setContentTitle("Satellite Alarm")
                .setContentText("A satellite event you scheduled has started.")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, builder.build());
        } else {
            Log.e(TAG, "NotificationManager is null — cannot show notification.");
        }
    }
}