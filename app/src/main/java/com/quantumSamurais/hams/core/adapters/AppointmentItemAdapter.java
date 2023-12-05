package com.quantumSamurais.hams.core.adapters;

import static com.quantumSamurais.hams.database.RequestStatus.APPROVED;
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
import com.quantumSamurais.hams.appointment.Appointment;
import com.quantumSamurais.hams.core.enums.FragmentTab;
import com.quantumSamurais.hams.core.listeners.RequestsActivityListener;
import com.quantumSamurais.hams.database.Database;
import com.quantumSamurais.hams.patient.Patient;

import java.util.ArrayList;




public class AppointmentItemAdapter extends RecyclerView.Adapter<AppointmentItemAdapter.RequestViewHolder> {
    private final RequestsActivityListener requestClickListener;
    private ArrayList<Appointment> appointments; // Renamed from 'requests'
    private final Context currentContext;
    FragmentTab activeTab;
    Database db = Database.getInstance();


    public AppointmentItemAdapter(Context context, FragmentTab activeTab, ArrayList<Appointment> appointmentsFromDatabase, RequestsActivityListener listener) {
        this.activeTab = activeTab;
        setAppointments(appointmentsFromDatabase);
        currentContext = context;
        requestClickListener = listener;
    }


    public void setAppointments(ArrayList<Appointment> appointmentsFromDatabase) {
        ArrayList<Appointment> filteredAppointments = new ArrayList<>();
        for (Appointment appointment : appointmentsFromDatabase){
            switch(activeTab){
                case ALL_REQUESTS:
                    //This really means all upcoming, too lazy to change it
                    if (appointment.getAppointmentStatus() == APPROVED){
                        filteredAppointments.add(appointment);
                    }
                    break;
                case PENDING_REQUESTS:
                    if (appointment.getAppointmentStatus() == PENDING){
                        filteredAppointments.add(appointment);
                    }
                    break;
                case REJECTED_REQUESTS:
                    if (appointment.getAppointmentStatus() == REJECTED){
                        filteredAppointments.add(appointment);
                    }
                    break;
                default:
                    Log.d("AppointmentFragmentError", "The current fragment was initialized with a wrong tab.");
            }

        }

        appointments = filteredAppointments;
        notifyDataSetChanged();
    }


    public static class RequestViewHolder extends RecyclerView.ViewHolder{
        TextView name, emailAddress, date;
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

        private void setData(Appointment appointment) throws Exception {
            name = itemView.findViewById(R.id.nameRequest);
            emailAddress = itemView.findViewById(R.id.emailAddressRequest);
            date = itemView.findViewById(R.id.dateOfRequest);
            Patient patient = appointment.getPatient();



            //
            name.setText(patient.getFirstName() + " " + patient.getLastName());
            emailAddress.setText(patient.getEmail());



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
            db.getAppointment(appointmentID).thenAccept(appointment ->
            {
                holder.setAppointment(appointments.get(position));
                try {
                    holder.setData(appointment);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            );
        } catch (Exception e) {
            Log.d("Requests Screen", e.getMessage() + " " + e.getCause());
        }
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

}
