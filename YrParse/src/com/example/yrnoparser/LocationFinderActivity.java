package com.example.yrnoparser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.yrnoparser.data.ForecastLocation;
import com.example.yrnoparser.data.GeoName;
import com.example.yrnoparser.utils.UrlBuilder;
import com.example.yrnoparser.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class LocationFinderActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {

    private LocationClient locationClient;
    private GeoName currentLocationGeoname;
    private ForecastLocation forecastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loc_finder);

        // Check if Google Play service is available and up to date.
        if (!servicesConnected())
            finish();

        locationClient = new LocationClient(this, this, this);

        Button getForecastButton = (Button) findViewById(R.id.getForecastButton);
        getForecastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url;
                String country = null;
                String region = null;
                String region2 = null;
                String place = null;
                for(GeoName g : forecastLocation.getGeonameList()) {
                    if(g.getFcode().equals("PCLI"))
                        country = g.getName();
                    if(g.getFcode().equals("ADM1"))
                        region = g.getName();
                    if(g.getFcode().equals("ADM2"))
                        region2 = g.getName();
                    if(g.getFcode().contains("PPL"))
                        place = g.getName();
                }

                url = UrlBuilder.buildNorwegianBaseURL(country, region, region2, place);

                // Launch activity
                Intent intent = new Intent(LocationFinderActivity.this, ForecastActivity.class);
                intent.putExtra("info", url);
                startActivity(intent);
                Log.d("APP", url);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationClient.disconnect();
    }

    @Override
    public void onConnected(Bundle arg0) {

        if (!servicesConnected())
            return;
        Location location = locationClient.getLastLocation();

        TextView textView = (TextView) findViewById(R.id.textView);
        if(location == null)
            return;
        textView.setText(location.getLatitude() + ", " + location.getLongitude());

        new AsyncReverseGeocode().execute(Double.toString(location.getLatitude()),
                Double.toString(location.getLongitude()));

    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {

        Toast.makeText(this, "Connection failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDisconnected() {
        Toast.makeText(this, "Disconnected", Toast.LENGTH_LONG).show();
    }

    private class AsyncReverseGeocode extends AsyncTask<String, String, String> {
        ProgressDialog pDialog;

        protected String doInBackground(String... args) {

            try {

                URL url = new URL("http://api.geonames.org/findNearbyPlaceName?lat="
                        + args[0] + "&lng=" + args[1] + "&username=mgnusl");

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();

                // Get the XML from an input stream
                xpp.setInput(Utils.getInputStream(url), "UTF_8");

                boolean insideGeoname = false;
                currentLocationGeoname = new GeoName();

                // Returns the type of current event
                int eventType = xpp.getEventType();
                // Loop through all elements as long as they are not END_DOCUMENT
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equalsIgnoreCase("geoname")) {
                            // Inside geoname
                            insideGeoname = true;
                        } else if (xpp.getName().equalsIgnoreCase("geonameId")) {
                            if (insideGeoname) {
                                currentLocationGeoname.setGeonameID(Integer.parseInt(xpp.nextText()));
                            }
                        }
                    }

                    eventType = xpp.next(); //move to next element
                }

                // Check if we found a result. If not, show error and stop asynctask
                if (currentLocationGeoname.getGeonameID() == 0) {
                    Log.d("APP", "NO GEONAME ID SET");
                    return null;
                }

                // ***
                // If we have a geonameID, extract data from its corresponding url
                URL geonameURL = new URL("http://api.geonames.org/hierarchy?geonameId=" +
                        currentLocationGeoname.getGeonameID() + "&username=mgnusl");

                XmlPullParserFactory factory2 = XmlPullParserFactory.newInstance();
                factory2.setNamespaceAware(false);
                XmlPullParser xpp2 = factory.newPullParser();

                // Get the XML from an input stream
                xpp2.setInput(Utils.getInputStream(geonameURL), "UTF_8");

                // Set location fields for data we already know
                forecastLocation = new ForecastLocation();
                forecastLocation.setGeonamesID(currentLocationGeoname.getGeonameID());

                boolean insideGeoname2 = false;
                GeoName geoname = new GeoName();

                // Returns the type of current event
                int eventType2 = xpp2.getEventType();
                // Loop through all elements as long as they are not END_DOCUMENT
                while (eventType2 != XmlPullParser.END_DOCUMENT) {
                    if (eventType2 == XmlPullParser.START_TAG) {
                        if (xpp2.getName().equalsIgnoreCase("geoname")) {
                            // Inside geoname
                            insideGeoname2 = true;
                        } else if (xpp2.getName().equalsIgnoreCase("name")) {
                            if (insideGeoname2) {
                                geoname.setName(xpp2.nextText());
                            }
                        } else if (xpp2.getName().equalsIgnoreCase("fcode")) {
                            if (insideGeoname2) {
                                geoname.setFcode(xpp2.nextText());
                            }
                        }
                    } else if (eventType2 == XmlPullParser.END_TAG && xpp2.getName().equalsIgnoreCase("geoname")) {
                        insideGeoname2 = false;
                        forecastLocation.addGeoname(geoname);
                        geoname = new GeoName();
                    }

                    eventType2 = xpp2.next(); //move to next element

                } // end while

            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LocationFinderActivity.this);
            pDialog.setMessage("Working...");
            pDialog.show();
        }

        protected void onPostExecute(String result) {
            pDialog.dismiss();

            // FOR TESTING
            StringBuilder sb = new StringBuilder();
            for (GeoName g : forecastLocation.getGeonameList()) {
                sb.append(g.getName() + " - ");
            }

            testResult(sb.toString());

            // Run through the list of GeoNames the Location has and set the needed fields
            for (GeoName g : forecastLocation.getGeonameList()) {
                if (g.getFcode().equals("ADM2")) { // Child region. IE. "Bærum" or "Santa Barbara"
                    forecastLocation.setChildRegion(g.getName());
                }
                if (g.getFcode().equals("ADM1")) { // Parent region. IE. "Akershus" or "California"
                    forecastLocation.setRegion(g.getName());
                }
                if (g.getFcode().startsWith("PPL")) { //Place name
                    forecastLocation.setName(g.getName());
                }
                if (g.getFcode().equals("PCLI")) { //Country
                    forecastLocation.setCountry(g.getName());
                }
            }

            Intent intent = getIntent();
            intent.putExtra("nearbylocation", forecastLocation);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    public void testResult(String s) {
        TextView tv = (TextView) findViewById(R.id.textView2);
        tv.setText(s);
    }

    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("APP", "Google Play services is available.");
            Toast.makeText(this, "Available", Toast.LENGTH_SHORT).show();

            return true;
        } else {
            // Get the error code
            Toast.makeText(this, "Error code: " + Integer.toString(resultCode), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

}
