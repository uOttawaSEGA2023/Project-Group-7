package com.quantumSamurais.hams.patient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.appointment.Appointment;
import com.quantumSamurais.hams.database.Database;
import com.quantumSamurais.hams.patient.activities.PatientBookAppointmentActivity;
import com.quantumSamurais.hams.patient.activities.RateDoctorFragment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentListAdapter extends RecyclerView.Adapter<AppointmentListAdapter.AppointmentViewHolder> {

    Context context;
    @LayoutRes int layout;

    boolean isPast;
    boolean isBooking;

    FragmentManager man;

    static Database.UpdateAfterBook listener;
    List<Appointment> appointments;
    public AppointmentListAdapter(Context context, @LayoutRes int layout, List<Appointment> apps, boolean isPast, boolean isBooking, Database.UpdateAfterBook listener) {
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
        AppointmentListAdapter.listener = listener;
        this.appointments = newApps;
        this.isPast = isPast;
        this.isBooking = isBooking;
    }
    public AppointmentListAdapter(Context context, @LayoutRes int layout, List<Appointment> apps, boolean isPast, boolean isBooking, Database.UpdateAfterBook listener, FragmentManager manager) {
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
        AppointmentListAdapter.listener = listener;
        this.appointments = newApps;
        this.isPast = isPast;
        this.isBooking = isBooking;
        this.man = manager;
    }
    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(layout,parent,false);
        return new AppointmentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        holder.setViewData(isPast, isBooking, this.man, context);
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
        TextView docName, startTime, endTime, status, id;

        Appointment thisApp;
        Context context;

        FragmentManager man;
        boolean isPast, isBooking;
        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            bindViews(itemView);
        }

        public void bindViews(View v) {
                cancelBtn = v.findViewById(R.id.deny);
                rateBtn = v.findViewById(R.id.accept);
                showMoreBtn = v.findViewById(R.id.showMore);
                docName = v.findViewById(R.id.docNameAppointment);
                startTime = v.findViewById(R.id.appointStartTime);
                endTime = v.findViewById(R.id.appointEndTime);
                status = v.findViewById(R.id.appointStatus);
                id = v.findViewById(R.id.id);
        }

        public void setViewData(boolean isPassed, boolean isBooking, FragmentManager man, Context context) {
            this.isPast = isPassed;
            this.isBooking = isBooking;
            this.context = context;
            showMoreBtn.setVisibility(View.GONE);
            this.man =man;


            if(isPassed) {
                cancelBtn.setVisibility(View.GONE);
                rateBtn.setOnClickListener(this::rateDoctor);
                status.setVisibility(View.GONE);
            } else {
                rateBtn.setVisibility(View.GONE);
                cancelBtn.setOnClickListener(this::cancelAppointment);
            }
            if(isBooking) {
                rateBtn.setVisibility(View.VISIBLE);
                cancelBtn.setVisibility(View.GONE);
                rateBtn.setOnClickListener(this::bookAppointment);
            }
        }

        private void rateDoctor(View view) {
            if(thisApp.getRated()) {
                Toast.makeText(context,"You have already rated this doctor for this appointment.", Toast.LENGTH_SHORT).show();
                return;
            }
            RateDoctorFragment fragment = new RateDoctorFragment(this::rateCb);
                    fragment.show(man, "TAG");
        }

        public void rateCb(float rating) {
            Log.d("Rating Callback (AppointmentListAdapter:165)", "Rating was: " + rating);
            Database.getInstance().rateDoctorDB(thisApp.getAppointmentID(), thisApp.getShiftID(),rating);
            thisApp.setRated(true);
        }

        public void bookAppointment(View v) {
            Database.getInstance().addAppointmentRequest(thisApp,listener);
        }
        public void cancelAppointment(View v) {
            if(thisApp.getStartTimeLocalDate().minusMinutes(60).isAfter(LocalDateTime.now())) {
                Database.getInstance().cancelAppointment(thisApp.getAppointmentID());
            } else {
                Toast.makeText(context,"Can not cancel appointment that starts within the next 60 minutes",Toast.LENGTH_SHORT).show();
            }

        }

        @SuppressLint("SetTextI18n")
        public void setData(Appointment appointment) {
            thisApp = appointment;
            docName.setText(appointment.getDocName());
            startTime.setText(appointment.getStartTimeLocalDate().toString());
            endTime.setText(appointment.getEndTimeLocalDate().toString());
            status.setText(thisApp.getAppointmentStatus().toString());
            id.setText(Long.valueOf(appointment.getAppointmentID()).toString());
        }
    }
}



