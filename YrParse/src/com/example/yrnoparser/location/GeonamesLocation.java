package com.example.yrnoparser.location;

import org.geonames.*;

import java.util.List;

public class GeonamesLocation {

    private static final String GEONAMES_USERNAME = "mgnusl";

    public GeonamesLocation() {
        WebService.setUserName(GEONAMES_USERNAME);
    }

    public List<Toponym> findLocationsFromString(String locationName) throws Exception {
        ToponymSearchCriteria searchCriteria = getSearchCriteriaForNameSearch(locationName);
        ToponymSearchResult searchResult = WebService.search(searchCriteria);
        return searchResult.getToponyms();

    }

    /**
     * See http://www.geonames.org/export/codes.html to use Feature Code / Feature Class
     * http://www.geonames.org/source-code/javadoc/org/geonames/ToponymSearchCriteria.html
     * Feature class P - city, village,...
     *
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
