package com.quantumSamurais.hams.doctor.activities;

import android.app.AlertDialog;
import android.os.Build;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.appointment.Shift;
import com.quantumSamurais.hams.database.Database;
import com.quantumSamurais.hams.doctor.Doctor;
import com.quantumSamurais.hams.doctor.activities.fragments.DoctorViewAppointmentsFragment;
import com.quantumSamurais.hams.doctor.adapters.DoctorShiftsAdapter;
import com.quantumSamurais.hams.ui.settings.SettingFragment;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class DoctorMain extends AppCompatActivity implements DoctorShiftsAdapter.OnDeleteClickListener {
    private Doctor myDoctor;
    DoctorShiftsAdapter shiftsAdapter;
    RecyclerView shiftsStack;
    Database db;
    ArrayList<Shift> shifts;
    Handler refreshShifts;

    Fragment fragment;
    private DrawerLayout drawerLayout;
    ExtendedFloatingActionButton addShiftFAB;

    private ActionBarDrawerToggle actionBarDrawerToggle;

    private NavigationView navView;


    private boolean acceptsAppointmentsByDefault;

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
        Log.d("email", getIntent().getStringExtra("doctorEmailAddress"));
        shifts =  new ArrayList<>();
        setContentView(R.layout.main_doctor_view);
        // imp
        shiftsAdapter = new DoctorShiftsAdapter(shifts, this);
        shiftsStack = findViewById(R.id.shiftsRecyclerView);
        db.getShifts(myDoctor.getShiftIDs(), this::updateShifts);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        shiftsStack.setLayoutManager(layoutManager);
        shiftsStack.setAdapter(shiftsAdapter);
        // imp
        addShiftFAB = findViewById(R.id.extended_fab);
        addShiftFAB.setOnClickListener(v -> showAddShiftDialog());
        setup();
        refreshShifts = new Handler();
        refresh_runnable.run();



        String doctorName = myDoctor.getFirstName() + " " + myDoctor.getLastName();
        String doctorEmail = myDoctor.getEmail();

        acceptsAppointmentsByDefault = myDoctor.getAcceptsAppointmentsByDefault();

        fragment = null;
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navView = findViewById(R.id.navigation_view);
        Log.d("navView", "navView: " + navView);
        View navHeader = navView.getHeaderView(0);
        Log.d("navHeader", "navHeader: " + navHeader);



        TextView headerName = navHeader.findViewById(R.id.header_name);
        TextView headerEmail = navHeader.findViewById(R.id.header_email);
        Log.d("doctor name", "doctor name: " + headerName);
        Log.d("doctor email", "doctor email: " + headerEmail);
        headerName.setText(doctorName);
        headerEmail.setText(doctorEmail);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navView = findViewById(R.id.navigation_view);
        setupDrawerContent(navView);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    selectDrawerItem(menuItem);
                    return true;
                }
        );
    }


    private final Runnable refresh_runnable = new Runnable(){
        public void run(){
            myDoctor = Database.getInstance().getDoctor(myDoctor.getEmail());
            Database.getInstance().getShifts(myDoctor.getShiftIDs(), DoctorMain.this::updateShifts);
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

            if (isValidNewShift(startDateTime, endDateTime)) {
                    // Use the getDoctor method to get the latest doctor information
                    Doctor updatedDoctor = db.getDoctor(myDoctor.getEmail());
                    Database.getInstance().addShift(new Shift(updatedDoctor.getEmail(), startDateTime, endDateTime));
                    runOnUiThread(() -> {
                        refreshShifts.post(refresh_runnable);
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
    private boolean isValidNewShift(LocalDateTime startTime, LocalDateTime endTime) {
        // Check if the time passed aren't properly set
        if (endTime.isBefore(startTime) || endTime.isEqual(startTime)){
            return false;
        }
        // Check if present time is after the start time
        if (startTime.isBefore(LocalDateTime.now())){
            return false;
        }
        //Right before checking for conflicts check for interval
        Duration difference = Duration.between(startTime, endTime);
        boolean isAMultipleOf30Minutes = difference.toMinutes() % 30 == 0;
        if (!isAMultipleOf30Minutes) {
            return false;
        }

        // Check for conflicts with existing shifts
        for (Shift existingShift : shifts) {
            if (existingShift.overlapsWith(startTime, endTime)) {
                return false;
            }
        }

        return true;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onDeleteClick(int position) {
        Shift shiftToDelete = shiftsAdapter.getShiftAt(position);

        AlertDialog.Builder confirmDialog = new AlertDialog.Builder(this);
        confirmDialog.setTitle("Confirm Deletion");
        confirmDialog.setMessage("Are you sure you want to delete this shift?");
        confirmDialog.setPositiveButton("Yes", (dialog, which) -> {
            Database.getInstance().deleteShift(shiftToDelete.getShiftID());
            Log.d("ShiftIDToDelete", "The shift ID of the shift I want to delete is : " + shiftToDelete.getShiftID());
            refreshShifts.post(refresh_runnable);
            Toast.makeText(this, "Shift deleted successfully", Toast.LENGTH_SHORT).show();
        });
        confirmDialog.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        confirmDialog.show();
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


    public void selectDrawerItem(MenuItem item) {
        myDoctor = db.getDoctor(getIntent().getStringExtra("doctorEmailAddress"));
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (item.getItemId() == R.id.nav_settings) {
            fragment = new SettingFragment(myDoctor);
        } else if (item.getItemId() == R.id.nav_appointments) {
            fragment = new DoctorViewAppointmentsFragment(myDoctor);
        } else if (item.getItemId() == R.id.nav_home) {
            if (fragment != null) {
                transaction.remove(fragment).commit();
                fragment = null;
            }
            if (addShiftFAB.getVisibility() != View.VISIBLE) {
                addShiftFAB.setVisibility(View.VISIBLE);
            }
        }
        Log.d("DoctorMain: ", "FragmentIsNull: " + (fragment == null));
        drawerLayout.closeDrawers();
        if (fragment != null) {
            transaction.replace(R.id.flContent, fragment).commit();
        }
    }

    public void updateShifts(ArrayList<Shift> shifts){
        this.shifts = shifts;
        shiftsAdapter.updateList(shifts);
        shiftsAdapter.notifyDataSetChanged();
    }

}
