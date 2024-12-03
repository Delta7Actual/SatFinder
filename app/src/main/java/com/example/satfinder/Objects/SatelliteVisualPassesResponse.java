package com.example.satfinder.Objects;

import java.util.List;

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
