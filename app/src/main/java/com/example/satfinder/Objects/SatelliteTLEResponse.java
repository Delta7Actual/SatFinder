package com.example.satfinder.Objects;

/**
 * Represents the response from the N2YO API for satellite TLE data.
 * Contains satellite information and a TLE string.
 */
public class SatelliteTLEResponse implements com.example.satfinder.Objects.Interfaces.ISatelliteResponse {

    private SatelliteInfo info;

    private String tle;

    // REQUIRED BY GSON
    public SatelliteTLEResponse() {
    }

    public SatelliteInfo getInfo() {
        return info;
    }

    public String getTle() {
        return tle;
    }

    public void setInfo(SatelliteInfo info) {
        this.info = info;
    }

    public void setTle(String tle) {
        this.tle = tle;
    }

    @Override
    public String toString() {
        return "SatelliteTLEResponse{" +
                "info=" + info +
                ", tle='" + tle + '\'' +
                '}';
    }
}
