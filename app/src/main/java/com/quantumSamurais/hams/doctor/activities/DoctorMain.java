package com.quantumSamurais.hams.doctor.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.doctor.Doctor;

public class DoctorMain extends AppCompatActivity {
    private Doctor myDoctor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent().getSerializableExtra("doctor") == null){
            Log.d("doctorMain", "somehow the intent wasn't passed properly.");
            Toast.makeText(this, "An error occurred, this session will be terminated, try again", Toast.LENGTH_SHORT).show();
            finish();
        }

        myDoctor = (Doctor) getIntent().getSerializableExtra("doctor");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_doctor_view);
        setup();
    }

    private void setup(){
        //Sets the info for the Tab Layout
        TextView firstName = findViewById(R.id.firstNameDoc);
        firstName.setText(myDoctor.getFirstName());

    }
}
