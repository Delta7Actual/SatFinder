package com.example.satfinder.Activities;

import android.content.Intent;
import android.os.Bundle;
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

    private Button btnLocate;
    private SearchFragment searchFragment;
    private SatellitePosition satPosition;
    private ObserverLocation currentLocation;

    private void setupUI() {
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
        if (view.getId() == R.id.btn_locate) {
            String satIdString = ((EditText)searchFragment.getView().findViewById(R.id.et_satellite_id)).getText().toString();
            if (satIdString.isEmpty()) {
                Toast.makeText(this, "Please fill in satellite ID", Toast.LENGTH_SHORT).show();
                return;
            }
            int satId = Integer.parseInt(satIdString);
            StorageManager storageManager = StorageManager.getInstance(this);
            currentLocation = storageManager.spGetUserLocation();

            SatelliteManager satelliteManager = SatelliteManager.getInstance();
            satelliteManager.fetchSatellitePositions(satId,
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude(),
                    currentLocation.getAltitude(),
                    1,
                    new IN2YOCallback() {
                        @Override
                        public void onSuccess(ISatelliteResponse response) {
                            SatellitePositionsResponse spResponse = (SatellitePositionsResponse) response;
                            if (spResponse != null && !spResponse.getPositions().isEmpty()) {
                                satPosition = spResponse.getPositions().get(0);
                                // Only start the activity if the satellite position is successfully fetched
                                Intent intent = new Intent(BrowserActivity.this, LocateActivity.class);
                                intent.putExtra("sat_azimuth", satPosition.getAzimuth());
                                intent.putExtra("sat_angle", SatUtils.getAngleFromHorizon(currentLocation
                                        , new ObserverLocation(satPosition.getSatlatitude()
                                                , satPosition.getSatlongitude()
                                                , satPosition.getSataltitude())));
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(BrowserActivity.this, "No satellite data found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Toast.makeText(BrowserActivity.this, "Failed to fetch satellite location, make sure you have a correct ID", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}