package com.example.satfinder.Misc.Utility;

import com.example.satfinder.Objects.ObserverLocation;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class MathUtils {
    private MathUtils() {
        // Utility class
    }

    /**
     * Check if data is stale based on timestamp
     * @param timestamp A datapoint's UTC timestamp
     * @param threshold The threshold to consider data stale in seconds
     * @return True if data is stale, false otherwise
     */
    public static boolean isStale(long timestamp, long threshold) {
        long currentTime = System.currentTimeMillis() / 1000;
        return (currentTime - timestamp) > threshold;
    }

    /**
     * Calculate the angle above the horizon needed to see target from source
     * @param source The observer's location
     * @param target The target's location
     * @return The angle above the horizon in degrees
     */
    public static float getAngleFromHorizon(ObserverLocation source, ObserverLocation target) {
        double lat1 = Math.toRadians(source.getLatitude());
        double lon1 = Math.toRadians(source.getLongitude());
        double lat2 = Math.toRadians(target.getLatitude());
        double lon2 = Math.toRadians(target.getLongitude());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Radius of the Earth in kilometers
        double R = 6371.0;
        double distance = R * c;
        double altitudeDiff = target.getAltitude() - source.getAltitude();

        double angleRad = Math.atan2(altitudeDiff, distance * 1000); // Convert distance to meters
        return (float) Math.toDegrees(angleRad);
    }

    /**
     * Convert UTC time to local time
     * @param utcMillis The UTC time
     * @return The local time
     */
    public static String convertUTCToLocalTime(long utcMillis) {
        Instant instant = Instant.ofEpochMilli(utcMillis); // FIX: use milliseconds!
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM HH:mm");
        return localDateTime.format(formatter);
    }


    /**
     * Turn float azimuth into string direction
     * @return cardinal direction as a String
     */
    public static String getDirectionFromAzimuth(float azimuth) {
        if (azimuth >= 348.75 || azimuth < 11.25) {
            return "N";
        } else if (azimuth >= 11.25 && azimuth < 33.75) {
            return "NNE";
        } else if (azimuth >= 33.75 && azimuth < 56.25) {
            return "NE";
        } else if (azimuth >= 56.25 && azimuth < 78.75) {
            return "ENE";
        } else if (azimuth >= 78.75 && azimuth < 101.25) {
            return "E";
        } else if (azimuth >= 101.25 && azimuth < 123.75) {
            return "ESE";
        } else if (azimuth >= 123.75 && azimuth < 146.25) {
            return "SE";
        } else if (azimuth >= 146.25 && azimuth < 168.75) {
            return "SSE";
        } else if (azimuth >= 168.75 && azimuth < 191.25) {
            return "S";
        } else if (azimuth >= 191.25 && azimuth < 213.75) {
            return "SSW";
        } else if (azimuth >= 213.75 && azimuth < 236.25) {
            return "SW";
        } else if (azimuth >= 236.25 && azimuth < 258.75) {
            return "WSW";
        } else if (azimuth >= 258.75 && azimuth < 281.25) {
            return "W";
        } else if (azimuth >= 281.25 && azimuth < 303.75) {
            return "WNW";
        } else if (azimuth >= 303.75 && azimuth < 326.25) {
            return "NW";
        } else if (azimuth >= 326.25 && azimuth < 348.75) {
            return "NNW";
        } else {
            return "Unknown";
        }
    }
}
