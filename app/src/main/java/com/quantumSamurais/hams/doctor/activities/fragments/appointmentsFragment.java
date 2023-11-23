package com.quantumSamurais.hams.doctor.activities.fragments;

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
import com.quantumSamurais.hams.appointment.Appointment;
import com.quantumSamurais.hams.core.adapters.AppointmentItemAdapter;
import com.quantumSamurais.hams.core.enums.FragmentTab;
import com.quantumSamurais.hams.core.listeners.RequestsActivityListener;
import com.quantumSamurais.hams.database.Database;
import com.quantumSamurais.hams.database.callbacks.ResponseListener;
import com.quantumSamurais.hams.doctor.Doctor;
import com.quantumSamurais.hams.patient.Patient;

import java.util.ArrayList;


public class appointmentsFragment extends Fragment implements RequestsActivityListener, ResponseListener<ArrayList<Appointment>> {
    FragmentTab activeTab;
    AppointmentItemAdapter requestsAdapter;
    RecyclerView requestsStack;
    Database db;
    ArrayList<Appointment> appointments;
    Doctor myDoctor;

    public appointmentsFragment() {
        // Required empty public constructor
    }

    public static appointmentsFragment newInstance(FragmentTab activeTab, Doctor doctor) {
        appointmentsFragment fragment = new appointmentsFragment();
        fragment.setMyDoctor(doctor);
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
        db = Database.getInstance();
        appointments = new ArrayList<>();

        Bundle args = getArguments();
        if (args != null) {
            activeTab = (FragmentTab) args.getSerializable("activeTab");
            refreshHandler.post(refreshRunnable);
            Log.d("requests Fragment", "Instance requests size after refreshRunnable : " + appointments.size());
            requestsAdapter = new AppointmentItemAdapter(getActivity(), activeTab, appointments, this);
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
    public void onResume() {
        super.onResume();
        viewAppointments();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        stopDataRefresh();
    }

    public void setMyDoctor(Doctor someDoctor){myDoctor = someDoctor;}

    public void viewAppointments() {
        db.getDoctorAppointments(myDoctor);
    }


    // This handler is needed to allow automatic refresh of the screen
    private final Handler refreshHandler = new Handler(Looper.getMainLooper());

    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            viewAppointments();
        }
    };

    @Override
    public void onSuccess(ArrayList<Appointment> appointmentsFromDatabase) {
        Log.d("RequestFragment", "Number of items in requests prechange: " + appointments.size());
        appointments = appointmentsFromDatabase;
        Log.d("RequestFragmentX", "Number of items in requests after change: " + appointments.size());

        getActivity().runOnUiThread(() -> {
            requestsAdapter.setAppointments(appointments);
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
    public void onFailure(Exception error) {
        Log.d("admin view", "Something went off when trying to access the DB." + error.getStackTrace());

    }

    @Override
    public void onAcceptClick(int position) {
        stopDataRefresh(); //I do not want the requests to be updated as I am accessing it.
        Log.d("requests Fragment", "accept click was pressed");
        long idToAccept = appointments.get(position).getAppointmentID();
        db.approveAppointment(idToAccept);
        refreshHandler.post(refreshRunnable);
    }

    @Override
    public void onRejectClick(int position) {
        stopDataRefresh();
        Log.d("requests Fragment", "reject click was pressed");
        long idToReject = appointments.get(position).getAppointmentID();
        db.rejectAppointment(idToReject);
        refreshHandler.post(refreshRunnable);
    }


    @Override
    public void onShowMoreClick(int position) {
        stopDataRefresh();
        //Get the selected request from the list
        Appointment selectedAppointment = appointments.get(position);
        Intent showMoreIntent = new Intent(getActivity(), ShowMoreActivity.class);

        Patient somePatient = db.getPatientFromAppointmentID(selectedAppointment.getAppointmentID());
        showMoreIntent.putExtra("userType", PATIENT);
        showMoreIntent.putExtra("firstName", somePatient.getFirstName());
        showMoreIntent.putExtra("lastName", somePatient.getLastName());
        showMoreIntent.putExtra("email", somePatient.getEmail());
        showMoreIntent.putExtra("phoneNumber", somePatient.getPhone());
        showMoreIntent.putExtra("address", somePatient.getAddress());
        showMoreIntent.putExtra("healthCardNumber", somePatient.getHealthCardNumber());

        startActivity(showMoreIntent);
    }
}
