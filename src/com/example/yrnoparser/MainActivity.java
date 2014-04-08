package com.example.yrnoparser;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends Activity {

    private Location location;
    private WeatherForecast forecast;
    private ArrayList<WeatherForecast> listOfForecasts;
    private ListView sixHourListView;
    private Context context;
    private TypedArray weatherIcons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        location = new Location();
        forecast = new WeatherForecast();
        listOfForecasts = new ArrayList<WeatherForecast>();
        weatherIcons = getResources().obtainTypedArray(R.array.weather_icons);

        sixHourListView = (ListView) findViewById(R.id.sixHourListView);

        context = this;

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
                boolean insideTabular = false;

                //links.add(xpp.getAttributeValue(null, "from"));

                // Returns the type of current event: START_TAG, END_TAG, etc..
                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {

                        // Get location info
                        if (xpp.getName().equalsIgnoreCase("location")) {
                            insideLocation = true;
                        } else if (xpp.getName().equalsIgnoreCase("name")) {
                            if (insideLocation)
                                location.setName(xpp.nextText());
                        } else if (xpp.getName().equalsIgnoreCase("type")) {
                            if (insideLocation)
                                location.setType(xpp.nextText());
                        } else if (xpp.getName().equalsIgnoreCase("country")) {
                            if (insideLocation)
                                location.setCountry(xpp.nextText());
                        }

                        // Get weather info
                        if (xpp.getName().equalsIgnoreCase("tabular")) {
                            insideTabular = true;
                        } else if (xpp.getName().equalsIgnoreCase("time")) {
                            if (insideTabular) {
                                // Inside time and tabular
                                forecast = new WeatherForecast();
                                forecast.setFromTime(xpp.getAttributeValue(null, "from"));
                                forecast.setToTime(xpp.getAttributeValue(null, "to"));
                                forecast.setPeriod(Integer.parseInt(xpp.getAttributeValue(null, "period")));
                            }
                        } else if (xpp.getName().equalsIgnoreCase("symbol")) {
                            if (insideTabular) {
                                forecast.setSymbol(xpp.getAttributeValue(null, "number"));
                                forecast.setWeatherType(xpp.getAttributeValue(null, "name"));
                            }
                        } else if (xpp.getName().equalsIgnoreCase("windDirection")) {
                            if (insideTabular) {
                                forecast.setWindDirection(xpp.getAttributeValue(null, "name"));
                            }
                        } else if (xpp.getName().equalsIgnoreCase("windSpeed")) {
                            if (insideTabular) {
                                forecast.setWindSpeed(xpp.getAttributeValue(null, "mps"));
                            }
                        } else if (xpp.getName().equalsIgnoreCase("temperature")) {
                            if (insideTabular) {
                                forecast.setTemperature(xpp.getAttributeValue(null, "value"));
                            }
                        } else if (xpp.getName().equalsIgnoreCase("pressure")) {
                            if (insideTabular) {
                                forecast.setPressure(xpp.getAttributeValue(null, "value"));
                            }
                        }

                    } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("location")) {
                        insideLocation = false;
                    } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("tabular")) {
                        insideTabular = false;
                    } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("time")
                            && insideTabular) {
                        // Done processing this "time"
                        listOfForecasts.add(forecast);
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


            Log.d("APP", location.toString());

            for (WeatherForecast f : listOfForecasts)
                Log.d("APP", f.getFromTime());

            return "lol";
        }


        @Override
        protected void onPostExecute(String result) {

            OverviewListAdapter sixHourListAdapter = new OverviewListAdapter(context,
                    R.layout.row_data_six_hour, listOfForecasts, weatherIcons);
            sixHourListView.setAdapter(sixHourListAdapter);

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
