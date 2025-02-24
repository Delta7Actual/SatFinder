package com.example.satfinder.Activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.satfinder.Adapters.SavedSatelliteAdapter;
import com.example.satfinder.Managers.SatelliteManager;
import com.example.satfinder.Managers.StorageManager;
import com.example.satfinder.Objects.Interfaces.IN2YOCallback;
import com.example.satfinder.Objects.Interfaces.ISatelliteResponse;
import com.example.satfinder.Objects.Interfaces.IStorageCallback;
import com.example.satfinder.Objects.ObserverLocation;
import com.example.satfinder.Objects.SatelliteTLEResponse;
import com.example.satfinder.Objects.SatelliteVisualPassesResponse;
import com.example.satfinder.R;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerSatelliteTLEList;
    private SavedSatelliteAdapter adapter;
    private List<SatelliteTLEResponse> satelliteTLEResponses;
    private TextView tvISSPassDetails;

    private void setupUI() {
        tvISSPassDetails = findViewById(R.id.tv_iss_pass_details);
        recyclerSatelliteTLEList = findViewById(R.id.recycler_satellite_list);
        satelliteTLEResponses = new ArrayList<>();
        adapter = new SavedSatelliteAdapter(satelliteTLEResponses);
        recyclerSatelliteTLEList.setLayoutManager(new LinearLayoutManager(this));
        recyclerSatelliteTLEList.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupUI();
        updateWithClosestISSPass();
        updateSavedSatelliteList();
    }

    private void updateWithClosestISSPass() {
        SatelliteManager satelliteManager = SatelliteManager.getInstance();
        StorageManager storageManager = StorageManager.getInstance();

        ObserverLocation observerLocation = storageManager.spGetUserLocation(this);
        satelliteManager.fetchSatelliteVisualPasses(25544,
                observerLocation.getLatitude(),
                observerLocation.getLongitude(),
                observerLocation.getAltitude(),
                7,
                30,
                new IN2YOCallback() {
                    @Override
                    public void onSuccess(ISatelliteResponse response) {
                        SatelliteVisualPassesResponse svpResponse = (SatelliteVisualPassesResponse) response;
                        if (svpResponse != null) {
                            tvISSPassDetails.setText(String.format("Next pass is in: %s",
                                    convertUTCToLocalTime(svpResponse
                                            .getPasses()
                                            .get(0)
                                            .getStartUTC())));
                        } else {
                            tvISSPassDetails.setText("No ISS sightings in the next week :)");
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateSavedSatelliteList() {
        StorageManager storageManager = StorageManager.getInstance();
        storageManager.getFavouriteSatelliteIds(new IStorageCallback<List<String>>() {

            @Override
            public void onSuccess(List<String> result) {
                satelliteTLEResponses.clear();
                for (int i = 0; i < result.size(); i++) {
                    SatelliteManager.getInstance().fetchSatelliteTLE(Integer.parseInt(result.get(i)), new IN2YOCallback() {

                        @Override
                        public void onSuccess(ISatelliteResponse response) {
                            SatelliteTLEResponse satelliteTLEResponse = (SatelliteTLEResponse) response;
                            satelliteTLEResponses.add(satelliteTLEResponse);
                            adapter.notifyItemInserted(satelliteTLEResponses.size() - 1);
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Put in a utils class?
    private String convertUTCToLocalTime(int utc) {
        Instant instant = Instant.ofEpochSecond(utc);

        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM HH:mm");

        return localDateTime.format(formatter);
    }
}
