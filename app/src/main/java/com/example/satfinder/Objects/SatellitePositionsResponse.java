package com.example.satfinder.Objects;

import java.util.List;

public class SatellitePositionsResponse implements com.example.satfinder.Objects.ISatelliteResponse {

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

    @Override
    public String toString() {
        return "SatellitePositionsResponse{" +
                "info=" + info.toString() +
                ", positions=" + positions +
                '}';
    }
}
