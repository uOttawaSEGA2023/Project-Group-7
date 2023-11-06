package com.quantumSamurais.hams.doctor.activities;

import static com.quantumSamurais.hams.user.UserType.DOCTOR;
import static com.quantumSamurais.hams.utils.Validator.checkIfEmployeeNumberExists;
import static com.quantumSamurais.hams.utils.Validator.checkIfPhoneNumberExists;
import static com.quantumSamurais.hams.utils.Validator.emailAddressIsValid;
import static com.quantumSamurais.hams.utils.Validator.nameIsValid;
import static com.quantumSamurais.hams.utils.Validator.passwordIsValid;
import static com.quantumSamurais.hams.utils.Validator.phoneNumberIsValid;
import static com.quantumSamurais.hams.utils.Validator.textFieldsAreEmpty;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.database.Database;
import com.quantumSamurais.hams.database.Request;
import com.quantumSamurais.hams.database.callbacks.ResponseListener;
import com.quantumSamurais.hams.doctor.Doctor;
import com.quantumSamurais.hams.doctor.Specialties;
import com.quantumSamurais.hams.doctor.adapters.CheckableItemAdapter;
import com.quantumSamurais.hams.login.LoginActivity;
import com.quantumSamurais.hams.utils.ValidationTaskResult;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.concurrent.ExecutionException;

public class DoctorSignUpActivity extends AppCompatActivity implements ResponseListener<ArrayList<Request>> {

    private EditText  firstNameET, lastNameET, emailAddressET, passwordET, phoneNumberET, postalAddressET,employeeNumberET;

    private RecyclerView specialtiesSelect;

    private CheckableItemAdapter<Specialties> adapter;
    private Button signUp;

    private Doctor currentDoctor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_register_form);
        setup();
        signUp.setOnClickListener(this::onSignUpClicked);
    }

    private void setup() {
        firstNameET = findViewById(R.id.firstNameDoctorReg);
        lastNameET = findViewById(R.id.lastNameDoctorReg);
        emailAddressET = findViewById(R.id.emailAddressDoctorReg);
        passwordET = findViewById(R.id.passwordDoctorReg);
        phoneNumberET = findViewById(R.id.phoneNumberDoctorReg);
        postalAddressET = findViewById(R.id.postalAddressDoctorReg);
        employeeNumberET = findViewById(R.id.employeeNumberDoctorReg);
        signUp = findViewById(R.id.signUpButtonDoctorReg);
        specialtiesSelect = findViewById(R.id.specialtiesSelect);
        // Setup RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        adapter = new CheckableItemAdapter<>(this, EnumSet.allOf(Specialties.class));
        specialtiesSelect.setLayoutManager(layoutManager);
        specialtiesSelect.setAdapter(adapter);
    }

    private void onSignUpClicked(View view) {
        String firstName = trimText(firstNameET);
        String lastName = trimText(lastNameET);
        String emailAddress = trimText(emailAddressET);
        String password = trimText(passwordET);
        String phoneNumber = trimText(phoneNumberET);
        String postalAddress = trimText(postalAddressET);
        String employeeNumber = trimText(employeeNumberET);
        EnumSet<Specialties> specialties = adapter.getCheckedOptions(Specialties.class);
        ArrayList<Specialties> specialtiesArrayList = new ArrayList<>(specialties);

        if (textFieldsAreEmpty(firstName, lastName, emailAddress, password, phoneNumber, postalAddress, employeeNumber)) {
            shortToast("Please make sure to fill all the fields.");
            return;
        }
        if (!nameIsValid(firstName) || !nameIsValid(lastName)) {
            shortToast("Please make sure your name follows a human format (no numbers, spaces, etc.)");
            return;
        }


        try {
            ValidationTaskResult emailValidityCheck = emailAddressIsValid(emailAddress, DOCTOR);

            if (emailValidityCheck == ValidationTaskResult.INVALID_FORMAT) {
                shortToast("This email address is not formatted like an email address.");
                return;

            } else if (emailValidityCheck == ValidationTaskResult.INVALID_DOMAIN) {
                shortToast("Please ensure this email address' domain exists");
                return;

            } else if (emailValidityCheck == ValidationTaskResult.INVALID_LOCAL_EMAIL_ADDRESS) {
                shortToast("Please ensure the localPart of your email address is correct, ensure there are no spaces.");
                return;
            } else if (emailValidityCheck == ValidationTaskResult.ATTRIBUTE_ALREADY_REGISTERED) {
                shortToast("This email address is already in use, please try signing in instead.");
                return;
            }
        } catch (ExecutionException e) {
            shortToast("Something went wrong during email's domain verification, please check your connection and try again.");
            Log.d("emailVerificationDoctor", "ExecutionException occurred : " + e.getCause());
            return;
        } catch (InterruptedException e) {
            shortToast("Something went wrong with the email address' verification thread, please wait a bit and try again.");
            Log.d("emailVerificationDoctor", "InterruptedException occurred : " + e.getCause());
            return;

        }
        if (!passwordIsValid(password)) {
            shortToast("This password is not secure enough. It must have 8 characters...");
            shortToast("and at least: 1 capital, 1 small letter; one number; and one special character.");
            return;
        }
        if(!phoneNumberIsValid(phoneNumber)) {
            shortToast("Please make sure your phone number contains exactly 10 numbers, and only numbers.");
            return;
        }
        boolean phoneNumberIsAlreadyInDatabase = true; //we assume it's there to prevent creation if something is wrong
        try {
            phoneNumberIsAlreadyInDatabase = checkIfPhoneNumberExists(phoneNumber, DOCTOR);
            if (phoneNumberIsAlreadyInDatabase) {
                shortToast("This phone number is already in use, please try signing in.");
                return;
            }
        } catch (ExecutionException e) {
            shortToast("Something went wrong during phone number's verification, please check your connection and try again.");
            Log.d("phoneNumberVerificationDoctor", "ExecutionException occurred : " + e.getCause());
            return;
        } catch (InterruptedException e) {
            shortToast("Something went wrong with the phone number's verification thread, please wait a bit and try again.");
            Log.d("phoneNumberVerificationDoctor", "InterruptedException occurred : " + e.getCause());
            return;

        }
        boolean employeeNumberIsAlreadyInDatabase = true; //we assume it's there to prevent creation if something is wrong
        try {
            employeeNumberIsAlreadyInDatabase = checkIfEmployeeNumberExists(employeeNumber);
            if (employeeNumberIsAlreadyInDatabase) {
                shortToast("This employee number is already in use, please try signing in.");
                return;
            }
        } catch (ExecutionException e) {
            shortToast("Something went wrong during employee number's verification, please check your connection and try again.");
            Log.d("employeeNumberVerification", "ExecutionException occurred : " + e.getCause());
            return;
        } catch (InterruptedException e) {
            shortToast("Something went wrong with the employee number's verification thread, please wait a bit and try again.");
            Log.d("employeeNumberVerification", "InterruptedException occurred : " + e.getCause());
            return;

        }
        if(specialtiesArrayList.size() < 1) {
            shortToast("Please select one or more specialties.");
            return;
        }

        signUp.setEnabled(false);
        boolean acceptsByDefault = false; //TODO: Connect it to XML that sets that.
        currentDoctor = new Doctor(firstName,lastName, password.toCharArray(),emailAddress,phoneNumber,postalAddress,employeeNumber, specialtiesArrayList, acceptsByDefault);
        Database db = Database.getInstance();
        db.getSignUpRequests(this);
    }

    private void shortToast(String text) {
        Toast.makeText(DoctorSignUpActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    private String trimText(EditText e) {
        return e.getText().toString().trim();
    }

    @Override
    public void onSuccess(ArrayList<Request> requests) {
        for (Request r : requests) {
            if(r.getUserType() != DOCTOR)
                continue;
            if(!r.getDoctor().equals(currentDoctor))
                continue;
            runOnUiThread(() -> shortToast("Registration successful"));
            // Switch to login
            Intent login = new Intent(this, LoginActivity.class);
            login.putExtra("userType",r.getUserType());
            startActivity(login);
            finish();
        }
    }

    @Override
    public void onFailure(Exception error) {
        runOnUiThread(() -> shortToast("Registration error, please try again in a few minutes."));
        signUp.setEnabled(true);
    }
}
