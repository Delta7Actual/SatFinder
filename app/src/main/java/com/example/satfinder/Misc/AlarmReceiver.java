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

    private static final String TAG = "SatAR";
    private static final String CHANNEL_ID = "alarm_channel";
    private static final int NOTIFICATION_ID = 1112;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            Log.e(TAG, "Received null intent — aborting.");
            return;
        }

        Log.i(TAG, "Alarm triggered! Preparing notification...");

        String satName = intent.getStringExtra("satName");
        if (satName == null || satName.isEmpty()) {
            satName = "Unknown Satellite";
        }

        Log.d(TAG, "Retrieved satellite name: " + satName);
        showNotification(context, satName);
    }

    private void createNotificationChannel(Context context) {
        Log.d(TAG, "Creating notification channel...");
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

    private void showNotification(Context context, String satName) {
        createNotificationChannel(context);

        Intent notificationIntent = new Intent(context, BrowserActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

        String content = "Satellite " + satName + " is visible right now!";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_satellite_alt_24)
                .setContentTitle(satName + " is visible!")
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            Log.i(TAG, "Showing notification for satellite: " + satName);
            manager.notify(NOTIFICATION_ID, builder.build());
        } else {
            Log.e(TAG, "NotificationManager is null — cannot show notification.");
        }
    }
}