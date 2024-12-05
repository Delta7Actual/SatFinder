package com.example.satfinder.Objects;

/**
 * Represents a position for a given satellite.
 */
public class SatellitePosition {

    private float satlatitude;
    private float satlongitude;
    private float sataltitude;
    private float azimuth;
    private float elevation;
    private float ra;
    private float dec;
    private long timestamp;

    //REQUIRED BY GSON
    public SatellitePosition() {
    }

    public float getSatlatitude() {
        return satlatitude;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public float getDec() {
        return dec;
    }

    public float getRa() {
        return ra;
    }

    public float getElevation() {
        return elevation;
    }

    public float getAzimuth() {
        return azimuth;
    }

    public float getSataltitude() {
        return sataltitude;
    }

    public float getSatlongitude() {
        return satlongitude;
    }

    @Override
    public String toString() {
        return "SatellitePosition{" +
                "satlatitude=" + satlatitude +
                ", satlongitude=" + satlongitude +
                ", sataltitude=" + sataltitude +
                ", azimuth=" + azimuth +
                ", elevation=" + elevation +
                ", ra=" + ra +
                ", dec=" + dec +
                ", timestamp=" + timestamp +
                '}';
    }
}
