package com.quantumSamurais.hams.patient.activities;

import static com.quantumSamurais.hams.utils.ValidationTaskResult.ATTRIBUTE_ALREADY_REGISTERED;
import static com.quantumSamurais.hams.utils.Validator.checkIfEmployeeNumberExists;
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

import com.quantumSamurais.hams.LoginInteractiveMessage;
import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.doctor.activities.DoctorSignUpActivity;
import com.quantumSamurais.hams.login.LoginActivity;
import com.quantumSamurais.hams.patient.Patient;
import com.quantumSamurais.hams.user.UserType;
import com.quantumSamurais.hams.utils.ValidationTaskResult;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class PatientSignUpActivity extends AppCompatActivity {
    private EditText firstNameEditText, lastNameEditText, emailAddressEditText, passwordEditText, phoneNumberEditText, postalAddressEditText, healthCardNumberEditText;
    private Button signUpButton;

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
            if (textFieldIsEmpty(firstName) || textFieldIsEmpty(lastName) || textFieldIsEmpty(emailAddress) || textFieldIsEmpty(password) || textFieldIsEmpty(phoneNumber) || textFieldIsEmpty(postalAddress) || textFieldIsEmpty(healthCardNumber)){
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
                ValidationTaskResult validationResult = emailAddressIsValid(emailAddress, UserType.PATIENT);

                if(validationResult == ValidationTaskResult.INVALID_FORMAT) {
                    shortToast("This email address is not formatted like an email address.");
                    return;
                }
                else if (validationResult == ValidationTaskResult.INVALID_DOMAIN) {
                    shortToast("Please ensure this email address' domain exists");
                    return;
                }
                else if (validationResult == ValidationTaskResult.INVALID_LOCAL_EMAIL_ADDRESS) {
                    shortToast("Please ensure the localPart of your email address is correct, ensure there are no spaces.");
                    return;
                }
                else if (validationResult == ValidationTaskResult.ATTRIBUTE_ALREADY_REGISTERED) {
                    shortToast("This email address is already in use. Try to sign in instead.");
                    return;
                }
            }
            catch(ExecutionException e){
                Toast.makeText(PatientSignUpActivity.this, "Something went wrong during email's domain verification, please check your connection and try again.", Toast.LENGTH_SHORT).show();
                return;
            }
            catch(InterruptedException e){
                shortToast("Something went wrong with the email address' verification thread, please wait a bit and try again.");
                return;

            }
            if (!passwordIsValid(password)){
                shortToast("Password must contain at least 8 chars, one capital letter and one small letter, one number, and one special character.");
                return;
            }
            if (!phoneNumberIsValid(phoneNumber)){
                shortToast("Please make sure your phone number contains exactly 10 numbers, and only numbers.");
                return;
            }
                    boolean phoneNumberIsInDatabase = true;//We assume it's in, to block registration if it can't be verified
                    try {
                        phoneNumberIsInDatabase = checkIfPhoneNumberExists(phoneNumber, UserType.PATIENT) == ATTRIBUTE_ALREADY_REGISTERED;
                    } catch (ExecutionException e) {
                        Log.d("phoneNumberValidation", "ExecutionException occurred: " + e.getCause());
                        shortToast("Something seems to have went wrong during phone number validation, please try again.");
                        return;
                    } catch (InterruptedException e) {
                        Log.d("phoneNumberValidation", "InterruptedException occurred: " + e.getCause());
                        shortToast("Something seems to have went wrong during phone number validation, please try again.");
                        return;
                    }
                    if (phoneNumberIsInDatabase){
                        shortToast("This phone number appears to already be in the database.");
                        return;
                    }
                    boolean healthCardNumberIsInDatabase = true;//We assume it's in, to block registration if it can't be verified
                    try {
                        healthCardNumberIsInDatabase = checkIfHealthCardNumberExists(healthCardNumber) == ATTRIBUTE_ALREADY_REGISTERED;
                    } catch (ExecutionException e) {
                        Log.d("healthCardNumberValidation", "ExecutionException occurred: " + e.getCause());
                        shortToast("Something seems to have went wrong during employee number validation, please try again.");
                        return;
                    } catch (InterruptedException e) {
                        Log.d("healthCardNumberValidation", "InterruptedException occurred: " + e.getCause());
                        shortToast("Something seems to have went wrong during employee number validation, please try again.");
                        return;
                    }
                    if (healthCardNumberIsInDatabase){
                        shortToast("This health card number appears to already be registered. Please try signing in instead.");
                        return;
                    }

            //If we haven't returned yet, it means the verifiable inputs have been verified. So we can attempt registration.
            Patient newUser = new Patient(firstName, lastName, password.toCharArray(), emailAddress, phoneNumber, postalAddress, healthCardNumber);

            // Store the user data in the database
            if (savedUser(newUser)) {
                // Registration successful
                Toast.makeText(PatientSignUpActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                // Switch to login
                Intent login = new Intent(this, LoginActivity.class);
                startActivity(login);
                finish();
            } else {
                // Error while saving to the database
                Toast.makeText(PatientSignUpActivity.this, "Error occurred. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
        );
    }

    private void shortToast(String text) {
        Toast.makeText(PatientSignUpActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    private boolean savedUser(Patient newUser) {
        List<Map<String, Object>> registeredUsers = Patient.getRegisteredPatients();
        return registeredUsers.contains(newUser.getNewUserInformation());
    }







}


