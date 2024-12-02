package com.example.satfinder.Objects;

import java.util.List;

public class SatellitePositionsResponse implements com.example.satfinder.Objects.ISatelliteResponse {

    private int satid;
    private String satname;
    private int transactionscount;
    private List<SatellitePosition> positions;

    //REQUIRED BY GSON
    public SatellitePositionsResponse() {
    }

    public int getSatid() {
        return satid;
    }

    public List<SatellitePosition> getPositions() {
        return positions;
    }

    public int getTransactionscount() {
        return transactionscount;
    }

    public String getSatname() {
        return satname;
    }

    @Override
    public String toString() {
        return "SatellitePositionsResponse{" +
                "satid=" + satid +
                ", satname='" + satname + '\'' +
                ", transactionscount=" + transactionscount +
                ", positions=" + positions +
                '}';
    }
}
