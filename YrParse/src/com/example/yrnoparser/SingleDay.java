package com.example.yrnoparser;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.util.ArrayList;

public class SingleDay {

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
        if(forecasts.size() > 0)
            return dateFormatter.print(forecasts.get(0).getFromTime());
        return null;
    }

    public void setForecasts(ArrayList<Forecast> forecasts) {
        this.forecasts = forecasts;
    }

    public void addForecast(Forecast f) {
        forecasts.add(f);
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

}