package com.example.satfinder.Misc;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.satfinder.Misc.Utility.SatUtils;

public class AlarmScheduler {

    private static final String TAG = "AlarmScheduler";

    public static void scheduleNotification(Context context, long futureTimeInMillis, int requestCode) {
        if (futureTimeInMillis <= System.currentTimeMillis()) {
            Log.w(TAG, "Alarm time in the past!");
            Log.w(TAG, "Current: " + SatUtils.convertUTCToLocalTime(System.currentTimeMillis()) + ", Alarm: " + SatUtils.convertUTCToLocalTime(futureTimeInMillis));
            Toast.makeText(context, "Cannot schedule alarm in the past!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                alarmIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            Log.d(TAG, "Scheduling alarm -> Current: " + System.currentTimeMillis() + ", Alarm: " + futureTimeInMillis);

            if (alarmManager.canScheduleExactAlarms()) {
                Log.d(TAG, "Using setExact() for alarm at UTC: " + SatUtils.convertUTCToLocalTime(futureTimeInMillis));
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, futureTimeInMillis, alarmPendingIntent);
            } else {
                Log.w(TAG, "Using setExactAndAllowWhileIdle() due to restricted background activity.");
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, futureTimeInMillis, alarmPendingIntent);
            }
        } else {
            Log.e(TAG, "AlarmManager is null — unable to schedule alarm.");
        }
    }
}