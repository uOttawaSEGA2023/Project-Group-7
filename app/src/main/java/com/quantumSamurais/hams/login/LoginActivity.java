package com.quantumSamurais.hams.login;
import static com.quantumSamurais.hams.utils.Validator.textFieldIsEmpty;
import static com.quantumSamurais.hams.utils.Validator.textFieldsAreEmpty;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.firestore.FirebaseFirestore;
import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.database.RequestStatus;
import com.quantumSamurais.hams.user.UserType;

public class LoginActivity extends AppCompatActivity implements LoginEventListener {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText emailEditText, passwordEditText;
    private Button signInButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_form);
        setup();
        addListeners();
    }

    private void setup() {
        signInButton = findViewById(R.id.signInButton);
        emailEditText = findViewById(R.id.EmailAddressSlot);
        passwordEditText = findViewById(R.id.PasswordSlot);
    }
    private void addListeners() {
        signInButton.setOnClickListener(this::signInBtnClicked);
    }

    public void signInBtnClicked(View view) {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        //we verify that the email and password is not empty.
        if (textFieldsAreEmpty(email,password)) {
            toast("Please make sure to fill out all fields.",Toast.LENGTH_LONG);
            return;
        }
        char[] parsePass = password.toCharArray();
        Intent intent = getIntent();
        UserType userType = (UserType) intent.getSerializableExtra("userType");
        Login loginProcessor = new Login(email,parsePass,userType,this,this);
        loginProcessor.attemptLogin();
    }


    @Override
    public void loginResponse(LoginStatusCodes tryLogin, RequestStatus status) {
        switch (status) {
            case APPROVED:
                switch (tryLogin) {
                    case INCORRECT_PASSWORD:
                        runOnUiThread(() -> {
                            toast("Either the email or password fields is incorrect. Please try again.",Toast.LENGTH_LONG);
                        });
                        break;
                    case SUCCESS:
                        runOnUiThread(() -> {
                            toast("Successfully Logged in!", Toast.LENGTH_SHORT);
                        });
                        break;
                    case USER_DOES_NOT_EXIST:
                        runOnUiThread(() -> {
                            toast("This account does not exist, please sign up before trying to log in", Toast.LENGTH_SHORT);
                        });
                }
                break;
            case REJECTED:
                runOnUiThread(() -> {
                    toast("Your registration request was denied by the Administrator.",Toast.LENGTH_SHORT);
                    toast("For further inquiry contact the Admin at (819)-123-1234",Toast.LENGTH_SHORT);
                });
                break;
            case PENDING:
                runOnUiThread(() -> {
                    toast("Your registration has not been approved yet.", Toast.LENGTH_LONG);
                });
                break;
        }
    }

    public void toast(String message, int duration) {
        Toast.makeText(this,message,duration).show();
    }
}

