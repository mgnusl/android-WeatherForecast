package com.example.yrnoparser;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

public class WeatherForecast {
    private Location location;
    private String windSpeed;
    private DateTime fromTime;
    private DateTime toTime;
    private int period;
    private String windDirection;
    private int symbol;
    private String weatherType;
    private String temperature;
    private String pressure;

    private DateTimeFormatter dateFormatter;
    private DateTimeFormatter twentyFourHourDateFormatter;

    public WeatherForecast() {
        dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
        twentyFourHourDateFormatter = new DateTimeFormatterBuilder().appendPattern("dd/MM/YY HH:mm").toFormatter();
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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

    public int getSymbol() {
        return symbol;
    }

    public void setSymbol(int symbol) {
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

    public DateTime getToTime() {
        return toTime;
    }

    public String getToTimeString() {
        return twentyFourHourDateFormatter.print(toTime);
    }

    public void setToTime(String t) {
        // Convert from ISO8601 time format to Joda DateTime
        DateTime toTime = dateFormatter.parseDateTime(t);
        this.toTime = toTime;
    }

    public DateTime getFromTime() {
        return fromTime;
    }

    public String getFromTimeString() {
        return twentyFourHourDateFormatter.print(fromTime);
    }

    public void setFromTime(String t) {
        // Convert from ISO8601 time format to Joda DateTime
        DateTime fromTime = dateFormatter.parseDateTime(t);
        this.fromTime = fromTime;
    }
}
