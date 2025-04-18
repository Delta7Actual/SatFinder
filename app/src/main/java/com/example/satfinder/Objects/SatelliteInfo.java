package com.example.satfinder.Objects;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Represents the information about a given satellite.
 */
public class SatelliteInfo {
    @SerializedName("satid")
    private final int satid;

    @SerializedName("satname")
    private final String satname;

    @SerializedName("transactionscount")
    private final int transactionscount;

    public SatelliteInfo(int satid, String satname) {
        this.satid = satid;
        this.satname = satname;
        this.transactionscount = 0;
    }

    public int getSatid() {
        return satid;
    }

    public String getSatname() {
        return satname;
    }

    public int getTransactionscount() {
        return transactionscount;
    }

    @NonNull
    @Override
    public String toString() {
        return "SatelliteInfo{" +
                "satid=" + satid +
                ", satname='" + satname + '\'' +
                ", transactionscount=" + transactionscount +
                '}';
    }
}