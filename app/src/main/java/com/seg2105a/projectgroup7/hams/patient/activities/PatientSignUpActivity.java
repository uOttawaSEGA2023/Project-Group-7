package com.seg2105a.projectgroup7.hams.patient.activities;

import static com.seg2105a.projectgroup7.hams.utils.Validator.emailAddressIsValid;
import static com.seg2105a.projectgroup7.hams.utils.Validator.nameIsValid;
import static com.seg2105a.projectgroup7.hams.utils.Validator.passwordIsValid;
import static com.seg2105a.projectgroup7.hams.utils.Validator.phoneNumberIsValid;
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
                if (emailAddressIsValid(emailAddress) < 0) {
                    if (emailAddressIsValid(emailAddress) == -1) {
                        Toast.makeText(PatientSignUpActivity.this, "This email address is not formatted like an email address.", Toast.LENGTH_SHORT).show();
                        return;

                    } else if (emailAddressIsValid(emailAddress) == -2) {
                        Toast.makeText(PatientSignUpActivity.this, "Please ensure this email address' domain exists", Toast.LENGTH_SHORT).show();
                        return;

                    }
                    Toast.makeText(PatientSignUpActivity.this, "Please ensure the localPart of your email address is correct, ensure there are no spaces.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            catch(ExecutionException e){
                Toast.makeText(PatientSignUpActivity.this, "Something went wrong during email's domain verification, please check your connection and try again.", Toast.LENGTH_SHORT).show();
                return;
            }
            catch(InterruptedException e){
                Toast.makeText(PatientSignUpActivity.this, "Something went wrong with the email address' verification thread, please wait a bit and try again.", Toast.LENGTH_SHORT).show();
                return;

            }
            if (!passwordIsValid(password)){
                Toast.makeText(PatientSignUpActivity.this, "Password must contain at least 8 chars, one capital letter and one small letter, one number, and one special character.", Toast.LENGTH_LONG).show();
                return;
            }
            if (!phoneNumberIsValid(phoneNumber)){
                Toast.makeText(PatientSignUpActivity.this, "Please make sure your phone number contains exactly 10 numbers, and only numbers.", Toast.LENGTH_SHORT).show();
                return;
            }
            //If we haven't returned yet, it means the verifiable inputs have been verified. So we can attempt registration.
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
        }
        );
    }


    private boolean savedUser(Patient newUser) {
        List<Map<String, Object>> registeredUsers = Patient.getRegisteredPatients();
        return registeredUsers.contains(newUser.getNewUserInformation());
    }







}


