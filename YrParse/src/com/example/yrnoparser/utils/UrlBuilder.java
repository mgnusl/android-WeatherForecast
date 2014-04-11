package com.example.yrnoparser.utils;

public class UrlBuilder {

    public static String buildInternationalURL(String country, String region, String placename, String type) {
        String url = null;
        if(type.equals("sixhour"))
            url = "http://www.yr.no/place/"+
                    country+"/"+region+"/"+placename+"/forecast.xml";
        return url.replaceAll(" ", "_").toLowerCase();
    }

    public static String buildNorwegianURL(String country, String region, String region2, String placename, String type) {
        String url = null;
        if(type.equals("sixhour"))
            url = "http://www.yr.no/place/"+
                    country+"/"+region+"/"+region2+"/"+placename+"/forecast.xml";
        return url.replaceAll(" ", "_").toLowerCase();
    }


}
