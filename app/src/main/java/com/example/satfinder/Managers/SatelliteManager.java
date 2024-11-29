package com.example.satfinder.Managers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.satfinder.Objects.SatellitePosition;
import com.example.satfinder.Objects.SatellitePositionsResponse;
import com.example.satfinder.Objects.SatelliteVisualPass;
import com.example.satfinder.Objects.SatelliteVisualPassesResponse;
import com.example.satfinder.Services.N2YOClientService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SatelliteManager {
    private final N2YOClientService clientService;

    public SatelliteManager() {
        clientService = new N2YOClientService();
    }

    // Fetch satellite visual passes
    public void fetchVisualPasses(int satelliteId,
                                  double latitude,
                                  double longitude,
                                  double altitude,
                                  int days,
                                  int minVisibility,
                                  String apiKey) {

        clientService.getSatellitePasses(apiKey,
                satelliteId,
                (float) latitude,
                (float) longitude,
                (float) altitude,
                days,
                minVisibility,
                new Callback<SatelliteVisualPassesResponse>() {

            @Override
            public void onResponse(@NonNull Call<SatelliteVisualPassesResponse> call,
                                   @NonNull Response<SatelliteVisualPassesResponse> response) {
                if (response.isSuccessful()) {
                    handleVisualPassesResponse(response.body());
                } else {
                    handleErrorResponse("Failed to fetch visual passes.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<SatelliteVisualPassesResponse> call,
                                  @NonNull Throwable t) {
                handleError(t);
            }
        });
    }

    // Fetch satellite position
    public void fetchSatellitePosition(String apiKey,
                                       int id,
                                       float observer_lat,
                                       float observer_lng,
                                       float observer_alt,
                                       int seconds) {

        clientService.getSatellitePositions(apiKey,
                id,
                observer_lat,
                observer_lng,
                observer_alt,
                seconds, new
                        Callback<SatellitePositionsResponse>() {

            @Override
            public void onResponse(@NonNull Call<SatellitePositionsResponse> call,
                                   @NonNull Response<SatellitePositionsResponse> response) {
                if (response.isSuccessful()) {
                    handlePositionResponse(response.body());
                } else {
                    handleErrorResponse("Failed to fetch satellite position.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<SatellitePositionsResponse> call,
                                  @NonNull Throwable t) {
                handleError(t);
            }
        });
    }

    private void handleVisualPassesResponse(SatelliteVisualPassesResponse response) {
        if (response != null && response.getPasses() != null) {
            List<SatelliteVisualPass> passes = response.getPasses();
            // Process and update UI with passes
        } else {
            handleErrorResponse("No visual passes data available.");
        }
    }

    private void handlePositionResponse(SatellitePositionsResponse response) {
        if (response != null && response.getPosition() != null) {
            SatellitePosition position = response.getPosition();
            // Process and update UI with position data
        } else {
            handleErrorResponse("No satellite position data available.");
        }
    }

    private void handleErrorResponse(String errorMessage) {
        Log.e("SatelliteManager", errorMessage);
    }

    private void handleError(Throwable t) {
        Log.e("SatelliteManager", "API call failed", t);
    }
}
