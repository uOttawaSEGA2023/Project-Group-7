package com.quantumSamurais.hams.ui.settings;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.databinding.FragmentSettingspageBinding;
import com.quantumSamurais.hams.doctor.activities.DoctorSignUpActivity;


public class SettingFragment extends Fragment{

    RadioButton acceptApptDefaultRB;
    private FragmentSettingspageBinding binding;

    Bundle args;
    View myView;

    boolean rBVal;

    public SettingFragment() {
    }


    public static SettingFragment newInstance(boolean value){
        final SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putBoolean("value", value);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateview(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        myView = inflater.inflate(R.layout.fragment_settingspage, container, false);


        Bundle args = getArguments();
        if (args != null) {
            rBVal = args.getBoolean("acceptsByDefault");
        }
        acceptApptDefaultRB = (RadioButton) getView().findViewById(R.id.acceptRB);
        if (rBVal) {
            acceptApptDefaultRB.toggle();
        }

        acceptApptDefaultRB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean stateRB = acceptApptDefaultRB.isChecked();

                Intent newIntent = new Intent(getActivity(), DoctorSignUpActivity.class);
                if (stateRB) { // was clicked; meaning that the option was not done before.
                    newIntent.putExtra("acceptsByDefault", true);

                } else {
                    newIntent.putExtra("acceptsByDefault", false);

                }
                startActivity(newIntent);
            }
        });

        return myView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

