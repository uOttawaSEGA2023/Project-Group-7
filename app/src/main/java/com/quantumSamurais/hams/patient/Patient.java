package com.quantumSamurais.hams.patient;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.firestore.Exclude;
import com.quantumSamurais.hams.appointment.Appointment;
import com.quantumSamurais.hams.appointment.Shift;
import com.quantumSamurais.hams.database.Database;
import com.quantumSamurais.hams.database.RequestStatus;
import com.quantumSamurais.hams.database.callbacks.ResponseListener;
import com.quantumSamurais.hams.doctor.Doctor;
import com.quantumSamurais.hams.patient.activities.PatientMainActivity;
import com.quantumSamurais.hams.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Patient extends User {

    private String healthCardNumber;

    //TODO: UPDATE CACHE WHEN DOCTOR CANCELS APPOINTMENT
    private List<Appointment> appointments;
    private List<Appointment> availableAtDate;

    @Exclude
    private LocalDate date;

    public Patient() {
        Database db = Database.getInstance();
        this.appointments = db.getPatientAppointments(this);
    }

    //Used during account creation
    public Patient(String firstName, String lastName, char[] rawPassword, String emailAddress, String phoneNumber, String postalAddress, String healthCardNumber){
        super(firstName, lastName, rawPassword, emailAddress, phoneNumber, postalAddress);
        this.healthCardNumber = healthCardNumber;
        this.appointments = new ArrayList<>();
        Database db = Database.getInstance();
        db.addSignUpRequest(this);
        date = LocalDate.now();
    }

    //Used for DEBUGGING
    public Patient(String firstName, String lastName, ArrayList<Integer> hashedPassword, ArrayList<Integer> salt, String emailAddress, String phoneNumber, String postalAddress, String healthCardNumber){
        super(firstName, lastName, hashedPassword, salt, emailAddress, phoneNumber, postalAddress);
        this.healthCardNumber = healthCardNumber;
        this.appointments = new ArrayList<>();
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
    public LocalDate getDate() {
        return date;
    }

    public void setAppointmentDate(LocalDate date) {

    }
    @Exclude
    public List<Appointment> getAppointments() {
        return appointments;
    }

    public String getHealthCardNumber() {
        return healthCardNumber;
    }

    public Patient setHealthCardNumber(String healthCardNumber) {
        this.healthCardNumber = healthCardNumber;
        return this;
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
