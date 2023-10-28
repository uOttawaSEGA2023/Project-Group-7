package com.quantumSamurais.hams.admin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.admin.adapters.SpecialtyItemAdapter;
import com.quantumSamurais.hams.doctor.Specialties;
import com.quantumSamurais.hams.user.UserType;

import java.util.ArrayList;

public class ShowMoreActivity extends AppCompatActivity {
    TextView firstName, lastName, email, phoneNumber, address, employeeNumber, healthCardNumber;
    RecyclerView specialtiesStack;
    ArrayList<Specialties> specialties;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Retrieve data passed
        Intent intent = getIntent();
        if (intent != null) {
            switch ((UserType) intent.getSerializableExtra("userType")) {
                case DOCTOR:
                    setContentView(R.layout.activity_show_more_doctor);
                    setupDoctor();
                    firstName.setText("First Name: " + intent.getStringExtra("firstName"));
                    lastName.setText("Last Name: " + intent.getStringExtra("lastName"));
                    email.setText("Email Address: " + intent.getStringExtra("email"));
                    phoneNumber.setText("Phone Number: " + intent.getStringExtra("phoneNumber"));
                    address.setText("Postal Address: " + intent.getStringExtra("address"));
                    employeeNumber.setText("Employee Number: " + intent.getStringExtra("employeeNumber"));
                    specialties = (ArrayList<Specialties>) intent.getSerializableExtra("specialties");


                    SpecialtyItemAdapter specialtyItemAdapter = new SpecialtyItemAdapter(this, specialties);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
                    specialtiesStack.setLayoutManager(layoutManager);
                    specialtiesStack.setAdapter(specialtyItemAdapter);
                    break;





                case PATIENT:
                    setContentView(R.layout.activity_show_more_patient);
                    setupPatient();

                    //Duplicated from above since the ids, and intents are the same for the relevant
                    //fields.
                    firstName.setText("First Name: " + intent.getStringExtra("firstName"));
                    lastName.setText("Last Name: " + intent.getStringExtra("lastName"));
                    email.setText("Email Address: " + intent.getStringExtra("email"));
                    phoneNumber.setText("Phone Number: " + intent.getStringExtra("phoneNumber"));
                    address.setText("Postal Address: " + intent.getStringExtra("address"));
                    employeeNumber.setText("Health Card Number: " + intent.getStringExtra("healthCardNumber"));

                case ADMIN:
                    //I literally won't pass this
            }
        }


    }

    private void setupDoctor() {
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        phoneNumber = findViewById(R.id.phoneNumber);
        address = findViewById(R.id.address);
        employeeNumber = findViewById(R.id.healthCardNumber);
        specialtiesStack = findViewById(R.id.specialtiesRecyclerView);
    }

    private void setupPatient() {
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        phoneNumber = findViewById(R.id.phoneNumber);
        address = findViewById(R.id.address);
        healthCardNumber = findViewById(R.id.healthCardNumber);
    }
}

