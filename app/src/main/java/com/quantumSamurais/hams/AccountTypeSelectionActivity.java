package com.quantumSamurais.hams;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.quantumSamurais.hams.doctor.activities.DoctorSignUpActivity;
import com.quantumSamurais.hams.login.LoginActivity;
import com.quantumSamurais.hams.patient.activities.PatientSignUpActivity;
import com.quantumSamurais.hams.user.UserType;

import java.util.Objects;

public class AccountTypeSelectionActivity extends AppCompatActivity {
    Button continueBtn;
    TextView accountTypeTextView;
    RadioGroup accountTypeSelection;

    private boolean signInOrUp;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acount_type_selection_view);
        setup(getIntent());
        addListeners();
    }
    public void setup(Intent intent) {
        continueBtn = findViewById(R.id.continueBtn);
        accountTypeSelection = findViewById(R.id.userTypeSelection);
        accountTypeTextView = findViewById(R.id.accountTypeText);
        if(Objects.equals(intent.getStringExtra("selectType"), "signIn")) {
            accountTypeTextView.setText(R.string.selectTypeSignIn);
            signInOrUp = true;
        }
        else if(Objects.equals(intent.getStringExtra("selectType"), "signUp")) {
            accountTypeTextView.setText(R.string.selectTypeSignUp);
            signInOrUp = false;
        }
    }
    public void addListeners() {
        continueBtn.setOnClickListener(this::continueClicked);
    }
    public void continueClicked(View view) {
        if (accountTypeSelection.getCheckedRadioButtonId() == -1){
            Toast.makeText(getApplicationContext(), "Please select an option.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(signInOrUp) {
            Intent signIn = new Intent(this, LoginActivity.class);
            int checkedBtnId =  accountTypeSelection.getCheckedRadioButtonId();
            if(checkedBtnId == R.id.toPatient) {
                signIn.putExtra("userType", UserType.PATIENT);
            }
            if(checkedBtnId == R.id.toDoctor) {
                signIn.putExtra("userType", UserType.DOCTOR);
            }
            if(checkedBtnId == R.id.toAdmin) {
                signIn.putExtra("userType", UserType.ADMIN);
            }
            startActivity(signIn);
        } else {
            int checkedBtnId =  accountTypeSelection.getCheckedRadioButtonId();
            if(checkedBtnId == R.id.toPatient) {
                Intent patientRegistrationForm = new Intent(this, PatientSignUpActivity.class);
                startActivity(patientRegistrationForm);
            }
            if(checkedBtnId == R.id.toDoctor) {
                Intent doctorRegistrationForm = new Intent(this, DoctorSignUpActivity.class);
                startActivity(doctorRegistrationForm);
            }
            if(checkedBtnId == R.id.toAdmin) {
                Toast.makeText(this,"Cannot Sign Up as admin",Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

}
