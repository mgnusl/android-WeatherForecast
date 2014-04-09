package com.example.yrnoparser;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.util.List;

public class OverviewListAdapter extends ArrayAdapter<Forecast> {

    private int resource;
    private LayoutInflater inflater;
    private Context context;
    private TypedArray icons;
    private DateTimeFormatter formatter;
    private DateTimeFormatter twentyFourHourDateFormat;


    public OverviewListAdapter(Context context, int resourceId, List<Forecast> objects, TypedArray icons) {
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

        Forecast rowItem = getItem(position);

        // Convert from ISO8601 time format to Joda DateTime
        //DateTime fromTime = formatter.parseDateTime(rowItem.getFromTime());
        //String fromTimeString = twentyFourHourDateFormat.print(fromTime);

        //DateTime toTime = formatter.parseDateTime(rowItem.getToTime());
        //String toTimeString = twentyFourHourDateFormat.print(toTime);

        // Find the correct symbol
        //TODO: find symbol based on time
        int symbol;
        if(rowItem.getSymbol() == 1)
            if(rowItem.getPeriod() == 3 || rowItem.getPeriod() == 0)
                symbol = 0;
            else
                symbol = 1;
        else {
            // Check if day or night period
            if(rowItem.getPeriod() == 3 || rowItem.getPeriod() == 0) //"natt"
                symbol = (rowItem.getSymbol()*2)-1;
            else
                symbol = (rowItem.getSymbol()*2)-2;
        }


        // Views
        ImageView rowImage = (ImageView) convertView.findViewById(R.id.weatherTypeImageView);
        rowImage.setImageDrawable(icons.getDrawable(symbol));

        TextView fromTextView = (TextView) convertView.findViewById(R.id.fromTextView);
        //fromTextView.setText(fromTimeString);

        TextView toTextView = (TextView) convertView.findViewById(R.id.toTextView);
        //toTextView.setText(toTimeString);

        return convertView;

    }
}
