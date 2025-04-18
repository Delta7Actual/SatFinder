package com.example.satfinder.Objects;

import androidx.annotation.NonNull;

/**
 * Represents the TLE data for a specific satellite.
 * Handles mathematical calculations for orbital parameters.
 */
public class SatelliteTLE {
    private final String line1;
    private final String line2;
    private final double stdEarthGrav = 398600.4418; // km^3/s^2
    private final double stdEarthRad = 6378.137; // km

    public SatelliteTLE(String tle) {
        if (tle == null) {
            tle = "";
        }
        String[] lines = tle.split("\r\n");
        if (lines.length != 2) {
            this.line1 = "";
            this.line2 = "";
            return;
        }
        line1 = lines[0];
        line2 = lines[1];
    }

    private double getEccentricity() {
        return Double.parseDouble("0." + line2.substring(27, 33));
    }

    private double getMeanMotion() {
        return Double.parseDouble(line2.substring(53, 63));
    }

    private double getSemiMajorAxis() {
        double meanMotion = getMeanMotion() * 2 * Math.PI / 86400; // convert orbits/day to radians/sec
        return Math.pow(stdEarthGrav / (meanMotion * meanMotion), 1.0 / 3.0); // km
    }

    public double getPerigee() {
        return getSemiMajorAxis() * (1 - getEccentricity()) - stdEarthRad;
    }

    public double getApogee() {
        return getSemiMajorAxis() * (1 + getEccentricity()) - stdEarthRad;
    }

    public double getInclination() {
        return Double.parseDouble(line2.substring(8, 16));
    }

    public double getOrbitalPeriod() {
        return 86400 / getMeanMotion(); // seconds per orbit
    }

    public String getLine1() {
        return line1;
    }

    public String getLine2() {
        return line2;
    }

    @NonNull
    @Override
    public String toString() {
        return "SatelliteTLE{" +
                "line1='" + line1 + '\'' +
                ", line2='" + line2 + '\'' +
                ", inclination=" + getInclination() +
                ", apogee=" + getApogee() +
                ", perigee=" + getPerigee() +
                ", orbitalPeriod=" + getOrbitalPeriod() +
                ", stdEarthGrav=" + stdEarthGrav +
                ", stdEarthRad=" + stdEarthRad +
                '}';
    }
}