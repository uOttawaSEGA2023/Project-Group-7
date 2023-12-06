package com.quantumSamurais.hams.doctor.activities.fragments;

import static com.quantumSamurais.hams.user.UserType.PATIENT;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.quantumSamurais.hams.doctor.Doctor;

import java.util.ArrayList;


public class appointmentsFragment extends Fragment implements RequestsActivityListener {
    FragmentTab activeTab;
    AppointmentItemAdapter requestsAdapter;
    RecyclerView requestsStack;
    Database db;
    ArrayList<Appointment> appointments;
    Doctor myDoctor;


    public void onReceivedAppointments(ArrayList<Appointment> appointments){
        if (myDoctor.getAcceptsAppointmentsByDefault()){
            for (Appointment appointment: appointments){
                Database.getInstance().approveAppointment(appointment.getAppointmentID());

            }
        }
        this.appointments = appointments;
        requestsAdapter.setAppointments(appointments);
        requestsAdapter.notifyDataSetChanged();
    }



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
            requestsAdapter = new AppointmentItemAdapter(getActivity(), activeTab, appointments, this);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
            requestsStack.setLayoutManager(layoutManager);
            requestsStack.setAdapter(requestsAdapter);


            //Add event listeners to this
            for (long shiftID : myDoctor.getShiftIDs()){
                Database.getInstance().listenForAppointmentChangeOfStatus(shiftID, this::onReceivedAppointments);
            }



            return view;
        }
        throw new IllegalStateException("This fragment was generated without using the newInstance method and hence has no FragmentTab.");

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    public void setMyDoctor(Doctor someDoctor){myDoctor = someDoctor;}









    @Override
    public void onAcceptClick(int position) {
        Log.d("requests Fragment", "accept click was pressed");
        long idToAccept = appointments.get(position).getAppointmentID();
        Log.d("requests Fragment", "The id is : " + idToAccept);
        db.approveAppointment(idToAccept);
    }

    @Override
    public void onRejectClick(int position) {
        Log.d("requests Fragment", "reject click was pressed");
        long idToReject = appointments.get(position).getAppointmentID();
        db.rejectAppointment(idToReject);
    }


    @Override
    public void onShowMoreClick(int position) {
        //Get the selected request from the list
        Appointment selectedAppointment = appointments.get(position);
        Intent showMoreIntent = new Intent(getActivity(), ShowMoreActivity.class);
        showMoreIntent.putExtra("userType", PATIENT);
        db.getPatientFromAppointmentID(selectedAppointment.getAppointmentID()).thenAccept(patient ->
        {
            if (patient != null){
                showMoreIntent.putExtra("patient", patient);
                startActivity(showMoreIntent);

            }
        });




    }
}
