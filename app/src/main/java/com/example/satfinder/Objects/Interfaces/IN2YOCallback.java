package com.example.satfinder.Objects.Interfaces;

import com.example.satfinder.Objects.ISatelliteResponse;

public interface IN2YOCallback {
    void onCallSuccess(ISatelliteResponse response);
    void onCallError(String errorMessage);
}
