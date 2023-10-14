package com.quantumSamurais.hams;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.quantumSamurais.hams.Logoff;
import com.quantumSamurais.hams.user.UserType;


public class LoginInteractiveMessage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_interactive_message);

        // Find the TextView in the layout
        TextView welcomeMessageTextView = findViewById(R.id.welcomeMessageTextView);

        // Get the user type from the Intent's extra
        Intent intent = getIntent();
        UserType userType = (UserType) intent.getSerializableExtra("userType");

        // Check the user type and create the welcome message accordingly
        String welcomeMessage;
        if (userType == UserType.DOCTOR) {
            welcomeMessage = "Welcome! You are logged in as a Doctor.";
        } else {
            welcomeMessage = "Welcome! You are logged in as a Patient.";
        }

        // Set the welcome message in the TextView
        welcomeMessageTextView.setText(welcomeMessage);
        Button logoffButton = findViewById(R.id.logoffButton);

        logoffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the logoff method from LogoffUtil
                Logoff.logoff(LoginInteractiveMessage.this);
                finish();
            }
        });
    }
}
