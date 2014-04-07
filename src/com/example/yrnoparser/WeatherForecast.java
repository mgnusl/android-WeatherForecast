package com.example.yrnoparser;

public class WeatherForecast {
    private Location location;
    private String fromTime;
    private String toTime;
    private String windSpeed;
    private int period;
    private String windDirection;
    private String symbol;
    private String weatherType;
    private String temperature;
    private String pressure;

    public WeatherForecast() {
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getWeatherType() {
        return weatherType;
    }

    public void setWeatherType(String weatherType) {
        this.weatherType = weatherType;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    @Override
    public String toString() {
        return "WeatherForecast{" +
                "location=" + location +
                ", fromTime='" + fromTime + '\'' +
                ", toTime='" + toTime + '\'' +
                ", windSpeed='" + windSpeed + '\'' +
                ", period=" + period +
                ", windDirection='" + windDirection + '\'' +
                ", symbol=" + symbol +
                ", weatherType='" + weatherType + '\'' +
                ", temperature='" + temperature + '\'' +
                ", pressure='" + pressure + '\'' +
                '}';
    }
}
