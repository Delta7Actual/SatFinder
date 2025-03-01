package com.example.satfinder.Activities;

import android.os.Bundle;
import android.util.Log;
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
import com.example.satfinder.Objects.Interfaces.ICacheUpdateCallback;
import com.example.satfinder.Objects.Interfaces.IStorageCallback;
import com.example.satfinder.Objects.SatelliteInfo;
import com.example.satfinder.Objects.SatelliteTLEResponse;
import com.example.satfinder.R;

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
//        SatelliteManager satelliteManager = SatelliteManager.getInstance();
        StorageManager storageManager = StorageManager.getInstance(this);
        storageManager.spSaveAndUpdateSatelliteData(SatelliteManager.getInstance(), new ICacheUpdateCallback() {

            @Override
            public void onComplete() {
                String result = storageManager.spGetSatelliteClosestPass(25544);
                if (!result.isEmpty()) {
                    tvISSPassDetails.setText(String.format("Next pass is in: %s", SatUtils.convertUTCToLocalTime(Long.parseLong(result.split(",")[1]))));
                }
//        ObserverLocation observerLocation = storageManager.spGetUserLocation();
//        satelliteManager.fetchSatelliteVisualPasses(25544,
//                observerLocation.getLatitude(),
//                observerLocation.getLongitude(),
//                observerLocation.getAltitude(),
//                7,
//                30,
//                new IN2YOCallback() {
//                    @Override
//                    public void onSuccess(ISatelliteResponse response) {
//                        SatelliteVisualPassesResponse svpResponse = (SatelliteVisualPassesResponse) response;
//                        if (svpResponse != null) {
//                            tvISSPassDetails.setText(String.format("Next pass is in: %s",
//                                    SatUtils.convertUTCToLocalTime(svpResponse
//                                            .getPasses()
//                                            .get(0)
//                                            .getStartUTC())));
//                        } else {
//                            tvISSPassDetails.setText("No ISS sightings in the next week :)");
//                        }
//                    }
//
//                    @Override
//                    public void onError(String errorMessage) {
//                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
//                    }
//                });
            }
        });
    }

    private void updateSavedSatelliteList() {
        StorageManager storageManager = StorageManager.getInstance(this);
        // Refreshes data from storage
        storageManager.spSaveAndUpdateSatelliteData(SatelliteManager.getInstance(), new ICacheUpdateCallback() {
            @Override
            public void onComplete() {
                storageManager.getFavouriteSatelliteIds(new IStorageCallback<List<String>>() {

                    @Override
                    public void onSuccess(List<String> result) {
                        satelliteTLEResponses.clear();
                        Log.d("HELP", "" + result.size());
                        for (int i = 0; i < result.size(); i++) {
                            String tleData = storageManager.spGetSatelliteTLE(Integer.parseInt(result.get(i)));
                            SatelliteTLEResponse tleResponse = new SatelliteTLEResponse();
                            if (tleData != null) {
                                    tleResponse.setTle(tleData.split(",")[0]);
                                    Log.d("HELP", tleData);
                                    Log.d("HELP", result.get(i));
                                    Log.d("HELP", storageManager.spGetSatelliteTLE(Integer.parseInt(result.get(i))));

                                    tleResponse.setInfo(new SatelliteInfo(Integer.parseInt(tleData.split(",")[1]), tleData.split(",")[2]));
                            }
                            satelliteTLEResponses.add(tleResponse);
                            adapter.notifyItemInserted(satelliteTLEResponses.size() - 1);
//                    SatelliteManager.getInstance().fetchSatelliteTLE(Integer.parseInt(result.get(i)), new IN2YOCallback() {
//
//                        @Override
//                        public void onSuccess(ISatelliteResponse response) {
//                            SatelliteTLEResponse satelliteTLEResponse = (SatelliteTLEResponse) response;
//                            satelliteTLEResponses.add(satelliteTLEResponse);
//                            adapter.notifyItemInserted(satelliteTLEResponses.size() - 1);
//                        }
//
//                        @Override
//                        public void onError(String errorMessage) {
//                            Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
//                        }
//                    });
                        }
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
