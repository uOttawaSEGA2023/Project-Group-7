package com.quantumSamurais.hams.admin.activities;

import static com.quantumSamurais.hams.admin.activities.fragments.requestsFragment.newInstance;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.admin.adapters.RequestItemAdapter;
import com.quantumSamurais.hams.admin.adapters.ViewPagerAdapter;
import com.quantumSamurais.hams.database.DatabaseUtils;
import com.quantumSamurais.hams.database.Request;

import java.util.ArrayList;

public class ViewRequestsActivity extends AppCompatActivity  {
    DatabaseUtils tools = new DatabaseUtils();
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ArrayList<Request> requests;
    RequestItemAdapter requestsAdapter;
    RecyclerView requestsStack;
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
        vpAdapter.addFragments(newInstance(RequestItemAdapter.FragmentTab.ALL_REQUESTS), "All Requests");
        vpAdapter.addFragments(newInstance(RequestItemAdapter.FragmentTab.PENDING_REQUESTS), "Pending");
        vpAdapter.addFragments(newInstance(RequestItemAdapter.FragmentTab.REJECTED_REQUESTS), "Rejected");
        viewPager.setAdapter(vpAdapter);
    }




    public class ShowMoreActivity extends AppCompatActivity{
        @Override
        protected void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_show_more);

            // Retrieve data passed
            Intent intent = getIntent();
            Request selectedRequest = intent.getParcelableExtra("selectedRequest");


        }
    }


}
