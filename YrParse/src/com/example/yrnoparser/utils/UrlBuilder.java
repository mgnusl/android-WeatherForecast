package com.example.yrnoparser.utils;

import java.util.HashMap;

public class UrlBuilder {

    public static String buildInternationalURL(String country, String region, String placename, String type) {

        String url = null;
        if (type.equals("sixhour"))
            url = "http://www.yr.no/place/" + country + "/" + region + "/" + placename + "/forecast.xml";
        return url.replaceAll(" ", "_").toLowerCase();
    }

    public static String buildNorwegianURL(String country, String region, String region2,
                                           String placename, String type) {

        // Key-value pairs for norwegian regions for building URLs "the yr.no way"
        HashMap<String, String> regionFormatNO = new HashMap<String, String>();
        regionFormatNO.put("Finnmark Fylke", "finnmark");
        regionFormatNO.put("Vestfold", "vestfold");
        regionFormatNO.put("Vest-Agder Fylke", "vest-agder");
        regionFormatNO.put("Troms Fylke", "troms");
        regionFormatNO.put("Telemark", "telemark");
        regionFormatNO.put("Sør-Trøndelag Fylke", "sør-trøndelag");
        regionFormatNO.put("Sogn og Fjordane Fylke", "sogn og fjordane");
        regionFormatNO.put("Rogaland Fylke", "rogaland");
        regionFormatNO.put("Østfold", "østfold");
        regionFormatNO.put("Oslo County", "oslo");
        regionFormatNO.put("Oppland", "oppland");
        regionFormatNO.put("Nord-Trøndelag Fylke", "nord-trøndelag");
        regionFormatNO.put("Nordland Fylke", "nordland");
        regionFormatNO.put("Møre og Romsdal fylke", "møre og romsdal");
        regionFormatNO.put("Hordaland Fylke", "hordaland");
        regionFormatNO.put("Hedmark", "hedmark");
        regionFormatNO.put("Buskerud", "buskerud");
        regionFormatNO.put("Aust-Agder", "aust agder");
        regionFormatNO.put("Akershus", "akershus");

        String url = null;
        if (type.equals("sixhour"))
            url = "http://www.yr.no/place/" + country + "/" + regionFormatNO.get(region) + "/" + region2 + "/"
                    + placename + "/forecast.xml";
        return url.replaceAll(" ", "_").toLowerCase();
    }


}
