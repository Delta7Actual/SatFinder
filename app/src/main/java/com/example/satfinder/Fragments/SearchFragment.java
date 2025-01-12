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
import com.example.satfinder.Objects.Interfaces.IN2YOCallback;
import com.example.satfinder.Objects.Interfaces.ISatelliteResponse;
import com.example.satfinder.Objects.SatelliteTLEResponse;
import com.example.satfinder.R;

public class SearchFragment extends Fragment {

    public SearchFragment() {
        // Required empty public constructor
    }

    private EditText etSatelliteId;
    private Button btnSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        etSatelliteId = view.findViewById(R.id.et_satellite_id);
        btnSearch = view.findViewById(R.id.btn_search);

        btnSearch.setOnClickListener(v -> handleSearch());

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
                TextView tvSatelliteName = requireActivity()
                        .getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_container)
                        .getView()
                        .findViewById(R.id.tv_satellite_name);
                tvSatelliteName.setText(tleResponse.getInfo().getSatname());
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(SearchFragment.this.getContext(), "Couldn't fetch data. Make sure you provided a valid ID", Toast.LENGTH_LONG).show();
            }
        });
    }
}