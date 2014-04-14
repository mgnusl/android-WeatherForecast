package com.example.yrnoparser;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

public class LocationFinderActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {

    private LocationClient locationClient;

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

        TextView textView = (TextView)findViewById(R.id.textView);
        textView.setText(location.getLatitude() + ", " + location.getLongitude());

    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        Toast.makeText(this, "Connection failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDisconnected() {
        Toast.makeText(this, "Disconnected", Toast.LENGTH_LONG).show();
    }

}
