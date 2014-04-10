package com.example.yrnoparser.location;

import android.util.Log;
import org.geonames.Toponym;
import org.geonames.ToponymSearchResult;

import java.util.List;

public class LocationFinder {

    public void findLocationsFromString() throws Exception {
        String locationToSearch = "sandvika";
        GeonamesLocationService locationService = new GeonamesLocationService();
        ToponymSearchResult searchResult = locationService.getLocationsByString(locationToSearch);
        List<Toponym> toponyms = searchResult.getToponyms();

        boolean locationFound = false;
        for (Toponym toponym : toponyms) {
            if (toponym.getName().equalsIgnoreCase(locationToSearch)) {
                locationFound = true;
                Log.d("APP", toponym.getName());
                Log.d("APP", toponym.getFeatureCode());
                Log.d("APP", toponym.getCountryName());
                Log.d("APP", Integer.toString(toponym.getGeoNameId()));
                Log.d("APP", "--------");
            }
        }
    }
}
