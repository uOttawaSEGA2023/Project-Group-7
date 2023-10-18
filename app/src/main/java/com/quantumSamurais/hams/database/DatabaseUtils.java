package com.quantumSamurais.hams.database;

import com.google.firebase.firestore.FirebaseFirestore;
import com.quantumSamurais.hams.database.callbacks.DatabaseResponseListener;
import com.quantumSamurais.hams.database.callbacks.DoctorsResponseListener;
import com.quantumSamurais.hams.database.callbacks.PatientsResponseListener;
import com.quantumSamurais.hams.database.callbacks.RequestsResponseListener;
import com.quantumSamurais.hams.doctor.Doctor;
import com.quantumSamurais.hams.patient.Patient;

public class DatabaseUtils {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static int currentSignUpRequestID;
    // Deliverable 2
    public DatabaseUtils() {
        //Tasks.await(db.collection("users").document("software").collection("requests").get().addOnSuccessListener(this));
    }
    public void addPatientSignUpRequest(Patient patient) {

    }

    public void addDoctorSignUpRequest(Doctor doctor) {

    }
    public void approveSignUpRequest(int id) {

    }
    public void rejectSignUpRequest(int id) {

    }

    // <editor-fold desc="Deliverable 3 & 4">
    public void addAppointmentRequest() {

    }
    public void approveAppointment(int id) {

    }
    public void rejectAppointment(int id) {

    }
    public void cancelAppointment(int id) {

    }
    public void addShift() {

    }
    public void deleteShift(int id) {

    }
    public void getPatientAppointments() {

    }
    public void getDoctorAppointments() {

    }
    public void getShifts() {

    }
    // </editor-fold>

    // Deliverable 2
    public void getSignUpRequests(RequestsResponseListener listener) {

    }
    public void getPatients(PatientsResponseListener listener) {

    }
    public void getDoctors(DoctorsResponseListener listener) {

    }
}
