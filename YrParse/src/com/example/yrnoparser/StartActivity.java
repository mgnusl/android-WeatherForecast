package com.example.yrnoparser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class StartActivity extends Activity {

    private Button sixHourButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        sixHourButton = (Button)findViewById(R.id.getSixHourButton);
        sixHourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StartActivity.this, SixHourForecastActivity.class);
                startActivity(i);
            }
        });
    }
}