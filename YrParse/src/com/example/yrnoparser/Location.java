package com.example.yrnoparser;

public class Location {
    private String name;
    private String type;
    private String country;

    public Location(String name, String type, String country ) {
        this.name = name;
        this.type = type;
        this.country = country;
    }

    public Location() {

    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Location{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", country='" + country + '\'' +
                '}';
    }

}
