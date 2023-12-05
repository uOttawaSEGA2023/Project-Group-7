package com.quantumSamurais.hams.patient.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.appointment.Appointment;
import com.quantumSamurais.hams.database.Database;
import com.quantumSamurais.hams.doctor.Specialties;
import com.quantumSamurais.hams.doctor.adapters.CheckableItemAdapter;
import com.quantumSamurais.hams.patient.AppointmentListAdapter;
import com.quantumSamurais.hams.patient.Patient;

import java.util.ArrayList;
import java.util.EnumSet;

public class PatientMainActivity extends AppCompatActivity {


    TextView topText;
    RecyclerView currentApps, pastApps;

    AppointmentListAdapter currentAp, pastAp;
    Patient loggedIn;

    Button bookAppointmentBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_main);
        setup();
        addListeners();
    }

    @SuppressLint("SetTextI18n")
    private void setup() {
        // Retrieve Patient Object
        loggedIn = (Patient) getIntent().getSerializableExtra("patient");

        topText = findViewById(R.id.welcomeMessage);
        topText.setText("Welcome, " + loggedIn.getFirstName());

        // Get Views
        currentApps = findViewById(R.id.upcomingAppointmentsRecyclerView);
        pastApps = findViewById(R.id.pastAppointmentsRecyclerView);

        bookAppointmentBtn = findViewById(R.id.bookAppointmentBtn);

        // Bind Recycler View Adapters
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);


        currentAp = new AppointmentListAdapter(this, R.layout.appoinment_item, loggedIn.getAppointments(),false, false,null);
        pastAp = new AppointmentListAdapter(this, R.layout.appoinment_item, loggedIn.getAppointments(),true, false,null);

        currentApps.setLayoutManager(layoutManager);
        currentApps.setAdapter(currentAp);
        pastApps.setLayoutManager(layoutManager2);
        pastApps.setAdapter(pastAp);
    }
    private void addListeners() {
        bookAppointmentBtn.setOnClickListener(this::bookAppointmentClicked);
        Database.getInstance().getPatientAppointments(loggedIn,this::updateAppointments);
        Database.getInstance().listenForAppointmentCancelPatient(loggedIn,this::updateAppointments);
    }


    private void updateAppointments(ArrayList<Appointment> apps) {
        currentAp.updateData(apps);
        pastAp.updateData(apps);
        currentAp.notifyDataSetChanged();
        pastAp.notifyDataSetChanged();
    }

    private void bookAppointmentClicked(View v) {
        Intent book = new Intent(this, PatientBookAppointmentActivity.class);
        book.putExtra("patient",loggedIn);
        startActivity(book);
    }

}
