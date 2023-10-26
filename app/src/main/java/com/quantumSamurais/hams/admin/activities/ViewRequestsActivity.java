package com.quantumSamurais.hams.admin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.admin.adapters.RequestItemAdapter;
import com.quantumSamurais.hams.admin.listeners.RequestsActivityListener;
import com.quantumSamurais.hams.database.DatabaseUtils;
import com.quantumSamurais.hams.database.Request;
import com.quantumSamurais.hams.database.callbacks.RequestsResponseListener;

import java.util.ArrayList;

public class ViewRequestsActivity extends AppCompatActivity implements RequestsActivityListener, RequestsResponseListener {
    DatabaseUtils tools = new DatabaseUtils();
    ArrayList<Request> requests;
    RequestItemAdapter requestsAdapter;
    RecyclerView requestsStack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setup();
    }

    public void setup() {
        RecyclerView.LayoutManager requestsLayoutManger = new LinearLayoutManager(this);
        requestsStack = findViewById(R.id.requestsRecyclerView);

        // Setup RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        requestsAdapter = new RequestItemAdapter(this, null, this);
        requestsStack.setLayoutManager(layoutManager);
        requestsStack.setAdapter(requestsAdapter);

        //I call the refresh runnable method for the first time (instantly)
        //Will then be called periodically
        refreshHandler.post(refreshRunnable);
    }

    public void viewRegistrationRequests() {
        // a list of registration requests from Patients and Doctors
        tools.getSignUpRequests(this);
    }

    public void RefreshButtonClicked(View v){
        viewRegistrationRequests();
    }

    // This handler is needed to allow automatic refresh of the screen
    private Handler refreshHandler = new Handler(Looper.getMainLooper());

    private Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            viewRegistrationRequests();
            refreshHandler.postDelayed(this, 5000);
        }
    };
    @Override
    public void onSuccess(ArrayList<Request> requests) {
        //show the requests
        runOnUiThread(() -> {
            requestsAdapter = new RequestItemAdapter(this, requests, this);
        });
        // Start the periodic data refresh
        refreshHandler.postDelayed(refreshRunnable, 5000);
    }
    private void stopDataRefresh() {
        refreshHandler.removeCallbacks(refreshRunnable);
    }

    @Override
    public void onFailure(Error error) {
        Log.d("admin view", "Something went off when trying to access the DB.");

    }

    @Override
    public void onAcceptClick(int position) {
        long idToAccept = requests.get(position).getID();
        tools.approveSignUpRequest(idToAccept);
        refreshHandler.post(refreshRunnable);
    }

    @Override
    public void onRejectClick(int position) {
        long idToReject = requests.get(position).getID();
        tools.approveSignUpRequest(idToReject);
        refreshHandler.post(refreshRunnable);

    }

    @Override
    public void onShowMoreClick(int position, Intent showMore) {


    }
}
