package com.quantumSamurais.hams;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.quantumSamurais.hams.Logoff;


public class LoginInteractiveMessage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_interactive_message);

        // Find the TextView in the layout
        TextView welcomeMessageTextView = findViewById(R.id.welcomeMessageTextView);

        // Get the user type from the Intent's extra
        Intent intent = getIntent();
        String userType = intent.getStringExtra("userRole");

        // Create the welcome message
        String welcomeMessage = "Welcome! You are logged in as " + userType;

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
