package com.quantumSamurais.hams.doctor.adapters;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.appointment.Shift;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DoctorShiftsAdapter extends RecyclerView.Adapter<DoctorShiftsAdapter.ShiftViewHolder> {

    private List<Shift> shifts;

    private static OnButtonClickedListener listener;

    public interface OnButtonClickedListener {
        void onButtonClicked(int position);
    }
    private static OnDeleteClickListener deleteClickListener;
    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }
    public DoctorShiftsAdapter(List<Shift> shifts, OnDeleteClickListener deleteClickListener, OnButtonClickedListener listener) {
        this.shifts = (ArrayList<Shift>) shifts.stream().filter(shift -> {return shift.getEndTime().isAfter(LocalDateTime.now());}).collect(Collectors.toList());

        this.deleteClickListener = deleteClickListener;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ShiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shift, parent, false);
        return new ShiftViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShiftViewHolder holder, int position) {
        Shift shift = shifts.get(position);

        // Set your shift information to the views
        holder.textViewDate.setText("Date: " + shift.getStartTime().getMonth());
        holder.textViewStartTime.setText("Start Time: " + shift.getStartTime());
        holder.textViewEndTime.setText("End Time: " + shift.getEndTime());
        holder.textViewShiftID.setText(Long.valueOf(shift.getShiftID()).toString());
    }

    @Override
    public int getItemCount() {
        return shifts.size();
    }

    public Shift getShiftAt(int position) {
        return shifts.get(position);
    }

    public void updateList(List<Shift> newShifts) {
        shifts = newShifts;
        notifyDataSetChanged();
    }

    public static class ShiftViewHolder extends RecyclerView.ViewHolder {

        TextView textViewDate, textViewStartTime, textViewEndTime, textViewShiftID;
        Button btnDeleteShift, buttonShowAppointments;

        public ShiftViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewStartTime = itemView.findViewById(R.id.textViewStartTime);
            textViewEndTime = itemView.findViewById(R.id.textViewEndTime);
            textViewShiftID = itemView.findViewById(R.id.shiftID);
            buttonShowAppointments = itemView.findViewById(R.id.see_appointments);
            buttonShowAppointments.setOnClickListener(v -> {
                if (listener != null){
                    if (getAdapterPosition() != NO_POSITION){
                        listener.onButtonClicked(getAdapterPosition());
                    }
                }
            });
            btnDeleteShift = itemView.findViewById(R.id.btnDeleteShift);
            btnDeleteShift.setOnClickListener(v -> {
                if (deleteClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != NO_POSITION) {
                        deleteClickListener.onDeleteClick(position);
                    }
                }
            });
        }
    }
}
