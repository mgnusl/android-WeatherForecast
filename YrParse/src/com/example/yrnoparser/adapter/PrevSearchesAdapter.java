package com.example.yrnoparser.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import com.applidium.headerlistview.SectionAdapter;
import com.example.yrnoparser.R;
import com.example.yrnoparser.data.ForecastLocation;

import java.util.ArrayList;

public class PrevSearchesAdapter extends SectionAdapter {

    private Context context;
    private ArrayList<ForecastLocation> data;

    public PrevSearchesAdapter(Context context, ArrayList<ForecastLocation> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int numberOfSections() {
        return 1;
    }

    @Override
    public int numberOfRows(int section) {
        return data.size();
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
            convertView = inflater.inflate(R.layout.row_prev_searches, parent, false);
        }

        ForecastLocation fl = data.get(row);
        TextView cityTV = (TextView) convertView.findViewById(R.id.cityTextView);
        TextView regionTV = (TextView) convertView.findViewById(R.id.regionTextView);
        TextView countryTV = (TextView) convertView.findViewById(R.id.countryTextView);

        cityTV.setText(fl.getName());
        regionTV.setText(fl.getRegion());
        countryTV.setText(fl.getCountry());

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
            convertView = inflater.inflate(context.getResources().getLayout(R.layout.prev_searches_header), null);
        }

        TextView text = (TextView) convertView.findViewById(R.id.prevHeaderTextView);
        text.setTextColor(context.getResources().getColor(R.color.white));

        convertView.setBackgroundColor(context.getResources().getColor(R.color.custom_theme_primary));

        return convertView;
    }

    @Override
    public void onRowItemClick(AdapterView<?> parent, View view, int section, int row, long id) {
        super.onRowItemClick(parent, view, section, row, id);
        Log.d("APP", "Section: " + Integer.toString(section) + ". Row: " + Integer.toString(row));
    }


}
