package com.example.yrnoparser.location;

import android.util.Log;
import org.geonames.*;

import java.util.List;

public class LocationFinder {

    private static final String GEONAMES_USERNAME = "mgnusl";

    public LocationFinder() {
        WebService.setUserName(GEONAMES_USERNAME);
    }

    public List<Toponym> findLocationsFromString(String locationName) throws Exception {
        ToponymSearchCriteria searchCriteria = getSearchCriteriaForNameSearch(locationName);
        ToponymSearchResult searchResult = WebService.search(searchCriteria);
        List<Toponym> toponyms = searchResult.getToponyms();

        // For testing
        /*for (Toponym toponym : toponyms) {
            if (toponym.getName().equalsIgnoreCase(locationName)) {
                Log.d("APP", toponym.getName());
                Log.d("APP", toponym.getFeatureCode());
                Log.d("APP", toponym.getCountryName());
                Log.d("APP", Integer.toString(toponym.getGeoNameId()));
                Log.d("APP", "--------");
            }
        }*/
        return toponyms;
    }

    /**
     * See http://www.geonames.org/export/codes.html to use Feature Code / Feature Class
     * http://www.geonames.org/source-code/javadoc/org/geonames/ToponymSearchCriteria.html
     * Feature class P - city, village,...
     * @param locationName
     * @return
     */
    private ToponymSearchCriteria getSearchCriteriaForNameSearch(String locationName) {
        ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();
        searchCriteria.setNameEquals(locationName);
        searchCriteria.setFeatureClass(FeatureClass.P);
        //searchCriteria.setFeatureCode("PPL");
        searchCriteria.setMaxRows(10);
        searchCriteria.setStyle(Style.FULL);
        return searchCriteria;
    }
}
