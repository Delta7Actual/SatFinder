package com.example.satfinder.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.satfinder.Fragments.SearchFragment;
import com.example.satfinder.Managers.SatelliteManager;
import com.example.satfinder.Managers.StorageManager;
import com.example.satfinder.Objects.Interfaces.IN2YOCallback;
import com.example.satfinder.Objects.Interfaces.ISatelliteResponse;
import com.example.satfinder.Objects.ObserverLocation;
import com.example.satfinder.Objects.SatellitePosition;
import com.example.satfinder.Objects.SatellitePositionsResponse;
import com.example.satfinder.R;

public class BrowserActivity extends AppCompatActivity {

    private static final String TAG = "SatBrowser";  // Tag for logging
    private Button btnLocate;
    private SearchFragment searchFragment;
    private SatellitePosition satPosition;
    private ObserverLocation currentLocation;

    private void setupUI() {
        Log.d(TAG, "Setting up UI");
        searchFragment = new SearchFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.browser_fragment_container, searchFragment)
                .addToBackStack(null)
                .commit();
        btnLocate = findViewById(R.id.btn_locate);
        btnLocate.setOnClickListener(this::locateSatellite);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Activity created");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_browser);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupUI();
    }

    private void locateSatellite(View view) {
        Log.d(TAG, "Locate button clicked");
        if (view.getId() == R.id.btn_locate) {
            String satIdString = ((EditText) searchFragment.getView().findViewById(R.id.et_satellite_id)).getText().toString();
            if (satIdString.isEmpty()) {
                Log.w(TAG, "Satellite ID field is empty");
                Toast.makeText(this, "Please fill in satellite ID", Toast.LENGTH_SHORT).show();
                return;
            }
            int satId = Integer.parseInt(satIdString);
            Log.d(TAG, "Fetching satellite position for ID: " + satId);

            StorageManager storageManager = StorageManager.getInstance(this);
            currentLocation = storageManager.spGetUserLocation();
            Log.d(TAG, "Current location fetched: " + currentLocation);

            SatelliteManager satelliteManager = SatelliteManager.getInstance();
            satelliteManager.fetchSatellitePositions(satId,
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude(),
                    currentLocation.getAltitude(),
                    1,
                    new IN2YOCallback() {
                        @Override
                        public void onSuccess(ISatelliteResponse response) {
                            Log.d(TAG, "Satellite positions fetched successfully");
                            SatellitePositionsResponse spResponse = (SatellitePositionsResponse) response;
                            if (spResponse != null && !spResponse.getPositions().isEmpty()) {
                                satPosition = spResponse.getPositions().get(0);
                                Log.d(TAG, "Satellite position: " + satPosition);

                                // Only start the activity if the satellite position is successfully fetched
                                Intent intent = new Intent(BrowserActivity.this, LocateActivity.class);
                                intent.putExtra("sat_azimuth", satPosition.getAzimuth());
                                intent.putExtra("sat_pitch", satPosition.getElevation());
                                startActivity(intent);
                                finish();
                            } else {
                                Log.w(TAG, "No satellite data found");
                                Toast.makeText(BrowserActivity.this, "No satellite data found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.e(TAG, "Error fetching satellite location: " + errorMessage);
                            Toast.makeText(BrowserActivity.this, "Failed to fetch satellite location, make sure you have a correct ID", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}