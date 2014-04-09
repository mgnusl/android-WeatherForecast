package com.example.yrnoparser;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class SingleDay {

    private ArrayList<Forecast> forecasts;
    private DateTime date;

    public SingleDay() {
        forecasts = new ArrayList<Forecast>();
    }

    public ArrayList<Forecast> getForecasts() {
        return forecasts;
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