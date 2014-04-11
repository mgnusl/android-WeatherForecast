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
import com.example.yrnoparser.data.GeoName;
import com.example.yrnoparser.location.LocationFinder;
import com.example.yrnoparser.utils.UrlBuilder;
import org.geonames.Toponym;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class StartActivity extends Activity {

    private List<Toponym> resultsFromSearch;
    private List<GeoName> listOfGeonames;

    private Button searchButton;
    private EditText searchEditText;

    private Toponym selectedLocation;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        resultsFromSearch = new ArrayList<Toponym>();
        listOfGeonames = new ArrayList<GeoName>();

        searchEditText = (EditText) findViewById(R.id.searchEditText);

        searchButton = (Button) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchString = searchEditText.getText().toString();
                if (searchString.equals(""))
                    Toast.makeText(StartActivity.this, "Tomt søkefelt", Toast.LENGTH_LONG).show();
                else
                    new AsyncSearchLocationFromString().execute(searchEditText.getText().toString());

            }
        });
    }

    private void showPopupMenu() {
        PopupMenu popup = new PopupMenu(StartActivity.this, searchEditText);
        int i = 0;
        for (Toponym t : resultsFromSearch) {
            popup.getMenu().add(Menu.NONE, i, Menu.NONE, t.getName() + " - " + t.getCountryName());
            i++;
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                selectedLocation = resultsFromSearch.get(item.getItemId());
                new AsyncBuildURLTask().execute(Integer.toString(selectedLocation.getGeoNameId()));
                return true;
            }
        });
        popup.show();

    }

    private void handleLocationSelection() {
        String url;
        String regionName = null;
        String regionNameParent = null;

        // Loop through listOfGeonames to extract the needed information about region(s)
        for(GeoName g : listOfGeonames) {
            if(g.getFcode().equalsIgnoreCase("ADM1"))
                regionName = g.getName();
            if(g.getFcode().equalsIgnoreCase("ADM2"))
                regionNameParent = g.getName();
        }

        // Build URL
        if(selectedLocation.getCountryCode().equalsIgnoreCase("no")) {
            url = UrlBuilder.buildNorwegianURL(selectedLocation.getCountryName(), regionName, regionNameParent,
                    selectedLocation.getName(), "sixhour");
        }
        else {
            url = UrlBuilder.buildInternationalURL(selectedLocation.getCountryName(),
                    regionName, selectedLocation.getName(), "sixhour");
        }

        // Launch activity
        Intent intent = new Intent(StartActivity.this, SixHourForecastActivity.class);
        intent.putExtra("info", url);
        startActivity(intent);


    }

    public InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    private class AsyncSearchLocationFromString extends AsyncTask<String, String, String> {
        ProgressDialog pDialog;

        protected String doInBackground(String... urls) {

            try {
                LocationFinder locfinder = new LocationFinder();
                resultsFromSearch = locfinder.findLocationsFromString(urls[0]);
                for (Toponym t : resultsFromSearch)
                    Log.d("APP", t.getName() + " - " + t.getCountryName() + " - " + Integer.toString(t.getGeoNameId()) +
                            " - " + t.getCountryCode() + "." + t.getAdminCode1());


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

    private class AsyncBuildURLTask extends AsyncTask<String, String, String> {

        ProgressDialog pDialog;

        @Override
        protected String doInBackground(String... params) {

            try {

                //http://api.geonames.org/hierarchy?geonameId=2759794&username=mgnusl

                URL url = new URL("http://api.geonames.org/hierarchy?geonameId=" +
                        params[0] + "&username=mgnusl");

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();

                // Get the XML from an input stream
                xpp.setInput(getInputStream(url), "UTF_8");

                boolean insideGeoname = false;
                GeoName geoname = new GeoName();

                // Returns the type of current event
                int eventType = xpp.getEventType();
                // Loop thru all elements as long as they are not END_DOCUMENT
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equalsIgnoreCase("geoname")) {
                            // Inside geoname
                            insideGeoname = true;
                            geoname = new GeoName();
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
                        listOfGeonames.add(geoname);
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

            return null;

        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(StartActivity.this);
            pDialog.setMessage("Working...");
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            pDialog.dismiss();
            handleLocationSelection();
        }

    }
}