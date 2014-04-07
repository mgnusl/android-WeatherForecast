package com.example.yrnoparser;

public enum WeatherType {
    SUN(1), FAIR(2), PARTLY_CLOUDY(3), CLOUDY(4), RAIN_SHOWERS(5), SLEET_SHOWERS(7),
    SNOW_SHOWERS(8), RAIN(9), HEAVY_RAIN(10), RAIN_AND_THUNDER(11), SLEET(12), SNOW(13),
    SNOW_AND_THUNDER(14), FOG(15);

    private int value;

    private WeatherType(int value) {
        this.value = value;
    }

}
