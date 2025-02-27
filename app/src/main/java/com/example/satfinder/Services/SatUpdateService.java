package com.example.satfinder.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.satfinder.Managers.SatelliteManager;
import com.example.satfinder.Managers.StorageManager;
import com.example.satfinder.R;

public class SatUpdateService extends Service {

    private final String TAG = "SatUpdateService";

    private static final long UPDATE_INTERVAL_MILLIS = 1000 * 60 * 60 * 24; // 1 day
    private static final int NOTIFICATION_ID = 1111;
    private static final String CHANNEL_ID = "sat_update_channel";

    private Handler handler;
    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "run: updating satellite data");
            StorageManager.getInstance(SatUpdateService.this).spSaveAndUpdateSatelliteData(SatelliteManager.getInstance());
            handler.postDelayed(this, UPDATE_INTERVAL_MILLIS);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID
                , "Satellite Auto-Updater"
                , NotificationManager.IMPORTANCE_LOW);

        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification getNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Satellite Auto-Updater")
                .setContentText("This is a foreground service that updates satellite " +
                        "data in the background every day to ensure up-to-date information.")
                .setSmallIcon(R.drawable.baseline_satellite_alt_24)
                .build();
    }
}
