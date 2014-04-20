package com.example.yrnoparser.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class ForecastLocation implements Parcelable{
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

    // Parcelable methods
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(country);
        parcel.writeString(countryCode);
        parcel.writeString(type);
        parcel.writeString(childRegion);
        parcel.writeString(region);
        parcel.writeInt(geonamesID);
        parcel.writeList(geonameList);
    }

    public static final Parcelable.Creator<ForecastLocation> CREATOR = new Parcelable.Creator<ForecastLocation>() {
        public ForecastLocation createFromParcel(Parcel in) {
            return new ForecastLocation(in);
        }

        public ForecastLocation[] newArray(int size) {
            return new ForecastLocation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    private ForecastLocation(Parcel in) {
        name = in.readString();
        country = in.readString();
        countryCode = in.readString();
        type = in.readString();
        childRegion = in.readString();
        region = in.readString();
        geonamesID = in.readInt();

        geonameList = new ArrayList<GeoName>();
        in.readList(geonameList, null);
    }
}
