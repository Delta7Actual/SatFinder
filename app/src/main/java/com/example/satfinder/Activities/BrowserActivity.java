package com.example.satfinder.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

    private static final String TAG = "SatBrowser";
    private SearchFragment searchFragment;
    private SatellitePosition satPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_browser);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupUI();
    }

    private void setupUI() {
        Log.d(TAG, "setupUI: Initializing UI components");

        // Initialize the SearchFragment
        searchFragment = new SearchFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.browser_fragment_container, searchFragment)
                .addToBackStack(null)
                .commit();

        Button btnLocate = findViewById(R.id.btn_locate);
        btnLocate.setOnClickListener(v -> locateSatellite());
    }

    private void locateSatellite() {
        Log.d(TAG, "locateSatellite: Locate button clicked");

        if (searchFragment.getView() == null) {
            Log.e(TAG, "locateSatellite: Fragment view is null");
            Toast.makeText(this, "Unable to access search input. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText etSatelliteId = searchFragment.getView().findViewById(R.id.et_satellite_id);
        String satIdString = etSatelliteId.getText().toString().trim();

        if (satIdString.isEmpty()) {
            Log.w(TAG, "locateSatellite: Empty Satellite ID");
            Toast.makeText(this, "Please enter a satellite ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Handle invalid satellite ID
        int satId;
        try {
            satId = Integer.parseInt(satIdString);
        } catch (NumberFormatException e) {
            Log.e(TAG, "locateSatellite: Invalid Satellite ID");
            Toast.makeText(this, "Invalid satellite ID", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "locateSatellite: Fetching satellite position for ID " + satId);

        StorageManager storageManager = StorageManager.getInstance(this);
        ObserverLocation currentLocation = storageManager.spGetUserLocation();

        Log.d(TAG, "locateSatellite: Current location: " + currentLocation);

        // Fetch satellite position from API
        SatelliteManager.getInstance().fetchSatellitePositions(
                satId,
                currentLocation.getLatitude(),
                currentLocation.getLongitude(),
                currentLocation.getAltitude(),
                1,
                new IN2YOCallback() {
                    @Override
                    public void onSuccess(ISatelliteResponse response) {
                        SatellitePositionsResponse spResponse = (SatellitePositionsResponse) response;

                        if (spResponse == null || spResponse.getPositions().isEmpty()) {
                            Log.w(TAG, "locateSatellite: No satellite data found");
                            Toast.makeText(BrowserActivity.this, "No satellite data found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        satPosition = spResponse.getPositions().get(0);
                        Log.d(TAG, "locateSatellite: Satellite position: " + satPosition);

                        Intent intent = new Intent(BrowserActivity.this, LocateActivity.class);
                        intent.putExtra("sat_azimuth", satPosition.getAzimuth());
                        intent.putExtra("sat_pitch", satPosition.getElevation());
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e(TAG, "locateSatellite: API error - " + errorMessage);
                        Toast.makeText(BrowserActivity.this, "Failed to fetch satellite location. Check the ID and try again.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}