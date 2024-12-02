package com.example.satfinder.Objects;

import com.google.gson.annotations.SerializedName;

public class SatelliteInfo {
    @SerializedName("satid")
    private int satid;

    @SerializedName("satname")
    private String satname;

    @SerializedName("transactionscount")
    private int transactionscount;

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
