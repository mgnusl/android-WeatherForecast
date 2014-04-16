package com.example.yrnoparser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.example.yrnoparser.data.ForecastLocation;
import com.example.yrnoparser.data.GeoName;
import com.example.yrnoparser.location.GeonamesLocation;
import com.example.yrnoparser.utils.UrlBuilder;
import com.example.yrnoparser.utils.Utils;
import org.geonames.Toponym;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class StartActivity extends Activity {

    private List<Toponym> resultsFromSearch;
    private List<ForecastLocation> listOfForecastLocations;

    private EditText searchEditText;

    private ForecastLocation selectedForecastLocation;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        resultsFromSearch = new ArrayList<Toponym>();
        listOfForecastLocations = new ArrayList<ForecastLocation>();

        searchEditText = (EditText) findViewById(R.id.searchEditText);

        Button searchButton = (Button) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchString = searchEditText.getText().toString();
                // Clear location list to remove results from earlier searches
                listOfForecastLocations.clear();
                if (searchString.equals(""))
                    Toast.makeText(StartActivity.this, "Tomt søkefelt", Toast.LENGTH_LONG).show();
                else
                    new AsyncSearchLocationFromString().execute(searchEditText.getText().toString());

            }
        });

        Button locationButton = (Button)findViewById(R.id.locfindButton);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this, LocationFinderActivity.class));
            }
        });
    }

    private void showPopupMenu() {
        PopupMenu popup = new PopupMenu(StartActivity.this, searchEditText);

        if(resultsFromSearch.isEmpty()) {
            Toast.makeText(this, "No results", Toast.LENGTH_SHORT).show();
            return;
        }

        int i = 0;
        for (ForecastLocation l : listOfForecastLocations) {
            popup.getMenu().add(Menu.NONE, i, Menu.NONE, l.getName() + " - " + l.getRegion() +
                    " - " + l.getCountry());
            i++;
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                selectedForecastLocation = listOfForecastLocations.get(item.getItemId());
                handleLocationSelection();
                return true;
            }
        });
        popup.show();

    }

    private void handleLocationSelection() {
        String url;

        // Build URL
        if (selectedForecastLocation.getCountryCode().equalsIgnoreCase("no")) {
            url = UrlBuilder.buildNorwegianURL(selectedForecastLocation.getCountry(), selectedForecastLocation.getRegion(),
                    selectedForecastLocation.getChildRegion(), selectedForecastLocation.getName(), "sixhour");
        } else {
            url = UrlBuilder.buildInternationalURL(selectedForecastLocation.getCountry(),
                    selectedForecastLocation.getRegion(), selectedForecastLocation.getName(), "sixhour");
        }

        // Launch activity
        Intent intent = new Intent(StartActivity.this, SixHourForecastActivity.class);
        intent.putExtra("info", url);
        startActivity(intent);

    }

    private class AsyncSearchLocationFromString extends AsyncTask<String, String, String> {
        ProgressDialog pDialog;

        protected String doInBackground(String... args) {

            try {

                GeonamesLocation locfinder = new GeonamesLocation();
                resultsFromSearch = locfinder.findLocationsFromString(args[0]);

                // For each search result
                for (Toponym toponym : resultsFromSearch) {

                    // The URL to get geoname data from
                    URL url = new URL("http://api.geonames.org/hierarchy?geonameId=" +
                            toponym.getGeoNameId() + "&username=mgnusl");

                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(false);
                    XmlPullParser xpp = factory.newPullParser();

                    // Get the XML from an input stream
                    xpp.setInput(Utils.getInputStream(url), "UTF_8");

                    // Set location fields for data we already know
                    ForecastLocation forecastLocation = new ForecastLocation();
                    forecastLocation.setCountry(toponym.getCountryName());
                    forecastLocation.setGeonamesID(toponym.getGeoNameId());
                    forecastLocation.setName(toponym.getName());
                    forecastLocation.setCountryCode(toponym.getCountryCode());

                    boolean insideGeoname = false;
                    GeoName geoname = new GeoName();

                    // Returns the type of current event
                    int eventType = xpp.getEventType();
                    // Loop through all elements as long as they are not END_DOCUMENT
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_TAG) {
                            if (xpp.getName().equalsIgnoreCase("geoname")) {
                                // Inside geoname
                                insideGeoname = true;
                            } else if (xpp.getName().equalsIgnoreCase("name")) {
                                if (insideGeoname) {
                                    geoname.setName(xpp.nextText());
                                }
                            } else if (xpp.getName().equalsIgnoreCase("fcode")) {
                                if (insideGeoname) {
                                    geoname.setFcode(xpp.nextText());
                                }
                            }
                        } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("geoname")) {
                            insideGeoname = false;
                            forecastLocation.addGeoname(geoname);
                            geoname = new GeoName();
                        }

                        eventType = xpp.next(); //move to next element

                    } // end while

                    // Run through the list of GeoNames the Location has and set the needed fields
                    for (GeoName g : forecastLocation.getGeonameList()) {
                        if (g.getFcode().equals("ADM2")) { // Child region. IE. "Bærum" or "Santa Barbara"
                            forecastLocation.setChildRegion(g.getName());
                        }
                        if (g.getFcode().equals("ADM1")) { // Parent region. IE. "Akershus" or "California"
                            forecastLocation.setRegion(g.getName());
                        }
                    }
                    // Add the Location to the list of Locations to be shown later in the popupmenu
                    listOfForecastLocations.add(forecastLocation);

                    Log.d("APP", forecastLocation.getName() + " - " + forecastLocation.getRegion()
                            + " - " + forecastLocation.getGeonamesID() + " - " + forecastLocation.getChildRegion()
                            + " - " + forecastLocation.getType());


                } // end for

            } catch (Exception e) {
                Log.d("APP", "exception");
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(StartActivity.this);
            pDialog.setMessage("Working...");
            pDialog.show();
        }

        protected void onPostExecute(String result) {
            pDialog.dismiss();
            showPopupMenu();
        }
    }
}