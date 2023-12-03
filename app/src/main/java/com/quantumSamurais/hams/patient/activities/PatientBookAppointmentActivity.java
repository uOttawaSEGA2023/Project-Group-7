package com.quantumSamurais.hams.patient.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.doctor.Specialties;

import java.util.EnumSet;
import java.util.Iterator;

public class PatientBookAppointmentActivity extends AppCompatActivity {


    Spinner selectSpec;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_appointments);
        setup();
        addListeners();
        //get the spinner from the xml.
        //create a list of items for the spinner.

    }

    private void setup() {
        selectSpec = findViewById(R.id.selectSpeciality);
        initSelectSpec();
    }
    private void initSelectSpec() {
        EnumSet<Specialties> s = EnumSet.allOf(Specialties.class);
        String[] items = new String[s.size()];
        int k = 0;
        for (Specialties specialties : s) {
            StringBuilder text = new StringBuilder();
            String nexItem = specialties.toString();
            String[] splitString = nexItem.split("_");
            for (int i = 0; i < splitString.length; i++) {
                String q = splitString[i];
                text.append(q.substring(0, 1).toUpperCase());
                text.append(q.substring(1).toLowerCase());
                if (i != splitString.length - 1)
                    text.append(' ');
            }
            items[k] = text.toString();
            k++;
        }

        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        selectSpec.setAdapter(adapter);
    }

    private void addListeners() {}

}
