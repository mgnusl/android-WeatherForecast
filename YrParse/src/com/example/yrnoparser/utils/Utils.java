package com.example.yrnoparser.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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

    public static boolean isNetworkAvailable(Context context) {
        boolean isConnectedWifi = false;
        boolean isConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = cm.getAllNetworkInfo();
        for (NetworkInfo n : networkInfo) {
            if (n.getTypeName().equalsIgnoreCase("WIFI"))
                if (n.isConnected())
                    isConnectedWifi = true;
            if (n.getTypeName().equalsIgnoreCase("MOBILE"))
                if (n.isConnected())
                    isConnectedMobile = true;
        }
        return isConnectedWifi || isConnectedMobile;
    }
}
