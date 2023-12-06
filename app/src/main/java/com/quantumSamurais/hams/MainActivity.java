package com.quantumSamurais.hams;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.quantumSamurais.hams.doctor.activities.DoctorMain;

import com.quantumSamurais.hams.doctor.activities.DoctorSignUpActivity;
import com.quantumSamurais.hams.login.LoginActivity;
import com.quantumSamurais.hams.patient.activities.PatientSignUpActivity;
import com.quantumSamurais.hams.appointment.Appointment;
import com.quantumSamurais.hams.database.Database;
import com.quantumSamurais.hams.database.RequestStatus;
import com.quantumSamurais.hams.doctor.Specialties;
import com.quantumSamurais.hams.doctor.activities.DoctorMain;
import com.quantumSamurais.hams.doctor.activities.DoctorSignUpActivity;
import com.quantumSamurais.hams.login.LoginActivity;
import com.quantumSamurais.hams.patient.Patient;
import com.quantumSamurais.hams.patient.activities.PatientMainActivity;
import com.quantumSamurais.hams.patient.activities.PatientSignUpActivity;
import com.quantumSamurais.hams.utils.ArrayUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button signUpBtn, signInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setup();
        addListeners();
    }

    public void setup() {
        signInBtn = findViewById(R.id.signInBtnMain);
        signUpBtn = findViewById(R.id.signUpBtnMain);
    }
    public void addListeners() {
        signUpBtn.setOnClickListener(this::signUpClicked);
        signInBtn.setOnClickListener(this::signInClicked);
    }

    public void signUpClicked(View view){
        Intent accountTypeSelect = new Intent(this, AccountTypeSelectionActivity.class);
        accountTypeSelect.putExtra("selectType","signUp");
        startActivity(accountTypeSelect);
    }
    public void signInClicked(View view){
        Intent accountTypeSelect = new Intent(this, AccountTypeSelectionActivity.class);
        accountTypeSelect.putExtra("selectType","signIn");
        startActivity(accountTypeSelect);
    }


}