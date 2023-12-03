package com.quantumSamurais.hams.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.core.enums.FragmentTab;
import com.quantumSamurais.hams.doctor.Doctor;
import com.quantumSamurais.hams.database.Database;


public class SettingFragment extends Fragment {

    Fragment activeTab;

    Doctor myDoctor;

    Database db;

    ConstraintLayout background;
    private CheckBox acceptApptDefaultCB;
    private Intent intent;

    ExtendedFloatingActionButton extendingFAB;
    public SettingFragment(Doctor doctor) {
        setMyDoctor(doctor);
        // required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.setting_fragment, container,false);
        acceptApptDefaultCB = (CheckBox) view.findViewById(R.id.acceptCB);
        db = Database.getInstance();
        background = view.findViewById(R.id.settings_layout);
        extendingFAB = getActivity().findViewById(R.id.extended_fab);
        if (myDoctor.getAcceptsAppointmentsByDefault()) {
            acceptApptDefaultCB.toggle();
        }

        acceptApptDefaultCB.setOnClickListener(this::onCBClicked);
        background.setOnClickListener(this::onBackgroundClicked);


        if (extendingFAB.getVisibility() !=(View.GONE)) {
            extendingFAB.setVisibility(View.GONE);
        }
        return view;
    }

    private void onCBClicked(View view) {
        boolean stateCB = acceptApptDefaultCB.isChecked();
        if (stateCB) {
            myDoctor.setAcceptsAppointmentsByDefault(true);
            db.getDoctor(myDoctor.getEmail()).setAcceptsAppointmentsByDefault(true);
        } else {
            db.getDoctor(myDoctor.getEmail()).setAcceptsAppointmentsByDefault(false);
        }
    }


    private void onBackgroundClicked(View view) {
        //this is an empty method on purpose; used to block off new shift.

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    public void setMyDoctor(Doctor someDoctor){myDoctor = someDoctor;}
}
