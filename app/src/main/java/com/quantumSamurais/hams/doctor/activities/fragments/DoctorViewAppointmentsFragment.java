package com.quantumSamurais.hams.doctor.activities.fragments;

import static com.quantumSamurais.hams.core.enums.FragmentTab.ALL_REQUESTS;
import static com.quantumSamurais.hams.core.enums.FragmentTab.PENDING_REQUESTS;
import static com.quantumSamurais.hams.core.enums.FragmentTab.REJECTED_REQUESTS;
import static com.quantumSamurais.hams.doctor.activities.fragments.appointmentsFragment.newInstance;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.quantumSamurais.hams.core.adapters.ViewPagerAdapter;
import com.quantumSamurais.hams.doctor.Doctor;
import com.quantumSamurais.hams.R;

public class DoctorViewAppointmentsFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Doctor myDoctor;

    private TextView nameView;
    View view;
    ExtendedFloatingActionButton extendingFAB;

    public DoctorViewAppointmentsFragment(Doctor doctor) {
        setMyDoctor(doctor);
        //constructor used to call for a new Fragment.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.see_appointments, container, false);
        setup();
        return view;
    }

    public void setup() {
        tabLayout = (TabLayout) view.findViewById(R.id.appointmentsTabs);
        viewPager = view.findViewById(R.id.tabsViewPager4Appointments);

        Log.d("tabsViewPager4Appointments value", "value of R.id.tabsViewPager... = " + R.id.tabsViewPager4Appointments);
        Log.d("viewPager value", "value of ViewPager = " + viewPager);
        Log.d("tablayout value", "value of tablayout = " + tabLayout);

        extendingFAB = getActivity().findViewById(R.id.extended_fab);

        if (extendingFAB.getVisibility() != View.GONE) {
            extendingFAB.setVisibility(View.GONE);
        }

        tabLayout.setupWithViewPager(viewPager);


        ViewPagerAdapter vpAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addFragments(newInstance(ALL_REQUESTS, myDoctor), "All Requests");
        vpAdapter.addFragments(newInstance(PENDING_REQUESTS, myDoctor), "Pending");
        vpAdapter.addFragments(newInstance(REJECTED_REQUESTS, myDoctor), "Rejected");
        viewPager.setAdapter(vpAdapter);

        nameView = view.findViewById(R.id.textView4);
        nameView.setText(myDoctor.getFirstName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    public void setMyDoctor(Doctor doctor) {myDoctor = doctor;}
}
