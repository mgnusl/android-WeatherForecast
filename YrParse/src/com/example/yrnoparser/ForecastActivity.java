package com.example.yrnoparser;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import com.example.yrnoparser.adapter.MyPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ForecastActivity extends FragmentActivity {
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        List<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(new OneHourFragment());
        fragments.add(new SixHourFragment());

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new MyPagerAdapter(fragmentManager, fragments));

    }
}
