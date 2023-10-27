package com.quantumSamurais.hams.database;

import android.util.Log;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.quantumSamurais.hams.admin.Administrator;
import com.quantumSamurais.hams.database.callbacks.DoctorsResponseListener;
import com.quantumSamurais.hams.database.callbacks.PatientsResponseListener;
import com.quantumSamurais.hams.database.callbacks.RequestsResponseListener;
import com.quantumSamurais.hams.doctor.Doctor;
import com.quantumSamurais.hams.patient.Patient;
import com.quantumSamurais.hams.user.User;
import com.quantumSamurais.hams.user.UserType;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Arrays;
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
                    Log.d("Database Access Thread Error:", "Cause: " + e.getCause() + " Stack Trace: " + Arrays.toString(e.getStackTrace()));
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
                Log.d("Database Access Thread Error:", "Cause: " + e.getCause() + " Stack Trace: " + Arrays.toString(e.getStackTrace()));
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
                    Request current = document.toObject(Request.class);
                    db.collection("users").document("software")
                            .collection("requests").document(document.getId()).update("status", RequestStatus.APPROVED);
                    switch(current.getUserType()) {
                        case PATIENT:
                            db.collection("users").document("software")
                                    .collection("patients").add(current.getPatient());
                            break;
                        case DOCTOR:
                            db.collection("users").document("software")
                                    .collection("doctors").add(current.getDoctor());
                            break;
                    }
                    db.collection("users").document("software").collection("requests").document(document.getId()).delete();
                }
            } catch (ExecutionException | InterruptedException e) {
                Log.d("Database Access Thread Error:", "Cause: " + e.getCause() + " Stack Trace: " + Arrays.toString(e.getStackTrace()));
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
                    if (documentID != id)
                        continue;
                    db.collection("users").document("software")
                            .collection("requests").document(document.getId()).update("status", RequestStatus.REJECTED);
                }
            } catch (ExecutionException | InterruptedException e) {
                Log.d("Database Access Thread Error:", "Cause: " + e.getCause() + " Stack Trace: " + Arrays.toString(e.getStackTrace()));
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
                }
                listener.onSuccess(requestArrayList);
            } catch (ExecutionException | InterruptedException e) {
                Log.d("Database Access Thread Error:", "Cause: " + e.getCause() + " Stack Trace: " + Arrays.toString(e.getStackTrace()));
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
                }
                listener.onSuccess(patientArrayList);
            } catch (ExecutionException | InterruptedException e) {
                Log.d("Database Access Thread Error:", "Cause: " + e.getCause() + " Stack Trace: " + Arrays.toString(e.getStackTrace()));
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
                }
                listener.onSuccess(doctorstArrayList);
            } catch (ExecutionException | InterruptedException e) {
                Log.d("Database Access Thread Error:", "Cause: " + e.getCause() + " Stack Trace: " + Arrays.toString(e.getStackTrace()));
            }
        }).start();
    }

    public RequestStatus getStatus(String email, UserType userType) {
        boolean foundInPatients = checkUserInPatients("patients", email);
        boolean foundInDoctors = checkUserInDoctors("doctors", email);
        boolean foundInRequests = checkUserInRequests(email);

        if (foundInPatients || foundInDoctors) {
            return RequestStatus.APPROVED;
        } else if (foundInRequests) {
            RequestStatus requestStatus = getRequestStatusFromRequests(email);
            return requestStatus;
        }
        return RequestStatus.REJECTED;
    }

    private boolean checkUserInPatients(String collectionName, String email) {
        try {
            QuerySnapshot collectionSnapshot = Tasks.await(db.collection("users").document("software").collection("patients").whereEqualTo("email", email).get());
            return !collectionSnapshot.isEmpty();
        } catch (ExecutionException | InterruptedException e) {
            // Handle the exception.
            return false;
        }
    }
    private boolean checkUserInDoctors(String collectionName, String email) {
        try {
            QuerySnapshot collectionSnapshot = Tasks.await(db.collection("users").document("software").collection("doctors").whereEqualTo("email", email).get());
            return !collectionSnapshot.isEmpty();
        } catch (ExecutionException | InterruptedException e) {
            // Handle the exception.
            return false;
        }
    }

    private boolean checkUserInRequests(String email) {
        try {
            QuerySnapshot requestsSnapshot = Tasks.await(db.collection("users").document("software").collection("requests").whereEqualTo("email", email).get());
            return !requestsSnapshot.isEmpty();
        } catch (ExecutionException | InterruptedException e) {
            // Handle the exception.
            return false;
        }
    }

    private RequestStatus getRequestStatusFromRequests(String email) {
        try {
            QuerySnapshot requestsSnapshot = Tasks.await(db.collection("users").document("software").collection("requests").whereEqualTo("email", email).get());
            if (!requestsSnapshot.isEmpty()) {
                String requestStatus = requestsSnapshot.getDocuments().get(0).getString("status");
                if ("PENDING".equals(requestStatus)) {
                    return RequestStatus.PENDING;
                } else if ("REJECTED".equals(requestStatus)) {
                    return RequestStatus.REJECTED;
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            // Handle the exception.
        }
        return RequestStatus.REJECTED;
    }
}
