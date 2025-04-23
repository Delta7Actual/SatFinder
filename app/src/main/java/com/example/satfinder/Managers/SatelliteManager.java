package com.example.satfinder.Managers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.satfinder.Objects.Interfaces.IN2YOCallback;
import com.example.satfinder.Objects.ObserverLocation;
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

    private static final String TAG = "SATELLITE";
    private static SatelliteManager instance;
    private final N2YOClientService clientService;

    private SatelliteManager() {
        this.clientService = N2YOClientService.getInstance();
    }

    /**
     * Gets the singleton instance of SatelliteManager.
     * @return The singleton instance of SatelliteManager.
     */
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
    *  @param location      The observer's location.
     * @param days          The number of days to fetch passes for.
     * @param minVisibility The minimum visibility threshold in seconds.
     * @param callback      The callback to notify with the result or error.
     */
    public void fetchSatelliteVisualPasses(int satelliteId, ObserverLocation location, int days, int minVisibility, final IN2YOCallback callback) {

        Log.d(TAG, String.format("Requesting visual passes -> Satellite ID: %d, Lat: %.2f, Lng: %.2f, Alt: %.2f, Days: %d, Min Visibility: %d",
                satelliteId, location.getLatitude(), location.getLongitude(), location.getAltitude(), days, minVisibility));

        clientService.getSatellitePasses(
                satelliteId,
                (float) location.getLatitude(),
                (float) location.getLongitude(),
                (float) location.getAltitude(),
                days,
                minVisibility,
                new Callback<SatelliteVisualPassesResponse>() {

                    @Override
                    public void onResponse(@NonNull Call<SatelliteVisualPassesResponse> call,
                                           @NonNull Response<SatelliteVisualPassesResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "Visual passes fetched successfully: " + response.body());
                            callback.onSuccess(response.body());
                        } else {
                            Log.e(TAG, "Failed to fetch visual passes. HTTP Code: " + response.code());
                            callback.onError("Failed to fetch visual passes. Error code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<SatelliteVisualPassesResponse> call,
                                          @NonNull Throwable t) {
                        Log.e(TAG, "Error fetching visual passes: " + t.getMessage(), t);
                        callback.onError("Network error: " + t.getMessage());
                    }
                });
    }

    /**
     * Fetches the next [seconds] positions of a specified satellite.
     *
     * @param id            The ID of the satellite.
     *
     * @param seconds       The duration in seconds to fetch positions for.
     * @param callback      The callback to notify with the result or error.
     */
    public void fetchSatellitePositions(int id,
                                        ObserverLocation location,
                                        int seconds,
                                        final IN2YOCallback callback) {

        Log.d(TAG, String.format("Requesting satellite positions -> Satellite ID: %d, Lat: %.2f, Lng: %.2f, Alt: %.2f, Seconds: %d",
                id, location.getLatitude(), location.getLongitude(), location.getAltitude(), seconds));

        clientService.getSatellitePositions(id,
                location.getLatitude(),
                location.getLongitude(),
                location.getAltitude(),
                seconds,
                new Callback<SatellitePositionsResponse>() {

                    @Override
                    public void onResponse(@NonNull Call<SatellitePositionsResponse> call,
                                           @NonNull Response<SatellitePositionsResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "Satellite positions fetched successfully: " + response.body());
                            callback.onSuccess(response.body());
                        } else {
                            Log.e(TAG, "Failed to fetch satellite positions. HTTP Code: " + response.code());
                            callback.onError("Failed to fetch satellite positions. Error code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<SatellitePositionsResponse> call,
                                          @NonNull Throwable t) {
                        Log.e(TAG, "Error fetching satellite positions: " + t.getMessage(), t);
                        callback.onError("Network error: " + t.getMessage());
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
        Log.d(TAG, "Requesting TLE data for Satellite ID: " + id);

        clientService.getSatelliteTLE(id, new Callback<SatelliteTLEResponse>() {

            @Override
            public void onResponse(@NonNull Call<SatelliteTLEResponse> call,
                                   @NonNull Response<SatelliteTLEResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Satellite TLE fetched successfully: " + response.body());
                    callback.onSuccess(response.body());
                } else {
                    Log.e(TAG, "Failed to fetch satellite TLE. HTTP Code: " + response.code());
                    callback.onError("Failed to fetch satellite TLE. Error code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<SatelliteTLEResponse> call,
                                  @NonNull Throwable t) {
                Log.e(TAG, "Error fetching satellite TLE: " + t.getMessage(), t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
}