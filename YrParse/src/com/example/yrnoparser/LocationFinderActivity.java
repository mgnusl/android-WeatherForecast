package com.example.yrnoparser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.example.yrnoparser.data.GeoName;
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

public class LocationFinderActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {

    private LocationClient locationClient;
    private GeoName currentLocationGeoname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loc_finder);

        // Check if Google Play service is available and up to date.
        final int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            Toast.makeText(this, "Google Play service is not available", Toast.LENGTH_LONG).show();
            finish();
        }

        locationClient = new LocationClient(this, this, this);
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
        Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show();

        Location location = locationClient.getLastLocation();
        Log.d("APP", "location: " + location.toString());

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(location.getLatitude() + ", " + location.getLongitude());

        new AsyncReverseGeocode().execute(location.getLatitude(), location.getLongitude());

    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        Toast.makeText(this, "Connection failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDisconnected() {
        Toast.makeText(this, "Disconnected", Toast.LENGTH_LONG).show();
    }

    private class AsyncReverseGeocode extends AsyncTask<Double, String, String> {
        ProgressDialog pDialog;

        protected String doInBackground(Double... args) {

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

            } catch(XmlPullParserException e) {
                e.printStackTrace();
            } catch(IOException e) {
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
            Log.d("APP", Integer.toString(currentLocationGeoname.getGeonameID()));
        }
    }

}
