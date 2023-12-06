package com.quantumSamurais.hams.doctor.activities.fragments;

import static com.quantumSamurais.hams.core.enums.FragmentTab.ALL_REQUESTS;
import static com.quantumSamurais.hams.core.enums.FragmentTab.PENDING_REQUESTS;
import static com.quantumSamurais.hams.core.enums.FragmentTab.REJECTED_REQUESTS;
import static com.quantumSamurais.hams.doctor.activities.fragments.appointmentsFragment4Shift.newInstance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.core.adapters.ViewPagerAdapter;

public class DoctorViewAppointments4ShiftFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private TextView nameView;
    View view;
    ExtendedFloatingActionButton extendingFAB;
    long shiftID;

    public DoctorViewAppointments4ShiftFragment(long shiftID) {
        this.shiftID = shiftID;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.see_appointments_4_shifts, container, false);
        setup();
        return view;
    }

    public void setup() {
        tabLayout = (TabLayout) view.findViewById(R.id.appointmentsTabs);
        viewPager = view.findViewById(R.id.tabsViewPager4Appointments);

        extendingFAB = getActivity().findViewById(R.id.extended_fab);

        if (extendingFAB.getVisibility() != View.GONE) {
            extendingFAB.setVisibility(View.GONE);
        }

        tabLayout.setupWithViewPager(viewPager);


        ViewPagerAdapter vpAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addFragments(newInstance(ALL_REQUESTS, shiftID), "Upcoming");
        vpAdapter.addFragments(newInstance(PENDING_REQUESTS, shiftID), "Pending");
        vpAdapter.addFragments(newInstance(REJECTED_REQUESTS, shiftID), "Rejected");


        viewPager.setAdapter(vpAdapter);

        nameView = view.findViewById(R.id.textView3);
        nameView.setText("Stored as Shift # " + shiftID);


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }



}


