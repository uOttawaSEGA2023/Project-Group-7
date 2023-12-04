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

import java.util.ArrayList;
import java.util.List;

public class AppointmentListAdapter extends RecyclerView.Adapter<AppointmentListAdapter.AppointmentViewHolder> {

    Context context;
    @LayoutRes int layout;

    boolean isPassed;
    List<Appointment> appointments;
    public AppointmentListAdapter(Context context, @LayoutRes int layout, List<Appointment> apps, boolean isPast) {
        this.context = context;
        this.layout = layout;
        this.appointments = apps;
        this.isPassed = isPast;

    }
    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(layout,parent,false);
        return new AppointmentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        holder.setViewData(isPassed);
        holder.setData(appointments.get(position));
    }

    public void updateData(List<Appointment> apps) {
        this.appointments = apps;
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        ImageButton cancelBtn;
        TextView docName, startTime, endTime;
        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            bindViews(itemView);
        }

        public void bindViews(View v) {
                cancelBtn = v.findViewById(R.id.deny);
                docName = v.findViewById(R.id.docNameAppointment);
                startTime = v.findViewById(R.id.appointStartTime);
                endTime = v.findViewById(R.id.appointEndTime);
        }

        public void setViewData(boolean isPassed) {
            if(isPassed) {
                cancelBtn.setVisibility(View.GONE);
            }
        }

        public void setData(Appointment appointment) {
            docName.setText(appointment.getDocName());
            startTime.setText(appointment.getStartTimeLocalDate().toString());
            endTime.setText(appointment.getEndTimeLocalDate().toString());
        }
    }
}



