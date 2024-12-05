package com.example.satfinder.Objects;

public class SatelliteTLEResponse implements com.example.satfinder.Objects.ISatelliteResponse {

    private SatelliteInfo info;

    private String tle;

    public SatelliteInfo getInfo() {
        return info;
    }

    public String getTle() {
        return tle;
    }

    @Override
    public String toString() {
        return "SatelliteTLEResponse{" +
                "info=" + info +
                ", tle='" + tle + '\'' +
                '}';
    }
}
