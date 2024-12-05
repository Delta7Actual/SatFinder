package com.example.satfinder.Objects;

/**
 * Represents the TLE data for a specific satellite.
 * Handles mathematical calculations for orbital parameters.
 */
public class SatelliteTLE {
    private String line1;
    private String line2;
    private double inclination;
    private double apogee;
    private double perigee;
    private double orbitalPeriod;

    private final double stdEarthGrav = 398600.4418;
    private final double stdEarthRad = 6378.137;

    public SatelliteTLE() {
    }

    public SatelliteTLE(String tle) {
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
        return Double.parseDouble(line2.substring(53, 63)) * (2 * Math.PI) / 86400;
    }

    private double getSemiMajorAxis() {
        return Math.pow(stdEarthGrav / Math.pow(getMeanMotion(), 2), (double)1/3);
    }

    public String getLine1() {
        return line1;
    }

    public String getLine2() {
        return line2;
    }

    public double getPerigee() {
        return this.getSemiMajorAxis() * (1 - getEccentricity()) - stdEarthRad;
    }

    public double getApogee() {
        return this.getSemiMajorAxis() * (1 + getEccentricity()) - stdEarthRad;
    }

    public double getInclination() {
        return Double.parseDouble(line2.substring(8, 16));
    }

    public double getOrbitalPeriod() {
        return (2 * Math.PI / getMeanMotion()) / 360;
    }

    @Override
    public String toString() {
        return "SatelliteTLE{" +
                "line1='" + line1 + '\'' +
                ", line2='" + line2 + '\'' +
                ", inclination=" + inclination +
                ", apogee=" + apogee +
                ", perigee=" + perigee +
                ", orbitalPeriod=" + orbitalPeriod +
                ", stdEarthGrav=" + stdEarthGrav +
                ", stdEarthRad=" + stdEarthRad +
                '}';
    }
}
