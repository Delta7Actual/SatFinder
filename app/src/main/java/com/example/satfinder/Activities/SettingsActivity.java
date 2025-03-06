package com.example.satfinder.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.satfinder.Misc.AlarmScheduler;
import com.example.satfinder.R;
import com.example.satfinder.Services.SatUpdateService;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SatSettings"; // Updated TAG for logging

    private Button btnBack, btnKillDaemon, btnTestNotification;

    private void setupUI() {
        Log.d(TAG, "Setting up UI components...");

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            Log.d(TAG, "Back button clicked. Navigating to MainActivity.");
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        btnKillDaemon = findViewById(R.id.btn_kill_daemon);
        btnKillDaemon.setOnClickListener(v -> {
            Log.d(TAG, "Kill Daemon button clicked. Stopping SatUpdateService.");
            Intent intent = new Intent(this, SatUpdateService.class);
            stopService(intent);
        });

        btnTestNotification = findViewById(R.id.btn_test_notification);
        btnTestNotification.setOnClickListener(v -> {
            Log.d(TAG, "Test Notification button clicked. Scheduling a notification.");
            AlarmScheduler.scheduleNotification(this, System.currentTimeMillis() + 5000, 1234);
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: SettingsActivity started.");

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            Log.d(TAG, "Applied system bar insets: " + systemBars);
            return insets;
        });

        setupUI();
    }
}