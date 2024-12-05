package com.example.satfinder.Objects;

/**
 * Represents the response from the N2YO API for satellite TLE data.
 * Contains satellite information and a TLE string.
 */
public class SatelliteTLEResponse implements com.example.satfinder.Objects.Interfaces.ISatelliteResponse {

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
