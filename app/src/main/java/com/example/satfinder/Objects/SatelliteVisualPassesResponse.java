package com.example.satfinder.Objects;

import java.util.List;

/**
 * Represents the response from the N2YO API for satellite visual passes.
 * Contains satellite information and a list of visual passes for the satellite.
 */
public class SatelliteVisualPassesResponse implements com.example.satfinder.Objects.ISatelliteResponse {

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

    @Override
    public String toString() {
        return "SatelliteVisualPassesResponse{" +
                "info=" + info.toString() +
                ", passes=" + passes +
                '}';
    }
}
