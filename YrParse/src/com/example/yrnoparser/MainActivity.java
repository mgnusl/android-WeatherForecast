package com.example.yrnoparser;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import com.applidium.headerlistview.HeaderListView;
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
    private Forecast forecast;
    private ArrayList<Forecast> listOfForecasts;
    private ListView sixHourListView;
    private Context context;
    private TypedArray weatherIcons;
    private TextView infoTextView;

    private ArrayList<SingleDay> listOfDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        location = new Location();
        forecast = new Forecast();
        listOfForecasts = new ArrayList<Forecast>();
        listOfDays = new ArrayList<SingleDay>();
        weatherIcons = getResources().obtainTypedArray(R.array.weather_icons);

        infoTextView = (TextView) findViewById(R.id.infoTextView);

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

    public void updateListView() {
        HeaderListView list = new HeaderListView(this);
        list.setAdapter(new OverviewSectionAdapter(this, listOfDays, weatherIcons));
        setContentView(list);
    }

    /**
     * Organize forecasts into SingleDay objects
     */
    public void handleForecasts() {

        SingleDay day = new SingleDay();

        for (Forecast f : listOfForecasts) {
            if (f.getPeriod() == 0) {
                day = new SingleDay();
                day.addForecast(f);
                continue;
            }
            if (f.getPeriod() == 3) {
                day.addForecast(f);
                listOfDays.add(day);
                continue;
            }
            day.addForecast(f);
        }
        listOfDays.add(day);

        // Update list with new data
        updateListView();

    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {
                URL url = new URL("http://www.yr.no/sted/Norge/Troms/Troms%C3%B8/Troms%C3%B8/varsel.xml");

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();

                // Get the XML from an input stream
                xpp.setInput(getInputStream(url), "UTF_8");

                boolean insideLocation = false;
                boolean insideTabular = false;

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
                                forecast = new Forecast();
                                forecast.setFromTime(xpp.getAttributeValue(null, "from"));
                                forecast.setToTime(xpp.getAttributeValue(null, "to"));
                                forecast.setPeriod(Integer.parseInt(xpp.getAttributeValue(null, "period")));
                            }
                        } else if (xpp.getName().equalsIgnoreCase("symbol")) {
                            if (insideTabular) {
                                forecast.setSymbol(Integer.parseInt(xpp.getAttributeValue(null, "number")));
                                forecast.setWeatherType(xpp.getAttributeValue(null, "name"));
                            }
                        } else if (xpp.getName().equalsIgnoreCase("precipitation")) {
                            if (insideTabular) {
                                String value = xpp.getAttributeValue(null, "value");
                                forecast.setPrecipitation(value);
                                if(!value.equals("0")) {
                                    forecast.setPrecipitationMin(xpp.getAttributeValue(null, "minvalue"));
                                    forecast.setPrecipitationMax(xpp.getAttributeValue(null, "maxvalue"));
                                }
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


            return "lol";
        }


        @Override
        protected void onPostExecute(String result) {

            // Handle the forecasts
            handleForecasts();


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
