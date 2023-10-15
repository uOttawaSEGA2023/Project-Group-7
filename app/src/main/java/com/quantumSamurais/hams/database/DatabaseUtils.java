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
    private static int currentRequestID;
    public DatabaseUtils() {
        //Tasks.await(db.collection("users").document("software").collection("requests").get().addOnSuccessListener(this));
    }
    public void addPatientRequest(Patient patient) {

    }

    public void addDoctorRequest(Doctor doctor) {

    }
    public void approveRequest(int id) {

    }
    public void rejectRequest(int id) {

    }
    public void getRequests(RequestsResponseListener listener) {

    }
    public void getPatients(PatientsResponseListener listener) {

    }

    public void getDoctors(DoctorsResponseListener listener) {

    }
}
