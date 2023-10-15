package com.quantumSamurais.hams.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.quantumSamurais.hams.MainActivity;
import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.user.UserType;


public class LoginInteractiveMessage extends AppCompatActivity {

    TextView welcomeMessageTextView;
    Button logoffButton;
    String welcomeMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_interactive_message);
        setup(getIntent());
        addListeners();
    }
    public void setup(Intent intent) {
        // Find the TextView in the layout
        welcomeMessageTextView = findViewById(R.id.welcomeMessageTextView);
        logoffButton = findViewById(R.id.logoffButton);
        // Get the user type from the Intent's extra
        UserType userType = (UserType) intent.getSerializableExtra("userType");
        Log.d("loginInteractiveMessage", "intent userType : " + userType);
        // Check the user type and create the welcome message accordingly
        if (userType == UserType.DOCTOR) {
            welcomeMessage = "Welcome! You are logged in as a Doctor.";
        } else if(userType == UserType.ADMIN) {
            welcomeMessage = "Welcome! You are logged in as an Admin.";
        } else {
            welcomeMessage = "Welcome! You are logged in as a Patient.";
        }
        // Set the welcome message in the TextView
        welcomeMessageTextView.setText(welcomeMessage);
    }
    public void addListeners() {
        logoffButton.setOnClickListener(this::logOffClicked);
    }

    public void logOffClicked(View view) {
        finish();
        Intent backToMain = new Intent(this, MainActivity.class);
        startActivity(backToMain);
    }
}
