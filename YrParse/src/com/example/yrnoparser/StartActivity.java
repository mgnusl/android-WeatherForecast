package com.example.yrnoparser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.yrnoparser.location.LocationFinder;
import org.geonames.Toponym;

import javax.xml.datatype.Duration;
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
        searchEditText = (EditText)findViewById(R.id.searchEditText);

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
                if(searchString == null)
                    Toast.makeText(StartActivity.this, "Tomt søkefelt", Toast.LENGTH_LONG).show();
                else
                    new AsyncSearchLocationFromString().execute(searchEditText.getText().toString());
            }
        });




    }

    private class AsyncSearchLocationFromString extends AsyncTask<String, Void, Void> {
        ProgressDialog pDialog;

        protected Void doInBackground(String... urls) {

            try {
                LocationFinder locfinder = new LocationFinder();
                List<Toponym> list = locfinder.findLocationsFromString(urls[0]);
                for (Toponym t : list)
                    Log.d("APP", t.getName());
            } catch (Exception e) {
                Log.d("APP", "exception");
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(StartActivity.this);
            pDialog.setMessage("Working...");
            pDialog.show();
        }

        protected void onPostExecute(Long result) {
            pDialog.dismiss();

        }
    }
}