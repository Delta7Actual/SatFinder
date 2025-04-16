package com.example.satfinder.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
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
import com.example.satfinder.Misc.Utility.MathUtils;
import com.example.satfinder.Objects.ObserverLocation;
import com.example.satfinder.R;
import com.example.satfinder.Services.SatUpdateService;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    private static final String TAG = "SatIntro";
    private static final int PERMISSION_REQUEST_CODE = 1234;

    private TextView tvIntroDetails;
    private LocationManager locationManager;

    private boolean isInitialized = false;
    private boolean gotLocation = false;

    private final android.location.LocationListener locationListener = this::onLocationChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intro);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvIntroDetails = findViewById(R.id.tv_intro_details);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Log.d(TAG, "onCreate: Initializing...");
        tvIntroDetails.setText("Initializing...");

        checkAndRequestPermissions();
    }

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
            Log.d(TAG, "Requesting permissions: " + permissionsNeeded);
            ActivityCompat.requestPermissions(this,
                    permissionsNeeded.toArray(new String[0]),
                    PERMISSION_REQUEST_CODE);
        } else {
            Log.d(TAG, "All permissions already granted.");
            proceedWithInitialization();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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

    private void checkForStaleData() {
        Log.d(TAG, "Checking and updating stale satellite data...");
        tvIntroDetails.setText("Updating satellite data...");
        StorageManager.getInstance(this).spSaveAndUpdateSatelliteData(SatelliteManager.getInstance(),
                () -> Log.d(TAG, "Cache save and update complete"));
    }

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

    private void getCachedLocation() {
        tvIntroDetails.setText("Retrieving cached location...");
        Log.d(TAG, "Fetching cached location...");

        ObserverLocation savedLocation = StorageManager.getInstance(this).spGetUserLocation();
        long threshold = 3 * 24 * 60 * 60; // 3 days

        boolean isStale = MathUtils.isStale(StorageManager.getInstance(this).spGetUserLocationTime(), threshold);

        if (savedLocation.getLatitude() == 0 || savedLocation.getLongitude() == 0 || savedLocation.getAltitude() == 0) {
            Log.w(TAG, "No cached location available.");
            Toast.makeText(this, "No cached location available.", Toast.LENGTH_SHORT).show();
        } else if (isStale) {
            Log.w(TAG, "Cached location is stale!");
        } else {
            Log.d(TAG, "Using cached location: " + savedLocation);
            gotLocation = true;
            proceedToNextActivity();
        }
    }

    private void getLocation() {
        tvIntroDetails.setText("Getting current location...");
        Log.d(TAG, "Fetching real-time location...");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "getLocation: Permission missing after request.");
            return;
        }

        // Try last known location first
        Location lastKnown = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        long threshold = 5 * 60 * 1000; // 5 minutes
        if (lastKnown != null && !MathUtils.isStale(lastKnown.getTime(), threshold)) {
            ObserverLocation loc = new ObserverLocation(lastKnown);
            StorageManager.getInstance(this).spSaveUserLocation(loc);
            Log.d(TAG, "Using recent last known location: " + loc);
            gotLocation = true;
            proceedToNextActivity();
            return;
        }

        Log.w(TAG, "Last known location retrieval failed or is stale, checking live updates...");
        // Fallback to live updates
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                10,
                locationListener
        );
    }

    private void proceedToNextActivity() {
        if (!gotLocation) {
            Log.e(TAG, "Failed to determine location!");
            Toast.makeText(this, "Failed to determine location! Enable GPS and restart the app.", Toast.LENGTH_SHORT).show();
            return;
        }
        tvIntroDetails.setText("Redirecting...");
        Log.d(TAG, "Redirecting to LoginActivity...");
        startActivity(new Intent(IntroActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Unregistering location listener in onDestroy");
            locationManager.removeUpdates(locationListener);
        }
    }

    private void onLocationChanged(Location location) {
        ObserverLocation observerLocation = new ObserverLocation(location);
        StorageManager.getInstance(this).spSaveUserLocation(observerLocation);
        locationManager.removeUpdates(locationListener);
        Log.d(TAG, "Location retrieved: " + observerLocation);
        gotLocation = true;
        proceedToNextActivity();
    }
}