package com.quantumSamurais.hams;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.quantumSamurais.hams.admin.Administrator;
import com.quantumSamurais.hams.doctor.activities.DoctorSignUpActivity;
import com.quantumSamurais.hams.login.LoginActivity;
import com.quantumSamurais.hams.patient.activities.PatientSignUpActivity;
import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.user.User;

import org.checkerframework.checker.units.qual.A;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openSignUp(View view){
        setContentView(R.layout.signup_acount_type_selection_view);
    }

    public void clickedContinueRegistration(View view){
        RadioGroup accountTypeSelection = findViewById(R.id.userTypeSelection);
        if (accountTypeSelection.getCheckedRadioButtonId() == -1){
            User Admin = new Administrator();
            Admin.changeView(this);
            Toast.makeText(getApplicationContext(), "Please select an option.", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (accountTypeSelection.getCheckedRadioButtonId() == R.id.toPatient){
            Intent patientRegistrationForm = new Intent(this, PatientSignUpActivity.class);
            startActivity(patientRegistrationForm);
        }
        else{
            Intent doctorRegistrationForm = new Intent(this, DoctorSignUpActivity.class);
            startActivity(doctorRegistrationForm);
        }

    }

    // Hooks back button event to return to main view if user was in account type selection view
    public void onBackPressed() {
        setContentView(R.layout.activity_main);
    }

    public void openSignIn(View view){
        Intent signIn = new Intent(this, LoginActivity.class);
        startActivity(signIn );
    }

}