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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.database.RequestStatus;
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
/*
        RequestStatus requestStatus = getRequestStatus(email, password);

        switch (requestStatus) {
            case APPROVED:
                Intent redirectIntent = new Intent(LoginActivity.this, LoginInteractiveMessage.class);
                redirectIntent.putExtra("userType", userType);
                startActivity(redirectIntent);
                break;
            case DENIED:
                Toast.makeText(this, "Your registration request was denied by the Administrator.", Toast.LENGTH_LONG).show();
                break;
            case PENDING:
                Toast.makeText(this, "Your registration has not been approved yet.", Toast.LENGTH_LONG).show();
                break;
        }*/
    }

    private RequestStatus getRequestStatus(String email, String password) {

        // Get an instance of Firebase Firestore.
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Check if the email is in the "patients" collection.
        Query patientsQuery = db.collection("users").document("software").collection("patients").whereEqualTo("emailAddress", email);
        Query doctorsQuery = db.collection("users").document("software").collection("doctors").whereEqualTo("emailAddress", email);
        Query requestsQuery = db.collection("users").document("software").collection("requests").whereEqualTo("emailAddress", email);

        try {
            QuerySnapshot patientsResult = Tasks.await(patientsQuery.get());
            QuerySnapshot doctorsResult = Tasks.await(doctorsQuery.get());
            QuerySnapshot requestsResult = Tasks.await(requestsQuery.get());

            if (!patientsResult.isEmpty() || !doctorsResult.isEmpty()) {
                // If email is found in "patients" or "doctors" collection, return APPROVED.
                return RequestStatus.APPROVED;
            } else if (!requestsResult.isEmpty()) {
                // If email is found in "requests" collection, get the status.
                DocumentSnapshot requestDocument = requestsResult.getDocuments().get(0);
                String status = requestDocument.getString("status");

                if (status != null) {
                    switch (status) {
                        case "APPROVED":
                            return RequestStatus.APPROVED;
                        case "DENIED":
                            return RequestStatus.REJECTED;
                        case "PENDING":
                            return RequestStatus.PENDING;
                    }
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return RequestStatus.REJECTED;
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

