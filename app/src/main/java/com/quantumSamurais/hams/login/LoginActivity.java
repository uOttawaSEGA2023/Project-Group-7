package com.quantumSamurais.hams.login;
import static com.quantumSamurais.hams.utils.Validator.textFieldIsEmpty;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.database.RequestStatus;
import com.quantumSamurais.hams.database.DatabaseUtils;
import com.quantumSamurais.hams.database.callbacks.DoctorsResponseListener;
import com.quantumSamurais.hams.database.callbacks.PatientsResponseListener;
import com.quantumSamurais.hams.database.callbacks.RequestsResponseListener;
import com.quantumSamurais.hams.user.UserType;

import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity implements LoginEventListener {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText emailEditText, passwordEditText;
    private Button signInButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_form);
        signInButton = findViewById(R.id.signInButton);
        emailEditText = findViewById(R.id.EmailAddressSlot);
        passwordEditText = findViewById(R.id.PasswordSlot);
        signInButton.setOnClickListener(this::signInBtnClicked);

    }

    public void signInBtnClicked(View view) {
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

        RequestStatus requestStatus = getRequestStatus(email, password, userType);

        switch (requestStatus) {
            case APPROVED:
                Intent redirectIntent = new Intent(LoginActivity.this, LoginInteractiveMessage.class);
                redirectIntent.putExtra("userType", userType);
                startActivity(redirectIntent);
                break;
            case REJECTED:
                Toast.makeText(this, "Your registration request was denied by the Administrator.", Toast.LENGTH_LONG).show();
                break;
            case PENDING:
                Toast.makeText(this, "Your registration has not been approved yet.", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private RequestStatus getRequestStatus(String email, String password, UserType userType) {
        DatabaseUtils db = new DatabaseUtils();
        return db.getStatus(email, userType);
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
            });
        }
    }
}

