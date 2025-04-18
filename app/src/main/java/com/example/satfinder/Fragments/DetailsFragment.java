package com.example.satfinder.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.satfinder.R;

public class DetailsFragment extends Fragment {

    private static final String TAG = "SatDetailsF";

    private TextView tvSatelliteName;
    private TextView tvInclination;
    private TextView tvLatitude;
    private TextView tvLongitude;
    private TextView tvNextPass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        setupUI(view);
        return view;
    }

    private void setupUI(View view) {
        Log.d(TAG, "Initializing UI components...");

        tvSatelliteName = view.findViewById(R.id.tv_satellite_name);
        tvInclination = view.findViewById(R.id.tv_inclination);
        tvLatitude = view.findViewById(R.id.tv_latitude);
        tvLongitude = view.findViewById(R.id.tv_longitude);
        tvNextPass = view.findViewById(R.id.tv_next_pass);
    }

    public void updateSatellitePosition(String latitude, String longitude) {
        Log.d(TAG, "updateSatellitePosition: Latitude=" + latitude + ", Longitude=" + longitude);
        tvLatitude.setText(latitude);
        tvLongitude.setText(longitude);
    }

    public void updateSatelliteDetails(String satName, String satInclination) {
        Log.d(TAG, "updateSatelliteDetails: Name=" + satName + ", Inclination=" + satInclination);
        tvSatelliteName.setText(satName);
        tvInclination.setText(satInclination);
    }

    public void updateNextPass(String nextPassTime) {
        Log.d(TAG, "updateNextPass: Next Pass=" + nextPassTime);
        tvNextPass.setText(nextPassTime);
    }
}