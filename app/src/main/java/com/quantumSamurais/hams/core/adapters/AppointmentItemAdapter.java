package com.quantumSamurais.hams.core.adapters;

import static android.view.View.GONE;
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
        this.appointments = new ArrayList<>();
        currentContext = context;
        requestClickListener = listener;
    }


    public void setAppointments(ArrayList<Appointment> appointmentsFromDatabase) {
        ArrayList<Appointment> tempAppointments = appointmentsFromDatabase;
        switch(activeTab){
            case ALL_REQUESTS:
                ArrayList<Appointment> approvedAppointments = new ArrayList<>();
                for (Appointment appointment : appointmentsFromDatabase){
                    if (appointment.getAppointmentStatus() == APPROVED && !appointment.appointmentIsPassed()){
                        approvedAppointments.add(appointment);
                    }
                }
                tempAppointments = approvedAppointments;
                break;
            case PENDING_REQUESTS:
                ArrayList<Appointment> pendingAppointments = new ArrayList<>();
                for (Appointment appointment : appointmentsFromDatabase){
                    if (appointment.getAppointmentStatus() == PENDING && !appointment.appointmentIsPassed()){
                        pendingAppointments.add(appointment);
                    }
                }
                tempAppointments = pendingAppointments;
                break;

            case REJECTED_REQUESTS:
                ArrayList<Appointment> rejectedAppointments = new ArrayList<>();
                for (Appointment appointment : appointmentsFromDatabase){
                    if (appointment.getAppointmentStatus() == REJECTED && !appointment.appointmentIsPassed()){
                        rejectedAppointments.add(appointment);
                    }
                }
                tempAppointments = rejectedAppointments;
                break;

            case PAST:
                ArrayList<Appointment> passedAppointments = new ArrayList<>();
                for (Appointment appointment : appointmentsFromDatabase){
                    if (appointment.appointmentIsPassed() && appointment.getAppointmentStatus() == APPROVED){
                        passedAppointments.add(appointment);
                    }
                }
                tempAppointments = passedAppointments;
                break;
        }
        appointments = tempAppointments;
        notifyDataSetChanged();

        }




    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView name, emailAddress, id, date;
        ImageButton accept, reject, moreInfo;
        Appointment appointment;
        RequestsActivityListener requestsActivityListener;
        FragmentTab activeTab;

        public RequestViewHolder(@NonNull View itemView, RequestsActivityListener requestClickListener, FragmentTab activeTab) {
            super(itemView);
            requestsActivityListener = requestClickListener;
            accept = itemView.findViewById(R.id.accept);
            reject = itemView.findViewById(R.id.deny);
            moreInfo = itemView.findViewById(R.id.showMore);
            this.activeTab = activeTab;

        }

        private void setData(Appointment appointment) throws Exception {
            name = itemView.findViewById(R.id.nameRequest);
            emailAddress = itemView.findViewById(R.id.emailAddressRequest);
            date = itemView.findViewById(R.id.dateOfRequest);
            id = itemView.findViewById(R.id.idRequest);
            Patient patient = appointment.getPatient();


            //
            name.setText(patient.getFirstName() + " " + patient.getLastName());
            emailAddress.setText(patient.getEmail());
            date.setText(appointment.getStartTime().toDate().toString() + "\n" +
                    appointment.getEndTime().toDate().toString());
            id.setText(appointment.getAppointmentID().toString());


            setOnClickListeners(appointment, activeTab);
        }

        public void setAppointment(Appointment appointment) {
            this.appointment = appointment;
        }

        private void setOnClickListeners(Appointment appointment, FragmentTab activeTab) {
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
            View.OnClickListener cancelListener = view -> {
                if (requestsActivityListener != null) {
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        requestsActivityListener.onCancelClick(position);
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
            moreInfo.setOnClickListener(showMoreListener);
            switch (activeTab){
                case ALL_REQUESTS:
                    //All Requests is a standin for Approved
                    accept.setVisibility(GONE);

                    reject.setVisibility(View.VISIBLE);
                    reject.setEnabled(true);
                    reject.setOnClickListener(cancelListener);
                    break;

                case PENDING_REQUESTS:
                    //For Pending set both
                    accept.setVisibility(View.VISIBLE);
                    accept.setEnabled(true);
                    accept.setOnClickListener(acceptListener);
                    reject.setVisibility(View.VISIBLE);
                    reject.setEnabled(true);
                    reject.setOnClickListener(rejectListener);
                    break;
                case REJECTED_REQUESTS:
                    reject.setVisibility(GONE);

                    accept.setVisibility(View.VISIBLE);
                    accept.setEnabled(true);
                    accept.setOnClickListener(acceptListener);
                    break;
                case PAST:
                    accept.setVisibility(GONE);
                    reject.setVisibility(GONE);
            }


          /*  if (appointment.getAppointmentStatus() == APPROVED && !appointment.appointmentIsPassed()) {

            } else {
                //Since the view might be recycled from an object which was REJECTED
                //To make sure the reject button is accessible we have to set it the onclick

            }

            //It only makes sense to have X button for requests that are pending.
            if (appointment.getAppointmentStatus() == REJECTED && !appointment.appointmentIsPassed()) {
                reject.setVisibility(GONE);
            } else {
                //Since the view might be recycled from an object which was REJECTED
                //To make sure the reject button is accessible we have to set it the onclick
                 //If the appointment is not rejected, then it's approved
            }

            //It only makes sense to have X button for requests that are in the future
            if (appointment.appointmentIsPassed()) {

            } else {
                //Since the view might be recycled from an object which was REJECTED
                //To make sure the reject button is accessible we have to set it the onclick
                reject.setVisibility(View.VISIBLE);
                reject.setEnabled(true);
                reject.setOnClickListener(rejectListener);
            }*/
        }

    }

    @NonNull
    @Override
    public AppointmentItemAdapter.RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(currentContext).inflate(R.layout.patient_appointment, parent,false);
        return new RequestViewHolder(v, requestClickListener, activeTab);
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
