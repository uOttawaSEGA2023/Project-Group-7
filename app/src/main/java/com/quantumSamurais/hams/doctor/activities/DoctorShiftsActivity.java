package com.quantumSamurais.hams.doctor.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.appointment.Shift;
import com.quantumSamurais.hams.doctor.Doctor;
import com.quantumSamurais.hams.doctor.adapters.DoctorShiftsAdapter;

import java.util.List;

public class DoctorShiftsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewShifts;
    private DoctorShiftsAdapter shiftsAdapter;
    private Doctor currentDoctor;  

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_shifts);

        recyclerViewShifts = findViewById(R.id.recyclerViewShifts);

        List<Shift> shifts = currentDoctor.getShifts();

        shiftsAdapter = new DoctorShiftsAdapter(shifts);
        recyclerViewShifts.setAdapter(shiftsAdapter);

        recyclerViewShifts.setLayoutManager(new LinearLayoutManager(this));
    }
}
