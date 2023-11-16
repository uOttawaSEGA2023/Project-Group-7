package com.quantumSamurais.hams.doctor.activities;

import android.app.AlertDialog;
import android.os.Build;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import com.google.android.material.navigation.NavigationView;
import com.quantumSamurais.hams.MainActivity;
import com.quantumSamurais.hams.R;

import com.quantumSamurais.hams.appointment.Shift;
import com.quantumSamurais.hams.database.Database;
import com.quantumSamurais.hams.doctor.Doctor;
import com.quantumSamurais.hams.doctor.adapters.DoctorShiftsAdapter;


//<>
import androidx.drawerlayout.widget.DrawerLayout;

import com.quantumSamurais.hams.ui.settings.SettingActivity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DoctorMain extends AppCompatActivity implements DoctorShiftsAdapter.OnDeleteClickListener, NavigationView.OnNavigationItemSelectedListener{
    private Doctor myDoctor;
    DoctorShiftsAdapter shiftsAdapter;
    RecyclerView shiftsStack;
    Database db;
    ArrayList<Shift> shifts;
    Handler refreshShifts;

    DrawerLayout drawerLayout;
    ExtendedFloatingActionButton addShiftFAB;

    ActionBarDrawerToggle actionBarDrawerToggle;

    NavigationView navView;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getStringExtra("doctorEmailAddress") == null){
            Log.d("doctorMain", "somehow the intent wasn't passed properly.");
            Toast.makeText(this, "An error occurred, this session will be terminated, try again", Toast.LENGTH_SHORT).show();
            finish();
        }

        db = Database.getInstance();
        myDoctor = db.getDoctor(getIntent().getStringExtra("doctorEmailAddress"));
        shifts = myDoctor.getShifts();
        setContentView(R.layout.main_doctor_view);

        addShiftFAB = findViewById(R.id.extended_fab);
        addShiftFAB.setOnClickListener(v -> showAddShiftDialog());
        setup();
        refreshShifts = new Handler();
        refresh_runnable.run();



        String doctorName = myDoctor.getFirstName() + " " + myDoctor.getLastName();
        String doctorEmail = myDoctor.getEmail();

        // headerName = findViewById(R.id.header_name);
        // headerEmail = findViewById(R.id.header_email);

        boolean acceptsAppointmentsByDefault = myDoctor.getAcceptsAppointmentsByDefault();


        drawerLayout = findViewById(R.id.drawer_layout);


        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        navView = findViewById(R.id.navigation_view);
        navView.setNavigationItemSelectedListener(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



    }


    private final Runnable refresh_runnable = new Runnable(){
        public void run(){
            Log.d("Doctor", "My doctor to string: " + myDoctor.toString());
            Log.d("Doctor", "My shifts: " + shifts);
            myDoctor = db.getDoctor(myDoctor.getEmail());
            shiftsAdapter.updateList(myDoctor.getShifts());
            shiftsAdapter.notifyDataSetChanged();
            refreshShifts.postDelayed(refresh_runnable, 5000);
        }
    };

    private void setup(){
        //Sets the info for the Tab Layout
        TextView firstName = findViewById(R.id.firstNameDoc);
        firstName.setText(myDoctor.getFirstName());
        Log.d("doctor", "This is my name: " + myDoctor.toString());
        //Setting up the RecyclerView for Shifts
        shiftsAdapter = new DoctorShiftsAdapter(shifts, this);
        shiftsStack = findViewById(R.id.shiftsRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        shiftsStack.setLayoutManager(layoutManager);
        shiftsStack.setAdapter(shiftsAdapter);
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
                Database.getInstance().addShift(new Shift(myDoctor.getEmployeeNumber(), startDateTime, endDateTime));
                updateShiftsList();
                Toast.makeText(this, "Shift added successfully", Toast.LENGTH_SHORT).show();
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
            showWarningMessage("Selected date is in the past. Please choose a future date.");
            return false;
        }

        // Check if start time is before end time
        if (startTime.isEqual(endTime) || startTime.isAfter(endTime)) {
            showWarningMessage("End time must be after start time. Please adjust the times.");
            return false;
        }

        // Check for conflicts with existing shifts
        for (Shift existingShift : myDoctor.getShifts()) {
            if (existingShift.overlapsWith(new Shift(myDoctor.getEmployeeNumber(), startTime, endTime))) {
                showWarningMessage("Shift conflicts with an existing shift. Please choose different times.");
                return false;
            }
        }

        // Check if the start and end times are in increments of 30 minutes
        long interval = startTime.until(endTime, java.time.temporal.ChronoUnit.MINUTES);
        if (interval % 30 != 0) {
            showWarningMessage("Shift times must be in increments of 30 minutes. Please adjust the times.");
            return false;
        }

        return true;
    }

    private void showWarningMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onDeleteClick(int position) {
        Shift shiftToDelete = shiftsAdapter.getShiftAt(position);

        AlertDialog.Builder confirmDialog = new AlertDialog.Builder(this);
        confirmDialog.setTitle("Confirm Deletion");
        confirmDialog.setMessage("Are you sure you want to delete this shift?");
        confirmDialog.setPositiveButton("Yes", (dialog, which) -> {
            Database.getInstance().deleteShift(shiftToDelete.getShiftID());
            updateShiftsList();
            Toast.makeText(this, "Shift deleted successfully", Toast.LENGTH_SHORT).show();
        });
        confirmDialog.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        confirmDialog.show();
    }

    private void updateShiftsList() {
        List<Shift> updatedShifts = Database.getInstance().getDoctor(myDoctor.getEmail()).getShifts();
        shiftsAdapter.updateList(updatedShifts);
    }

    /*@Override
    public void onDeleteClick(int position) {
        long shiftID = shifts.get(position).getShiftID();
        myDoctor.cancelShift(shiftID);
        // Remove first to avoid, any potential slurry of callbacks problem
        refreshShifts.removeCallbacks(refresh_runnable);
        // Then we update
        refreshShifts.post(refresh_runnable);
    }*/

    @Override
    public void onResume() {
        super.onResume();
        //Updates the doctor
        refreshShifts.post(refresh_runnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        refreshShifts.removeCallbacks(refresh_runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
     if(actionBarDrawerToggle.onOptionsItemSelected(item)) {
         return true;
     }
     return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent newIntent;

        if(item.getItemId() == R.id.nav_home) {
            if (getWindow().getDecorView().findViewById(android.R.id.content).getId() !=
            R.id.drawer_layout) {
                newIntent = new Intent(this, DoctorMain.class);
                startActivity(newIntent);
            }
        } else if (item.getItemId() == R.id.nav_settings) {
            newIntent = new Intent(this, SettingActivity.class);
            startActivity(newIntent);

        } else if (item.getItemId() == R.id.nav_appointments ) {
            newIntent = new Intent(this,MainActivity.class);
            startActivity(newIntent);
        }

        return true;
    }

}
