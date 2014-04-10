package com.example.yrnoparser.location;

import org.geonames.*;

public class GeonamesLocationService {

    private static final String GEONAMES_USERNAME = "mgnusl";

    /**
     * Displays based on a specified name a list of possible locations back
     *
     * @param locationName
     * @throws Exception
     */
    public ToponymSearchResult getLocationsByString(String locationName) throws Exception {
        WebService.setUserName(GEONAMES_USERNAME);
        ToponymSearchCriteria searchCriteria = getSearchCriteriaForNameSearch(locationName);
        ToponymSearchResult searchResult = WebService.search(searchCriteria);
        return searchResult;
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
