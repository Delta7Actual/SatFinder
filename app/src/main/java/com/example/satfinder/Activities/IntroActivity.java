package com.example.satfinder.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.satfinder.R;

public class IntroActivity extends AppCompatActivity {

    private Location location;

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

        // Check for location permissions and start location retrieval
        checkLocationPermissions();
    }

    private void checkLocationPermissions() {
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
                // Permissions granted, retrieve location
                getLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // GPS is disabled -> check if previous location was saved
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.e("IntroActivity", "GPS is not enabled.");
            Toast.makeText(this, "GPS is not enabled, using previous location if exists", Toast.LENGTH_SHORT).show();
            getLocationOffline();  // Check SharedPreferences if location is saved
        } else {
            // GPS enabled -> request location updates
            try {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        5000,
                        10,
                        currentLocation -> {
                            location = currentLocation;
                            saveLocationToSharedPreferences(location);
                            proceedToNextActivity();
                        });
            } catch (SecurityException e) {
                Log.e("IntroActivity", "SecurityException: " + e.getMessage());
            }
        }
    }

    private void getLocationOffline() {
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);

        String latitude = sharedPreferences.getString("latitude", null);
        String longitude = sharedPreferences.getString("longitude", null);
        String altitude = sharedPreferences.getString("altitude", null);

        if (longitude == null || latitude == null || altitude == null) {
            Toast.makeText(this, "No previous location found", Toast.LENGTH_SHORT).show();
        } else {
            location = new Location("saved_location");
            location.setLongitude(Double.parseDouble(longitude));
            location.setLatitude(Double.parseDouble(latitude));
            location.setAltitude(Double.parseDouble(altitude));
            Log.d("IntroActivity", "Retrieved saved location: " + location);
            proceedToNextActivity();
        }
    }

    // TODO upgrade to local storage with SQLite?
    private void saveLocationToSharedPreferences(Location location) {
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("longitude", String.valueOf(location.getLongitude()));
        editor.putString("latitude", String.valueOf(location.getLatitude()));
        editor.putString("altitude", String.valueOf(location.getAltitude()));

        editor.apply();
    }

    private void proceedToNextActivity() {

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, 500);
    }
}
