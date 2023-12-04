package com.quantumSamurais.hams.patient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.doctor.Specialties;
import com.quantumSamurais.hams.doctor.adapters.CheckableItemAdapter;
import com.quantumSamurais.hams.patient.AppointmentListAdapter;
import com.quantumSamurais.hams.patient.Patient;

import java.util.EnumSet;

public class PatientMainActivity extends AppCompatActivity {


    RecyclerView currentApps, pastApps;
    Patient loggedIn;

    Button bookAppointmentBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_main);
        setup();
        addListeners();
    }

    private void setup() {
        // Retrieve Patient Object
        loggedIn = (Patient) getIntent().getSerializableExtra("patient");
        // Get Views
        currentApps = findViewById(R.id.upcomingAppointmentsRecyclerView);
        pastApps = findViewById(R.id.pastAppointmentsRecyclerView);

        bookAppointmentBtn = findViewById(R.id.bookAppointmentBtn);

        // Bind Recycler View Adapters
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);


        AppointmentListAdapter currentAdapter = new AppointmentListAdapter(this, R.layout.appoinment_item, loggedIn.getAppointments(),false);
        AppointmentListAdapter pastAdapter = new AppointmentListAdapter(this, R.layout.appoinment_item, loggedIn.getAppointments(),true);

        currentApps.setLayoutManager(layoutManager);
        currentApps.setAdapter(currentAdapter);
        pastApps.setLayoutManager(layoutManager2);
        pastApps.setAdapter(pastAdapter);
    }
    private void addListeners() {
        bookAppointmentBtn.setOnClickListener(this::bookAppointmentClicked);
    }

    private void bookAppointmentClicked(View v) {
        Intent book = new Intent(this, PatientBookAppointmentActivity.class);
        book.putExtra("patient",loggedIn);
        startActivity(book);
    }

}
