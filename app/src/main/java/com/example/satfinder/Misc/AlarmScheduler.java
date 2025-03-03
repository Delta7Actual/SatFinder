package com.example.satfinder.Misc;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.satfinder.Misc.Utility.SatUtils;

public class AlarmScheduler {

    private final static String TAG = "AlarmScheduler";

    public static void scheduleNotification(Context context, long futureTimeInMillis) {
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            Log.d(TAG, "Scheduling alarm for: " + futureTimeInMillis);

            if (alarmManager.canScheduleExactAlarms()) {
                Log.d(TAG, "Exact alarms are supported. Scheduling exact alarm for UTC: " + SatUtils.convertUTCToLocalTime(futureTimeInMillis));
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, futureTimeInMillis, alarmPendingIntent);
            } else {
                Log.e(TAG, "Can't schedule exact alarms on this device. Trying setExactAndAllowWhileIdle.");
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, futureTimeInMillis, alarmPendingIntent);
            }
        }
    }
}