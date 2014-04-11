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
import com.example.yrnoparser.location.LocationFinder;
import com.example.yrnoparser.utils.UrlBuilder;
import org.geonames.Toponym;

import java.util.ArrayList;
import java.util.List;


public class StartActivity extends Activity {

    private List<Toponym> resultsFromSearch;

    private Button sixHourButton, searchButton;
    private EditText searchEditText;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        resultsFromSearch = new ArrayList<Toponym>();
        searchEditText = (EditText) findViewById(R.id.searchEditText);


        sixHourButton = (Button) findViewById(R.id.getSixHourButton);
        sixHourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StartActivity.this, SixHourForecastActivity.class);
                startActivity(i);
            }
        });

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
                handleLocationSelection(item.getItemId());
                return true;
            }
        });
        popup.show();

    }

    private void handleLocationSelection(int selectedLocationIndex) {
        Toponym selectedLocation = resultsFromSearch.get(selectedLocationIndex);
        String url;
        /*if(selectedLocation.getCountryCode().equalsIgnoreCase("no"))
            // Build norwegian URL
        else
            // Build int'l*/

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

        protected String doInBackground(String... urls) {

            try {



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