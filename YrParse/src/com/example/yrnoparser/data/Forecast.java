package com.example.yrnoparser.data;

import android.os.Parcel;
import android.os.Parcelable;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Forecast implements Parcelable {
    private ForecastLocation forecastLocation;
    private String windSpeed;
    private DateTime fromTime;
    private DateTime toTime;
    private String windDirection;
    private int period;
    private int symbol;
    private String weatherType;
    private String temperature;
    private String pressure;
    private String precipitation;
    private String precipitationMin;
    private String precipitationMax;

    private DateTimeFormatter dateFormatter;

    public Forecast() {
        dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
    }

    public ForecastLocation getForecastLocation() {
        return forecastLocation;
    }

    public void setForecastLocation(ForecastLocation forecastLocation) {
        this.forecastLocation = forecastLocation;
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

    public String getPrecipitation() {
        return precipitation;
    }

    public String getPrecipitationMin() {
        return precipitationMin;
    }

    public void setPrecipitationMin(String precipitationMin) {
        this.precipitationMin = precipitationMin;
    }

    public String getPrecipitationMax() {
        return precipitationMax;
    }

    public void setPrecipitationMax(String precipitationMax) {
        this.precipitationMax = precipitationMax;
    }

    public void setPrecipitation(String precipitation) {
        this.precipitation = precipitation;
    }

    public DateTime getToTime() {
        return toTime;
    }

    public void setToTime(String t) {
        // Convert from ISO8601 time format to Joda DateTime
        DateTime toTime = dateFormatter.parseDateTime(t);
        this.toTime = toTime;
    }

    public DateTime getFromTime() {
        return fromTime;
    }

    public void setFromTime(String t) {
        // Convert from ISO8601 time format to Joda DateTime
        DateTime fromTime = dateFormatter.parseDateTime(t);
        this.fromTime = fromTime;
    }

    // Parcelable methods
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(forecastLocation, i);
        parcel.writeString(windSpeed);
        parcel.writeString(fromTime.toString());
        parcel.writeString(toTime.toString());
        parcel.writeString(windDirection);
        parcel.writeInt(period);
        parcel.writeInt(symbol);
        parcel.writeString(weatherType);
        parcel.writeString(temperature);
        parcel.writeString(pressure);
        parcel.writeString(precipitation);
        parcel.writeString(precipitationMin);
        parcel.writeString(precipitationMax);
    }

    public static final Parcelable.Creator<Forecast> CREATOR = new Parcelable.Creator<Forecast>() {
        public Forecast createFromParcel(Parcel in) {
            return new Forecast(in);
        }

        public Forecast[] newArray(int size) {
            return new Forecast[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    private Forecast(Parcel in) {
        forecastLocation = in.readParcelable(null);
        windSpeed = in.readString();
        fromTime = DateTime.parse(in.readString());
        toTime = DateTime.parse(in.readString());
        windDirection = in.readString();
        period = in.readInt();
        symbol = in.readInt();
        weatherType = in.readString();
        temperature = in.readString();
        pressure = in.readString();
        precipitation = in.readString();
        precipitationMin = in.readString();
        precipitationMax = in.readString();
    }

}
