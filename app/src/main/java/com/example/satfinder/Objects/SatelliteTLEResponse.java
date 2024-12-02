package com.example.satfinder.Objects;

public class SatelliteTLEResponse implements com.example.satfinder.Objects.ISatelliteResponse {
    private int satid;
    private String satname;
    private int transactionscount;
    private String tle;

    //REQUIRED BY GSON
    public SatelliteTLEResponse() {
    }

    public int getTransactionscount() {
        return transactionscount;
    }

    public String getTle() {
        return tle;
    }

    public String getSatname() {
        return satname;
    }

    public int getSatid() {
        return satid;
    }

    @Override
    public String toString() {
        return "SatelliteTLEResponse{" +
                "satid=" + satid +
                ", satname='" + satname + '\'' +
                ", transactionscount=" + transactionscount +
                ", tle='" + tle + '\'' +
                '}';
    }
}
