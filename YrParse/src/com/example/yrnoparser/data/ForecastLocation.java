package com.example.yrnoparser.data;

import java.util.ArrayList;
import java.util.List;

public class ForecastLocation {
    private String name;
    private String country;
    private String countryCode;
    private String type;
    private String childRegion;
    private String region;
    private int geonamesID;
    private List<GeoName> geonameList;

    public ForecastLocation() {
        geonameList = new ArrayList<GeoName>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getChildRegion() {
        return childRegion;
    }

    public void setChildRegion(String childRegion) {
        this.childRegion = childRegion;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getGeonamesID() {
        return geonamesID;
    }

    public void setGeonamesID(int geonamesID) {
        this.geonamesID = geonamesID;
    }

    public List<GeoName> getGeonameList() {
        return geonameList;
    }

    public void setGeonameList(List<GeoName> geonameList) {
        this.geonameList = geonameList;
    }

    public void addGeoname(GeoName g) {
        geonameList.add(g);
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
