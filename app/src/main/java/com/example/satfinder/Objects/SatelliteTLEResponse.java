package com.example.satfinder.Objects;

import com.google.gson.annotations.SerializedName;

public class SatelliteTLEResponse implements com.example.satfinder.Objects.ISatelliteResponse {
    @SerializedName("info")
    private SatelliteInfo info;

    @SerializedName("tle")
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
