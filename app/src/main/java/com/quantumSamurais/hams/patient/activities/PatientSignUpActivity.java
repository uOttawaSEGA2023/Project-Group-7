package com.quantumSamurais.hams.patient.activities;

import static com.quantumSamurais.hams.user.UserType.PATIENT;
import static com.quantumSamurais.hams.utils.Validator.checkIfHealthCardNumberExists;
import static com.quantumSamurais.hams.utils.Validator.checkIfPhoneNumberExists;
import static com.quantumSamurais.hams.utils.Validator.emailAddressIsValid;
import static com.quantumSamurais.hams.utils.Validator.nameIsValid;
import static com.quantumSamurais.hams.utils.Validator.passwordIsValid;
import static com.quantumSamurais.hams.utils.Validator.phoneNumberIsValid;
import static com.quantumSamurais.hams.utils.Validator.textFieldIsEmpty;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.database.Database;
import com.quantumSamurais.hams.database.Request;
import com.quantumSamurais.hams.database.callbacks.ResponseListener;
import com.quantumSamurais.hams.login.LoginActivity;
import com.quantumSamurais.hams.patient.Patient;
import com.quantumSamurais.hams.utils.ValidationTaskResult;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class PatientSignUpActivity extends AppCompatActivity implements ResponseListener<ArrayList<Request>> {
    private EditText firstNameEditText, lastNameEditText, emailAddressEditText, passwordEditText, phoneNumberEditText, postalAddressEditText, healthCardNumberEditText;
    private Button signUpButton;
    private Patient currentPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_register_form);

        firstNameEditText = findViewById(R.id.firstNameSlot);
        lastNameEditText = findViewById(R.id.lastNameSlot);
        emailAddressEditText = findViewById(R.id.emailAddressSlot);
        passwordEditText = findViewById(R.id.passwordSlot);
        phoneNumberEditText = findViewById(R.id.phoneNumberSlot);
        postalAddressEditText = findViewById(R.id.postalAddressSlot);
        healthCardNumberEditText = findViewById(R.id.healthCardNumberSlot);
        signUpButton = findViewById(R.id.formSignUpButton);

        signUpButton.setOnClickListener(v -> {
            String firstName = firstNameEditText.getText().toString().trim();
            String lastName = lastNameEditText.getText().toString().trim();
            String emailAddress = emailAddressEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String phoneNumber = phoneNumberEditText.getText().toString().trim();
            String postalAddress = postalAddressEditText.getText().toString().trim();
            String healthCardNumber = healthCardNumberEditText.getText().toString().trim();

            if (textFieldIsEmpty(firstName) || textFieldIsEmpty(lastName) || textFieldIsEmpty(emailAddress) || textFieldIsEmpty(password) || textFieldIsEmpty(phoneNumber) || textFieldIsEmpty(postalAddress) || textFieldIsEmpty(healthCardNumber)) {
                showSnackbar("Please make sure to fill all the fields.");
                return;
            }

            if (!(nameIsValid(firstName) && nameIsValid(lastName))) {
                showSnackbar("Please make sure your name follows a human format (no numbers, spaces, etc.)");
                return;
            }

            try {
                ValidationTaskResult emailValidityCheck = emailAddressIsValid(emailAddress, PATIENT);

                if (emailValidityCheck == ValidationTaskResult.INVALID_FORMAT) {
                    showSnackbar("This email address is not formatted like an email address.");
                    return;
                } else if (emailValidityCheck == ValidationTaskResult.INVALID_DOMAIN) {
                    showSnackbar("Please ensure this email address' domain exists.");
                    return;
                } else if (emailValidityCheck == ValidationTaskResult.INVALID_LOCAL_EMAIL_ADDRESS) {
                    showSnackbar("Please ensure the localPart of your email address is correct, ensure there are no spaces.");
                    return;
                } else if (emailValidityCheck == ValidationTaskResult.ATTRIBUTE_ALREADY_REGISTERED) {
                    showSnackbar("This email address is already in use, please try signing in instead.");
                    return;
                }
            } catch (ExecutionException e) {
                showSnackbar("Something went wrong during email's domain verification, please check your connection and try again.");
                Log.d("emailVerificationPatient", "ExecutionException occurred : " + e.getCause());
                return;
            } catch (InterruptedException e) {
                showSnackbar("Something went wrong with the email address' verification thread, please wait a bit and try again.");
                Log.d("emailVerificationPatient", "InterruptedException occurred : " + e.getCause());
                return;
            }

            if (!passwordIsValid(password)) {
                showSnackbar("This password is not secure enough. It must have 8 characters...");
                showSnackbar("and at least: 1 capital, 1 small letter; one number; and one special character.");
                return;
            }

            if (!phoneNumberIsValid(phoneNumber)) {
                showSnackbar("Please make sure your phone number contains exactly 10 numbers, and only numbers.");
                return;
            }

            boolean phoneNumberIsAlreadyInDatabase = true;
            try {
                phoneNumberIsAlreadyInDatabase = checkIfPhoneNumberExists(phoneNumber, PATIENT);
                if (phoneNumberIsAlreadyInDatabase) {
                    showSnackbar("This phone number is already in use, please try signing in.");
                    return;
                }
            } catch (ExecutionException e) {
                showSnackbar("Something went wrong during phone number's verification, please check your connection and try again.");
                Log.d("phoneNumberVerificationPatient", "ExecutionException occurred : " + e.getCause());
                return;
            } catch (InterruptedException e) {
                showSnackbar("Something went wrong with the phone number's verification thread, please wait a bit and try again.");
                Log.d("phoneNumberVerificationPatient", "InterruptedException occurred : " + e.getCause());
                return;
            }

            boolean healthCardNumberIsAlreadyInDatabase = true;
            try {
                healthCardNumberIsAlreadyInDatabase = checkIfHealthCardNumberExists(healthCardNumber);
                if (healthCardNumberIsAlreadyInDatabase) {
                    showSnackbar("This health card number is already in use, please try signing in.");
                    return;
                }
            } catch (ExecutionException e) {
                showSnackbar("Something went wrong during health card number's verification, please check your connection and try again.");
                Log.d("healthCardNumberVerification", "ExecutionException occurred : " + e.getCause());
                return;
            } catch (InterruptedException e) {
                showSnackbar("Something went wrong with the health card number's verification thread, please wait a bit and try again.");
                Log.d("healthCardNumberVerification", "InterruptedException occurred : " + e.getCause());
                return;
            }

            signUpButton.setEnabled(false);
            currentPatient = new Patient(firstName, lastName, password.toCharArray(), emailAddress, phoneNumber, postalAddress, healthCardNumber);
            Database db = Database.getInstance();
            db.getSignUpRequests(this);
        });
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onSuccess(ArrayList<Request> requests) {
        for (Request r : requests) {
            if (r.getUserType() != PATIENT)
                continue;
            if (!r.getPatient().equals(currentPatient))
                continue;
            runOnUiThread(() -> showSnackbar("Registration successful"));
            Intent login = new Intent(this, LoginActivity.class);
            login.putExtra("userType", r.getUserType());
            startActivity(login);
            finish();
        }
    }

    @Override
    public void onFailure(Exception error) {
        runOnUiThread(() -> showSnackbar("Registration error, please try again in a few minutes."));
        signUpButton.setEnabled(true);
    }
}
