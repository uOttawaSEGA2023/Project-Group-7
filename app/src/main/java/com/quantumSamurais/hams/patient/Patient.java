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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class Patient extends User implements ResponseListener<ArrayList<Doctor>> {

    private String healthCardNumber;

    //TODO: UPDATE CACHE WHEN DOCTOR CANCELS APPOINTMENT
    private List<Appointment> appointments;
    private List<Appointment> availableAtDate;

    @Exclude
    private LocalDate date;

    public Patient() {
        Database db = Database.getInstance();
        this.appointments = db.getPatientAppointments(this);
        db.getDoctors(this);

    }

    //Used during account creation
    public Patient(String firstName, String lastName, char[] rawPassword, String emailAddress, String phoneNumber, String postalAddress, String healthCardNumber){
        super(firstName, lastName, rawPassword, emailAddress, phoneNumber, postalAddress);
        this.healthCardNumber = healthCardNumber;
        this.appointments = new ArrayList<>();
        Database db = Database.getInstance();
        db.addSignUpRequest(this);
        date = LocalDate.now();
        this.getShifts();
    }

    //Used for logins
    public Patient(String firstName, String lastName, ArrayList<Integer> hashedPassword, ArrayList<Integer> salt, String emailAddress, String phoneNumber, String postalAddress, String healthCardNumber){
        super(firstName, lastName, hashedPassword, salt, emailAddress, phoneNumber, postalAddress);
        this.healthCardNumber = healthCardNumber;
        Database db = Database.getInstance();
        List<Appointment> test= db.getPatientAppointments(this);
        if(test != null) {
            this.appointments = test;
        } else {
            this.appointments = new ArrayList<>();
        }
        date = LocalDate.now();
        this.getShifts();
    }

    @Override
    public void onSuccess(ArrayList<Doctor> data) {
        List<Appointment> apps = new ArrayList<>();
        for(Doctor d: data) {
            for(Shift s :  d.getShifts()) {
                if(s.getStartTime().toLocalDate().equals(date)) {
                    // TODO: Add apps for shift
                };
            }
        }
        this.availableAtDate = apps;
    }
    @Override
    public void onFailure(Exception error) {
        Log.e("Patient: 70", Objects.requireNonNull(error.getCause()).toString());
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
    public void getShifts() {
        Database.getInstance().getDoctors(this);
    }

    public void setAppointmentDate(LocalDate date) {

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
