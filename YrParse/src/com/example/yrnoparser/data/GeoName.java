package com.example.yrnoparser.data;

import android.os.Parcel;
import android.os.Parcelable;

public class GeoName implements Parcelable {

    private String name;
    private String fcode;
    private String countryCode;
    private int geonameID;

    public GeoName() {
        geonameID = 0;
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
        parcel.writeString(fcode);
        parcel.writeInt(geonameID);
    }

    public static final Parcelable.Creator<GeoName> CREATOR = new Parcelable.Creator<GeoName>() {
        public GeoName createFromParcel(Parcel in) {
            return new GeoName(in);
        }

        public GeoName[] newArray(int size) {
            return new GeoName[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    private GeoName(Parcel in) {
        name = in.readString();
        fcode = in.readString();
        geonameID = in.readInt();
    }
}
