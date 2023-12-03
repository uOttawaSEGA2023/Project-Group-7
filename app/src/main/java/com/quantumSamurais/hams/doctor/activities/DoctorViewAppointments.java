package com.quantumSamurais.hams.doctor.activities;

import static com.quantumSamurais.hams.core.enums.FragmentTab.ALL_REQUESTS;
import static com.quantumSamurais.hams.core.enums.FragmentTab.PENDING_REQUESTS;
import static com.quantumSamurais.hams.core.enums.FragmentTab.REJECTED_REQUESTS;
import static com.quantumSamurais.hams.doctor.activities.fragments.appointmentsFragment.newInstance;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.core.adapters.ViewPagerAdapter;
import com.quantumSamurais.hams.doctor.Doctor;

public class DoctorViewAppointments extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Doctor myDoctor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getSerializableExtra("doctor") != null){
            myDoctor = (Doctor) getIntent().getSerializableExtra("doctor");

        }
        setContentView(R.layout.see_appointments);
        setup();
    }

    public void setup() {
        tabLayout = findViewById(R.id.appointmentsTabs);
        viewPager = findViewById(R.id.tabsViewPager4Appointments);

        tabLayout.setupWithViewPager(viewPager);

        //Sets the info for the Tab Layout
        ViewPagerAdapter vpAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addFragments(newInstance(ALL_REQUESTS, myDoctor), "All Requests");
        vpAdapter.addFragments(newInstance(PENDING_REQUESTS, myDoctor), "Pending");
        vpAdapter.addFragments(newInstance(REJECTED_REQUESTS, myDoctor), "Rejected");
        viewPager.setAdapter(vpAdapter);
    }


}
