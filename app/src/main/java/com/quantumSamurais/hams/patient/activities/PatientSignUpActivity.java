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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
                    // Get user input
                    String firstName = firstNameEditText.getText().toString().trim();
                    String lastName = lastNameEditText.getText().toString().trim();
                    String emailAddress = emailAddressEditText.getText().toString().trim();
                    String password = passwordEditText.getText().toString().trim();
                    String phoneNumber = phoneNumberEditText.getText().toString().trim();
                    String postalAddress = postalAddressEditText.getText().toString().trim();
                    String healthCardNumber = healthCardNumberEditText.getText().toString().trim();

                    //First make sure no field is empty
                    if (textFieldIsEmpty(firstName) || textFieldIsEmpty(lastName) || textFieldIsEmpty(emailAddress) || textFieldIsEmpty(password) || textFieldIsEmpty(phoneNumber) || textFieldIsEmpty(postalAddress) || textFieldIsEmpty(healthCardNumber)) {
                        Toast.makeText(PatientSignUpActivity.this, "Please make sure to fill all the fields.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //Validations Gauntlet
                    // Validate names
                    if (!(nameIsValid(firstName) && nameIsValid(lastName))) {
                        Toast.makeText(PatientSignUpActivity.this, "Please make sure your name follows a human format (no numbers, spaces, etc.)", Toast.LENGTH_LONG).show();
                        return;
                    }
                    //emailAddressIsValid throws errors due to be a wifi/threaded method, so we require a try/catch
                    try {
                        ValidationTaskResult emailValidityCheck = emailAddressIsValid(emailAddress, PATIENT);

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
                        Log.d("emailVerificationPatient", "ExecutionException occurred : " + e.getCause());
                        return;
                    } catch (InterruptedException e) {
                        shortToast("Something went wrong with the email address' verification thread, please wait a bit and try again.");
                        Log.d("emailVerificationPatient", "InterruptedException occurred : " + e.getCause());
                        return;

                    }
                    if (!passwordIsValid(password)) {
                        shortToast("Psswd must have 8 chars, 1 Cap & 1 small, one number, and one special char.");
                        return;
                    }
                    if (!phoneNumberIsValid(phoneNumber)) {
                        shortToast("Please make sure your phone number contains exactly 10 numbers, and only numbers.");
                        return;
                    }
                    boolean phoneNumberIsAlreadyInDatabase = true; //we assume it's there to prevent creation if something is wrong
                    try {
                        phoneNumberIsAlreadyInDatabase = checkIfPhoneNumberExists(phoneNumber, PATIENT);
                        if (phoneNumberIsAlreadyInDatabase) {
                            shortToast("This phone number is already in use, please try signing in.");
                            return;
                        }
                    } catch (ExecutionException e) {
                        shortToast("Something went wrong during phone number's verification, please check your connection and try again.");
                        Log.d("phoneNumberVerificationPatient", "ExecutionException occurred : " + e.getCause());
                        return;
                    } catch (InterruptedException e) {
                        shortToast("Something went wrong with the phone number's verification thread, please wait a bit and try again.");
                        Log.d("phoneNumberVerificationPatient", "InterruptedException occurred : " + e.getCause());
                        return;

                    }
                    boolean healthCardNumberIsAlreadyInDatabase = true; //we assume it's there to prevent creation if something is wrong
                    try {
                        healthCardNumberIsAlreadyInDatabase = checkIfHealthCardNumberExists(healthCardNumber);
                        if (healthCardNumberIsAlreadyInDatabase) {
                            shortToast("This health card number is already in use, please try signing in.");
                            return;
                        }
                    } catch (ExecutionException e) {
                        shortToast("Something went wrong during health card number's verification, please check your connection and try again.");
                        Log.d("healthCardNumberVerification", "ExecutionException occurred : " + e.getCause());
                        return;
                    } catch (InterruptedException e) {
                        shortToast("Something went wrong with the health card number's verification thread, please wait a bit and try again.");
                        Log.d("healthCardNumberVerification", "InterruptedException occurred : " + e.getCause());
                        return;

                    }


                    signUpButton.setEnabled(false);
                    //If we haven't returned yet, it means the verifiable inputs have been verified. So we can attempt registration.
                    currentPatient = new Patient(firstName, lastName, password.toCharArray(), emailAddress, phoneNumber, postalAddress, healthCardNumber);
                    Database db = Database.getInstance();
                    db.getSignUpRequests(this);
                }
        );
    }

    private void shortToast(String text) {
        Toast.makeText(PatientSignUpActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess(ArrayList<Request> requests) {
        for (Request r : requests) {
            if (r.getUserType() != PATIENT)
                continue;
            if (!r.getPatient().equals(currentPatient))
                continue;
            runOnUiThread(() -> shortToast("Registration successful"));
            // Switch to login
            Intent login = new Intent(this, LoginActivity.class);
            startActivity(login);
            finish();
        }
    }

    @Override
    public void onFailure(Exception error) {
        runOnUiThread(() -> shortToast("Registration error, please try again in a few minutes."));
        signUpButton.setEnabled(true);
    }


}


