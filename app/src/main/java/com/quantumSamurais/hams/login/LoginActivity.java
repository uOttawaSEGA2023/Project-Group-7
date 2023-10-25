package com.quantumSamurais.hams.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.quantumSamurais.hams.R;

import static com.quantumSamurais.hams.utils.Validator.textFieldIsEmpty;
import com.quantumSamurais.hams.user.UserType;

public class LoginActivity extends AppCompatActivity implements LoginEventListener {
    private EditText emailEditText, passwordEditText;
    private Button signInButton;

    // not too sure what this will be used for. Might need to call for methods with this activity message.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_form);
        signInButton = findViewById(R.id.signInButton);
        emailEditText = findViewById(R.id.EmailAddressSlot);
        passwordEditText = findViewById(R.id.PasswordSlot);
        signInButton.setOnClickListener(this::signUpBtnClicked);
    }

    public void signUpBtnClicked(View view) {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        //we verify that the email and password is not empty.
        if (textFieldIsEmpty(email) || textFieldIsEmpty(password)) {
            Toast.makeText(this, "Please make sure to fill out all fields.", Toast.LENGTH_LONG).show();
            return;
        }
        char[] parsePass = password.toCharArray();
        Intent intent = getIntent();
        UserType userType = (UserType) intent.getSerializableExtra("userType");
        Login.login(email, parsePass, userType, this, this);
    }


    @Override
    public void loginResponse(LoginReturnCodes tryLogin) {
        boolean loggedIn = false; // kept as false for now; will find a boolean statement later.
        if (tryLogin == (LoginReturnCodes.INCORRECT_PASSWORD)) {
            runOnUiThread(() -> {
                Toast.makeText(LoginActivity.this, "Either the email or password fields is incorrect. Please try again.", Toast.LENGTH_LONG).show();
            });
        } else if (tryLogin == (LoginReturnCodes.SUCCESS)) {
            loggedIn = true;
        }
        if (loggedIn) {
            runOnUiThread(() -> {
                Toast.makeText(LoginActivity.this, "Successfully Logged in!", Toast.LENGTH_SHORT).show();
                // Start the login activity
//                Intent interactiveMessage = new Intent(LoginActivity.this, LoginInteractiveMessage.class);
//                startActivity(interactiveMessage);
            });
        }
    }
}

