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

import java.util.ArrayList;


public class appointmentsFragment4Shift extends Fragment implements RequestsActivityListener {
    FragmentTab activeTab;
    AppointmentItemAdapter requestsAdapter;
    RecyclerView requestsStack;
    Database db;
    ArrayList<Appointment> appointments;
    long shiftID;


    public void onReceivedAppointments(ArrayList<Appointment> appointments){
        this.appointments = appointments;
        requestsAdapter.setAppointments(appointments);
        requestsAdapter.notifyDataSetChanged();
    }



    public appointmentsFragment4Shift() {
        // Required empty public constructor
    }

    public static appointmentsFragment4Shift newInstance(FragmentTab activeTab, long shiftID) {
        appointmentsFragment4Shift fragment = new appointmentsFragment4Shift();
        Bundle args = new Bundle();
        args.putSerializable("activeTab", activeTab);
        args.putLong("shiftID", shiftID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_requests, container, false);
        requestsStack = view.findViewById(R.id.requestsRecyclerViewFragment);
        db = Database.getInstance();
        appointments = new ArrayList<>();

        Bundle args = getArguments();
        if (args != null) {
            activeTab = (FragmentTab) args.getSerializable("activeTab");
            shiftID = args.getLong("shiftID");
            Log.d("ShiftID", "The shift ID is " + shiftID);
            requestsAdapter = new AppointmentItemAdapter(getActivity(), activeTab, appointments, this);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
            requestsStack.setLayoutManager(layoutManager);
            requestsStack.setAdapter(requestsAdapter);




            return view;
        }
        throw new IllegalStateException("This fragment was generated without using the newInstance method and hence has no FragmentTab.");

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume(){
        super.onResume();
        Database.getInstance().getAppointments(shiftID, this::onReceivedAppointments);


        //Add event listeners to this

        Database.getInstance().listenForAppointmentChangeOfStatus(shiftID, this::onReceivedAppointments);
    }







    @Override
    public void onAcceptClick(int position) {
        Log.d("requests Fragment", "accept click was pressed");
        long idToAccept = appointments.get(position).getAppointmentID();
        Log.d("requests Fragment", "The id is : " + idToAccept);
        db.approveAppointment(idToAccept);
    }

    @Override
    public void onCancelClick(int position) {
        Log.d("requests Fragment", "cancel click was pressed");
        long idToCancel = appointments.get(position).getAppointmentID();
        db.cancelAppointment(idToCancel);
    }

    @Override
    public void onRejectClick(int position) {
        //This shouldn't be called
    }


    @Override
    public void onShowMoreClick(int position) {
        //Get the selected request from the list
        Appointment selectedAppointment = appointments.get(position);
        Intent showMoreIntent = new Intent(getActivity(), ShowMoreActivity.class);
        showMoreIntent.putExtra("userType", PATIENT);
        db.getPatientFromAppointmentID(selectedAppointment.getAppointmentID()).thenAccept(
                patient ->
        {
            if (patient != null){
                showMoreIntent.putExtra("patient", patient);
                startActivity(showMoreIntent);

            }
        });




    }
}
