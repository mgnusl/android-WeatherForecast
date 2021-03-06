package com.example.yrnoparser;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.example.yrnoparser.data.ForecastLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

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
            // Check if the result is numberOfSearches variable
            if (result.size() < 2)
                continue;
            // Build object
            ForecastLocation fl = new ForecastLocation();
            fl.setCountry(result.get(0));
            fl.setRegion(result.get(1));
            fl.setChildRegion(result.get(2));
            fl.setName(result.get(3));
            fl.setCountryCode(result.get(4));
            // Add it to list
            list.add(fl);
        }

        if (list.size() > NUMBER_OF_SEARCHES_TO_SHOW) {
            list.subList(0, list.size() - NUMBER_OF_SEARCHES_TO_SHOW).clear();
        }

        previousSearches = list;
        cleanSharedPreferences();

        return list;
    }

    public void printAllPrefs() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        Map<String, ?> map = sp.getAll();
        for (String key : map.keySet()) {
            Log.d("APP", "Key: " + key + " - Value: " + map.get(key));
        }
    }

    private void cleanSharedPreferences() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        numberOfSearches = 1;
        for (ForecastLocation fl : previousSearches) {
            editor.putString(SAVED_FORECASTLOCATION + numberOfSearches, convertForecastLocationToString(fl));
            numberOfSearches++;
        }
        editor.putInt(NUMBER_OF_SEARCHES, numberOfSearches);
        editor.commit();
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
        s += fl.getName() + ";";
        s += fl.getCountryCode();

        return s;
    }

    private ArrayList<String> convertStringToArrayList(String s) {

        return new ArrayList<String>(Arrays.asList(s.split(";")));

    }
}
