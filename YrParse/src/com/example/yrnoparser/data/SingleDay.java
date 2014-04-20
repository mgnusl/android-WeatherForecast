package com.example.yrnoparser.data;

import android.os.Parcel;
import android.os.Parcelable;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.util.ArrayList;

public class SingleDay implements Parcelable {

    private ArrayList<Forecast> forecasts;
    private DateTime date;
    private DateTimeFormatter dateFormatter;

    public SingleDay() {
        forecasts = new ArrayList<Forecast>();
        dateFormatter = new DateTimeFormatterBuilder().appendPattern("dd/MM/YY").toFormatter();
    }

    public ArrayList<Forecast> getForecasts() {
        return forecasts;
    }

    public String getDateString() {
        if (forecasts.size() > 0)
            return dateFormatter.print(forecasts.get(0).getFromTime());
        return null;
    }

    public DateTime getDate() {
        if (forecasts.size() > 0)
            return forecasts.get(0).getFromTime();
        return null;
    }

    public void setForecasts(ArrayList<Forecast> forecasts) {
        this.forecasts = forecasts;
    }

    public void addForecast(Forecast f) {
        forecasts.add(f);
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    // Parcelable methods
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeList(forecasts);
        parcel.writeString(date.toString());
    }

    public static final Parcelable.Creator<SingleDay> CREATOR = new Parcelable.Creator<SingleDay>() {
        public SingleDay createFromParcel(Parcel in) {
            return new SingleDay(in);
        }

        public SingleDay[] newArray(int size) {
            return new SingleDay[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    private SingleDay(Parcel in) {
        forecasts = new ArrayList<Forecast>();
        in.readList(forecasts, null);
        date = DateTime.parse(in.readString());
    }

}