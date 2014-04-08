package com.example.yrnoparser;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.util.List;

public class OverviewListAdapter extends ArrayAdapter<WeatherForecast> {

    private int resource;
    private LayoutInflater inflater;
    private Context context;
    private TypedArray icons;
    private DateTimeFormatter formatter;
    private DateTimeFormatter twentyFourHourDateFormat;


    public OverviewListAdapter(Context context, int resourceId, List<WeatherForecast> objects, TypedArray icons) {
        super(context, resourceId, objects);
        resource = resourceId;
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.icons = icons;
        formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
        twentyFourHourDateFormat = new DateTimeFormatterBuilder().appendPattern("dd/MM/YY HH:mm").toFormatter();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = (RelativeLayout) inflater.inflate(resource, null);

        WeatherForecast rowItem = getItem(position);

        // Find the correct symbol
        //TODO: find symbol based on time
        int symbol;
        if(rowItem.getSymbol() == 1)
            symbol = 0;
        else {
            // Hvis dag (-2 hvis natt)
            symbol = (rowItem.getSymbol()*2)-1;
        }
        // Convert from ISO8601 time format to Joda DateTime
        DateTime fromTime = formatter.parseDateTime(rowItem.getFromTime());
        String fromTimeString = twentyFourHourDateFormat.print(fromTime);

        DateTime toTime = formatter.parseDateTime(rowItem.getToTime());
        String toTimeString = twentyFourHourDateFormat.print(toTime);

        // Views
        ImageView rowImage = (ImageView) convertView.findViewById(R.id.weatherTypeImageView);
        rowImage.setImageDrawable(icons.getDrawable(symbol));

        TextView fromTextView = (TextView) convertView.findViewById(R.id.fromTextView);
        fromTextView.setText(fromTimeString);

        TextView toTextView = (TextView) convertView.findViewById(R.id.toTextView);
        toTextView.setText(toTimeString);

        return convertView;

    }
}
