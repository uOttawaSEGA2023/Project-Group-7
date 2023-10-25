package com.quantumSamurais.hams.admin.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.database.DatabaseUtils;
import com.quantumSamurais.hams.database.Request;
import com.quantumSamurais.hams.database.callbacks.RequestsResponseListener;
import com.quantumSamurais.hams.login.LoginActivity;
import com.quantumSamurais.hams.user.User;

public class ViewRequestsActivity extends AppCompatActivity implements RequestsResponseListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setup();
        addListeners();
    }

    public void setup() {
        RecyclerView.LayoutManager requestsLayoutManger = new LinearLayoutManager(this);

    }
    public void addListeners() {

    }

    public void viewRegistrationRequests() {
        // a list of registration requests from Patients and Doctors
        DatabaseUtils tools = new DatabaseUtils();
        tools.getSignUpRequests(this);
    }

    public void RefreshButtonClicked(View v){

    }

    public void approveRegistrationRequest(User user) {
        // Approve the registration request for the given user
    }

    public void rejectRegistrationRequest(User user) {
        // Reject the registration request for the given user
    }

    @Override
    public void onSuccess(Request[] requests) {
        //show the requests
        runOnUiThread(() -> {
            Toast.makeText(this, "Either the email or password fields is incorrect. Please try again.", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onFailure(Error error) {
        Log.d("admin view", "Something went off when trying to access the DB.");

    }
}
