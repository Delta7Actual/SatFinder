package com.example.satfinder.Objects.Interfaces;

/**
 * Callback interface for handling {@link com.example.satfinder.Managers.SatelliteManager} responses.
 */
public interface IN2YOCallback {
    void onSuccess(ISatelliteResponse response);
    void onError(String errorMessage);
}