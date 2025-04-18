package com.example.satfinder.Misc;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.satfinder.Managers.StorageManager;
import com.example.satfinder.Misc.Utility.MathUtils;

/**
 * Class for scheduling alarms and notifications.
 */
public class AlarmScheduler {

    private static final String TAG = "SatAS";

    public static void scheduleNotification(Context context, long futureTimeInMillis, int requestCode, int satId) {
        if (futureTimeInMillis <= System.currentTimeMillis()) {
            Log.w(TAG, "Alarm time in the past!");
            Log.w(TAG, "Current: " + MathUtils.convertUTCToLocalTime(System.currentTimeMillis()) + ", Alarm: " + MathUtils.convertUTCToLocalTime(futureTimeInMillis));
            Toast.makeText(context, "Cannot schedule alarm in the past!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.putExtra("satName", getSatelliteName(context, satId));
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
                Log.i(TAG, "Using setExact() for alarm at UTC: " + MathUtils.convertUTCToLocalTime(futureTimeInMillis));
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, futureTimeInMillis, alarmPendingIntent);
            } else {
                Log.w(TAG, "Using setExactAndAllowWhileIdle() due to restricted background activity.");
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, futureTimeInMillis, alarmPendingIntent);
            }
        } else {
            Log.e(TAG, "AlarmManager is null — unable to schedule alarm.");
        }
    }

    private static String getSatelliteName(Context context, int satId) {
        if (satId == -1) {
            // This is a dummy value
            Log.i(TAG, "getSatelliteName: Dummy value");
            return "Test Satellite";
        }

        // Satellite is from the favourites list so it should be in the cache
        StorageManager storageManager = StorageManager.getInstance(context);
        String satTle = storageManager.spGetSatelliteTLE(satId);
        if (!(satTle == null | satTle.isEmpty()) && !satTle.equals("NONE,,")) {
            String[] parts = satTle.split(",");
            if (parts.length >= 3) {
                Log.i(TAG, "getSatelliteName: " + parts[2]);
                return parts[2]; // The third part is the satellite name
            } else {
                Log.w(TAG, "getSatelliteName: Malformed TLE");
            }
        }
        Log.w(TAG, "getSatelliteName: Failed to get satellite name from cache");
        return "Unknown Satellite";
    }
}