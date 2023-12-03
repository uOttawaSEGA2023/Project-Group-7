package com.quantumSamurais.hams.doctor.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
 
import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.appointment.Shift;

import java.util.List;

public class DoctorShiftsAdapter extends RecyclerView.Adapter<DoctorShiftsAdapter.ShiftViewHolder> {

    private List<Shift> shifts;
    private static OnDeleteClickListener deleteClickListener;
    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }
    public DoctorShiftsAdapter(List<Shift> shifts, OnDeleteClickListener deleteClickListener) {
        this.shifts = shifts;
        this.deleteClickListener = deleteClickListener;
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
        holder.textViewDate.setText("Date: " + shift.getStartTime().getDayOfYear());
        holder.textViewStartTime.setText("Start Time: " + shift.getStartTime());
        holder.textViewEndTime.setText("End Time: " + shift.getEndTime());
    }

    @Override
    public int getItemCount() {
        return shifts.size();
    }

    public Shift getShiftAt(int position) {
        return shifts.get(position);
    }

    public void updateList(List<Shift> newShifts) {
        shifts.clear();
        shifts.addAll(newShifts);
        // Remove notifyDataSetChanged here
    }

    public static class ShiftViewHolder extends RecyclerView.ViewHolder {

        TextView textViewDate, textViewStartTime, textViewEndTime;
        Button btnDeleteShift;

        public ShiftViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewStartTime = itemView.findViewById(R.id.textViewStartTime);
            textViewEndTime = itemView.findViewById(R.id.textViewEndTime);
            btnDeleteShift = itemView.findViewById(R.id.btnDeleteShift);
            btnDeleteShift.setOnClickListener(v -> {
                if (deleteClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        deleteClickListener.onDeleteClick(position);
                    }
                }
            });
        }
    }
}
