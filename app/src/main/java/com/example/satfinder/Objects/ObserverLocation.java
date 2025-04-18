package com.example.satfinder.Objects;

import android.location.Location;

import androidx.annotation.NonNull;

/**
 * Represents the location of an observer.
 */
public class ObserverLocation {
    float latitude;
    float longitude;
    float altitude;

    public ObserverLocation(float longitude, float latitude, float altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public ObserverLocation() {
        this.latitude = 0;
        this.longitude = 0;
        this.altitude = 0;
    }

    public ObserverLocation(Location location) {
        this.latitude = (float) location.getLatitude();
        this.longitude = (float) location.getLongitude();
        this.altitude = (float) location.getAltitude();
    }

    public float getAltitude() {
        return altitude;
    }

    public void setAltitude(float altitude) {
        this.altitude = altitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    @NonNull
    @Override
    public String toString() {
        return "ObserverLocation{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                '}';
    }
}