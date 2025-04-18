package com.example.satfinder.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

    private boolean isInitialized = false; // Prevents multiple initialization
    private boolean gotLocation = false; // Did we get the user's location?

    private final android.location.LocationListener locationListener = this::onLocationChanged;

    @SuppressLint("SetTextI18n")
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

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        setupUI();
        checkAndRequestPermissions();
    }

    @SuppressLint("SetTextI18n")
    private void setupUI() {
        Log.d(TAG, "setupUI: Initializing UI components");
        tvIntroDetails = findViewById(R.id.tv_intro_details);
        tvIntroDetails.setText("Initializing...");
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

        if (!permissionsNeeded.isEmpty()) { // Some permissions need to be requested
            Log.d(TAG, "Requesting permissions: " + permissionsNeeded);
            ActivityCompat.requestPermissions(this,
                    permissionsNeeded.toArray(new String[0]),
                    PERMISSION_REQUEST_CODE);
        } else { // Permissions already granted
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

    @SuppressLint("SetTextI18n")
    private void proceedWithInitialization() {
        if (isInitialized) return; // Already initialized

        Log.d(TAG, "Proceeding with initialization...");
        checkLocation();

        if (UserManager.getInstance().isUserLoggedIn()) {
            checkForStaleData();
        }

        tvIntroDetails.setText("Starting update service...");
        if (!SatUpdateService.isRunning) {
            Log.d(TAG, "Starting SatUpdateService...");
            startForegroundService(new Intent(this, SatUpdateService.class));
        } else {
            Log.d(TAG, "SatUpdateService already running.");
        }

        isInitialized = true;
    }

    @SuppressLint("SetTextI18n")
    private void checkForStaleData() {
        Log.d(TAG, "Checking and updating stale satellite data...");

        tvIntroDetails.setText("Updating satellite data...");
        StorageManager.getInstance(this).spSaveAndUpdateSatelliteData(SatelliteManager.getInstance(),
                () -> Log.d(TAG, "Cache save and update complete"));
    }

    @SuppressLint("SetTextI18n")
    private void checkLocation() {
        tvIntroDetails.setText("Checking location...");

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.w(TAG, "GPS is disabled. Using cached location...");
            Toast.makeText(this, "GPS is off, using cached location if available.", Toast.LENGTH_SHORT).show();
            getCachedLocation();
        } else {
            Log.d(TAG, "GPS is enabled. Fetching current location...");
            getLocation();
        }
    }

    @SuppressLint("SetTextI18n")
    private void getCachedLocation() {
        tvIntroDetails.setText("Retrieving cached location...");
        ObserverLocation savedLocation = StorageManager.getInstance(this).spGetUserLocation();
        long threshold = 3 * 24 * 60 * 60; // 3 days

        boolean isStale = MathUtils.isStale(
                StorageManager.getInstance(this).spGetUserLocationTime(), threshold);

        if (savedLocation.getLatitude() == 0 || savedLocation.getLongitude() == 0 || savedLocation.getAltitude() == 0) {
            Log.w(TAG, "No cached location available.");
            Toast.makeText(this, "No cached location available.", Toast.LENGTH_SHORT).show();
        } else if (isStale) {
            Log.w(TAG, "Cached location is stale.");
        } else {
            Log.d(TAG, "Using cached location: " + savedLocation);
            gotLocation = true;
            proceedToNextActivity();
        }
    }

    private void getLocation() {
        tvIntroDetails.setText(R.string.getting_current_location);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "getLocation: Location permission not granted.");
            return;
        }

        try {
            Location lastKnown = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            long threshold = 10 * 60 * 1000; // 10 minutes

            if (lastKnown != null && !MathUtils.isStale(lastKnown.getTime(), threshold)) {
                ObserverLocation loc = new ObserverLocation(lastKnown);
                StorageManager.getInstance(this).spSaveUserLocation(loc);
                Log.d(TAG, "Using recent last known location: " + loc);
                gotLocation = true;
                proceedToNextActivity();
                return;
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to get last known location: " + e.getMessage());
        }

        Log.d(TAG, "Requesting real-time location updates...");
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                10,
                locationListener
        );
    }

    private void onLocationChanged(Location location) {
        ObserverLocation observerLocation = new ObserverLocation(location);
        StorageManager.getInstance(this).spSaveUserLocation(observerLocation);
        locationManager.removeUpdates(locationListener);

        Log.d(TAG, "Location acquired: " + observerLocation);
        gotLocation = true;
        proceedToNextActivity();
    }

    private void proceedToNextActivity() {
        if (!gotLocation) { // Do not proceed if we didn't get the location
            Log.e(TAG, "Location fetch failed.");
            Toast.makeText(this, "Unable to determine location. Enable GPS and restart.", Toast.LENGTH_SHORT).show();
            return;
        }

        tvIntroDetails.setText(R.string.redirecting);
        Log.d(TAG, "Redirecting to LoginActivity in 2s...");

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(IntroActivity.this, LoginActivity.class));
            finish();
        }, 2000);
    }

    // Cleanup
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Cleaning up: Removing location updates...");
            locationManager.removeUpdates(locationListener);
        }
    }
}