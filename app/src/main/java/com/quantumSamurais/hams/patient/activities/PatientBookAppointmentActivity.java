package com.quantumSamurais.hams.patient.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.appointment.Appointment;
import com.quantumSamurais.hams.appointment.Shift;
import com.quantumSamurais.hams.database.Database;
import com.quantumSamurais.hams.doctor.Specialties;
import com.quantumSamurais.hams.patient.AppointmentListAdapter;
import com.quantumSamurais.hams.patient.Patient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class PatientBookAppointmentActivity extends AppCompatActivity {


    TextView dateText;
    Button changeDate;
    Spinner selectSpec;
    Patient patient;

    Specialties selected;

    AppointmentListAdapter availAdapter;
    RecyclerView toBook;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_appointments);
        setup();
        addListeners();
    }

    private void setup() {
        Patient p = (Patient) getIntent().getSerializableExtra("patient");
        assert p != null;
        this.patient = p;
        selectSpec = findViewById(R.id.selectSpeciality);
        initSelectSpec();
        selected = Specialties.FAMILY_MEDICINE;
        dateText = findViewById(R.id.dateTextbox);
        changeDate = findViewById(R.id.datePickBtn);
        toBook = findViewById(R.id.availAppointments);

        Database.getInstance().getAllBookable(patient,getCurrentSpec(),patient.getDate(),this::bookableAppsCB);
        dateSet(p.getDate().toString());
        changeDate.setOnClickListener(this::pickDateClicked);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        availAdapter = new AppointmentListAdapter(this, R.layout.appoinment_item, new ArrayList<>(),false, true,this::updateApps);
        toBook.setLayoutManager(layoutManager);
        toBook.setAdapter(availAdapter);

    }

    public void updateApps() {
        Database.getInstance().getAllBookable(patient,getCurrentSpec(),patient.getDate(),this::bookableAppsCB);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void bookableAppsCB(ArrayList<Appointment> apps) {
        availAdapter.updateData(apps);
        availAdapter.notifyDataSetChanged();
    }

    private void initSelectSpec() {
        EnumSet<Specialties> s = EnumSet.allOf(Specialties.class);
        String[] items = new String[s.size()];
        int k = 0;
        for (Specialties specialties : s) {
            StringBuilder text = new StringBuilder();
            String nexItem = specialties.toString();
            String[] splitString = nexItem.split("_");
            for (int i = 0; i < splitString.length; i++) {
                String q = splitString[i];
                text.append(q.substring(0, 1).toUpperCase());
                text.append(q.substring(1).toLowerCase());
                if (i != splitString.length - 1)
                    text.append(' ');
            }
            items[k] = text.toString();
            k++;
        }

        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        selectSpec.setAdapter(adapter);
        selectSpec.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                EnumSet<Specialties> s = EnumSet.allOf(Specialties.class);
                int k = 0;
                for (Specialties specialties : s) {
                    if(k == position) {
                        selected = specialties;
                        dateSet(dateText.getText().toString());
                        break;
                    }
                    k++;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    private void addListeners() {
        changeDate.setOnClickListener(this::pickDateClicked);
    }

    private void pickDateClicked(View v) {
       DatePickerFragment picker = new DatePickerFragment(this::dateSet, dateText.getText().toString());
       picker.show(getSupportFragmentManager(),"datePicker");
    }

    private void dateSet(String date) {
        dateText.setText(date);
        Database.getInstance().getAllBookable(patient,getCurrentSpec(), LocalDate.parse(dateText.getText()),this::bookableAppsCB);
    }





    private Specialties getCurrentSpec() {
        return  selected;
    }

}
