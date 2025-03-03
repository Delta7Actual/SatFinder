package com.example.satfinder.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.satfinder.Activities.SatUtils;
import com.example.satfinder.Managers.SatelliteManager;
import com.example.satfinder.Managers.StorageManager;
import com.example.satfinder.Objects.Interfaces.IN2YOCallback;
import com.example.satfinder.Objects.Interfaces.ISatelliteResponse;
import com.example.satfinder.Objects.ObserverLocation;
import com.example.satfinder.Objects.SatellitePosition;
import com.example.satfinder.Objects.SatellitePositionsResponse;
import com.example.satfinder.Objects.SatelliteTLE;
import com.example.satfinder.Objects.SatelliteTLEResponse;
import com.example.satfinder.Objects.SatelliteVisualPassesResponse;
import com.example.satfinder.R;

public class SearchFragment extends Fragment {

    private static final String TAG = "SatFinder_SearchFragment";

    private EditText etSatelliteId;
    private Button btnSearch;
    private DetailsFragment detailsFragment;
    private FrameLayout detailsFragmentContainer;

    public SearchFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Initializing SearchFragment");
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        etSatelliteId = view.findViewById(R.id.et_satellite_id);
        btnSearch = view.findViewById(R.id.btn_search);
        detailsFragmentContainer = view.findViewById(R.id.fragment_container);

        // Initialize DetailsFragment
        detailsFragment = new DetailsFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailsFragment)
                .commit();

        btnSearch.setOnClickListener(v -> handleSearch());

        return view;
    }

    private void searchFailure(String message) {
        Log.e(TAG, "Search failure: " + message);
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    private void handleSearch() {
        Log.d(TAG, "Search button clicked");
        String idText = etSatelliteId.getText().toString().trim();
        if (idText.isEmpty()) {
            searchFailure("Please enter a valid satellite ID!");
            return;
        }

        int satelliteId;
        try {
            satelliteId = Integer.parseInt(idText);
            Log.d(TAG, "Parsed satellite ID: " + satelliteId);
        } catch (NumberFormatException e) {
            searchFailure("Invalid ID format.");
            Log.e(TAG, "Invalid ID format: " + idText, e);
            return;
        }

        StorageManager storageManager = StorageManager.getInstance(getContext());
        storageManager.spSaveAndUpdateSatelliteData(SatelliteManager.getInstance()
                , () -> getSatelliteData(satelliteId));
    }

    private void getSatelliteData(int satelliteId) {
        StorageManager storageManager = StorageManager.getInstance(getContext());

        // Fetch TLE data
        String tleData = storageManager.spGetSatelliteTLE(satelliteId);
        Log.d(TAG, "TLE data: " + tleData);
        if (tleData != null && !tleData.startsWith("err:") && !tleData.equals("NONE,,")) {
            displayTLEData(tleData);
        } else {
            Log.w(TAG, "Failed to fetch TLE data. Calling API...");
            fetchSatelliteTLE(satelliteId);
        }

        // Fetch Visual Pass
        String passData = storageManager.spGetSatelliteClosestPass(satelliteId);
        Log.d(TAG, "Pass data: " + passData);
        if (passData != null && !passData.startsWith("err:") && !passData.equals("NONE,,")) {
            displayNextPass(passData);
        } else {
            Log.w(TAG, "Failed to fetch next pass data. Calling API...");
            fetchSatelliteVisualPass(satelliteId, storageManager.spGetUserLocation());
        }

        // Fetch Satellite Position
        String posData = storageManager.spGetSatellitePos(satelliteId);
        Log.d(TAG, "Position data: " + posData);
        if (posData != null && !posData.startsWith("err:") && !posData.equals("NONE,,")) {
            displaySatellitePosition(posData);
        } else {
            Log.w(TAG, "Failed to fetch satellite position.");
            fetchSatellitePosition(satelliteId, storageManager.spGetUserLocation());
        }
    }

    private void fetchSatelliteTLE(int satelliteId) {
        SatelliteManager manager = SatelliteManager.getInstance();
        manager.fetchSatelliteTLE(satelliteId, new IN2YOCallback() {
            @Override
            public void onSuccess(ISatelliteResponse response) {
                SatelliteTLEResponse tleResponse = (SatelliteTLEResponse) response;
                if (tleResponse == null) {
                    searchFailure("Null TLE response.");
                    return;
                }
                displayTLEData(tleResponse.getTle() + ",NONE," + tleResponse.getInfo().getSatname());
            }

            @Override
            public void onError(String errorMessage) {
                searchFailure(errorMessage);
            }
        });
    }

    private void fetchSatelliteVisualPass(int satelliteId, ObserverLocation observerLocation) {
        SatelliteManager manager = SatelliteManager.getInstance();
        manager.fetchSatelliteVisualPasses(satelliteId
                ,observerLocation.getLatitude()
                ,observerLocation.getLongitude()
                ,observerLocation.getAltitude()
                ,7
                ,30
                , new IN2YOCallback() {

                    @Override
                    public void onSuccess(ISatelliteResponse response) {
                        SatelliteVisualPassesResponse svpResponse = (SatelliteVisualPassesResponse) response;
                        if (svpResponse == null) {
                            searchFailure("Null Visual-Passes response.");
                            return;
                        }
                        displayNextPass("NONE," + svpResponse
                                .getPasses()
                                .get(0).getStartUTC() + ",NONE");
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.w(TAG, errorMessage);
                    }
                });
    }

    private void fetchSatellitePosition(int satelliteId, ObserverLocation observerLocation) {
        SatelliteManager manager = SatelliteManager.getInstance();
        manager.fetchSatellitePositions(satelliteId
                , observerLocation.getLatitude()
                , observerLocation.getLongitude()
                , observerLocation.getAltitude()
                , 1
                , new IN2YOCallback() {
                    @Override
                    public void onSuccess(ISatelliteResponse response) {
                        SatellitePositionsResponse spResponse = (SatellitePositionsResponse) response;
                        if (spResponse == null) {
                            searchFailure("Null Positions response.");
                            return;
                        }
                        SatellitePosition position = spResponse.getPositions().get(0);
                        displaySatellitePosition("NONE," + position.getSatlatitude() + "," + position.getSatlongitude());
                    }

                    @Override
                    public void onError(String errorMessage) {
                        searchFailure(errorMessage);
                    }
                });
    }

    private void displayTLEData(String tleData) {
        try {
            String[] tleParts = tleData.split(",");
            if (tleParts.length >= 3) {
                SatelliteTLE tle = new SatelliteTLE(tleParts[0]);
                detailsFragment.updateSatelliteDetails(
                        tleParts[2],  // Satellite name
                        String.format("%.2f°", tle.getInclination())
                );
                detailsFragmentContainer.setVisibility(View.VISIBLE);
            } else {
                searchFailure("Malformed TLE data.");
                Log.e(TAG, "Malformed TLE data: " + tleData);
            }
        } catch (Exception e) {
            searchFailure("Error processing TLE data.");
            Log.e(TAG, "Error parsing TLE data: " + tleData, e);
        }
    }

    private void displayNextPass(String passData) {
        try {
            String[] passParts = passData.split(",");
            if (passParts.length >= 2) {
                long passTime = Long.parseLong(passParts[1]);
                detailsFragment.updateNextPass(SatUtils.convertUTCToLocalTime(passTime));
            } else {
                Log.e(TAG, "Unexpected pass data format: " + passData);
            }
        } catch (Exception e) {
            searchFailure("Error processing pass data.");
            Log.e(TAG, "Error parsing pass data: " + passData, e);
        }
    }

    private void displaySatellitePosition(String posData) {
        try {
            String[] posParts = posData.split(",");
            if (posParts.length >= 3) {
                detailsFragment.updateSatellitePosition(
                        String.format("%.2f", Float.parseFloat(posParts[1])),
                        String.format("%.2f", Float.parseFloat(posParts[2]))
                );
            } else {
                Log.e(TAG, "Unexpected position data format: " + posData);
            }
        } catch (Exception e) {
            searchFailure("Error processing position data.");
            Log.e(TAG, "Error parsing position data: " + posData, e);
        }
    }
}