package com.example.satfinder.Objects;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * Represents the response from the N2YO API for satellite visual passes.
 * Contains satellite information and a list of visual passes for the satellite.
 */
public class SatelliteVisualPassesResponse implements com.example.satfinder.Objects.Interfaces.ISatelliteResponse {

    private SatelliteInfo info;
    private List<SatelliteVisualPass> passes;

    //REQUIRED BY GSON
    public SatelliteVisualPassesResponse() {
    }

    public SatelliteInfo getInfo() {
        return info;
    }

    public List<SatelliteVisualPass> getPasses() {
        return passes;
    }

    @NonNull
    @Override
    public String toString() {
        return "SatelliteVisualPassesResponse{" +
                "info=" + info.toString() +
                ", passes=" + passes +
                '}';
    }
}