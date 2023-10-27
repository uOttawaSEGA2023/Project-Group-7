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

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class DatabaseUtils {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public void addSignUpRequest(Patient user) {
        new Thread(() -> {
                try {
                   DocumentSnapshot software = Tasks.await(db.collection("users").document("software").get());
                   Long id = (Long) software.get("requestID");
                   if(id == null) {
                       Tasks.await(db.collection("users").document("software").update("requestID",0));
                       id = 0L;
                   }
                   db.collection("users").document("software").collection("requests").add(new Request(id,user,RequestStatus.PENDING));
                   db.collection("users").document("software").update("requestID",id+1);
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
        }).start();
    }
    public void addSignUpRequest(Doctor user) {
        new Thread(() -> {
            try {
                DocumentSnapshot software = Tasks.await(db.collection("users").document("software").get());
                Long id = (Long) software.get("requestID");
                if(id == null) {
                    Tasks.await(db.collection("users").document("software").update("requestID",0));
                    id = 0L;
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
                QuerySnapshot requests = Tasks.await(db.collection("users").document("software").collection("requests").whereEqualTo("id",id).get());
                if(requests.getDocuments().size() > 1)
                    return;
                for (QueryDocumentSnapshot document : requests) {
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
                    Long documentID = (Long) document.get("id");
                    assert documentID != null;
                    Log.d("requestID", String.valueOf(documentID));
                    if (documentID != id)
                        continue;
                    db.collection("users").document("software")
                            .collection("requests").document(document.getId()).update("status", RequestStatus.REJECTED);
                }
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    // <editor-fold desc="Deliverable 3 & 4">
    public void addAppointmentRequest() {

    }
    public void approveAppointment(long id) {

    }
    public void rejectAppointment(long id) {

    }
    public void cancelAppointment(long id) {

    }
    public void addShift() {

    }
    public void deleteShift(long id) {

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
