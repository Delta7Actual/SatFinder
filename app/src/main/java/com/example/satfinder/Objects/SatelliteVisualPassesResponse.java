package com.example.satfinder.Objects;

import java.util.List;

public class SatelliteVisualPassesResponse implements com.example.satfinder.Objects.ISatelliteResponse {

    private int satid;
    private String satname;
    private int transactionscount;
    private int passescount;
    private List<SatelliteVisualPass> passes;

    //REQUIRED BY GSON
    public SatelliteVisualPassesResponse() {
    }

    public int getSatid() {
        return satid;
    }

    public List<SatelliteVisualPass> getPasses() {
        return passes;
    }

    public int getPassescount() {
        return passescount;
    }

    public int getTransactionscount() {
        return transactionscount;
    }

    public String getSatname() {
        return satname;
    }

    @Override
    public String toString() {
        return "SatelliteVisualPassesResponse{" +
                "satid=" + satid +
                ", satname='" + satname + '\'' +
                ", transactionscount=" + transactionscount +
                ", passescount=" + passescount +
                ", passes=" + passes +
                '}';
    }
}
