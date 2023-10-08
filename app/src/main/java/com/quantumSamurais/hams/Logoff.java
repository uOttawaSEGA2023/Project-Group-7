package com.quantumSamurais.hams;
import android.content.Context;
import android.content.Intent;
import com.quantumSamurais.hams.login.LoginActivity;

public class Logoff {
    public static void logoff(Context context) {
        // Create an Intent to navigate to the login page
        Intent intent = new Intent(context, LoginActivity.class);

        // Start the login activity
        context.startActivity(intent);
    }
}
