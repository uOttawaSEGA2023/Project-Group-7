package com.quantumSamurais.hams.patient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.appointment.Appointment;
import com.quantumSamurais.hams.database.Database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppointmentListAdapter extends RecyclerView.Adapter<AppointmentListAdapter.AppointmentViewHolder> {

    Context context;
    @LayoutRes int layout;

    boolean isPast;
    boolean isBooking;
    List<Appointment> appointments;
    public AppointmentListAdapter(Context context, @LayoutRes int layout, List<Appointment> apps, boolean isPast, boolean isBooking) {
        this.context = context;
        this.layout = layout;
        ArrayList<Appointment> newApps = new ArrayList<>();
        for(Appointment app: apps) {
            if(app.appointmentIsPassed()) {
                if(isPast) {
                    newApps.add(app);
                }
            } else {
                if(!isPast) {
                    newApps.add(app);
                }
            }
        }
        this.appointments = newApps;
        this.isPast = isPast;
        this.isBooking = isBooking;
    }
    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(layout,parent,false);
        return new AppointmentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        holder.setViewData(isPast, isBooking);
        holder.setData(appointments.get(position));
    }

    public void updateData(List<Appointment> apps) {
        ArrayList<Appointment> newApps = new ArrayList<>();
        for(Appointment app: apps) {
            if(app.appointmentIsPassed()) {
                if(isPast) {
                    newApps.add(app);
                }
            } else {
                if(!isPast) {
                    newApps.add(app);
                }
            }
        }
        this.appointments = newApps;
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        ImageButton cancelBtn, showMoreBtn, rateBtn;
        TextView docName, startTime, endTime;

        Appointment thisApp;

        boolean isPast, isBooking;
        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            bindViews(itemView);
            //TODO: Display Request Status
        }

        public void bindViews(View v) {
                cancelBtn = v.findViewById(R.id.deny);
                rateBtn = v.findViewById(R.id.accept);
                showMoreBtn = v.findViewById(R.id.showMore);
                docName = v.findViewById(R.id.docNameAppointment);
                startTime = v.findViewById(R.id.appointStartTime);
                endTime = v.findViewById(R.id.appointEndTime);
        }

        public void setViewData(boolean isPassed, boolean isBooking) {
            this.isPast = isPassed;
            this.isBooking = isBooking;
            showMoreBtn.setVisibility(View.GONE);

            if(isPassed) {
                cancelBtn.setVisibility(View.GONE);
                rateBtn.setOnClickListener(this::rateDoctor);
            } else {
                rateBtn.setVisibility(View.GONE);
                cancelBtn.setOnClickListener(this::cancelAppointment);
            }
            if(isBooking) {
                rateBtn.setOnClickListener(this::bookAppointment);
            }
        }

        public void bookAppointment(View v) {
            Database.getInstance().addAppointmentRequest(thisApp);
        }
        public void rateDoctor(View v) {
          //  Database.getInstance().rateDoctor(docName, rating);
        }

        public void cancelAppointment(View v) {
            //TODO: Check not before 60 min
            Database.getInstance().cancelAppointment(thisApp.getAppointmentID());

        }

        public void setData(Appointment appointment) {
            thisApp = appointment;
            docName.setText(appointment.getDocName());
            startTime.setText(appointment.getStartTimeLocalDate().toString());
            endTime.setText(appointment.getEndTimeLocalDate().toString());
        }
    }
}



