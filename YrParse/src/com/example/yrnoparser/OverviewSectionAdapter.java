package com.example.yrnoparser;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import com.applidium.headerlistview.SectionAdapter;

import java.util.ArrayList;

public class OverviewSectionAdapter extends SectionAdapter {

    private Context context;
    private ArrayList<Forecast> data;
    private TypedArray icons;

    public OverviewSectionAdapter(Context context, ArrayList<Forecast> data, TypedArray icons) {
        this.context = context;
        this.data = data;
        this.icons = icons;
    }

    @Override
    public int numberOfSections() {
        return data.size() / 4;
    }

    @Override
    public int numberOfRows(int section) {
        return 4;
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

        Forecast forecast = data.get(row);

        TextView fromTextView = (TextView) convertView.findViewById(R.id.fromTextView);
        fromTextView.setText(forecast.getFromTimeString());

        TextView toTextView = (TextView) convertView.findViewById(R.id.toTextView);
        toTextView.setText(forecast.getToTimeString());

        // Find the correct symbol
        //TODO: find symbol based on time
        int symbol;
        if (forecast.getSymbol() == 1)
            if (forecast.getPeriod() == 3 || forecast.getPeriod() == 0)
                symbol = 0;
            else
                symbol = 1;
        else {
            // Check if day or night period
            if (forecast.getPeriod() == 3 || forecast.getPeriod() == 0) //"natt"
                symbol = (forecast.getSymbol() * 2) - 2;
            else
                symbol = (forecast.getSymbol() * 2) - 1;
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
            if (getSectionHeaderItemViewType(section) == 0) {

                convertView = (TextView) inflater.inflate(context.getResources().getLayout(android.R.layout.simple_list_item_1), null);
            } else {
                convertView = inflater.inflate(context.getResources().getLayout(android.R.layout.simple_list_item_2), null);
            }
        }

        if (getSectionHeaderItemViewType(section) == 0) {
            ((TextView) convertView).setText("Header for section " + section);
        } else {
            ((TextView) convertView.findViewById(android.R.id.text1)).setText("Header for section " + section);
            ((TextView) convertView.findViewById(android.R.id.text2)).setText("Has a detail text field");
        }

        switch (section) {
            case 0:
                convertView.setBackgroundColor(context.getResources().getColor(R.color.holo_red_light));
                break;
            case 1:
                convertView.setBackgroundColor(context.getResources().getColor(R.color.holo_orange_light));
                break;
            case 2:
                convertView.setBackgroundColor(context.getResources().getColor(R.color.holo_green_light));
                break;
            case 3:
                convertView.setBackgroundColor(context.getResources().getColor(R.color.holo_blue_light));
                break;
        }
        return convertView;
    }

    @Override
    public void onRowItemClick(AdapterView<?> parent, View view, int section, int row, long id) {
        super.onRowItemClick(parent, view, section, row, id);
    }

}
