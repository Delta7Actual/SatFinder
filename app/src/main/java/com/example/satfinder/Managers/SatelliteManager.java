package com.example.satfinder.Managers;

import androidx.annotation.NonNull;

import com.example.satfinder.Objects.Interfaces.IN2YOCallback;
import com.example.satfinder.Objects.SatellitePositionsResponse;
import com.example.satfinder.Objects.SatelliteTLEResponse;
import com.example.satfinder.Objects.SatelliteVisualPassesResponse;
import com.example.satfinder.Services.N2YOClientService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Manages satellite-related operations, including fetching data via the N2YO API.
 */
public class SatelliteManager {

    private static SatelliteManager instance;
    private final N2YOClientService clientService;

    private SatelliteManager() {
        this.clientService = N2YOClientService.getInstance();
    }

    public static synchronized SatelliteManager getInstance() {
        if (instance == null) {
            instance = new SatelliteManager();
        }
        return instance;
    }

    /**
     * Fetches visual passes for a specified satellite and observer location.
     *
     * @param satelliteId   The ID of the satellite.
     * @param latitude      The observer's latitude.
     * @param longitude     The observer's longitude.
     * @param altitude      The observer's altitude in meters.
     * @param days          The number of days to fetch passes for.
     * @param minVisibility The minimum visibility threshold in seconds.
     * @param callback      The callback to notify with the result or error.
     */
    public void fetchSatelliteVisualPasses(int satelliteId,
                                           double latitude,
                                           double longitude,
                                           double altitude,
                                           int days,
                                           int minVisibility,
                                           final IN2YOCallback callback) {

        clientService.getSatellitePasses(
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
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Failed to fetch visual passes.");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<SatelliteVisualPassesResponse> call,
                                          @NonNull Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });
    }

    /**
     * Fetches the next [seconds] positions of a specified satellite.
     *
     * @param id            The ID of the satellite.
     * @param observer_lat  The observer's latitude.
     * @param observer_lng  The observer's longitude.
     * @param observer_alt  The observer's altitude in meters.
     * @param seconds       The duration in seconds to fetch positions for.
     * @param callback      The callback to notify with the result or error.
     */
    public void fetchSatellitePositions(int id,
                                        float observer_lat,
                                        float observer_lng,
                                        float observer_alt,
                                        int seconds,
                                        final IN2YOCallback callback) {

        clientService.getSatellitePositions(id,
                observer_lat,
                observer_lng,
                observer_alt,
                seconds,
                new Callback<SatellitePositionsResponse>() {

                    @Override
                    public void onResponse(@NonNull Call<SatellitePositionsResponse> call,
                                           @NonNull Response<SatellitePositionsResponse> response) {
                        if (response.isSuccessful()) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Failed to fetch satellite position.");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<SatellitePositionsResponse> call,
                                          @NonNull Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });
    }

    /**
     * Fetches the TLE data for a specified satellite.
     *
     * @param id       The ID of the satellite.
     * @param callback The callback to notify with the result or error.
     */
    public void fetchSatelliteTLE(int id, final IN2YOCallback callback) {
        clientService.getSatelliteTLE(id, new Callback<SatelliteTLEResponse>() {

            @Override
            public void onResponse(Call<SatelliteTLEResponse> call,
                                   Response<SatelliteTLEResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch satellite TLE.");
                }
            }

            @Override
            public void onFailure(Call<SatelliteTLEResponse> call,
                                  Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}
