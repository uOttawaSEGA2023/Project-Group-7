package com.quantumSamurais.hams.login;


import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;


import com.quantumSamurais.hams.login.Login;
import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.user.User;
import java.util.List;
import java.util.Map;
import static com.quantumSamurais.hams.utils.Validator.textFieldIsEmpty;
import static com.quantumSamurais.hams.patient.Patient.getRegisteredPatients;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private Button signInButton, backButton;
    private User loggedInUser;
    private List<Map<String, Object>> patientsList;
    private List<Map<String, Object>> doctorsList;
    // not too sure what this will be used for. Might need to call for methods with this activity message.


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_form);
    }

    public void clickedSignInButton(View view) {

        emailEditText = findViewById(R.id.EmailAddressSlot);
        passwordEditText = findViewById(R.id.PasswordSlot);
        signInButton = findViewById(R.id.signInButton);

        //getting user input from
        signInButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();


            //we verify that the email and password is not empty.
            if (textFieldIsEmpty(email) || textFieldIsEmpty(password)) {
                Toast.makeText(LoginActivity.this, "Either the email or password fields is empty. Please make sure to fill out either or both fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            // we get list of registered patients and doctors.
            patientsList = getRegisteredPatients();
            doctorsList = //getRegisteredDoctors();

            //we check to see the email or password is in the database. If not, we print saying otherwise
            // when doctor implements a getDoctorsList, replace this.

            //if (searchLoop(List, email)) {
            // find if the email is within the data base.
            // else, we say that it's not available.
            //}
            // still working on this.
            // if (we find valid email and password is valid) {
            // we connect this to Aryan's code.
        }
        );
    }

    public void onBackButton() {
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> {
            setContentView(R.layout.activity_main);
        });
    }

}
