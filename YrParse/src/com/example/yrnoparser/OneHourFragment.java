package com.example.yrnoparser;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.applidium.headerlistview.HeaderListView;
import com.example.yrnoparser.adapter.OneHourSectionAdapter;
import com.example.yrnoparser.data.ForecastLocation;
import com.example.yrnoparser.data.SingleDay;

import java.util.ArrayList;

public class OneHourFragment extends Fragment {
    private ArrayList<SingleDay> listOfOneHourForecasts;
    private ForecastLocation forecastLocation;
    private TypedArray weatherIcons;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.one_hour_fragment, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            forecastLocation = bundle.getParcelable("forecastlocation");
            listOfOneHourForecasts = bundle.getParcelableArrayList("listofonehourdays");
        }

        weatherIcons = getResources().obtainTypedArray(R.array.newicons);

        HeaderListView list = (HeaderListView) view.findViewById(R.id.oneHourListView);

        // Add header to the list
        View headerView = inflater.inflate(R.layout.six_hour_header, null);

        TextView locationTextView = (TextView) headerView.findViewById(R.id.sixHourLocationTextView);
        locationTextView.setText(forecastLocation.getName());
        TextView typeTextView = (TextView) headerView.findViewById(R.id.sixHourTypeTextView);
        typeTextView.setText(forecastLocation.getType());
        TextView countryTextView = (TextView) headerView.findViewById(R.id.sixHourCountryTextView);
        countryTextView.setText(forecastLocation.getCountry());

        ListView lv = list.getListView();
        lv.addHeaderView(headerView);
        list.setAdapter(new OneHourSectionAdapter(getActivity(), listOfOneHourForecasts, weatherIcons));

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
