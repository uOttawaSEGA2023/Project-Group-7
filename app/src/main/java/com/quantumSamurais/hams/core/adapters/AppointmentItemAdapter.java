package com.quantumSamurais.hams.core.adapters;

import static com.quantumSamurais.hams.database.RequestStatus.PENDING;
import static com.quantumSamurais.hams.database.RequestStatus.REJECTED;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.admin.Administrator;
import com.quantumSamurais.hams.appointment.Appointment;
import com.quantumSamurais.hams.core.listeners.RequestsActivityListener;
import com.quantumSamurais.hams.database.Database;
import com.quantumSamurais.hams.doctor.Doctor;
import com.quantumSamurais.hams.patient.Patient;
import com.quantumSamurais.hams.user.User;
import com.quantumSamurais.hams.core.enums.FragmentTab;

import java.util.ArrayList;




public class AppointmentItemAdapter extends RecyclerView.Adapter<AppointmentItemAdapter.RequestViewHolder> {
    private final RequestsActivityListener requestClickListener;
    private ArrayList<Appointment> appointments; // Renamed from 'requests'
    private final Context currentContext;
    FragmentTab activeTab;
    Database db = Database.getInstance();


    public AppointmentItemAdapter(Context context, FragmentTab activeTab, ArrayList<Appointment> appointmentsFromDatabase, RequestsActivityListener listener) {
        Log.d("AppointmentItemAdapter", "Number of items in appointments: " + appointmentsFromDatabase.size());
        this.activeTab = activeTab;
        setAppointments(appointmentsFromDatabase);
        currentContext = context;
        requestClickListener = listener;
        Log.d("AppointmentItemAdapter", "Number of items in appointments: " + appointments.size());
    }


    public void setAppointments(ArrayList<Appointment> appointmentsFromDatabase){
        // Filters the passed list, and makes it so it contains only the required info
        ArrayList<Appointment> tempAppointments = appointmentsFromDatabase;
        switch(activeTab){
            case ALL_REQUESTS:
                break;
            case PENDING_REQUESTS:
                ArrayList<Appointment> pendingAppointments = new ArrayList<>();
                for (Appointment appointment : appointmentsFromDatabase){
                    if (appointment.getAppointmentStatus() == PENDING){
                        pendingAppointments.add(appointment);
                    }
                }
                tempAppointments = pendingAppointments;
                break;

            case REJECTED_REQUESTS:
                ArrayList<Appointment> rejectedAppointments = new ArrayList<>();
                for (Appointment appointment : appointmentsFromDatabase){
                    if (appointment.getAppointmentStatus() == REJECTED){
                        rejectedAppointments.add(appointment);
                    }
                }
                tempAppointments  = rejectedAppointments;
                break;
        }
        appointments = tempAppointments ;
        notifyDataSetChanged();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder{
        TextView name, emailAddress, userType, requestId;
        ImageButton accept, reject, moreInfo;
        Appointment appointment;
        RequestsActivityListener requestsActivityListener;

        public RequestViewHolder(@NonNull View itemView, RequestsActivityListener requestClickListener) {
            super(itemView);
            requestsActivityListener = requestClickListener;
            accept = itemView.findViewById(R.id.accept);
            reject = itemView.findViewById(R.id.deny);
            moreInfo = itemView.findViewById(R.id.showMore);
        }

        private void setData(User user, long id) throws Exception {
            name = itemView.findViewById(R.id.docNameAppointment);
            emailAddress = itemView.findViewById(R.id.appointEndTime);
            userType = itemView.findViewById(R.id.appointStartTime);
            requestId = itemView.findViewById(R.id.idRequest);

            //
            name.setText(user.getFirstName() + " " + user.getLastName());
            emailAddress.setText(user.getEmail());
            requestId.setText(Long.toString(id));

            //Since it's an ENUM, default is unneeded.
            if (user instanceof Doctor) {
                userType.setText(R.string.doctor);
            } else if (user instanceof Patient) {
                userType.setText(R.string.patient);
            } else if (user instanceof Administrator) {
                Log.d("Request Screen", "Someone managed to create an account as ADMIN... how?");
            }
            setOnClickListeners(appointment);




        }

        public void setAppointment(Appointment appointment) {
            this.appointment = appointment;
        }
        private void setOnClickListeners(Appointment appointment){
            View.OnClickListener acceptListener = view -> {
                if (requestsActivityListener != null) {
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        requestsActivityListener.onAcceptClick(position);
                    }

                }
            };
            View.OnClickListener rejectListener = view -> {
                if (requestsActivityListener != null) {
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        requestsActivityListener.onRejectClick(position);
                    }

                }
            };
            View.OnClickListener showMoreListener = view -> {
                if (requestsActivityListener != null) {
                    int position = getAdapterPosition();
                    Intent intent = new Intent();

                    if (position != RecyclerView.NO_POSITION) {
                        requestsActivityListener.onShowMoreClick(position);
                    }

                }
            };
            //Always there.
            accept.setOnClickListener(acceptListener);
            moreInfo.setOnClickListener(showMoreListener);



            //It only makes sense to have X button for requests that are pending.
            if (appointment.getAppointmentStatus() == REJECTED) {
                reject.setVisibility(View.INVISIBLE); // Hide the button
                reject.setEnabled(false); // Make the button uninteractable
            } else {
                //Since the view might be recycled from an object which was REJECTED
                //To make sure the reject button is accessible we have to set it the onclick
                reject.setVisibility(View.VISIBLE);
                reject.setEnabled(true);
                reject.setOnClickListener(rejectListener);


            }
        }

    }

    @NonNull
    @Override
    public AppointmentItemAdapter.RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(currentContext).inflate(R.layout.patient_appointment, parent,false);
        return new RequestViewHolder(v, requestClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentItemAdapter.RequestViewHolder holder, int position) {
        try {
            long appointmentID = appointments.get(position).getAppointmentID();
            holder.setAppointment(appointments.get(position));
            holder.setData(db.getPatientFromAppointmentID(appointmentID), appointmentID);
        } catch (Exception e) {
            Log.d("Requests Screen", e.getMessage() + " " + e.getCause());
        }
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

}
