package com.example.yrnoparser.data;

public class GeoName {

    private String name;
    private String fcode;
    private int geonameID;

    public GeoName() {

    }

    public String getName() {
        return name;
    }

    public String getFcode() {
        return fcode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFcode(String fcode) {
        this.fcode = fcode;
    }

    public int getGeonameID() {
        return geonameID;
    }

    public void setGeonameID(int geonameID) {
        this.geonameID = geonameID;
    }
}
