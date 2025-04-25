package com.example.satfinder.Misc.Utility;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for mathematical operations.
 */
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
     * Convert UTC time to local time
     * @param utcMillis The UTC time
     * @return The local time
     */
    public static String convertUTCToLocalTime(long utcMillis) {
        Instant instant = Instant.ofEpochSecond(utcMillis);
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