package com.quantumSamurais.hams.patient.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.appointment.Appointment;
import com.quantumSamurais.hams.appointment.Shift;
import com.quantumSamurais.hams.doctor.Specialties;
import com.quantumSamurais.hams.patient.AppointmentListAdapter;
import com.quantumSamurais.hams.patient.Patient;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class PatientBookAppointmentActivity extends AppCompatActivity {


    EditText dateText;
    Button changeDate;
    Spinner selectSpec;

    RecyclerView toBook;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_appointments);
        setup();
        addListeners();
    }

    private void setup() {
        Patient p = (Patient) getIntent().getSerializableExtra("patient");
        assert p != null;
        selectSpec = findViewById(R.id.selectSpeciality);
        initSelectSpec();
        dateText = findViewById(R.id.dateTextbox);
        changeDate = findViewById(R.id.datePickBtn);
        toBook = findViewById(R.id.availAppointments);

        List<Appointment> apps = new ArrayList<>();
//        if(p.getShifts() != null) {
//            for(Shift s: p.getShifts()) {
//                apps.addAll(s.getAppointments());
//            }
//        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        AppointmentListAdapter availAdapter = new AppointmentListAdapter(this, R.layout.appoinment_item, apps,false);
        toBook.setLayoutManager(layoutManager);
        toBook.setAdapter(availAdapter);

    }

    private void generateAppointments() {

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

    private void addListeners() {
        changeDate.setOnClickListener(this::pickDateClicked);
    }

    private void pickDateClicked(View v) {
       DatePickerFragment picker = new DatePickerFragment(this::dateSet);
       picker.show(getSupportFragmentManager(),"datePicker");
    }

    private void dateSet(String date) {
        dateText.setText(date);
    }

}
