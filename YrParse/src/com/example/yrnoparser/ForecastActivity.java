package com.example.yrnoparser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;
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

public class ForecastActivity extends FragmentActivity {
    private ViewPager viewPager;
    private ForecastLocation forecastLocation;
    private Forecast forecast;
    private ArrayList<Forecast> listOfForecasts;
    private TypedArray weatherIcons;
    private ArrayList<SingleDay> listOfDays;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);



        forecastLocation = new ForecastLocation();
        forecast = new Forecast();
        listOfForecasts = new ArrayList<Forecast>();
        listOfDays = new ArrayList<SingleDay>();
        weatherIcons = getResources().obtainTypedArray(R.array.weather_icons);

        // Get intent and data passed with it
        Intent intent = getIntent();
        url = intent.getStringExtra("info");

        // Download and parse the XML
        new AsyncDownloadTask().execute(url);
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

        // Send data to Fragments
        initializeFragments();
    }

    public void initializeFragments() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("forecastlocation", forecastLocation);
        bundle.putParcelableArrayList("listofdays", listOfDays);
        bundle.putParcelableArrayList("listofforecasts", listOfForecasts);

        List<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(new OneHourFragment());
        SixHourFragment sixHourFragment = new SixHourFragment();
        sixHourFragment.setArguments(bundle);
        fragments.add(sixHourFragment);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new MyPagerAdapter(fragmentManager, fragments));
    }

    private class AsyncDownloadTask extends AsyncTask<String, String, String> {

        ProgressDialog pDialog;

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Working..."); // Calls onProgressUpdate()
            try {

                Log.d("APP", params[0]);
                URL url = new URL(params[0]);

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();

                // Get the XML from an input stream
                xpp.setInput(Utils.getInputStream(url), "UTF_8");

                boolean insideLocation = false;
                boolean insideTabular = false;

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
                        listOfForecasts.add(forecast);
                    }

                    eventType = xpp.next(); //move to next element

                }

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
            if (listOfForecasts.isEmpty()) {
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
}