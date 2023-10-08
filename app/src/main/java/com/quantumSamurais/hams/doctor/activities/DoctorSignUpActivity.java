package com.quantumSamurais.hams.doctor.activities;

import static com.quantumSamurais.hams.utils.Validator.emailAddressIsValid;
import static com.quantumSamurais.hams.utils.Validator.nameIsValid;
import static com.quantumSamurais.hams.utils.Validator.passwordIsValid;
import static com.quantumSamurais.hams.utils.Validator.phoneNumberIsValid;
import static com.quantumSamurais.hams.utils.Validator.textFieldsAreEmpty;

import android.os.Bundle;
import android.util.ArraySet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.doctor.Doctor;
import com.quantumSamurais.hams.doctor.Specialties;
import com.quantumSamurais.hams.user.User;

import java.util.Set;
import java.util.concurrent.ExecutionException;

public class DoctorSignUpActivity extends AppCompatActivity {

    private EditText  firstNameET, lastNameET, emailAddressET, passwordET, phoneNumberET, postalAddressET,employeeNumberET;
    private Button signUp;

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
    }

    private void onSignUpClicked(View v) {
        String firstName = trimText(firstNameET);
        String lastName = trimText(lastNameET);
        String emailAddress = trimText(emailAddressET);
        String password = trimText(passwordET);
        String phoneNumber = trimText(phoneNumberET);
        String postalAddress = trimText(postalAddressET);
        String employeeNumber = trimText(employeeNumberET);

        if (textFieldsAreEmpty(firstName, lastName, emailAddress, password, phoneNumber, postalAddress, employeeNumber)) {
            shortToast("Please make sure to fill all the fields.");
            return;
        }
        if (!nameIsValid(firstName) || !nameIsValid(lastName)) {
            shortToast("Please make sure your name follows a human format (no numbers, spaces, etc.)");
            return;
        }


        try {
            int validationResult = emailAddressIsValid(emailAddress);
            if (validationResult < 0) {
                if(validationResult == -1) {
                    shortToast("This email address is not formatted like an email address.");
                }
                else if (validationResult == -2) {
                    shortToast("Please ensure this email address' domain exists");
                }
                else {
                shortToast("Please ensure the localPart of your email address is correct, ensure there are no spaces.");
                }
                return;
            }
        } catch (
                ExecutionException e) {
           shortToast("Something went wrong during email's domain verification, please check your connection and try again.");
            return;
        } catch (InterruptedException e) {
           shortToast("Something went wrong with the email address' verification thread, please wait a bit and try again.");
            return;
        }
        if (!passwordIsValid(password)) {
            shortToast("Password must contain at least 8 chars, one capital letter and one small letter, one number, and one special character.");
            return;
        }
        if(!phoneNumberIsValid(phoneNumber)) {
            shortToast("Please make sure your phone number contains exactly 10 numbers, and only numbers.");
            return;
        }
        Set<Specialties> s = new ArraySet<>();
        s.add(Specialties.FAMILY_MEDICINE);

        Doctor newUser = new Doctor(firstName,lastName, password.toCharArray(),emailAddress,phoneNumber,postalAddress,employeeNumber, s);
        if(User.registeredDoctors.contains(newUser.getNewUserInformation())) {
            shortToast("Registration successful");
            newUser.changeView(this);
        } else {
            shortToast("An Error occurred please try again");
        }
    }

    private void shortToast(String text) {
        Toast.makeText(DoctorSignUpActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    private String trimText(EditText e) {
        return e.getText().toString().trim();
    }




}
