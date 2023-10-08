package com.quantumSamurais.hams.login;

import android.content.Context;

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
import com.quantumSamurais.hams.LoginInteractiveMessage;
import com.quantumSamurais.hams.user.UserType;
import com.quantumSamurais.hams.login.LoginReturnCodes;

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
                Toast.makeText(LoginActivity.this, "Either the email or password fields is empty. Please make sure to fill out either or both fields.", Toast.LENGTH_LONG).show();
                return;
            }

            char[] parsePass = password.toCharArray();
            Context context = this;
            Boolean loggedIn = false; // kept as false for now; will find a boolean statement later.

            UserType[] userArr = {UserType.PATIENT, UserType.ADMIN, UserType.DOCTOR};
            UserType type;
            for (int i = 0; i < userArr.length; i ++) {
                LoginReturnCodes tryLogin = Login.login(email,parsePass, userArr[i], LoginActivity.this);
                if (tryLogin.equals(LoginReturnCodes.IncorrectPassword)) {
                    Toast.makeText(LoginActivity.this, "Either the email or password fields is incorrect. Please try again.", Toast.LENGTH_LONG).show();
                } else if (tryLogin.equals(LoginReturnCodes.Success)) {
                    Toast.makeText(LoginActivity.this, "Success! Redirecting...", Toast.LENGTH_SHORT).show();
                    setContentView(R.layout.login_interactive_message);
                    break;
                }
            }
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
