package com.quantumSamurais.hams.doctor.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.appointment.Shift;
import com.quantumSamurais.hams.database.Database;
import com.quantumSamurais.hams.doctor.Doctor;
import com.quantumSamurais.hams.doctor.adapters.DoctorShiftsAdapter;

import java.util.ArrayList;

public class DoctorMain extends AppCompatActivity implements DoctorShiftsAdapter.OnDeleteClickListener {
    private Doctor myDoctor;
    DoctorShiftsAdapter shiftsAdapter;
    RecyclerView shiftsStack;
    Database db;
    ArrayList<Shift> shifts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent().getStringExtra("doctorEmailAddress") == null){
            Log.d("doctorMain", "somehow the intent wasn't passed properly.");
            /*Toast.makeText(this, "An error occurred, this session will be terminated, try again", Toast.LENGTH_SHORT).show();
            finish();*/
        }

        db = Database.getInstance();
        myDoctor = db.getDoctor(getIntent().getStringExtra("doctorEmailAddress"));
        shifts = myDoctor.getShifts();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_doctor_view);
        setup();
    }

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
    }

    @Override
    public void onResume() {
        super.onResume();
        //Updates the doctor
        myDoctor = db.getDoctor(myDoctor.getEmail());
    }

    //Refresh Handler to refresh the UI

}
