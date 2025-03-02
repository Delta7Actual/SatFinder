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

    private static final String TAG = "SatFinder_MainActivity";

    private RecyclerView recyclerSatelliteTLEList;
    private SavedSatelliteAdapter adapter;
    private List<SatelliteTLEResponse> satelliteTLEResponses;
    private TextView tvISSPassDetails;

    private void setupUI() {
        Log.d(TAG, "Setting up UI components...");
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
        Log.d(TAG, "onCreate: Activity started");

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
        Log.d(TAG, "Fetching closest ISS pass...");
        StorageManager storageManager = StorageManager.getInstance(this);
        storageManager.spSaveAndUpdateSatelliteData(SatelliteManager.getInstance(), () -> {
            try {
                String result = storageManager.spGetSatelliteClosestPass(25544);
                Log.d(TAG, "Closest ISS pass raw data: " + result);

                if (result != null && !result.startsWith("err:")) {
                    String[] resultParts = result.split(",");
                    if (resultParts.length >= 2) {
                        long time = Long.parseLong(resultParts[1]);
                        tvISSPassDetails.setText(String.format("Next pass is in: %s", SatUtils.convertUTCToLocalTime(time)));
                    } else {
                        Log.e(TAG, "Unexpected ISS pass data format: " + result);
                    }
                } else {
                    Log.e(TAG, "Error fetching ISS pass or null result.");
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception while processing ISS pass data", e);
            }
        });
    }

    private void updateSavedSatelliteList() {
        Log.d(TAG, "Fetching saved satellites...");
        StorageManager storageManager = StorageManager.getInstance(this);
        storageManager.spSaveAndUpdateSatelliteData(SatelliteManager.getInstance(), new ICacheUpdateCallback() {
            @Override
            public void onComplete() {
                storageManager.getFavouriteSatelliteIds(new IStorageCallback<List<String>>() {

                    @Override
                    public void onSuccess(List<String> result) {
                        try {
                            satelliteTLEResponses.clear();
                            Log.d(TAG, "Number of saved satellites: " + result.size());

                            for (String satelliteId : result) {
                                try {
                                    Log.d(TAG, "Fetching TLE for satellite ID: " + satelliteId);
                                    String tleData = storageManager.spGetSatelliteTLE(Integer.parseInt(satelliteId));
                                    Log.d(TAG, "Raw TLE data: " + tleData);

                                    SatelliteTLEResponse tleResponse = new SatelliteTLEResponse();
                                    if (tleData != null && !tleData.startsWith("err:")) {
                                        String[] tleParts = tleData.split(",");
                                        Log.d(TAG, "TLE parts count: " + tleParts.length);

                                        if (tleParts.length >= 3) {
                                            tleResponse.setTle(tleParts[0]);
                                            tleResponse.setInfo(new SatelliteInfo(Integer.parseInt(tleParts[1]), tleParts[2]));
                                            satelliteTLEResponses.add(tleResponse);
                                            adapter.notifyItemInserted(satelliteTLEResponses.size() - 1);
                                        } else {
                                            Log.e(TAG, "Malformed TLE data: " + tleData);
                                        }
                                    } else {
                                        Log.e(TAG, "Null or error TLE data for satellite ID: " + satelliteId);
                                    }
                                } catch (NumberFormatException e) {
                                    Log.e(TAG, "Invalid satellite ID: " + satelliteId, e);
                                } catch (ArrayIndexOutOfBoundsException e) {
                                    Log.e(TAG, "Array index out of bounds while parsing TLE data", e);
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Exception while processing saved satellite list", e);
                        }
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Log.e(TAG, "Failed to fetch saved satellites: " + errorMessage);
                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}