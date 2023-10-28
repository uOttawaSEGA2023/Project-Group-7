package com.quantumSamurais.hams.admin.activities.fragments;

import static com.quantumSamurais.hams.user.UserType.DOCTOR;
import static com.quantumSamurais.hams.user.UserType.PATIENT;

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
import com.quantumSamurais.hams.admin.activities.ShowMoreActivity;
import com.quantumSamurais.hams.admin.adapters.RequestItemAdapter;
import com.quantumSamurais.hams.admin.adapters.RequestItemAdapter.FragmentTab;
import com.quantumSamurais.hams.admin.listeners.RequestsActivityListener;
import com.quantumSamurais.hams.database.DatabaseUtils;
import com.quantumSamurais.hams.database.Request;
import com.quantumSamurais.hams.database.callbacks.RequestsResponseListener;
import com.quantumSamurais.hams.doctor.Doctor;
import com.quantumSamurais.hams.patient.Patient;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_requests, container, false);
        requestsStack = view.findViewById(R.id.requestsRecyclerViewFragment);
        tools = new DatabaseUtils();
        requests = new ArrayList<>();

        Bundle args = getArguments();
        if (args != null) {
            activeTab = (FragmentTab) args.getSerializable("activeTab");
            refreshHandler.post(refreshRunnable);
            Log.d("requests Fragment", "Instance requests size after refreshRunnable : " + requests.size());
            requestsAdapter = new RequestItemAdapter(getActivity(), activeTab, requests, this);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
            requestsStack.setLayoutManager(layoutManager);
            requestsStack.setAdapter(requestsAdapter);

            Log.d("requests Fragment", "We called from onCreateView");
            return view;
        }
        throw new IllegalStateException("This fragment was generated without using the newInstance method and hence has no FragmentTab.");

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void viewRegistrationRequests() {
        // a list of registration requests from Patients and Doctors
        tools.getSignUpRequests(this);
    }


    // This handler is needed to allow automatic refresh of the screen
    private Handler refreshHandler = new Handler(Looper.getMainLooper());

    private Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            viewRegistrationRequests();
        }
    };
    @Override
    public void onSuccess(ArrayList<Request> requestsFromDatabase) {
        Log.d("RequestFragment", "Number of items in requests prechange: " + requests.size());
        requests = requestsFromDatabase;
        Log.d("RequestFragmentX", "Number of items in requests after change: " + requests.size());

        getActivity().runOnUiThread(() -> {
            requestsAdapter.setRequests(requests);
        });
        //Clear the Handler Queue
        stopDataRefresh();
        //
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
        stopDataRefresh(); //I do not want the requests to be updated as I am accessing it.
        Log.d("requests Fragment", "accept click was pressed");
        long idToAccept = requests.get(position).getID();
        tools.approveSignUpRequest(idToAccept);
        //sendEmail(getActivity(), getUserFromRequest(requests.get(position)), APPROVED);
        refreshHandler.post(refreshRunnable);
    }

    @Override
    public void onRejectClick(int position) {
        stopDataRefresh();
        Log.d("requests Fragment", "reject click was pressed");
        long idToReject = requests.get(position).getID();
        tools.rejectSignUpRequest(idToReject);
        //sendEmail(getActivity(), getUserFromRequest(requests.get(position)), REJECTED);
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
    public void onShowMoreClick(int position) {
        //Get the selected request from the list
        Request selectedRequest = requests.get(position);
        Intent showMoreIntent = new Intent(getActivity(), ShowMoreActivity.class);
        switch (selectedRequest.getUserType()){
            case DOCTOR:
                Doctor someDoctor = (Doctor) getUserFromRequest(selectedRequest);
                showMoreIntent.putExtra("userType", DOCTOR);
                showMoreIntent.putExtra("firstName", someDoctor.getFirstName());
                showMoreIntent.putExtra("lastName", someDoctor.getLastName());
                showMoreIntent.putExtra("email", someDoctor.getEmail());
                showMoreIntent.putExtra("phoneNumber", someDoctor.getPhone());
                showMoreIntent.putExtra("address", someDoctor.getAddress());
                showMoreIntent.putExtra("employeeNumber", someDoctor.getEmployeeNumber());
                showMoreIntent.putExtra("specialties", someDoctor.getSpecialties()) ;
                break;
            case PATIENT:
                Patient somePatient = (Patient) getUserFromRequest(selectedRequest);
                showMoreIntent.putExtra("userType", PATIENT);
                showMoreIntent.putExtra("firstName", somePatient.getFirstName());
                showMoreIntent.putExtra("lastName", somePatient.getLastName());
                showMoreIntent.putExtra("email", somePatient.getEmail());
                showMoreIntent.putExtra("phoneNumber", somePatient.getPhone());
                showMoreIntent.putExtra("address", somePatient.getAddress());
                showMoreIntent.putExtra("healthCardNumber", somePatient.getHealthCardNumber());
                break;
            case ADMIN:
                //Shouldn't happen
        }

        startActivity(showMoreIntent);
    }
}
