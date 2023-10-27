package com.quantumSamurais.hams.database;

import android.util.Log;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.quantumSamurais.hams.database.callbacks.DoctorsResponseListener;
import com.quantumSamurais.hams.database.callbacks.PatientsResponseListener;
import com.quantumSamurais.hams.database.callbacks.RequestsResponseListener;
import com.quantumSamurais.hams.doctor.Doctor;
import com.quantumSamurais.hams.patient.Patient;
import com.quantumSamurais.hams.user.UserWrappedDB;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class DatabaseUtils {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static int currentSignUpRequestID;
    // Deliverable 2
    public DatabaseUtils() {
        //Tasks.await(db.collection("users").document("software").collection("requests").get().addOnSuccessListener(this));
    }
    public void addSignUpRequest(UserWrappedDB user) {
        new Thread(() -> {
                try {
                   DocumentSnapshot software = Tasks.await(db.collection("users").document("software").get());
                   Long id = (Long) software.get("requestID");
                   if(id == null) {
                       Tasks.await(db.collection("users").document("software").update("requestID",0));
                   }
                   db.collection("users").document("software").collection("requests").add(new Request(id,user,RequestStatus.PENDING));
                   db.collection("users").document("software").update("requestID",id+1);
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
        }).start();
    }
    public void approveSignUpRequest(long id) {
        new Thread(() -> {
            try {
                QuerySnapshot requests = Tasks.await(db.collection("users").document("software").collection("requests").get());
                for (QueryDocumentSnapshot document : requests) {
                    Long id2 = (Long) document.get("id");
                    Log.d("requestID", String.valueOf(id2));
                    if (id2 != id)
                        continue;
                    db.collection("users").document("software")
                            .collection("requests").document(document.getId()).update("status", RequestStatus.APPROVED);
                }
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
    public void rejectSignUpRequest(long id) {
        new Thread(() -> {
            try {
                QuerySnapshot requests = Tasks.await(db.collection("users").document("software").collection("requests").get());
                for (QueryDocumentSnapshot document : requests) {
                    long id2 = (long) document.get("id");
                    Log.d("requestID", String.valueOf(id2));
                    if (id2 != id)
                        continue;
                    db.collection("users").document("software")
                            .collection("requests").document(document.getId()).update("status", RequestStatus.DENIED);
                }
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
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
        new Thread(() -> {
            ArrayList<Request> requestArrayList = new ArrayList<>();
            try {
                QuerySnapshot requests = Tasks.await(db.collection("users").document("software").collection("requests").get());
                for (QueryDocumentSnapshot document : requests) {
                    requestArrayList.add(document.toObject(Request.class));
                    listener.onSuccess(requestArrayList);
                }
            } catch (ExecutionException | InterruptedException e) {
                Log.d("Get Sign Up Request", "Something went wrong" + e.getCause());
            }
            catch (Exception e){
                Log.d("Get Sign Up Request", "Something went wrong ~ " + e.getCause() + " ~ " + e.getStackTrace());
            }
        }).start();
    }
    public void getPatients(PatientsResponseListener listener) {
        new Thread(() -> {
            ArrayList<Patient> patientArrayList = new ArrayList<>();
            try {
                QuerySnapshot patients = Tasks.await(db.collection("users").document("software").collection("patients").get());
                for (QueryDocumentSnapshot document : patients) {
                    patientArrayList.add(document.toObject(Patient.class));
                    listener.onSuccess(patientArrayList);
                }
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
    public void getDoctors(DoctorsResponseListener listener) {
        new Thread(() -> {
            try {
                ArrayList<Doctor> doctorstArrayList = new ArrayList<>();
                QuerySnapshot doctors = Tasks.await(db.collection("users").document("software").collection("doctors").get());
                for (QueryDocumentSnapshot document : doctors) {
                    doctorstArrayList.add(document.toObject(Doctor.class));
                    listener.onSuccess(doctorstArrayList);
                }
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
