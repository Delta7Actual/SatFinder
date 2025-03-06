package com.example.satfinder.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.satfinder.Managers.SatelliteManager;
import com.example.satfinder.Managers.StorageManager;
import com.example.satfinder.Managers.UserManager;
import com.example.satfinder.Objects.ObserverLocation;
import com.example.satfinder.R;
import com.example.satfinder.Services.SatUpdateService;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    private static final String TAG = "SatIntro";  // Updated TAG for logging
    private static final int PERMISSION_REQUEST_CODE = 1234;

    private TextView tvIntroDetails;
    private LocationManager locationManager;

    private boolean isInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intro);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            Log.d(TAG, "Applied system bar insets: " + systemBars.toString());
            return insets;
        });

        tvIntroDetails = findViewById(R.id.tv_intro_details);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Log.d(TAG, "onCreate: Initializing...");
        tvIntroDetails.setText("Initializing...");

        checkAndRequestPermissions();
    }

    /**
     * Checks and requests runtime permissions if needed.
     */
    private void checkAndRequestPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS);
        }

        if (!permissionsNeeded.isEmpty()) {
            Log.d(TAG, "Requesting permissions: " + permissionsNeeded.toString());
            ActivityCompat.requestPermissions(this,
                    permissionsNeeded.toArray(new String[0]),
                    PERMISSION_REQUEST_CODE);
        } else {
            Log.d(TAG, "All permissions already granted.");
            proceedWithInitialization();
        }
    }

    /**
     * Handles the result of permission requests.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean allGranted = true;
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }

        if (allGranted) {
            Log.d(TAG, "Permissions granted.");
            proceedWithInitialization();
        } else {
            Log.e(TAG, "Some permissions were denied.");
            Toast.makeText(this, "Permissions required for full functionality.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Proceeds with location retrieval and stale data check.
     */
    private void proceedWithInitialization() {
        if (!isInitialized) {
            Log.d(TAG, "Proceeding with initialization...");
            checkLocation();
            if (UserManager.getInstance().isUserLoggedIn()) checkForStaleData();
            tvIntroDetails.setText("Starting update service...");

            if (!SatUpdateService.isRunning) {
                Log.d(TAG, "Starting SatUpdateService...");
                startForegroundService(new Intent(this, SatUpdateService.class));
            } else {
                Log.d(TAG, "SatUpdateService already running.");
            }
            isInitialized = true;
        }
    }

    /**
     * Checks for stale satellite data and updates if necessary.
     */
    private void checkForStaleData() {
        Log.d(TAG, "Checking and updating stale satellite data...");
        tvIntroDetails.setText("Updating satellite data...");
        StorageManager.getInstance(this).spSaveAndUpdateSatelliteData(SatelliteManager.getInstance()
                , () -> Log.d(TAG, "Cache save and update complete"));
    }

    /**
     * Checks if location services are enabled and retrieves location.
     */
    private void checkLocation() {
        tvIntroDetails.setText("Checking location...");

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.w(TAG, "GPS is disabled, using last saved location.");
            Toast.makeText(this, "GPS is off, using cached location if available.", Toast.LENGTH_SHORT).show();
            getCachedLocation();
        } else {
            Log.d(TAG, "GPS is enabled, retrieving current location...");
            getLocation();
        }
    }

    /**
     * Retrieves the device's last known location if GPS is unavailable.
     */
    private void getCachedLocation() {
        ObserverLocation savedLocation = StorageManager.getInstance(this).spGetUserLocation();

        if (savedLocation.getLatitude() == 0 || savedLocation.getLongitude() == 0) {
            Log.w(TAG, "No cached location available.");
            Toast.makeText(this, "No cached location available.", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "Using cached location: " + savedLocation);
            proceedToNextActivity();
        }
    }

    /**
     * Requests location updates from the GPS provider.
     */
    private void getLocation() {
        tvIntroDetails.setText("Getting current location...");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "getLocation: Permission missing after request.");
            return;
        }

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                10,
                location -> {
                    ObserverLocation observerLocation = new ObserverLocation(location);
                    StorageManager.getInstance(this).spSaveUserLocation(observerLocation);
                    Log.d(TAG, "Location retrieved: " + observerLocation);
                    proceedToNextActivity();
                });
    }

    /**
     * Proceeds to the login activity.
     */
    private void proceedToNextActivity() {
        tvIntroDetails.setText("Redirecting...");
        Log.d(TAG, "Redirecting to LoginActivity...");
        startActivity(new Intent(IntroActivity.this, LoginActivity.class));
        finish();
    }
}