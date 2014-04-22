package com.example.yrnoparser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import com.astuetz.PagerSlidingTabStrip;
import com.cengalabs.flatui.FlatUI;
import com.example.yrnoparser.adapter.MyPagerAdapter;
import com.example.yrnoparser.data.Forecast;
import com.example.yrnoparser.data.ForecastLocation;
import com.example.yrnoparser.data.SingleDay;
import com.example.yrnoparser.utils.Utils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ForecastActivity extends ActionBarActivity {
    private ViewPager viewPager;
    private ForecastLocation forecastLocation;
    private ArrayList<Forecast> listOfSixHourForecasts;
    private ArrayList<Forecast> listOfOneHourForecasts;
    private ArrayList<SingleDay> listOfSixHourDays;
    private ArrayList<SingleDay> listOfOneHourDays;
    private WeatherApplication globalApp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlatUI.setDefaultTheme(FlatUI.BLOOD);
        setContentView(R.layout.activity_forecast);

        globalApp = (WeatherApplication) getApplicationContext();

        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Style
        FlatUI.setActionBarTheme(this, FlatUI.DARK, false, true);
        getSupportActionBar().setBackgroundDrawable(FlatUI.getActionBarDrawable(FlatUI.DEEP, false));
        getActionBar().setTitle(Html.fromHtml("<font color=\"#f2f2f2\">" + getResources().getString(R.string.app_name)
                + "</font>"));

        forecastLocation = new ForecastLocation();
        listOfSixHourForecasts = new ArrayList<Forecast>();
        listOfOneHourForecasts = new ArrayList<Forecast>();
        listOfSixHourDays = new ArrayList<SingleDay>();
        listOfOneHourDays = new ArrayList<SingleDay>();

        // Get intent and data passed with it
        Intent intent = getIntent();
        String url = intent.getStringExtra("info");
        Log.d("APP", url);

        // Download and parse the XML
        new AsyncHandleXML().execute(url);
    }

    /**
     * Organize forecasts into SingleDay objects
     */
    public void handleForecasts() {
        // Handle six hour forecasts
        SingleDay day = new SingleDay();
        for (Forecast f : listOfSixHourForecasts) {
            if (f.getPeriod() == 0) {
                day = new SingleDay();
                day.addForecast(f);
                continue;
            }
            if (f.getPeriod() == 3) {
                day.addForecast(f);
                listOfSixHourDays.add(day);
                continue;
            }
            day.addForecast(f);
        }
        listOfSixHourDays.add(day); // Add any "non-finished" days to the list

        // Handle one hour forecasts
        day = new SingleDay();
        for (Forecast f : listOfOneHourForecasts) {
            if (f.getFromTime().getHourOfDay() == 23 ||
                    (f.getFromTime().getHourOfDay() < 23 && f.getFromTime().getHourOfDay() > 23)) {
                day.addForecast(f);
                listOfOneHourDays.add(day);
                day = new SingleDay();
                continue;
            }
            day.addForecast(f);
        }
        listOfOneHourDays.add(day); // Add any "non-finished" days to the list
        // Remove any empty days at the end of the list
        if (listOfOneHourDays.get(listOfOneHourDays.size() - 1).getForecasts().isEmpty())
            listOfOneHourDays.remove(listOfOneHourDays.size() - 1);

        // Initialize and send data to Fragments
        initializeFragments();
    }

    public void initializeFragments() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("forecastlocation", forecastLocation);
        bundle.putParcelableArrayList("listofdays", listOfSixHourDays);
        bundle.putParcelableArrayList("listofonehourdays", listOfOneHourDays);

        List<Fragment> fragments = new ArrayList<Fragment>();
        // Add bundle extras to fragments
        SixHourFragment sixHourFragment = new SixHourFragment();
        sixHourFragment.setArguments(bundle);
        OneHourFragment oneHourFragment = new OneHourFragment();
        oneHourFragment.setArguments(bundle);
        fragments.add(sixHourFragment);
        fragments.add(oneHourFragment);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new MyPagerAdapter(fragmentManager, fragments));
        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(viewPager);
        //Style tabs
        tabs.setIndicatorColor(getResources().getColor(R.color.yellowm));
        tabs.setShouldExpand(true);

        // Add the forecast to "search history"
        Intent intent = getIntent();
        ForecastLocation fl = intent.getExtras().getParcelable("location");
        globalApp.addPreviousSearch(fl);

    }

    private class AsyncHandleXML extends AsyncTask<String, String, String> {

        ProgressDialog pDialog;

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Working..."); // Calls onProgressUpdate()
            try {

                // Download and parse XML
                loadSixHourForecast(params[0]);
                loadOneHourForecast(params[0]);

            } catch (MalformedURLException e) {
                finish();
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                finish();
                e.printStackTrace();
            } catch (IOException e) {
                finish();
                e.printStackTrace();
            } catch (Exception e) {
                finish();
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ForecastActivity.this);
            pDialog.setMessage("Working...");
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            // Check if we actually found any forecasts while reading the URL
            if (listOfSixHourForecasts.isEmpty()) {
                Toast.makeText(ForecastActivity.this, "No forecasts available for this location",
                        Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Handle the forecasts
            handleForecasts();
            pDialog.dismiss();
        }

    }

    private void loadSixHourForecast(String baseUrl) throws XmlPullParserException, IOException {
        URL url = new URL(baseUrl + "/forecast.xml");

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(false);
        XmlPullParser xpp = factory.newPullParser();

        // Get the XML from an input stream
        xpp.setInput(Utils.getInputStream(url), "UTF_8");

        boolean insideLocation = false;
        boolean insideTabular = false;

        Forecast forecast = new Forecast();

        // Returns the type of current event
        int eventType = xpp.getEventType();
        // Loop through all elements as long as they are not END_DOCUMENT
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {

                // Get location info
                if (xpp.getName().equalsIgnoreCase("location")) {
                    insideLocation = true;
                } else if (xpp.getName().equalsIgnoreCase("name")) {
                    if (insideLocation)
                        forecastLocation.setName(xpp.nextText());
                } else if (xpp.getName().equalsIgnoreCase("type")) {
                    if (insideLocation)
                        forecastLocation.setType(xpp.nextText());
                } else if (xpp.getName().equalsIgnoreCase("country")) {
                    if (insideLocation)
                        forecastLocation.setCountry(xpp.nextText());
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
                        if (!value.equals("0")) {
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
                forecast.setForecastLocation(forecastLocation);
                listOfSixHourForecasts.add(forecast);
            }

            eventType = xpp.next(); //move to next element

        }
    }

    private void loadOneHourForecast(String baseUrl) throws XmlPullParserException, IOException {
        URL url = new URL(baseUrl + "/forecast_hour_by_hour.xml");

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(false);
        XmlPullParser xpp = factory.newPullParser();

        // Get the XML from an input stream
        xpp.setInput(Utils.getInputStream(url), "UTF_8");

        boolean insideTabular = false;

        Forecast forecast = new Forecast();

        // Returns the type of current event
        int eventType = xpp.getEventType();
        // Loop through all elements as long as they are not END_DOCUMENT
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {

                // Get weather info
                if (xpp.getName().equalsIgnoreCase("tabular")) {
                    insideTabular = true;
                } else if (xpp.getName().equalsIgnoreCase("time")) {
                    if (insideTabular) {
                        // Inside time and tabular
                        forecast = new Forecast();
                        forecast.setFromTime(xpp.getAttributeValue(null, "from"));
                        forecast.setToTime(xpp.getAttributeValue(null, "to"));
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
                        if (!value.equals("0")) {
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

            } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("tabular")) {
                insideTabular = false;
            } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("time")
                    && insideTabular) {
                // Done processing this "time"
                forecast.setForecastLocation(forecastLocation);
                listOfOneHourForecasts.add(forecast);
            }

            eventType = xpp.next(); //move to next element

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
