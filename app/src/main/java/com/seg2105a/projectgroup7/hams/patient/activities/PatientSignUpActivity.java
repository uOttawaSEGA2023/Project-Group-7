package com.seg2105a.projectgroup7.hams.patient.activities;

import static com.seg2105a.projectgroup7.hams.utils.Validator.nameIsValid;
import static com.seg2105a.projectgroup7.hams.utils.Validator.textFieldIsEmpty;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.seg2105a.projectgroup7.hams.R;
import com.seg2105a.projectgroup7.hams.patient.Patient;


import java.util.List;
import java.util.Map;

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
            // Validate input (For now only name)
            if (nameIsValid(firstName) && nameIsValid(lastName)) {
                // Create a new user object
                Patient newUser = new Patient(firstName, lastName, password.toCharArray(), emailAddress, phoneNumber, postalAddress, healthCardNumber);

                // Store the user data in the database
                if (savedUser(newUser)) {
                    // Registration successful
                    Toast.makeText(PatientSignUpActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    // Redirect to the login screen or do something else
                } else {
                    // Error while saving to the database
                    Toast.makeText(PatientSignUpActivity.this, "Error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Invalid input
                Toast.makeText(PatientSignUpActivity.this, "Invalid input. Please check your data.", Toast.LENGTH_SHORT).show();
            }
        }
        );
    }


    private boolean savedUser(Patient newUser) {
        List<Map<String, Object>> registeredUsers = Patient.getRegisteredPatients();
        return registeredUsers.contains(newUser.getNewUserInformation());
    }







}


