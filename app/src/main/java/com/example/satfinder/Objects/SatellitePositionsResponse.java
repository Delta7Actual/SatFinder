package com.example.satfinder.Objects;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * Represents the response from the N2YO API for satellite positions.
 * Contains satellite information and a list of future positions for the satellite.
 */
public class SatellitePositionsResponse implements com.example.satfinder.Objects.Interfaces.ISatelliteResponse {

    private SatelliteInfo info;
    private List<SatellitePosition> positions;

    //REQUIRED BY GSON
    public SatellitePositionsResponse() {
    }

    public SatelliteInfo getInfo() {
        return info;
    }

    public List<SatellitePosition> getPositions() {
        return positions;
    }

    @NonNull
    @Override
    public String toString() {
        return "SatellitePositionsResponse{" +
                "info=" + info.toString() +
                ", positions=" + positions +
                '}';
    }
}