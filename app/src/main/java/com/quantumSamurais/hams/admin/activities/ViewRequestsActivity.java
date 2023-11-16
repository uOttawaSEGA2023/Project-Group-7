package com.quantumSamurais.hams.admin.activities;

import static com.quantumSamurais.hams.admin.activities.fragments.requestsFragment.newInstance;
import static com.quantumSamurais.hams.core.enums.FragmentTab.ALL_REQUESTS;
import static com.quantumSamurais.hams.core.enums.FragmentTab.PENDING_REQUESTS;
import static com.quantumSamurais.hams.core.enums.FragmentTab.REJECTED_REQUESTS;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.core.adapters.ViewPagerAdapter;

public class ViewRequestsActivity extends AppCompatActivity  {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.see_requests_view_tabbed);
        setup();
    }

    public void setup() {
        tabLayout = findViewById(R.id.requestsTabs);
        viewPager = findViewById(R.id.tabsViewPager);

        tabLayout.setupWithViewPager(viewPager);

        //Sets the info for the Tab Layout
        ViewPagerAdapter vpAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addFragments(newInstance(ALL_REQUESTS), "All Requests");
        vpAdapter.addFragments(newInstance(PENDING_REQUESTS), "Pending");
        vpAdapter.addFragments(newInstance(REJECTED_REQUESTS), "Rejected");
        viewPager.setAdapter(vpAdapter);
    }







}
