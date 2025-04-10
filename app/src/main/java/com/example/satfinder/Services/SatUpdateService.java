package com.example.satfinder.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.satfinder.Managers.SatelliteManager;
import com.example.satfinder.Managers.StorageManager;
import com.example.satfinder.R;

public class SatUpdateService extends Service {

    private static final String TAG = "SatUpdateService";
    private static final long UPDATE_INTERVAL_MILLIS = 1000 * 60 * 30; // 30 minutes
    private static final int NOTIFICATION_ID = 1111;
    private static final String CHANNEL_ID = "sat_update_channel";
    private static final String ACTION_STOP = "STOP_SERVICE";

    public static boolean isRunning = false;

    private Handler handler;
    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "Updating satellite data...");
            StorageManager.getInstance(SatUpdateService.this)
                    .spSaveAndUpdateSatelliteData(SatelliteManager.getInstance(),
                            () -> Log.d(TAG, "run: Cache save and update complete"));
            handler.postDelayed(this, UPDATE_INTERVAL_MILLIS);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, getNotification());
        Log.d(TAG, "Service created.");

        handler = new Handler(Looper.getMainLooper());
        isRunning = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_STOP.equals(intent.getAction())) {
            stopSelf();
            return START_NOT_STICKY;
        }

        handler.post(updateRunnable);
        Log.d(TAG, "Service started — updates scheduled every 30 minutes");
        return START_STICKY; // Restart if needed
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(updateRunnable); // Remove any pending tasks
        }
        Log.d(TAG, "Service destroyed — updates halted");
        isRunning = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Satellite Auto-Updater",
                    NotificationManager.IMPORTANCE_LOW
            );
            manager.createNotificationChannel(channel);
        }
    }

    private Notification getNotification() {
        Intent stopIntent = new Intent(this, SatUpdateService.class);
        stopIntent.setAction(ACTION_STOP);
        PendingIntent pendingStopIntent = PendingIntent.getService(
                this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("SatFinder Update Service")
                .setContentText("Updating and caching satellite data...")
                .setSmallIcon(R.drawable.baseline_satellite_alt_24)
                .addAction(R.drawable.baseline_logout_24, "Stop Daemon", pendingStopIntent)
                .setOngoing(true)
                .build();
    }
}