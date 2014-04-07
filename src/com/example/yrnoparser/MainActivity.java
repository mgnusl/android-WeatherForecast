package com.example.yrnoparser;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    List headlines;
    ArrayList<String> links;
    private Location location;
    private WeatherForecast forecast;
    private ArrayList<WeatherForecast> listOfForecasts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        headlines = new ArrayList();
        links = new ArrayList<String>();
        location = new Location();
        forecast = new WeatherForecast();
        listOfForecasts = new ArrayList<WeatherForecast>();


        new AsyncTaskRunner().execute("");


    }


    public InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {
                URL url = new URL("http://www.yr.no/place/Norway/Telemark/Sauherad/Gvarv/forecast.xml");

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();

                // We will get the XML from an input stream
                xpp.setInput(getInputStream(url), "UTF_8");

                boolean insideLocation = false;
                boolean insideForecast = false;

                //links.add(xpp.getAttributeValue(null, "from"));

                // Returns the type of current event: START_TAG, END_TAG, etc..
                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {

                        // Get location info
                        if(xpp.getName().equalsIgnoreCase("location")) {
                            insideLocation = true;
                        }
                        else if(xpp.getName().equalsIgnoreCase("name")) {
                            if (insideLocation)
                                location.setName(xpp.nextText());
                        }
                        else if(xpp.getName().equalsIgnoreCase("type")) {
                            if (insideLocation)
                                location.setType(xpp.nextText());
                        }
                        else if(xpp.getName().equalsIgnoreCase("country")) {
                            if (insideLocation)
                                location.setCountry(xpp.nextText());
                        }

                        // Get weather info
                        if(xpp.getName().equalsIgnoreCase("forecast")) {
                            insideForecast = true;
                        }

                    }

                    else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("location")) {
                        insideLocation = false;
                    }

                    eventType = xpp.next(); //move to next element
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            for(String s : links)
                Log.d("APP", s);

            Log.d("APP", location.toString());

            return "lol";
        }


        @Override
        protected void onPostExecute(String result) {

        }


        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
        }

        @Override
        protected void onProgressUpdate(String... text) {
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }
    }
}
