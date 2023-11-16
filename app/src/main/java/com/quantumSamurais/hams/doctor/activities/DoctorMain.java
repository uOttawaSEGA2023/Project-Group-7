package com.quantumSamurais.hams.doctor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


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

//<> may be used; will delete later
//TODO potentially delete these items.

import java.util.ArrayList;

public class DoctorMain extends AppCompatActivity implements DoctorShiftsAdapter.OnDeleteClickListener, NavigationView.OnNavigationItemSelectedListener{
    private Doctor myDoctor;
    DoctorShiftsAdapter shiftsAdapter;
    RecyclerView shiftsStack;
    Database db;
    ArrayList<Shift> shifts;
    Handler refreshShifts;

    DrawerLayout drawerLayout;

    ActionBarDrawerToggle actionBarDrawerToggle;

    NavigationView navView;



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
        setup();
        refreshShifts = new Handler();
        refresh_runnable.run();

        boolean acceptApptDefault = myDoctor.getAcceptsAppointmentsByDefault();

        String doctorName = myDoctor.getFirstName() + " " + myDoctor.getLastName();
        String doctorEmail = myDoctor.getEmail();

        // headerName = findViewById(R.id.header_name);
        // headerEmail = findViewById(R.id.header_email);


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

    @Override
    public void onDeleteClick(int position) {
        long shiftID = shifts.get(position).getShiftID();
        myDoctor.cancelShift(shiftID);
        // Remove first to avoid, any potential slurry of callbacks problem
        refreshShifts.removeCallbacks(refresh_runnable);
        // Then we update
        refreshShifts.post(refresh_runnable);
    }

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
