package com.seg2105a.projectgroup7.hams;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.seg2105a.projectgroup7.hams.patient.activities.PatientSignUpActivity;


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
            Toast.makeText(getApplicationContext(), "Please select an option.", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (accountTypeSelection.getCheckedRadioButtonId() == R.id.toPatient){
            Intent patientRegistrationForm = new Intent(this, PatientSignUpActivity.class);
            startActivity(patientRegistrationForm);
        }
        else{
            //TODO: Make it so this goes to the Doctor Registration Form
        }

    }

    public void openSignIn(View view){
        //setContentView(R.layout.signin_acount_type_selection_view);
    }

}