package com.quantumSamurais.hams.admin.activities.fragments;

import static com.quantumSamurais.hams.database.DatabaseUtils.sendEmail;
import static com.quantumSamurais.hams.database.RequestStatus.APPROVED;
import static com.quantumSamurais.hams.database.RequestStatus.REJECTED;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.admin.adapters.RequestItemAdapter;
import com.quantumSamurais.hams.admin.adapters.RequestItemAdapter.FragmentTab;
import com.quantumSamurais.hams.admin.listeners.RequestsActivityListener;
import com.quantumSamurais.hams.database.DatabaseUtils;
import com.quantumSamurais.hams.database.Request;
import com.quantumSamurais.hams.database.callbacks.RequestsResponseListener;
import com.quantumSamurais.hams.user.User;

import java.util.ArrayList;


public class requestsFragment extends Fragment implements RequestsActivityListener, RequestsResponseListener {
    FragmentTab activeTab;
    RequestItemAdapter requestsAdapter;
    RecyclerView requestsStack;
    DatabaseUtils tools;
    ArrayList<Request> requests;

    public requestsFragment() {
        // Required empty public constructor
    }

    public static requestsFragment newInstance(FragmentTab activeTab) {
        requestsFragment fragment = new requestsFragment();
        Bundle args = new Bundle();
        args.putSerializable("activeTab", activeTab);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_requests, container, false);
        requestsStack = view.findViewById(R.id.requestsRecyclerView);

        Bundle args = getArguments();
        if (args != null) {
            activeTab = (FragmentTab) args.getSerializable("activeTab");
            ArrayList<Request> requests = new ArrayList<>();
            requestsAdapter = new RequestItemAdapter(getActivity(), activeTab, requests, this);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
            requestsStack.setLayoutManager(layoutManager);
            requestsStack.setAdapter(requestsAdapter);
            return view;
        }
        throw new IllegalStateException("This fragment was generated without using the newInstance methdod and hence has no FragmentTab.");

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        tools = new DatabaseUtils();



        refreshHandler.post(refreshRunnable);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
            //viewRegistrationRequests();
            refreshHandler.postDelayed(this, 5000);
        }
    };
    @Override
    public void onSuccess(ArrayList<Request> requests) {
        //show the requests

        requestsAdapter = new RequestItemAdapter(getActivity(), activeTab, requests, this);

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
        sendEmail(getActivity(), getUserFromRequest(requests.get(position)), APPROVED);
        refreshHandler.post(refreshRunnable);
    }

    @Override
    public void onRejectClick(int position) {
        long idToReject = requests.get(position).getID();
        tools.approveSignUpRequest(idToReject);
        sendEmail(getActivity(), getUserFromRequest(requests.get(position)), REJECTED);
        refreshHandler.post(refreshRunnable);
    }

    public static User getUserFromRequest(Request request){
        if (request == null){
            throw new NullPointerException("Please do not pass a null object to this method");
        }
        switch(request.getUserType()){
            case DOCTOR:
                return request.getDoctor();
            case PATIENT:
                return request.getPatient();
            case ADMIN:
                // We shouldn't get here
        }
        // We shouldn't get here either, since request shouldn't be null.
        return null;
    }

    @Override
    public void onShowMoreClick(int position, Intent showMore) {

        // Get the selected request from the list
        //Request selectedRequest = requests.get(position);

        // Create an Intent to navigate to the "Show More" page
        //Intent showMoreIntent = new Intent(this, ShowMoreActivity.class);

        // Pass the selected request's data to the "Show More" page
        //showMoreIntent.putExtra("selectedRequest", selectedRequest);


        // Start the "Show More" activity
        //startActivity(showMoreIntent);
    }
}
