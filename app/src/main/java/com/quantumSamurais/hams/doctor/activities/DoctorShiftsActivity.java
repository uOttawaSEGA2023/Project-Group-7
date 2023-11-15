package com.quantumSamurais.hams.doctor.activities;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.appointment.Shift;
import com.quantumSamurais.hams.database.Database;
import com.quantumSamurais.hams.database.callbacks.ResponseListener;
import com.quantumSamurais.hams.doctor.Doctor;
import com.quantumSamurais.hams.doctor.adapters.DoctorShiftsAdapter;
import java.util.List;
import android.app.AlertDialog; 
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class DoctorShiftsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewShifts;
    private DoctorShiftsAdapter shiftsAdapter;
    private Doctor currentDoctor;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_shifts);

        recyclerViewShifts = findViewById(R.id.recyclerViewShifts);

        List<Shift> shifts = currentDoctor.getShifts();

        shiftsAdapter = new DoctorShiftsAdapter(shifts, this::onShiftDeleteClick);
        recyclerViewShifts.setAdapter(shiftsAdapter);

        recyclerViewShifts.setLayoutManager(new LinearLayoutManager(this));

        Button btnAddShift = findViewById(R.id.btnAddShift);
        btnAddShift.setOnClickListener(v -> showAddShiftDialog());
    }

    public abstract class SimpleResponseListener<T> implements ResponseListener<T> {
        @Override
        public abstract void onSuccess(T response);

        @Override
        public void onFailure(Exception e) {
            // Provide a default implementation or override as needed
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showAddShiftDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_shift, null);
        dialogBuilder.setView(dialogView);

        DatePicker datePicker = dialogView.findViewById(R.id.datePicker);
        TimePicker startTimePicker = dialogView.findViewById(R.id.startTimePicker);
        TimePicker endTimePicker = dialogView.findViewById(R.id.endTimePicker);

        dialogBuilder.setPositiveButton("Add", (dialog, which) -> {
            int year = datePicker.getYear();
            int month = datePicker.getMonth() + 1;
            int day = datePicker.getDayOfMonth();
            int startHour = startTimePicker.getHour();
            int startMinute = startTimePicker.getMinute();
            int endHour = endTimePicker.getHour();
            int endMinute = endTimePicker.getMinute();

            LocalDate selectedDate = LocalDate.of(year, month, day);
            LocalDateTime startDateTime = LocalDateTime.of(selectedDate, LocalTime.of(startHour, startMinute));
            LocalDateTime endDateTime = LocalDateTime.of(selectedDate, LocalTime.of(endHour, endMinute));

            if (isValidNewShift(selectedDate, startDateTime, endDateTime)) {
                Database.getInstance().addShift(new Shift(currentDoctor.getEmployeeNumber(), selectedDate, startDateTime, endDateTime), new SimpleResponseListener<Void>() {
                    @Override
                    public void onSuccess(Void response) {
                        updateShiftsList();
                        Toast.makeText(DoctorShiftsActivity.this, "Shift added successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(DoctorShiftsActivity.this, "Failed to add shift. Please check for conflicts.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Invalid shift. Please check the date and time.", Toast.LENGTH_SHORT).show();
            }
        });

        dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isValidNewShift(LocalDate date, LocalDateTime startTime, LocalDateTime endTime) {
        // Check if the date is not in the past
        if (date.isBefore(LocalDate.now())) {
            return false;
        }

        // Check for conflicts with existing shifts
        for (Shift existingShift : currentDoctor.getShifts()) {
            if (existingShift.overlapsWith(new Shift(currentDoctor.getEmployeeNumber(), date, startTime, endTime))) {
                return false;
            }
        }

        // Check if the start and end times are in increments of 30 minutes
        long interval = startTime.until(endTime, java.time.temporal.ChronoUnit.MINUTES);
        return interval % 30 == 0;
    }

    private void onShiftDeleteClick(int position) {
        Shift shiftToDelete = shiftsAdapter.getShiftAt(position);

        AlertDialog.Builder confirmDialog = new AlertDialog.Builder(this);
        confirmDialog.setTitle("Confirm Deletion");
        confirmDialog.setMessage("Are you sure you want to delete this shift?");
        confirmDialog.setPositiveButton("Yes", (dialog, which) -> {
            Database.getInstance().deleteShift(shiftToDelete.getShiftID(), new SimpleResponseListener<Void>() {
                @Override
                public void onSuccess(Void response) {
                    updateShiftsList();
                    Toast.makeText(DoctorShiftsActivity.this, "Shift deleted successfully", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(DoctorShiftsActivity.this, "Failed to delete shift. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        });
        confirmDialog.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        confirmDialog.show();
    }

    private void updateShiftsList() {
        List<Shift> updatedShifts = Database.getInstance().getDoctor(currentDoctor.getEmail()).getShifts();
        shiftsAdapter.updateList(updatedShifts);
    }
}
