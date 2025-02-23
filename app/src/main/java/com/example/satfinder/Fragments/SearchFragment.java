package com.example.satfinder.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.satfinder.Managers.SatelliteManager;
import com.example.satfinder.Managers.StorageManager;
import com.example.satfinder.Objects.Interfaces.IN2YOCallback;
import com.example.satfinder.Objects.Interfaces.ISatelliteResponse;
import com.example.satfinder.Objects.ObserverLocation;
import com.example.satfinder.Objects.SatellitePositionsResponse;
import com.example.satfinder.Objects.SatelliteTLE;
import com.example.satfinder.Objects.SatelliteTLEResponse;
import com.example.satfinder.Objects.SatelliteVisualPassesResponse;
import com.example.satfinder.R;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class SearchFragment extends Fragment {

    public SearchFragment() {
        // Required empty public constructor
    }

    private EditText etSatelliteId;
    private Button btnSearch;
    private DetailsFragment detailsFragment;
    private FrameLayout detailsFragmentContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        etSatelliteId = view.findViewById(R.id.et_satellite_id);
        btnSearch = view.findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(v -> handleSearch());
        detailsFragment = (DetailsFragment) getChildFragmentManager().findFragmentById(R.id.details_fragment);
        detailsFragmentContainer = view.findViewById(R.id.fragment_container);

        return view;
    }

    private void searchFailure() {
        Toast.makeText(SearchFragment.this.getContext(), "Please enter a valid satellite ID!", Toast.LENGTH_LONG).show();
    }

    private void handleSearch() {
        if (etSatelliteId.getText().toString().isEmpty()) {
            searchFailure();
            return;
        }
        SatelliteManager manager = SatelliteManager.getInstance();
        int satelliteId = Integer.parseInt(etSatelliteId.getText().toString());

        manager.fetchSatelliteTLE(satelliteId, new IN2YOCallback() {
            @Override
            public void onSuccess(ISatelliteResponse response) {
                SatelliteTLEResponse tleResponse = (SatelliteTLEResponse) response;
                if (tleResponse == null) {
                    searchFailure();
                    return;
                }

                TextView tvSatName = detailsFragment.getView().findViewById(R.id.tv_satellite_name);
                TextView tvInclination = detailsFragment.getView().findViewById(R.id.tv_inclination);

                tvSatName.setText(tleResponse.getInfo().getSatname());
                tvInclination.setText(String.format("%.2f", new SatelliteTLE(tleResponse.getTle()).getInclination()));
                detailsFragmentContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(String errorMessage) {
                searchFailure();
            }
        });
        ObserverLocation observerLocation = StorageManager.getInstance().spGetUserLocation(this.getContext());
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
                            searchFailure();
                            return;
                        }

                        TextView tvNextPass = detailsFragment.getView().findViewById(R.id.tv_next_pass);
                        tvNextPass.setText(String.format("Next pass is in: %s", convertUTCToLocalTime(svpResponse.getPasses().get(0).getStartUTC())));
                    }

                    @Override
                    public void onError(String errorMessage) {
                        searchFailure();
                    }
                });
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
                            searchFailure();
                            return;
                        }

                        TextView tvLatitude = detailsFragment.getView().findViewById(R.id.tv_latitude);
                        TextView tvLongitude = detailsFragment.getView().findViewById(R.id.tv_longitude);
                        tvLatitude.setText(String.format("%.2f", spResponse.getPositions().get(0).getSatlatitude()));
                        tvLongitude.setText(String.format("%.2f", spResponse.getPositions().get(0).getSatlongitude()));
                    }

                    @Override
                    public void onError(String errorMessage) {
                        searchFailure();
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