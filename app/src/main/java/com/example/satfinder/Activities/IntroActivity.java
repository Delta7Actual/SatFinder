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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.satfinder.Managers.StorageManager;
import com.example.satfinder.Objects.ObserverLocation;
import com.example.satfinder.R;

public class IntroActivity extends AppCompatActivity {

    private Location location;
    private TextView tvIntroDetails;
    
    private String TAG = "IntroActivity";

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

        Log.d(TAG, "onCreate running");
        tvIntroDetails = findViewById(R.id.tv_intro_details);
        tvIntroDetails.setText("Initializing...");

        // Check for location permissions and start location retrieval
        checkLocationPermissions();
    }

    private void checkLocationPermissions() {
        tvIntroDetails.setText("Checking for location permissions...");
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request location permissions if not granted
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1234); // 1234 is the request code
        } else {
            // Permissions granted, retrieve location
            getLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1234) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: Accepted");
                // Permissions granted, retrieve location
                getLocation();
            } else {
                Log.e(TAG, "onRequestPermissionsResult: Denied");
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getLocation() {
        tvIntroDetails.setText("Getting location...");
        // Initialize LocationManager
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // GPS is disabled -> check if previous location was saved
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.e(TAG, "getLocation: GPS disabled");
            Toast.makeText(this, "GPS is not enabled, using previous location if exists", Toast.LENGTH_SHORT).show();
            getLocationOffline();  // Check SharedPreferences if location is saved
        } else {
            // GPS enabled -> request location updates
            Log.d(TAG, "getLocation: GPS enabled");
            try {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        5000,
                        10,
                        currentLocation -> {
                            ObserverLocation observerLocation = new ObserverLocation(currentLocation);
                            StorageManager.getInstance().spSaveUserLocation(this, observerLocation);
                            proceedToNextActivity();
                        });
            } catch (SecurityException e) {
                Log.e(TAG, "SecurityException: " + e.getMessage());
            }
        }
    }

    private void getLocationOffline() {
        tvIntroDetails.setText("Checking for cached location...");
        ObserverLocation retrievedLocation = StorageManager.getInstance().spGetUserLocation(this);

        if (retrievedLocation.getLatitude() == 0 || retrievedLocation.getLongitude() == 0 || retrievedLocation.getAltitude() == 0) {
            Toast.makeText(this, "No previous location found", Toast.LENGTH_SHORT).show();
        } else {
            location = new Location("saved_location");
            location.setLatitude(retrievedLocation.getLatitude());
            location.setLongitude(retrievedLocation.getLongitude());
            location.setAltitude(retrievedLocation.getAltitude());

            Log.d(TAG, "Retrieved saved location: " + location);
            proceedToNextActivity();
        }
    }


    private void proceedToNextActivity() {
        tvIntroDetails.setText("Redirecting...");
        Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
