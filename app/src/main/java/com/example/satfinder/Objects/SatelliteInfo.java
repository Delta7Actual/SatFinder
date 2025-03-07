package com.example.satfinder.Objects;

import com.google.gson.annotations.SerializedName;

/**
 * Represents the information about a given satellite.
 */
public class SatelliteInfo {
    @SerializedName("satid")
    private int satid;

    @SerializedName("satname")
    private String satname;

    @SerializedName("transactionscount")
    private int transactionscount;

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

    @Override
    public String toString() {
        return "SatelliteInfo{" +
                "satid=" + satid +
                ", satname='" + satname + '\'' +
                ", transactionscount=" + transactionscount +
                '}';
    }
}
