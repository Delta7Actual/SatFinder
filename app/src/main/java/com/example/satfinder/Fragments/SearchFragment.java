package com.example.satfinder.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.satfinder.Managers.SatelliteManager;
import com.example.satfinder.Managers.StorageManager;
import com.example.satfinder.Objects.Interfaces.IN2YOCallback;
import com.example.satfinder.Objects.Interfaces.ISatelliteResponse;
import com.example.satfinder.Objects.ObserverLocation;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        etSatelliteId = view.findViewById(R.id.et_satellite_id);
        btnSearch = view.findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(v -> handleSearch());
        detailsFragment = (DetailsFragment) getChildFragmentManager().findFragmentById(R.id.details_fragment);

        return view;
    }

    private void handleSearch() {
        if (etSatelliteId.getText().toString().isEmpty()) {
            Toast.makeText(SearchFragment.this.getContext(), "Please enter a valid satellite ID!", Toast.LENGTH_LONG).show();
            return;
        }
        SatelliteManager manager = SatelliteManager.getInstance();
        int satelliteId = Integer.parseInt(etSatelliteId.getText().toString());

        manager.fetchSatelliteTLE(satelliteId, new IN2YOCallback() {

            @Override
            public void onSuccess(ISatelliteResponse response) {
                SatelliteTLEResponse tleResponse = (SatelliteTLEResponse) response;
                if (tleResponse == null) {
                    Toast.makeText(SearchFragment.this.getContext(), "Couldn't fetch data. Make sure you provided a valid ID", Toast.LENGTH_LONG).show();
                    return;
                }

                TextView tvSatName = detailsFragment.getView().findViewById(R.id.tv_satellite_name);
                TextView tvInclination = detailsFragment.getView().findViewById(R.id.tv_inclination);

                tvSatName.setText(tleResponse.getInfo().getSatname());
                tvInclination.setText(String.format("%.2f", new SatelliteTLE(tleResponse.getTle()).getInclination()));
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(SearchFragment.this.getContext(), "Couldn't fetch data. Make sure you provided a valid ID", Toast.LENGTH_LONG).show();
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
                            Toast.makeText(SearchFragment.this.getContext(), "Couldn't fetch data. Make sure you provided a valid ID", Toast.LENGTH_LONG).show();
                            return;
                        }

                        TextView tvNextPass = detailsFragment.getView().findViewById(R.id.tv_next_pass);
                        tvNextPass.setText(String.format("Next pass is in: %s", convertUTCToLocalTime(svpResponse.getPasses().get(0).getStartUTC())));
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(SearchFragment.this.getContext(), "Couldn't fetch data. Make sure you provided a valid ID", Toast.LENGTH_LONG).show();
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