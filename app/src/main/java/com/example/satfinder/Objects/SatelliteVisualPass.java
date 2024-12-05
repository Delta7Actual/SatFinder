package com.example.satfinder.Objects;

/**
 * Represents a visual pass for a given satellite.
 */
public class SatelliteVisualPass {

    private float startAz;
    private String startAzCompass;
    private float startEl;
    private int startUTC;
    private float maxAz;
    private String maxAzCompass;
    private float maxEl;
    private int maxUTC;
    private float endAz;
    private String endAzCompass;
    private float endEl;
    private int endUTC;
    private float mag;
    private int duration;

    //REQUIRED BY GSON
    public SatelliteVisualPass() {
    }

    public float getStartAz() {
        return startAz;
    }

    public int getDuration() {
        return duration;
    }

    public float getMag() {
        return mag;
    }

    public int getEndUTC() {
        return endUTC;
    }

    public String getStartAzCompass() {
        return startAzCompass;
    }

    public String getEndAzCompass() {
        return endAzCompass;
    }

    public float getEndAz() {
        return endAz;
    }

    public float getEndEl() {
        return endEl;
    }

    public float getMaxEl() {
        return maxEl;
    }

    public float getStartEl() {
        return startEl;
    }

    public float getMaxAz() {
        return maxAz;
    }

    public int getStartUTC() {
        return startUTC;
    }

    public String getMaxAzCompass() {
        return maxAzCompass;
    }

    public int getMaxUTC() {
        return maxUTC;
    }

    @Override
    public String toString() {
        return "SatelliteVisualPass{" +
                "startAz=" + startAz +
                ", startAzCompass='" + startAzCompass + '\'' +
                ", startEl=" + startEl +
                ", startUTC=" + startUTC +
                ", maxAz=" + maxAz +
                ", maxAzCompass='" + maxAzCompass + '\'' +
                ", maxEl=" + maxEl +
                ", maxUTC=" + maxUTC +
                ", endAz=" + endAz +
                ", endAzCompass='" + endAzCompass + '\'' +
                ", endEl=" + endEl +
                ", endUTC=" + endUTC +
                ", mag=" + mag +
                ", duration=" + duration +
                '}';
    }
}
