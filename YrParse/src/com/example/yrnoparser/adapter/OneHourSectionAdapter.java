package com.example.yrnoparser.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import com.applidium.headerlistview.SectionAdapter;
import com.example.yrnoparser.R;
import com.example.yrnoparser.data.Forecast;
import com.example.yrnoparser.data.SingleDay;
import com.example.yrnoparser.utils.DateFormatter;

import java.util.ArrayList;

public class OneHourSectionAdapter extends SectionAdapter {

    private Context context;
    private ArrayList<SingleDay> data;
    private TypedArray icons;
    private String[] weekDayNames;

    public OneHourSectionAdapter(Context context, ArrayList<SingleDay> data, TypedArray icons) {
        this.context = context;
        this.data = data;
        this.icons = icons;
        weekDayNames = context.getResources().getStringArray(R.array.weekdays);
    }

    @Override
    public int numberOfSections() {
        return data.size();
    }

    @Override
    public int numberOfRows(int section) {
        return data.get(section).getForecasts().size();
    }

    @Override
    public Object getRowItem(int section, int row) {
        return null;
    }

    @Override
    public boolean hasSectionHeaderView(int section) {
        return true;
    }

    @Override
    public View getRowView(int section, int row, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_data_six_hour, parent, false);
        }

        Forecast forecast = data.get(section).getForecasts().get(row);

        TextView fromTextView = (TextView) convertView.findViewById(R.id.fromTextView);
        fromTextView.setText(DateFormatter.get24HourString(forecast.getFromTime()));

        TextView toTextView = (TextView) convertView.findViewById(R.id.toTextView);
        toTextView.setText(DateFormatter.get24HourString(forecast.getToTime()));

        TextView tempTextView = (TextView) convertView.findViewById(R.id.temperatureTextView);
        tempTextView.setText(forecast.getTemperature() + "°C");
        if (Integer.parseInt(forecast.getTemperature()) >= 0)
            tempTextView.setTextColor(context.getResources().getColor(R.color.red_text));
        else
            tempTextView.setTextColor(context.getResources().getColor(R.color.blue_text));

        TextView precipitationTextView = (TextView) convertView.findViewById(R.id.precipitationTextView);
        if (forecast.getPrecipitation().equals("0") || forecast.getPrecipitation() == null)
            precipitationTextView.setText("0 mm");
        else if (forecast.getPrecipitationMin() == null)
            precipitationTextView.setText("0 mm");
        else
            precipitationTextView.setText(forecast.getPrecipitationMin() + "-" + forecast.getPrecipitationMax() + " mm");

        // Find the correct symbol
        int symbol;
        if (forecast.getSymbol() == 1)
            if (forecast.getFromTime().getHourOfDay()<7)
                symbol = 1;
            else
                symbol = 0;
        else {
            // Check if day or night period
            if (forecast.getFromTime().getHourOfDay()<7) //"natt"
                symbol = (forecast.getSymbol() * 2) - 1;
            else
                symbol = (forecast.getSymbol() * 2) - 2;
        }

        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.weatherTypeImageView);
        iconImageView.setImageDrawable(icons.getDrawable(symbol));

        return convertView;

    }

    @Override
    public int getSectionHeaderViewTypeCount() {
        return 2;
    }

    @Override
    public int getSectionHeaderItemViewType(int section) {
        return 1;
    }

    @Override
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = (TextView) inflater.inflate(context.getResources().getLayout(android.R.layout.simple_list_item_1), null);
        }

        TextView text = (TextView) convertView.findViewById(android.R.id.text1);
        SingleDay day = data.get(section);
        text.setText(weekDayNames[day.getDate().getDayOfWeek() - 1] + " " + day.getDateString());
        text.setTextColor(context.getResources().getColor(R.color.sticky_headers_font));

        convertView.setBackgroundColor(context.getResources().getColor(R.color.custom_theme_primary));

        return convertView;
    }

    @Override
    public void onRowItemClick(AdapterView<?> parent, View view, int section, int row, long id) {
        super.onRowItemClick(parent, view, section, row, id);
        Log.d("APP", "Section: " + Integer.toString(section) + ". Row: " + Integer.toString(row));
    }

}
