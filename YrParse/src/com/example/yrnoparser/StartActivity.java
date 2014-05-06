package com.example.yrnoparser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.cengalabs.flatui.FlatUI;
import com.cengalabs.flatui.views.FlatButton;
import com.example.yrnoparser.adapter.PrevSearchesAdapter;
import com.example.yrnoparser.data.ForecastLocation;
import com.example.yrnoparser.data.GeoName;
import com.example.yrnoparser.location.GeonamesLocation;
import com.example.yrnoparser.utils.UrlBuilder;
import com.example.yrnoparser.utils.Utils;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import org.geonames.Toponym;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class StartActivity extends ActionBarActivity {

    private List<Toponym> resultsFromSearch;
    private List<ForecastLocation> listOfForecastLocations;
    private EditText searchEditText;
    private ForecastLocation selectedForecastLocation;
    private Style confirm, error;
    private ListView previousSearchesLV;
    private WeatherApplication globalApp;
    private ArrayList<ForecastLocation> searchHistory;
    private PrevSearchesAdapter listAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlatUI.setDefaultTheme(FlatUI.BLOOD);
        setContentView(R.layout.start);

        FlatUI.initDefaultValues(this);

        // TODO: CHECK FOR INTERNET CONNECTION BEFORE SEARCH

        globalApp = (WeatherApplication) getApplicationContext();
        searchHistory = globalApp.getPreviousSearches();

        resultsFromSearch = new ArrayList<Toponym>();
        listOfForecastLocations = new ArrayList<ForecastLocation>();

        // Actionbar title
        getActionBar().setTitle(Html.fromHtml("<font color=\"#f2f2f2\">" + getResources().getString(R.string.app_name)
                + "</font>"));

        // Crouton
        confirm = new Style.Builder().setBackgroundColor(R.color.greenm).build();
        error = new Style.Builder().setBackgroundColor(R.color.yellowm).build();

        searchEditText = (EditText) findViewById(R.id.searchEditText);
        previousSearchesLV = (ListView) findViewById(R.id.previousSearchesListView);
        listAdapter = new PrevSearchesAdapter(this, searchHistory);
        previousSearchesLV.setAdapter(listAdapter);
        previousSearchesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleLocationSelection(searchHistory.get(position-1));
            }
        });

        FlatButton searchButton = (FlatButton) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchString = searchEditText.getText().toString();
                // Clear location list to remove results from earlier searches
                listOfForecastLocations.clear();
                if (searchString.equals(""))
                    Crouton.makeText(StartActivity.this, "Please enter a location", error).show();

                else
                    new AsyncSearchLocationFromString().execute(searchEditText.getText().toString());

            }
        });

        FlatButton locationButton = (FlatButton) findViewById(R.id.locfindButton);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(StartActivity.this, LocationFinderActivity.class), 1);
            }
        });
    }

    private void preparePopupMenu() {
        // Check if we got no results
        if (resultsFromSearch.isEmpty()) {
            Crouton.makeText(this, "No results...", error).show();
            return;
        }

        // Check if we only got one result, in which case go to forecast straight away
        if (resultsFromSearch.size() == 1) {
            // Launch forecast activity
            // Build URL
            selectedForecastLocation = listOfForecastLocations.get(0);
            String url;
            if (selectedForecastLocation.getCountryCode().equalsIgnoreCase("no")) {
                url = UrlBuilder.buildNorwegianBaseURL(selectedForecastLocation.getCountry(), selectedForecastLocation.getRegion(),
                        selectedForecastLocation.getChildRegion(), selectedForecastLocation.getName());
            } else {
                url = UrlBuilder.buildInternationalBaseURL(selectedForecastLocation.getCountry(),
                        selectedForecastLocation.getRegion(), selectedForecastLocation.getName());
            }

            Intent intent = new Intent(StartActivity.this, ForecastActivity.class);
            intent.putExtra("info", url);
            intent.putExtra("location", listOfForecastLocations.get(0));
            startActivity(intent);
        }

        // If more than one result, show popup
        if (resultsFromSearch.size() > 1)
            showPopupMenu();
    }

    private void showPopupMenu() {

        PopupMenu popup = new PopupMenu(StartActivity.this, searchEditText);

        int i = 0;
        for (ForecastLocation l : listOfForecastLocations) {
            popup.getMenu().add(Menu.NONE, i, Menu.NONE, l.getName() + " - " + l.getRegion() +
                    " - " + l.getCountry());
            i++;
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                selectedForecastLocation = listOfForecastLocations.get(item.getItemId());
                handleLocationSelection(selectedForecastLocation);
                return true;
            }
        });

        popup.show();

    }

    private void handleLocationSelection(ForecastLocation fl) {
        String url;

        // Build URL
        if (fl.getCountryCode().equalsIgnoreCase("no")) {
            url = UrlBuilder.buildNorwegianBaseURL(fl.getCountry(), fl.getRegion(),
                    fl.getChildRegion(), fl.getName());
        } else {
            url = UrlBuilder.buildInternationalBaseURL(fl.getCountry(),
                    fl.getRegion(), fl.getName());
        }

        // Launch forecast activity
        Intent intent = new Intent(StartActivity.this, ForecastActivity.class);
        intent.putExtra("info", url);
        intent.putExtra("location", fl);
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
                    Log.d("APP", forecastLocation.getCountryCode());

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


                } // end for

            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
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
            preparePopupMenu();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        searchHistory = globalApp.getPreviousSearches();
        listAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Crouton.cancelAllCroutons();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            ForecastLocation nearbyLocation = data.getParcelableExtra("nearbylocation");
            if (resultCode == 2)
                Crouton.makeText(StartActivity.this, "Unable to find any nearby forecast locations", error).show();
            else
                handleLocationSelection(nearbyLocation);

            Log.d("APP", nearbyLocation.toString());
        }
    }
}