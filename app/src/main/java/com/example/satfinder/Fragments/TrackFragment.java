package com.example.satfinder.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.satfinder.R;

public class TrackFragment extends Fragment {

    public TrackFragment() {
        // Required empty public constructor
    }

    private TextView tvPlaceholder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track, container, false);

        tvPlaceholder = view.findViewById(R.id.tv_tracked);
        //testAPI();

        return view;
    }

//  private void testAPI() {
//      SatelliteManager manager = SatelliteManager.getInstance();
//
//        manager.fetchSatelliteTLE(25544, new IN2YOCallback() {
//            @Override
//            public void onCallSuccess(ISatelliteResponse response) {
//                SatelliteTLEResponse tleResponse = (SatelliteTLEResponse) response;
//                tvPlaceholder.setText(tleResponse.toString());
//            }
//
//            @Override
//            public void onCallError(String errorMessage) {
//                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
//            }
//        });
//
//        manager.fetchSatellitePositions(25544,
//                70,
//                -41,
//                230,
//                1,
//                new IN2YOCallback() {
//
//            @Override
//            public void onCallSuccess(ISatelliteResponse response) {
//                SatellitePositionsResponse positionsResponse = (SatellitePositionsResponse) response;
//                tvPlaceholder.setText(positionsResponse.toString());
//            }
//
//            @Override
//            public void onCallError(String errorMessage) {
//                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
//            }
//        });
//
//        manager.fetchSatelliteVisualPasses(25544,
//                70,
//                -41,
//                230,
//                1,
//                1,
//                new IN2YOCallback() {
//
//            @Override
//            public void onCallSuccess(ISatelliteResponse response) {
//                SatelliteVisualPassesResponse passesResponse = (SatelliteVisualPassesResponse) response;
//                tvPlaceholder.setText(passesResponse.toString());
//            }
//
//            @Override
//            public void onCallError(String errorMessage) {
//                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
//            }
//        });
    }
}