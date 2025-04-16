package com.example.satfinder.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.satfinder.Managers.SatelliteManager;
import com.example.satfinder.Managers.StorageManager;
import com.example.satfinder.Managers.UserManager;
import com.example.satfinder.Misc.AlarmScheduler;
import com.example.satfinder.Objects.Interfaces.IN2YOCallback;
import com.example.satfinder.Objects.Interfaces.ISatelliteResponse;
import com.example.satfinder.Objects.Interfaces.IUserAuthCallback;
import com.example.satfinder.R;
import com.example.satfinder.Services.SatUpdateService;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SatSettings"; // Updated TAG for logging

    private Button btnUpdateCache, btnKillDaemon, btnTestNotification, btnTestAPI, btnClearCache, btnDeleteUser, btnBack;

    private void setupUI() {
        Log.d(TAG, "Setting up UI components...");

        btnKillDaemon = findViewById(R.id.btn_kill_daemon);
        btnKillDaemon.setOnClickListener(v -> killDaemon());

        btnTestNotification = findViewById(R.id.btn_test_notification);
        btnTestNotification.setOnClickListener(v -> testNotification());

        btnTestAPI = findViewById(R.id.btn_test_api);
        btnTestAPI.setOnClickListener(v -> testAPI());

        btnClearCache = findViewById(R.id.btn_clear_cache);
        btnClearCache.setOnClickListener(v -> clearCache());

        btnDeleteUser = findViewById(R.id.btn_delete_user);
        btnDeleteUser.setOnClickListener(v -> deleteUser());

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            Log.d(TAG, "Back button clicked. Navigating to MainActivity.");
            startActivity(new Intent(this, MainActivity.class));
            finish();
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

    private void updateCache() {
        Log.d(TAG, "Update Cache button clicked. Retrieving and updaing cache...");
        StorageManager.getInstance(this).spSaveAndUpdateSatelliteData(SatelliteManager.getInstance(), () -> {
            Toast.makeText(SettingsActivity.this, "Cache updated successfully!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Cache save and update complete");
        });
    }

    private void killDaemon() {
        Log.d(TAG, "Kill Daemon button clicked. Stopping SatUpdateService...");
        Intent intent = new Intent(this, SatUpdateService.class);
        stopService(intent);
    }

    private void testNotification() {
        Log.d(TAG, "Test Notification button clicked. Scheduling a notification...");
        AlarmScheduler.scheduleNotification(this,
                System.currentTimeMillis() + 5000,
                1234);
    }

    private void testAPI() {
        Log.d(TAG, "Test API button clicked. Calling API...");
        SatelliteManager manager = SatelliteManager.getInstance();
        manager.fetchSatelliteTLE(25544, new IN2YOCallback() {

            @Override
            public void onSuccess(ISatelliteResponse response) {
                Log.d(TAG, "API test success. Servers are Online!");
                Toast.makeText(SettingsActivity.this, "API status: ONLINE!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "API test failure. Servers are Offline!");
                Toast.makeText(SettingsActivity.this, "API status: OFFLINE!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearCache() {
        Log.d(TAG, "Clear Cache button clicked. Clearing cache...");

        StorageManager manager = StorageManager.getInstance(this);
        manager.spClearSatelliteData(() -> {
            Log.d(TAG, "Cache cleared successfully");
            Toast.makeText(SettingsActivity.this, "Cache data cleared successfully!", Toast.LENGTH_SHORT).show();
        });
    }

    private void deleteUser() {
        Log.d(TAG, "Delete User button clicked. Deleting user...");
        UserManager manager = UserManager.getInstance();
        manager.deleteUser(new IUserAuthCallback() {

            @Override
            public void onSuccess(FirebaseUser user) {
                Log.d(TAG, "User deleted successfully. Signing out...");
                manager.logOutUser(new IUserAuthCallback() {

                    @Override
                    public void onSuccess(FirebaseUser user) {
                        Log.d(TAG, "User successfully logged out. Redirecting...");
                        Toast.makeText(SettingsActivity.this, "Redirecting to Login page!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e(TAG, "Failed to logout user! - " + error);
                        Toast.makeText(SettingsActivity.this, "Failed to logout user, Please restart app!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                        finish();
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Failed to delete current user! - " + error);
                Toast.makeText(SettingsActivity.this, "Failed to delete user, Try again later!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}