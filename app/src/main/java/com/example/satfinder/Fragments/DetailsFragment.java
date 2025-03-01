package com.example.satfinder.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.satfinder.R;

public class DetailsFragment extends Fragment {

    public DetailsFragment() {
        // Required empty public constructor
    }

    private TextView tvSatelliteName, tvInclination, tvLatitude, tvLongitude, tvNextPass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        setupUI(view);
        return view;
    }

    private void setupUI(View view) {
        tvSatelliteName = view.findViewById(R.id.tv_satellite_name);
        tvInclination = view.findViewById(R.id.tv_inclination);
        tvLatitude = view.findViewById(R.id.tv_latitude);
        tvLongitude = view.findViewById(R.id.tv_longitude);
        tvNextPass = view.findViewById(R.id.tv_next_pass);
    }

    public void updateSatellitePosition(String latitude, String longitude) {
        tvLatitude.setText(latitude);
        tvLongitude.setText(longitude);
    }

    public void updateSatelliteDetails(String satName, String satInclination) {
        tvSatelliteName.setText(satName);
        tvInclination.setText(satInclination);
    }

    public void updateNextPass(String nextPassTime) {
        tvNextPass.setText(nextPassTime);
    }
}