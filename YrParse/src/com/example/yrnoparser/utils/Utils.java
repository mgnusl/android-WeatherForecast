package com.example.yrnoparser.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Utils {

    public static InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            return null;
        }
    }
}
