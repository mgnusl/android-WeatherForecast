package com.example.yrnoparser;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.example.yrnoparser.data.ForecastLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WeatherApplication extends Application {

    public final static String NUMBER_OF_SEARCHES = "nr_of_searches";
    public final static String SAVED_FORECASTLOCATION = "saved_fl";
    public final static int NUMBER_OF_SEARCHES_TO_SHOW = 5;

    private ArrayList<ForecastLocation> previousSearches;
    private int numberOfSearches;

    @Override
    public void onCreate() {
        super.onCreate();
        previousSearches = new ArrayList<ForecastLocation>();
        numberOfSearches = 0;
    }

    public void addPreviousSearch(ForecastLocation fl) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();

        numberOfSearches++;
        editor.putString(SAVED_FORECASTLOCATION + numberOfSearches, convertForecastLocationToString(fl));
        editor.putInt(NUMBER_OF_SEARCHES, numberOfSearches);
        editor.commit();
        previousSearches.add(fl);
    }


    public ArrayList<ForecastLocation> getPreviousSearches() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        numberOfSearches = sp.getInt(NUMBER_OF_SEARCHES, 0);

        ArrayList<ForecastLocation> list = new ArrayList<ForecastLocation>();
        for (int i = 1; i <= numberOfSearches; i++) {
            // Get
            ArrayList<String> result = convertStringToArrayList(sp.getString(SAVED_FORECASTLOCATION + i, "lol-lol"));
            // Build object
            ForecastLocation fl = new ForecastLocation();
            fl.setCountry(result.get(0));
            fl.setRegion(result.get(1));
            fl.setChildRegion(result.get(2));
            fl.setName(result.get(3));
            // Add it to list
            list.add(fl);
        }

        for (ForecastLocation ll : list)
            Log.d("APP", ll.toString());

        if(list.size() > NUMBER_OF_SEARCHES_TO_SHOW) {
            list.subList(0, list.size()-NUMBER_OF_SEARCHES_TO_SHOW).clear();
        }

        return list;
    }

    private void cleanSharedPreferences() {

    }

    /*
            HELPER METHODS
     */
    private String convertForecastLocationToString(ForecastLocation fl) {
        // converts the information needed in ForecastLocation to a string
        // so it can be saved to sharedpreferences
        String s = "";
        s += fl.getCountry() + ";";
        s += fl.getRegion() + ";";
        if (fl.getChildRegion() == null)
            s += "null;";
        else
            s += fl.getChildRegion() + ";";
        s += fl.getName();

        return s;
    }

    private ArrayList<String> convertStringToArrayList(String s) {

        ArrayList<String> myList = new ArrayList<String>(Arrays.asList(s.split(";")));
        return myList;

    }
}
