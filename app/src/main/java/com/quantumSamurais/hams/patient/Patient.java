package com.quantumSamurais.hams.patient;

import android.content.Context;
import android.content.Intent;

import com.google.firebase.firestore.Exclude;
import com.quantumSamurais.hams.appointment.Appointment;
import com.quantumSamurais.hams.appointment.Shift;
import com.quantumSamurais.hams.database.Database;
import com.quantumSamurais.hams.database.RequestStatus;
import com.quantumSamurais.hams.doctor.Doctor;
import com.quantumSamurais.hams.patient.activities.PatientMainActivity;
import com.quantumSamurais.hams.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Patient extends User {

    private String healthCardNumber;

    //TODO: UPDATE CACHE WHEN DOCTOR CANCELS APPOINTMENT
    private List<Appointment> appointments;
    private List<Doctor> currentDoctors;
    public Patient() {

    }

    //Used during account creation
    public Patient(String firstName, String lastName, char[] rawPassword, String emailAddress, String phoneNumber, String postalAddress, String healthCardNumber){
        super(firstName, lastName, rawPassword, emailAddress, phoneNumber, postalAddress);
        this.healthCardNumber = healthCardNumber;
        Database db = Database.getInstance();
        db.addSignUpRequest(this);
    }

    //Used for logins
    public Patient(String firstName, String lastName, ArrayList<Integer> hashedPassword, ArrayList<Integer> salt, String emailAddress, String phoneNumber, String postalAddress, String healthCardNumber){
        super(firstName, lastName, hashedPassword, salt, emailAddress, phoneNumber, postalAddress);
        this.healthCardNumber = healthCardNumber;
        Database db = Database.getInstance();
        appointments = new ArrayList<>();
//        this.appointments = db.getPatientAppointments();
    }

    @Override
    public void changeView(Context currentContext) {
        Intent patientView = new Intent(currentContext, PatientMainActivity.class);
        patientView.putExtra("patient", this);
        currentContext.startActivity(patientView);
    }
    public void addAppointment() {

    }

    public boolean appointmentCancellable(Appointment appointment) {
        return false;
    }

    public boolean cancelAppointment() {
        return false;
    }


    @Exclude
    public List<Appointment> getAppointments() {
        appointments.add(new Appointment(LocalDateTime.of(2024,12,20,1,2,3),LocalDateTime.of(2024,12,23,2,2,3),new Shift(),this, RequestStatus.PENDING));
        return appointments;
    }

    public String getHealthCardNumber() {
        return healthCardNumber;
    }

    public Patient setHealthCardNumber(String healthCardNumber) {
        this.healthCardNumber = healthCardNumber;
        return this;
    }
    public boolean acceptAppointment(Appointment app){
        //TODO: Implement this
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Patient patient = (Patient) o;
        return Objects.equals(healthCardNumber, patient.healthCardNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), healthCardNumber);
    }
    
}
