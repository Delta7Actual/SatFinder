package com.example.satfinder.Objects;

public class SatelliteVisualPass implements com.example.satfinder.Objects.ISatelliteResponse {

    private float startAz;
    private String startAzCompass;
    private float startEl;
    private long startUTC;
    private float maxAz;
    private String maxAzCompass;
    private float maxEl;
    private long maxUTC;
    private float endAz;
    private String endAzCompass;
    private float endEl;
    private long endUTC;
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

    public long getEndUTC() {
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

    public long getStartUTC() {
        return startUTC;
    }

    public String getMaxAzCompass() {
        return maxAzCompass;
    }

    public long getMaxUTC() {
        return maxUTC;
    }
}
