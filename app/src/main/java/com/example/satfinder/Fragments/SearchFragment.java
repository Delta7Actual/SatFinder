package com.example.satfinder.Fragments;

import android.os.Bundle;
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
import com.example.satfinder.Objects.Interfaces.ICacheUpdateCallback;
import com.example.satfinder.Objects.Interfaces.IN2YOCallback;
import com.example.satfinder.Objects.Interfaces.ISatelliteResponse;
import com.example.satfinder.Objects.SatellitePositionsResponse;
import com.example.satfinder.Objects.SatelliteVisualPassesResponse;
import com.example.satfinder.R;

public class SearchFragment extends Fragment {

    private EditText etSatelliteId;
    private Button btnSearch;
    private DetailsFragment detailsFragment;
    private FrameLayout detailsFragmentContainer;

    public SearchFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    private void handleSearch() {
        String idText = etSatelliteId.getText().toString().trim();
        if (idText.isEmpty()) {
            searchFailure("Please enter a valid satellite ID!");
            return;
        }

        int satelliteId;
        try {
            satelliteId = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            searchFailure("Invalid ID format.");
            return;
        }

        StorageManager storageManager = StorageManager.getInstance(getContext());
        storageManager.spSaveAndUpdateSatelliteData(SatelliteManager.getInstance(), new ICacheUpdateCallback() {
            @Override
            public void onComplete() {

            }
        });

        SatelliteManager manager = SatelliteManager.getInstance();
//        ObserverLocation observerLocation = StorageManager.getInstance(getContext()).spGetUserLocation();

//        // Fetch TLE data
//        manager.fetchSatelliteTLE(satelliteId, new IN2YOCallback() {
//            @Override
//            public void onSuccess(ISatelliteResponse response) {
//                if (!(response instanceof SatelliteTLEResponse)) {
//                    searchFailure("Invalid response.");
//                    return;
//                }
//
//                SatelliteTLEResponse tleResponse = (SatelliteTLEResponse) response;
//                if (tleResponse.getTle() == null) {
//                    searchFailure("No TLE data found.");
//                    return;
//                }
//
//                SatelliteTLE tle = new SatelliteTLE(tleResponse.getTle());
//
//                // Update DetailsFragment
//                detailsFragment.updateSatelliteDetails(
//                        tleResponse.getInfo().getSatname(),
//                        String.format("%.2f°", tle.getInclination())
//                );
//
//                detailsFragmentContainer.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onError(String errorMessage) {
//                searchFailure("Failed to fetch TLE data.");
//            }
//        });

        // Fetch Visual Passes
        manager.fetchSatelliteVisualPasses(satelliteId, observerLocation.getLatitude(), observerLocation.getLongitude(),
                observerLocation.getAltitude(), 7, 30, new IN2YOCallback() {
                    @Override
                    public void onSuccess(ISatelliteResponse response) {
                        if (!(response instanceof SatelliteVisualPassesResponse)) {
                            searchFailure("Invalid response.");
                            return;
                        }

                        SatelliteVisualPassesResponse svpResponse = (SatelliteVisualPassesResponse) response;
                        if (svpResponse.getPasses().isEmpty()) {
                            searchFailure("No upcoming passes.");
                            return;
                        }

                        detailsFragment.updateNextPass(
                                SatUtils.convertUTCToLocalTime(svpResponse.getPasses().get(0).getStartUTC())
                        );
                    }

                    @Override
                    public void onError(String errorMessage) {
                        searchFailure("Failed to fetch visual passes.");
                    }
                });

        // Fetch Satellite Position
        manager.fetchSatellitePositions(satelliteId, observerLocation.getLatitude(), observerLocation.getLongitude(),
                observerLocation.getAltitude(), 1, new IN2YOCallback() {
                    @Override
                    public void onSuccess(ISatelliteResponse response) {
                        if (!(response instanceof SatellitePositionsResponse)) {
                            searchFailure("Invalid response.");
                            return;
                        }

                        SatellitePositionsResponse spResponse = (SatellitePositionsResponse) response;
                        if (spResponse.getPositions().isEmpty()) {
                            searchFailure("No position data.");
                            return;
                        }

                        detailsFragment.updateSatellitePosition(
                                String.format("%.2f", spResponse.getPositions().get(0).getSatlatitude()),
                                String.format("%.2f", spResponse.getPositions().get(0).getSatlongitude())
                        );
                    }

                    @Override
                    public void onError(String errorMessage) {
                        searchFailure("Failed to fetch position data.");
                    }
                });
    }
}