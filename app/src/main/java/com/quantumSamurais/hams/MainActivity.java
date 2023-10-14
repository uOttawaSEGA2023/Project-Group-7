package com.quantumSamurais.hams;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.quantumSamurais.hams.doctor.activities.DoctorSignUpActivity;
import com.quantumSamurais.hams.login.LoginActivity;
import com.quantumSamurais.hams.patient.activities.PatientSignUpActivity;


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
        accountTypeSelect.putExtra("selectType","signUn");
        startActivity(accountTypeSelect);
    }
    public void signInClicked(View view){
        Intent accountTypeSelect = new Intent(this, AccountTypeSelectionActivity.class);
        accountTypeSelect.putExtra("selectType","signIn");
        startActivity(accountTypeSelect);
    }

    // Hooks back button event to return to main view if user was in account type selection view
    public void onBackPressed() {
        setContentView(R.layout.activity_main);
    }



}