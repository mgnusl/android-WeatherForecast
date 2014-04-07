package com.example.yrnoparser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class OverviewListAdapter extends ArrayAdapter<WeatherForecast> {

    private int resource;
    private LayoutInflater inflater;
    private Context context;

    public OverviewListAdapter(Context context, int resourceId, List<WeatherForecast> objects) {
        super(context, resourceId, objects);
        resource = resourceId;
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = (RelativeLayout) inflater.inflate(resource, null);

        //ultrasound image/data for THIS row
        WeatherForecast rowItem = getItem(position);

        String symbol = rowItem.getSymbol();

        ImageView rowImage = (ImageView) convertView.findViewById(R.id.weatherTypeImageView);
        rowImage.setImageResource(Integer.parseInt(symbol));

        TextView commentTextView = (TextView) convertView.findViewById(R.id.infoTextView);
        commentTextView.setText(rowItem.toString());

        return convertView;

    }
}
