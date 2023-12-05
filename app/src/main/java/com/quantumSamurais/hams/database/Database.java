package com.quantumSamurais.hams.database;

import static com.google.android.gms.tasks.Tasks.await;
import static com.quantumSamurais.hams.user.UserType.DOCTOR;
import static com.quantumSamurais.hams.user.UserType.PATIENT;
import static com.quantumSamurais.hams.utils.ValidationType.EMAIL_ADDRESS;
import static com.quantumSamurais.hams.utils.ValidationType.EMPLOYEE_NUMBER;
import static com.quantumSamurais.hams.utils.ValidationType.HEALTH_CARD_NUMBER;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.quantumSamurais.hams.appointment.Appointment;
import com.quantumSamurais.hams.appointment.Shift;
import com.quantumSamurais.hams.database.callbacks.ResponseListener;
import com.quantumSamurais.hams.doctor.Doctor;
import com.quantumSamurais.hams.doctor.Specialties;
import com.quantumSamurais.hams.patient.Patient;
import com.quantumSamurais.hams.user.User;
import com.quantumSamurais.hams.user.UserType;
import com.quantumSamurais.hams.utils.ValidationType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Database {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static Database instance;

    private Long requestID;
    private ExecutorService myThreadPool = Executors.newFixedThreadPool(10);

    private final Lock signUpLock;

    private Database() {
        signUpLock = new ReentrantLock();
    }

    private List<Doctor> doctors;
    private List<Shift> shifts;

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    /**
     * @param user The patient to sign up.
     */
    public void addSignUpRequest(Patient user) {
        new Thread(() -> {
            // Acquire lock, this lock is used to make sure the request id is different between all requests.
            signUpLock.lock();
            try {
                DocumentSnapshot software = await(db.collection("users").document("software").get());
                requestID = (Long) software.get("requestID");
                if (requestID == null) {
                    await(db.collection("users").document("software").update("requestID", 0));
                    requestID = 0L;
                }
                db.collection("users").document("software").collection("requests").add(new Request(requestID, user, RequestStatus.PENDING));
                requestID++;
                db.collection("users").document("software").update("requestID", requestID);
            } catch (ExecutionException | InterruptedException e) {
                Log.d("Database Access Thread Error:", "Cause: " + e.getCause() + " Stack Trace: " + Arrays.toString(e.getStackTrace()));
            }
            // Release Lock
            signUpLock.unlock();
        }).start();
    }

    /**
     * @param user The doctor to sign up.
     */
    public void addSignUpRequest(Doctor user) {
        new Thread(() -> {
            // Acquire lock, this lock is used to make sure the request id is different between all requests.
            signUpLock.lock();
            try {
                if (requestID == null) {
                    DocumentSnapshot software = await(db.collection("users").document("software").get());
                    requestID = (Long) software.get("requestID");
                }
                if (requestID == null) {
                    await(db.collection("users").document("software").update("requestID", 0));
                    requestID = 0L;
                }
                db.collection("users").document("software")
                        .collection("requests").add(new Request(requestID, user, RequestStatus.PENDING));
                requestID++;
                db.collection("users").document("software").update("requestID", requestID);
            } catch (ExecutionException | InterruptedException e) {
                Log.d("Database Access Thread Error:", "Cause: " + e.getCause() + " Stack Trace: " + Arrays.toString(e.getStackTrace()));
            }
            // Release Lock
            signUpLock.unlock();
        }).start();
    }

    public Patient getPatientFromAppointmentID(long appointmentID) {

        Supplier<Patient> findPatientFromAppointmentID = () -> {
            QuerySnapshot appointments;
            try {
                appointments = await(db.collection("users").document("software").collection("appointments").get());
                for (QueryDocumentSnapshot appointmentDocument : appointments) {
                    Appointment properAppointment = appointmentDocument.toObject(Appointment.class);
                    if (properAppointment.getAppointmentID() == appointmentID) {
                        return properAppointment.getPatient();
                    }
                }
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            return null;
        };
        CompletableFuture<Patient> myPatient = supplyAsync(findPatientFromAppointmentID, myThreadPool);
        return myPatient.join();
    }

    /**
     * @param id Id of the request to approve
     */
    public void approveSignUpRequest(long id) {
        /*Supplier<User> findMatchingRequest = () -> {

            QuerySnapshot request = db.collection("users").document("software").
                    collection("requests").whereEqualTo("id", requestID).get().addOnCompleteListener(getRequestList ->{
                        if (getRequestList.isSuccessful()){
                            for (QueryDocumentSnapshot singularRequest : getRequestList.getResult()){
                                Request singularUserRequest = singularRequest.toObject(Request.class);
                                switch (singularUserRequest.getUserType()) {
                                    case PATIENT:
                                        Patient singularPatient = singularUserRequest.getPatient();
                                        return singularPatient;
                                    case DOCTOR:
                                        Doctor singularDoctor = singularUserRequest.getDoctor();
                                        return singularDoctor;
                                }
                            }
                        }else{
                            Log.d("Database", "An error occured while trying to fetch element: " + getRequestList.getException());
                        }
                    });

        };*/

        new Thread(() -> {
            try {
                QuerySnapshot requests = await(db.collection("users").document("software").collection("requests").whereEqualTo("id", id).get());
                if (requests.getDocuments().size() > 1)
                    return;
                for (QueryDocumentSnapshot document : requests) {
                    Request current = document.toObject(Request.class);
                    db.collection("users").document("software")
                            .collection("requests").document(document.getId()).update("status", RequestStatus.APPROVED);
                    switch (current.getUserType()) {
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

    /**
     * @param id Id of the request to reject
     */
    public void rejectSignUpRequest(long id) {
        new Thread(() -> {
            try {
                QuerySnapshot requests = await(db.collection("users").document("software").collection("requests").get());
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

    public void rateDoctorDB(long appointmentID, long shiftId, int rating) {
        DocumentReference softwareDocRef = db.collection("users").document("software");
        db.runTransaction(transaction -> {
            CollectionReference myShifts = db.collection("users").document("software").collection("shifts");
            myShifts.whereEqualTo("shiftID", shiftId).get().addOnCompleteListener(shift -> {
                if (shift.isSuccessful()) {
                    QuerySnapshot shiftSnap = shift.getResult();
                    Shift current = shiftSnap.toObjects(Shift.class).get(0);
                    CollectionReference myDoctors = db.collection("users").document("software").collection("doctors");
                    myDoctors.whereEqualTo("email", current.getDoctorEmailAddress()).get().addOnCompleteListener(getDoctor -> {
                        if (getDoctor.isSuccessful()) {
                            QuerySnapshot doctorSnap = getDoctor.getResult();
                            for (QueryDocumentSnapshot singleDoctor : doctorSnap) {
                                String doctorId = singleDoctor.getId();
                                DocumentReference docRef = myDoctors.document(doctorId);
                                db.runTransaction(transaction1 -> {
                                    transaction1.get(docRef);
                                    transaction1.update(docRef,"ratings",FieldValue.arrayUnion(rating));
                                    return null;
                                });
                            }
                        } else {
                            Log.d("Database", "Error getting documents: ", getDoctor.getException());
                        }
                    });
                } else {
                    Log.d("Database", "Error getting documents: ", shift.getException());
                }
            });
            return null;
        });
    }

    // <editor-fold desc="Deliverable 3 & 4">
    public void addAppointmentRequest(Appointment appointment, UpdateAfterBook callback) {
        DocumentReference softwareDocRef = db.collection("users").document("software");

        db.runTransaction((Transaction.Function<Long>) transaction ->{
                    // Get the current shiftID
                    DocumentSnapshot softwareSnapshot = transaction.get(softwareDocRef);
                    Long appointmentID = softwareSnapshot.getLong("appointmentID");
                    if (appointmentID == null) {
                        appointmentID = 0L;
                    }

                    // Initialize the shift ID
                    appointment.setAppointmentID(appointmentID);

                    // Increment the shiftID in the "software" document
                    transaction.update(softwareDocRef, "appointmentID", appointmentID + 1);

                    // Add the new shift to the "shifts" collection
                    DocumentReference newAppointmentRef = softwareDocRef.collection("appointments").document();
                    transaction.set(newAppointmentRef, appointment);

                    return appointmentID;
                }).addOnSuccessListener(new OnSuccessListener<Long>() {
                    @Override
                    public void onSuccess(Long result) {
                        Log.d("DatabaseAppointmentAdding", "Appointment #" + result + " was successfully added.");
                        // Tie the Appointment to a Shift
                        CollectionReference shifts = db.collection("users").document("software").collection("shifts");
                        shifts.whereEqualTo("shiftID", appointment.getShiftID()).get().addOnCompleteListener(shift -> {
                            if (shift.isSuccessful()) {
                                QuerySnapshot shiftSnap = shift.getResult();
                                for (QueryDocumentSnapshot singleShift : shiftSnap) {
                                    String shiftIDFirebase = singleShift.getId();
                                    DocumentReference docRef = shifts.document(shiftIDFirebase);
                                    db.runTransaction(addAppointment -> {
                                        addAppointment.update(docRef, "appointments", FieldValue.arrayUnion(appointment));
                                        return null;
                                    }).addOnCompleteListener(ignored -> {
                                        callback.update();
                                    });
                                }
                            } else {
                                Log.d("DatabaseAppointmentAddingFailure", "Error getting documents: ", shift.getException());
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("DatabaseAppointmentAddingFailure", "Adding appointments to DB failed.", e);
                    }
                });
    }

    public void listForShiftsChange(UpdateAfterBook shiftChangedCB) {
        db.collection("users").document("software").collection("shifts")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("Listening Pat Apps", "Listen failed.", e);
                            return;
                        }
                        assert value != null;

                        shiftChangedCB.update();
                    }
                });
    }

    public interface UpdateAfterBook {
        void update();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void approveAppointment(long appointmentID) {
        getAppointment(appointmentID).thenAccept(appointment -> {
            getShift(appointment.getShiftID()).thenAccept(shift -> {
                if (shift != null) {
                    //Asynchronous Handling of Shift
                    if (shift.takeAppointment(appointment)) {
                        Log.d("Do we even enter here", "bool: " + shift.takeAppointment(appointment));
                        //If we successfully added the appointment to the shift
                        CollectionReference shifts = db.collection("users").document("software").collection("shifts");
                        shifts.whereEqualTo("shiftID", shift.getShiftID()).get()
                                .addOnSuccessListener(shiftTask -> {
                                    DocumentReference shiftReference = shifts.document(shiftTask.getDocuments().get(0).getId());
                                    addAppointmentToShift(shiftReference, appointment);

                                }).addOnFailureListener(e -> {
                                    // Handle transaction failure
                                    Log.e("approveAppointment", "Transaction failed: ", e);
                                });
                        //Then modify the related appointment
                        CollectionReference appointments = db.collection("users").document("software").collection("appointments");
                        shifts.whereEqualTo("appointmentID", appointmentID).get()
                                .addOnSuccessListener(shiftTask -> {
                                    DocumentReference appointmentReference = appointments.document(shiftTask.getDocuments().get(0).getId());
                                    editAppointmentStatus(appointmentReference, RequestStatus.APPROVED);
                                }).addOnFailureListener(e -> {
                                    // Handle transaction failure
                                    Log.e("approveAppointment", "Transaction failed: ", e);
                                });
                    }
                } else {
                    // Currently if no shift is found, do nothing.
                }
            }).exceptionally(e -> {
                e.printStackTrace();
                return null;
            });

        });

    }

    private void editAppointmentStatus(DocumentReference toAppointment, RequestStatus newStatus){
        db.runTransaction(transaction -> {
            transaction.update(toAppointment, "appointmentStatus", newStatus);
            return null;
        });
    }

    private void editAppointmentStatusFromShiftReference(DocumentReference shiftReference, Appointment appointmentToRemove){
        db.runTransaction(transaction ->  {
            Shift shift  = transaction.get(shiftReference).toObject(Shift.class);
            ArrayList<Appointment> appointments = (ArrayList<Appointment>) shift.getAppointments();
            Iterator iterator = appointments.iterator();
            while (iterator.hasNext()){

            }
            for (Appointment appoinment : appointments){
                if (appoinment.getAppointmentID() == appointmentToRemove.getAppointmentID()){

                }
            }

            return null;
        });
    }

    public void rejectAppointment(long appointmentID) {

        getAppointment(appointmentID).thenAccept(appointment -> {
            if (appointment != null) {
                getShift(appointment.getShiftID()).thenAccept(shift -> {
                    if (shift != null && shift.cancelAppointment(appointment.getAppointmentID())) {
                        // If we would be able to successfully remove, then do so to reflect changes
                        CollectionReference shifts = db.collection("users").document("software").collection("shifts");
                        shifts.whereEqualTo("shiftID", shift.getShiftID()).get()
                                .addOnSuccessListener(shiftTask -> {
                                    DocumentReference shiftReference = shifts.document(shiftTask.getDocuments().get(0).getId());
                                    editAppointmentStatusFromShiftReference(shiftReference, appointment);
                                }).addOnFailureListener(e -> {
                                    // Handle transaction failure
                                    Log.e("cancelAppointment", "Transaction failed: ", e);
                                });
                        //Then modify the related appointment
                        CollectionReference appointments = db.collection("users").document("software").collection("appointments");
                        shifts.whereEqualTo("appointmentID", appointmentID).get()
                                .addOnSuccessListener(shiftTask -> {
                                    DocumentReference appointmentReference = appointments.document(shiftTask.getDocuments().get(0).getId());
                                    editAppointmentStatus(appointmentReference, RequestStatus.APPROVED);
                                }).addOnFailureListener(e -> {
                                    // Handle transaction failure
                                    Log.e("cancelAppointment", "Transaction failed: ", e);
                                });

                    }
                }).exceptionally(e -> {
                    Log.e("cancelAppointment", "Error getting shift: ", e);
                    return null;
                });
            }
        }).exceptionally(e -> {
            Log.e("cancelAppointment", "Error getting appointment: ", e);
            return null;
        });

        /*
        CollectionReference myAppointments = db.collection("users").document("software").collection("appointments");
        myAppointments.whereEqualTo("appointmentID", appointmentID).get().addOnCompleteListener(getAppointment -> {
            if (getAppointment.isSuccessful()) {
                QuerySnapshot appointmentSnap = getAppointment.getResult(); //Should be unique
                for (QueryDocumentSnapshot singularAppointment : appointmentSnap) {
                    String documentId = singularAppointment.getId();
                    DocumentReference docRef = myAppointments.document(documentId);
                    editAppointmentStatus(docRef, RequestStatus.REJECTED);
                }

            } else {
                Log.d("Database", "Error getting documents: ", getAppointment.getException());
            }

        })*/
        ;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void cancelAppointment(long appointmentID) {
        getAppointment(appointmentID).thenAccept(appointment -> {
            if (appointment != null) {
                getShift(appointment.getShiftID()).thenAccept(shift -> {
                    if (shift != null && shift.cancelAppointment(appointment.getAppointmentID())) {
                        // If we would be able to successfully remove, then do so to reflect changes
                        CollectionReference shifts = db.collection("users").document("software").collection("shifts");
                        shifts.whereEqualTo("shiftID", shift.getShiftID()).get()
                                .addOnSuccessListener(shiftTask -> {
                                    DocumentReference shiftReference = shifts.document(shiftTask.getDocuments().get(0).getId());
                                    removeAppointmentFromShift(shiftReference, appointment);
                                }).addOnFailureListener(e -> {
                                    // Handle transaction failure
                                    Log.e("cancelAppointment", "Transaction failed: ", e);
                                });
                        //Then modify the related appointment
                        CollectionReference appointments = db.collection("users").document("software").collection("appointments");
                        appointments.whereEqualTo("appointmentID", appointmentID).get()
                                .addOnSuccessListener(shiftTask -> {
                                    DocumentReference appointmentReference = appointments.document(shiftTask.getDocuments().get(0).getId());
                                    db.runTransaction(transaction -> {
                                        transaction.get(appointmentReference);
                                        transaction.delete(appointmentReference);
                                        return null;
                                    });
                                }).addOnFailureListener(e -> {
                                    // Handle transaction failure
                                    Log.e("cancelAppointment", "Transaction failed: ", e);
                                });

                    }
                }).exceptionally(e -> {
                    Log.e("cancelAppointment", "Error getting shift: ", e);
                    return null;
                });
            }
        }).exceptionally(e -> {
            Log.e("cancelAppointment", "Error getting appointment: ", e);
            return null;
        });
    }


    public CompletableFuture<Appointment> getAppointment(long appointmentID) {
        CompletableFuture<Appointment> appointmentCompletableFuture = new CompletableFuture<>();
        CollectionReference appointments = db.collection("users").document("software").collection("appointments");
        appointments.whereEqualTo("appointmentID", appointmentID).get().addOnCompleteListener(
                appointmentTask -> {
                    if(appointmentTask.isSuccessful() && !appointmentTask.getResult().isEmpty()){
                        Appointment appointmentToGet = appointmentTask.getResult().getDocuments().get(0).toObject(Appointment.class);
                        appointmentCompletableFuture.complete(appointmentToGet);
                    }
                }).addOnFailureListener(e -> {
                    Log.e("AppointmentGettingError", "Print: " + e.getStackTrace());
        });
        return appointmentCompletableFuture;

    }

    private void removeAppointmentFromShift(DocumentReference toShift, Appointment appointmentToRemove){
        db.runTransaction(transaction -> {
            transaction.update(toShift, "appointments", FieldValue.arrayRemove(appointmentToRemove));
            return  null;
        });


    }
    private void addAppointmentToShift(DocumentReference toShift, Appointment appointmentToAdd) {
        db.runTransaction(transaction -> {
            transaction.update(toShift, "appointments", FieldValue.arrayUnion(appointmentToAdd));
            return  null;
        });
    }

    private void updateAppointment(Transaction transaction, long appointmentID, RequestStatus newRequestStatus) {

    }

    public void updateAcceptsByDefault(String doctorEmail, boolean value) {
        CollectionReference myDoctors = db.collection("users").document("software").collection("doctors");
        myDoctors.whereEqualTo("email", doctorEmail).get().addOnCompleteListener(getDoctor -> {
            if (getDoctor.isSuccessful()) {
                QuerySnapshot doctorSnap = getDoctor.getResult();
                for (QueryDocumentSnapshot singleDoctor : doctorSnap) {
                    String doctorId = singleDoctor.getId();
                    DocumentReference docRef = myDoctors.document(doctorId);
                    updateAcceptsByDefault(docRef, value);
                }
            } else {
                Log.d("Database", "Error getting documents: ", getDoctor.getException());
            }
        });
    }

    private void updateAcceptsByDefault(DocumentReference docRef, boolean value) {
        db.runTransaction((Transaction.Function<Void>) transaction -> {
            transaction.update(docRef, "acceptsAppointmentsByDefault", value);
            return null;
        }).addOnSuccessListener(aVoid -> {
            Log.d("transaction success", "value successfully changed");
            // Transaction success logic
        }).addOnFailureListener(e -> {
            Log.d("transaction failure", "value not changed");
            // Transaction failure logic
        });
    }


    public void addShift(Shift shift) {
        DocumentReference softwareDocRef = db.collection("users").document("software");

        db.runTransaction((Transaction.Function<Long>) transaction ->{
            // Get the current shiftID
            DocumentSnapshot softwareSnapshot = transaction.get(softwareDocRef);
            Long shiftID = softwareSnapshot.getLong("shiftID");
            if (shiftID == null) {
                shiftID = 0L;
            }

            // Initialize the shift ID
            shift.setShiftID(shiftID);

            // Increment the shiftID in the "software" document
            transaction.update(softwareDocRef, "shiftID", shiftID + 1);

            // Add the new shift to the "shifts" collection
            DocumentReference newShiftRef = softwareDocRef.collection("shifts").document();
            transaction.set(newShiftRef, shift);

            return shiftID; // Return the shiftID that the shift was set as.
        }).addOnSuccessListener(new OnSuccessListener<Long>() {
                    @Override
                    public void onSuccess(Long idLong) {
                        // Tie the Shift to a Doctor
                        //We can only add a shift to a doctor, if the doctor was properly added to DB in the first place
                        CollectionReference myDoctors = db.collection("users").document("software").collection("doctors");
                        myDoctors.whereEqualTo("email", shift.getDoctorEmailAddress()).get().addOnCompleteListener(getDoctor -> {
                            if (getDoctor.isSuccessful()) {
                                QuerySnapshot doctorSnap = getDoctor.getResult();
                                for (QueryDocumentSnapshot singleDoctor : doctorSnap) {
                                    String doctorId = singleDoctor.getId();
                                    DocumentReference docRef = myDoctors.document(doctorId);
                                    updateDoctorShifts(docRef, idLong);
                                }
                            } else {
                                Log.d("Database", "Error getting documents: ", getDoctor.getException());
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("DatabaseShiftAdding", "Adding shift to DB failed.", e);
                    }
                });
    }


    private void updateDoctorShifts(DocumentReference docRef, Long idLong) {
        db.runTransaction((Transaction.Function<Void>) transaction -> {
            transaction.update(docRef, "shiftIDs", FieldValue.arrayUnion(idLong));
            return null;
        }).addOnSuccessListener(aVoid -> {
            Log.d("transaction success", "value successfully changed");
            // Transaction success logic
        }).addOnFailureListener(e -> {
            Log.d("transaction failure", "value not changed");
            // Transaction failure logic
        });

    }




    public void deleteShift(long shiftID, Context calledFrom) {
        CollectionReference shifts = db.collection("users").document("software").collection("shifts");
        CollectionReference doctors = db.collection("users").document("software").collection("doctors");

        //Delete From Doctor first, to the shift can be purged gracefully.
        getShift(shiftID).thenAccept(shift -> {
            if (shift != null) {
                //Only runs deletion if shift is empty
                if (shift.getAppointments().isEmpty()){
                    String doctorEmail =  shift.getDoctorEmailAddress();
                    doctors.whereEqualTo("email", doctorEmail).get().addOnSuccessListener(getDoctor -> {
                        for (QueryDocumentSnapshot singularDoctor : getDoctor){
                            String doctorDocumentID = singularDoctor.getId();
                            DocumentReference doctorToUntie = doctors.document(doctorDocumentID);
                            deleteShiftFromDoctor(doctorToUntie, shift.getShiftID());
                        }
                    });

                    //Delete DB
                    shifts.whereEqualTo("shiftID", shiftID).get().addOnCompleteListener(
                            getShift -> {
                                if(getShift.isSuccessful()){
                                    for (QueryDocumentSnapshot document : getShift.getResult()){
                                        String documentID = document.getId();
                                        DocumentReference shiftToDelete = shifts.document(documentID);
                                        deleteShiftFromDB(shiftToDelete);
                                    }
                                }
                            }
                    );

                }
                else{
                    Toast.makeText(calledFrom, "Cannot delete this shift because it is tied to appointments.", Toast.LENGTH_SHORT).show();
                    return;
                }

            } else {
                Log.d("ShiftID", "Somehow user tried to delete a shift that does not exist");
            }
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });


    }

    public void deleteShiftFromDB(DocumentReference shiftToDelete){
        db.runTransaction(transaction -> {
            transaction.delete(shiftToDelete);
            return null;
        }).addOnSuccessListener(aVoid -> {
            Log.d("transaction success", "shift successfully delete from DB.");
            // Transaction success logic
        }).addOnFailureListener(e -> {
            Log.d("transaction failure", "value not changed");
            // Transaction failure logic
        });
    }

    public void deleteShiftFromDoctor(DocumentReference doctorRef, long shiftIDToDelete) {
        db.runTransaction(transaction -> {
            // Read the current state of the doctor document
            transaction.update(doctorRef, "shiftIDs", FieldValue.arrayRemove(shiftIDToDelete));
            return null;
        }).addOnSuccessListener(aVoid -> {
            Log.d("transaction success", "Shift successfully deleted from doctor.");
        }).addOnFailureListener(e -> {
            Log.d("transaction failure", "Failed to delete shift from doctor", e);
        });
    }
    public void listenForShifts(String email, ShiftCallback cb) {
        db.collection("users").document("software").collection("shifts")
                .whereEqualTo("doctorEmailAddress", email)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("Listening Pat Apps", "Listen failed.", e);
                            return;
                        }
                        assert value != null;

                        cb.callback((ArrayList<Shift>) value.toObjects(Shift.class));
                    }
                });
    }

    public interface ShiftCallback{
        void callback(ArrayList<Shift> shifts);
    }

    public void listenForAppointmentChangeOfStatus(long shiftID, AppointmentCallback cb) {
        db.collection("users").document("software").collection("appointments")
                .whereEqualTo("shiftID", shiftID)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("Listening Pat Apps", "Listen failed.", e);
                            return;
                        }
                        assert value != null;

                        cb.callback((ArrayList<Appointment>) value.toObjects(Appointment.class));
                    }
                });
    }

    public void getAllBookable(Patient p, Specialties spec, LocalDate date, AppointmentCallback callback) {

        db.collection("users").document("software").collection("shifts").get().addOnSuccessListener(value -> {
           ArrayList<Appointment> appointments = new ArrayList<>();
           for(Shift shift: value.toObjects(Shift.class)) {
              db.collection("users").document("software").collection("doctors").
                      whereEqualTo("email", shift.getDoctorEmailAddress()).get()
                      .addOnSuccessListener(doc -> {
                            Doctor doctor = doc.toObjects(Doctor.class).get(0);
                            LocalDateTime startTime = shift.getStartTime().truncatedTo(ChronoUnit.SECONDS);
                            LocalDateTime endTime = shift.getEndTime().truncatedTo(ChronoUnit.SECONDS);
                            LocalDateTime currTime = startTime;
                            LocalDate startDate = shift.getStartTime().toLocalDate();
                            LocalDate endDate = shift.getEndTime().toLocalDate();

                            while(!currTime.isEqual(endTime)) {
                                Appointment next = new Appointment(currTime,currTime.plusMinutes(30),shift,doctor.getFirstName(),doctor.getSpecialties(),p,RequestStatus.PENDING);
                                if(doctor.getSpecialties().contains(spec) && (date.isEqual(startDate) || date.isEqual(endDate) || (date.isAfter(startDate) && date.isBefore(endDate)))) {
                                    //TODO: Uncomment this check
                                    if(shift.takeAppointment(next)) {
                                        appointments.add(next);
                                    }
                                }
                                currTime = currTime.plusMinutes(30).truncatedTo(ChronoUnit.SECONDS);
                            }
                      }).addOnCompleteListener(ignored -> {
                  callback.callback(appointments);
              });
           }
        });
    }

    public void listenForAppointmentCancelPatient(Patient patient, AppointmentCallback cb) {
        db.collection("users").document("software").collection("appointments")
                .whereEqualTo("patient", patient)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("Listening Pat Apps", "Listen failed.", e);
                            return;
                        }
                        assert value != null;

                        cb.callback((ArrayList<Appointment>) value.toObjects(Appointment.class));
                    }
                });
    }

    public interface AppointmentCallback {
        void callback(ArrayList<Appointment> apps);
    }


    public void getPatientAppointments(Patient patient, AppointmentCallback callback) {
        DocumentReference softwareRef = db.collection("users").document("software");
        softwareRef.collection("appointments").whereEqualTo("patient",patient).get().addOnSuccessListener(result -> {
            callback.callback((ArrayList<Appointment>) result.toObjects(Appointment.class));
        });
    }

    public CompletableFuture<Shift> getShift(long shiftID) {
        CompletableFuture<Shift> shiftFromDB = new CompletableFuture<>();
        CollectionReference shifts = db.collection("users").document("software").collection("shifts");

        shifts.whereEqualTo("shiftID", shiftID).get().addOnCompleteListener(getShift -> {
            if (getShift.isSuccessful() && !getShift.getResult().isEmpty()) {
                // Assuming shiftID is unique and only one document is expected
                DocumentSnapshot singularShift = getShift.getResult().getDocuments().get(0);
                Shift shift = singularShift.toObject(Shift.class);
                Log.d("what is the dang shift", shift.toString());
                shiftFromDB.complete(shift); // Set the result for the CompletableFuture
            } else {
                shiftFromDB.complete(null); // Complete with null if no document is found or query is not successful
            }
        }).addOnFailureListener(e -> shiftFromDB.completeExceptionally(e)); // Handle any exceptions

        return shiftFromDB;
    }


    public ArrayList<Appointment> getDoctorAppointments(String email, AppointmentsListener appointmentListener) {
        ArrayList<Appointment> appointments = new ArrayList<>();
        db.collection("users").document("software").collection("shifts").
                whereEqualTo("doctorEmailAddress", email).get().addOnSuccessListener(
                        getShifts -> {
                            if (!getShifts.isEmpty()){
                                //Parse all the appointments
                                for (DocumentSnapshot shiftDocument : getShifts.getDocuments()){
                                    Shift workerShift = shiftDocument.toObject(Shift.class);
                                    ArrayList<Appointment> appointmentsFromShift = (ArrayList<Appointment>) workerShift.getAppointments();
                                    for (Appointment appointment: appointmentsFromShift){
                                        appointments.add(appointment);
                                    }
                                }

                                //Call the listener
                                appointmentListener.onAppointmentsReceived(appointments);

                            }
                        }
                ).addOnFailureListener(e -> {
                    return;
                });
        return null;
    }
    public interface AppointmentsListener{
        public void onAppointmentsReceived(ArrayList<Appointment> appointments);
    }

    /**
     * A method to fetch the shifts from the database. IT RETURNS A COMPLETABLE FUTURE!
     * takes in a string, and returns a completableFuture.
     *
     * @param email : the email address of the doctor we're trying to access.
     **/
    public void getShifts(ArrayList<Long> shiftIDs, ShiftsCallback callback) {
        if (shiftIDs.isEmpty()){
            callback.onReceiveShifts(new ArrayList<>());
        }
        else{
            Database.getInstance().db.collection("users").document("software").
                    collection("shifts").
                    whereIn("shiftID", shiftIDs).get().addOnCompleteListener(getShifts -> {

                        if (getShifts.isSuccessful()){
                            ArrayList<Shift> shiftsDup = new ArrayList<>();

                            for (QueryDocumentSnapshot shiftDocument : getShifts.getResult()){
                                shiftsDup.add(shiftDocument.toObject(Shift.class));
                            }
                            callback.onReceiveShifts(shiftsDup);
                        }
                    });

        }

    }

    public interface ShiftsCallback{
        void onReceiveShifts(ArrayList<Shift> shifts);
    }
    // </editor-fold>

    // Deliverable 2

    /**
     * @param listener Returns array list of all sign up requests by calling listener.onSuccess;
     */
    public void getSignUpRequests(ResponseListener<ArrayList<Request>> listener) {
        new Thread(() -> {
            ArrayList<Request> requestArrayList = new ArrayList<>();
            signUpLock.lock();
            try {
                QuerySnapshot requests = await(db.collection("users").document("software").collection("requests").get());
                for (QueryDocumentSnapshot document : requests) {
                    requestArrayList.add(document.toObject(Request.class));
                }
                listener.onSuccess(requestArrayList);
            } catch (ExecutionException | InterruptedException e) {
                listener.onFailure(e);
                Log.d("Database Access Thread Error:", "Cause: " + e.getCause() + " Stack Trace: " + Arrays.toString(e.getStackTrace()));
            }
            signUpLock.unlock();
        }).start();
    }

    /**
     * @param listener Returns array list of all patients by calling listener.onSuccess;
     */
    public void getPatients(ResponseListener<ArrayList<Patient>> listener) {
        new Thread(() -> {
            ArrayList<Patient> patientArrayList = new ArrayList<>();
            try {
                QuerySnapshot patients = await(db.collection("users").document("software").collection("patients").get());
                for (QueryDocumentSnapshot document : patients) {
                    patientArrayList.add(document.toObject(Patient.class));
                }
                listener.onSuccess(patientArrayList);
            } catch (ExecutionException | InterruptedException e) {
                listener.onFailure(e);
                Log.d("Database Access Thread Error:", "Cause: " + e.getCause() + " Stack Trace: " + Arrays.toString(e.getStackTrace()));
            }
        }).start();
    }

    public Doctor getDoctor(String email) {
        Supplier<QuerySnapshot> getMyDoctor = () -> {
            QuerySnapshot doctor = null;
            try {
                doctor = await(db.collection("users").document("software").collection("doctors").whereEqualTo("email", email).get());
            } catch (ExecutionException | InterruptedException e) {
                Log.d("Database", "Error fetching doctor from DB", e);
                throw new IllegalArgumentException("Error fetching doctor from DB", e);
            }
            return doctor;
        };

        CompletableFuture<QuerySnapshot> doctorSnapshot = supplyAsync(getMyDoctor);

        List<DocumentSnapshot> documents = doctorSnapshot.join().getDocuments();

        if (documents.isEmpty()) {
            // Handle case where no doctor is found for the given email
            Log.d("Database", "No doctor found for email: " + email);
            return null;
        }

        return documents.get(0).toObject(Doctor.class);
    }

    /**
     * @param listener Returns array list of all doctor by calling listener.onSuccess;
     */
    public void getDoctors(ResponseListener<ArrayList<Doctor>> listener) {
        new Thread(() -> {
            try {
                ArrayList<Doctor> doctorstArrayList = new ArrayList<>();
                QuerySnapshot doctors = await(db.collection("users").document("software").collection("doctors").get());
                for (QueryDocumentSnapshot document : doctors) {
                    doctorstArrayList.add(document.toObject(Doctor.class));
                }
                listener.onSuccess(doctorstArrayList);
            } catch (ExecutionException | InterruptedException e) {
                listener.onFailure(e);
                Log.d("Database Access Thread Error:", "Cause: " + e.getCause() + " Stack Trace: " + Arrays.toString(e.getStackTrace()));
            }
        }).start();
    }

    /**
     * @param email    The patients email
     * @param listener Returns the patient object by calling listener.onSuccess(Patient);
     */
    public void getPatient(String email, ResponseListener<Patient> listener) {
        new Thread(() -> {
            try {
                QuerySnapshot patients = await(db.collection("users").document("software").collection("patients").whereEqualTo("email", email).get());
                listener.onSuccess(patients.getDocuments().get(0).toObject(Patient.class));
            } catch (ExecutionException | InterruptedException e) {
                listener.onFailure(e);
                Log.d("Database Access Thread Error:", "Cause: " + e.getCause() + " Stack Trace: " + Arrays.toString(e.getStackTrace()));
            }
        }).start();
    }

    /**
     * @param email    The doctors email
     * @param listener Returns the doctor object by calling listener.onSuccess(Patient);
     */
    public void getDoctor(String email, ResponseListener<Doctor> listener) {
        new Thread(() -> {
            try {
                QuerySnapshot doctors = await(db.collection("users").document("software").collection("doctors").whereEqualTo("email", email).get());
                listener.onSuccess(doctors.getDocuments().get(0).toObject(Doctor.class));
            } catch (ExecutionException | InterruptedException e) {
                listener.onFailure(e);
                Log.d("Database Access Thread Error:", "Cause: " + e.getCause() + " Stack Trace: " + Arrays.toString(e.getStackTrace()));
            }
        }).start();
    }

    /**
     * @param email    Email of the user
     * @param userType Type of user
     * @param listener Returns the request status by calling listener.onSuccess;
     */
    public void getRequestStatus(String email, UserType userType, ResponseListener<RequestStatus> listener) {
        new Thread(() -> listener.onSuccess(getStatus(email, userType))).start();
    }


    /**
     * @param email    Email of the user
     * @param userType Type of user
     * @return The status of the users signUp request, returns null if the user cannot be found. Always Returns Approved for admin.
     */
    private RequestStatus getStatus(String email, UserType userType) {
        boolean foundInPatients = checkUserIsInUsers(PATIENT, EMAIL_ADDRESS, email);
        boolean foundInDoctors = checkUserIsInUsers(DOCTOR, EMAIL_ADDRESS, email);
        boolean foundInRequests = checkUserIsInRequests(userType, EMAIL_ADDRESS, email);
        if (userType == UserType.ADMIN)
            return RequestStatus.APPROVED;

        if ((foundInPatients && userType == PATIENT) || (foundInDoctors && userType == DOCTOR)) {
            return RequestStatus.APPROVED;
        } else if (foundInRequests) {
            return getRequestStatusFromRequests(email);
        }
        return null;
    }

    public boolean checkUserIsInUsers(UserType userType, ValidationType checkToDo, String fieldToValidate) {
        //Sanity Check
        if (fieldToValidate == null || userType == null || checkToDo == null) {
            throw new NullPointerException("Please do not pass null arguments to this function");
        }
        //Coherence Checks
        else if (userType == PATIENT && checkToDo == EMPLOYEE_NUMBER) {
            throw new IllegalArgumentException("Patients cannot have employee numbers");
        } else if (userType == DOCTOR && checkToDo == HEALTH_CARD_NUMBER) {
            throw new IllegalArgumentException("Doctors cannot have health card numbers");
        }
        boolean itIs = true;
        switch (checkToDo) {
            case EMAIL_ADDRESS:
                itIs = checkEmailIsInUsers(userType, fieldToValidate);
                break;
            case PHONE_NUMBER:
                itIs = checkPhoneIsInUsers(userType, fieldToValidate);
                break;
            case HEALTH_CARD_NUMBER:
                itIs = checkHealthCardNumberIsInUsers(fieldToValidate);
                break;
            case EMPLOYEE_NUMBER:
                itIs = checkEmployeeNumberIsInUsers(fieldToValidate);
                break;
        }
        return itIs;
    }

    private boolean checkEmailIsInUsers(UserType userType, String email) {
        try {
            // Define a lambda task to run along the completable future.
            Supplier<QuerySnapshot> isEmailInUsers = () -> {
                try {
                    //return of the lambda.
                    switch (userType) {
                        case PATIENT:
                            return await(db.collection("users").document("software").collection("patients").whereEqualTo("email", email).get());
                        case DOCTOR:
                            return await(db.collection("users").document("software").collection("doctors").whereEqualTo("email", email).get());
                        case ADMIN:
                            throw new IllegalArgumentException("Admin is not stored in database.");
                    }
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                //Shouldn't be reached.
                return null;
            };
            //Supply both the task and the thread to use, to the completable future using supplyAsync
            CompletableFuture<QuerySnapshot> querySnap = supplyAsync(isEmailInUsers);

            //Use get.
            return !querySnap.get().isEmpty();
        } catch (ExecutionException | InterruptedException e) {
            Log.d("Zone of Despair", "Something happened during fetching for the data");
            throw new RuntimeException("Something went wrong while trying to get the data");
        }
    }

    private boolean checkPhoneIsInUsers(UserType userType, String phone) {
        try {
            Supplier<QuerySnapshot> isPhoneInUsers = () -> {
                try {
                    switch (userType) {
                        case PATIENT:
                            return await(db.collection("users").document("software").collection("patients").whereEqualTo("phone", phone).get());
                        case DOCTOR:
                            return await(db.collection("users").document("software").collection("doctors").whereEqualTo("phone", phone).get());
                        case ADMIN:
                            throw new IllegalArgumentException("Admin is not stored in database.");
                    }
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                //Shouldn't be reached.
                return null;
            };
            //Supply both the task and the thread to use, to the completable future using supplyAsync
            CompletableFuture<QuerySnapshot> querySnap = supplyAsync(isPhoneInUsers);

            //Use get.
            return !querySnap.get().isEmpty();
        } catch (ExecutionException | InterruptedException e) {
            Log.d("Zone of Despair", "Something happened during fetching for the data");
            throw new RuntimeException("Something went wrong while trying to get the data");
        }
    }

    private boolean checkHealthCardNumberIsInUsers(String healthCardNumber) {
        try {
            // Define a lambda task to run along the completable future.
            Supplier<QuerySnapshot> isHealthCardNumberInPatients = () -> {
                try {
                    //return of the lambda.
                    return await(db.collection("users").document("software").collection("patients").whereEqualTo("healthCardNumber", healthCardNumber).get());
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                //Shouldn't be reached.
            };
            //Supply both the task and the thread to use, to the completable future using supplyAsync
            CompletableFuture<QuerySnapshot> querySnap = supplyAsync(isHealthCardNumberInPatients);

            //Use get.
            return !querySnap.get().isEmpty();
        } catch (ExecutionException | InterruptedException e) {
            Log.d("Zone of Despair", "Something happened during fetching for the data");
            throw new RuntimeException("Something went wrong while trying to get the data");
        }
    }

    private boolean checkEmployeeNumberIsInUsers(String employeeNumber) {
        try {
            // Define a lambda task to run along the completable future.
            Supplier<QuerySnapshot> isEmployeeNumberInDoctors = () -> {
                try {
                    //return of the lambda.
                    return await(db.collection("users").document("software").collection("doctors").whereEqualTo("employeeNumber", employeeNumber).get());
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                //Shouldn't be reached.
            };
            //Supply both the task and the thread to use, to the completable future using supplyAsync
            CompletableFuture<QuerySnapshot> querySnap = supplyAsync(isEmployeeNumberInDoctors);

            //Use get.
            return !querySnap.get().isEmpty();
        } catch (ExecutionException | InterruptedException e) {
            Log.d("Zone of Despair", "Something happened during fetching for the data");
            throw new RuntimeException("Something went wrong while trying to get the data");
        }
    }


    public boolean checkUserIsInRequests(UserType userType, ValidationType checkToDo, String fieldToValidate) {
        //Sanity Check
        if (fieldToValidate == null || userType == null || checkToDo == null) {
            throw new NullPointerException("Please do not pass null arguments to this function");
        }
        //Coherence Checks
        else if (userType == PATIENT && checkToDo == EMPLOYEE_NUMBER) {
            throw new IllegalArgumentException("Patients cannot have employee numbers");
        } else if (userType == DOCTOR && checkToDo == HEALTH_CARD_NUMBER) {
            throw new IllegalArgumentException("Doctors cannot have health card numbers");
        }
        boolean itIs = true;
        switch (checkToDo) {
            case EMAIL_ADDRESS:
                itIs = checkEmailIsInRequests(userType, fieldToValidate);
                break;
            case PHONE_NUMBER:
                itIs = checkPhoneIsInRequests(userType, fieldToValidate);
                break;
            case HEALTH_CARD_NUMBER:
                itIs = checkHealthCardNumberIsInRequests(fieldToValidate);
                break;
            case EMPLOYEE_NUMBER:
                itIs = checkEmployeeNumberIsInRequests(fieldToValidate);
                break;
        }
        return itIs;
    }

    private Boolean checkEmailIsInRequests(UserType userType, String email) {
        Supplier<Boolean> isEmailInRequests = () -> {
            try {
                List<DocumentSnapshot> allRequests = await(db.collection("users").document("software").collection("requests").get()).getDocuments();
                for (DocumentSnapshot request : allRequests) {
                    Request currentRequest = request.toObject(Request.class);
                    UserType currentRequestType = currentRequest.getUserType();
                    if (userType == currentRequestType) {
                        if (currentRequest.getEmail().equals(email)) {
                            return true; //email is in requests.
                        }
                    }

                }
            } catch (Exception e) {
                Log.d("Zone of despair", "Please never reach this");
                throw new RuntimeException("Something didn't go well while checking to database");
            }
            return false;
        };
        CompletableFuture<Boolean> isEmailInRequest = supplyAsync(isEmailInRequests);

        return isEmailInRequest.join();
    }

    private boolean checkHealthCardNumberIsInRequests(String healthCardNumber) {
        Supplier<Boolean> isHealthCardNumberInRequests = () -> {
            try {
                List<DocumentSnapshot> allRequests = await(db.collection("users").document("software").collection("requests").get()).getDocuments();
                for (DocumentSnapshot request : allRequests) {
                    Request currentRequest = request.toObject(Request.class);
                    UserType currentRequestType = currentRequest.getUserType();
                    if (currentRequestType == PATIENT) {
                        if (currentRequest.getHealthCardNumber().equals(healthCardNumber)) {
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
                Log.d("Zone of despair", "Please never reach this");
                throw new RuntimeException("Something went wrong while checking to DB");
            }
            return false;
        };
        CompletableFuture<Boolean> healthCardNumberInRequests = supplyAsync(isHealthCardNumberInRequests);
        return healthCardNumberInRequests.join();
    }

    private boolean checkEmployeeNumberIsInRequests(String employeeNumber) {
        Supplier<Boolean> isEmployeeNumberInRequests = () -> {
            try {
                List<DocumentSnapshot> allRequests = await(db.collection("users").document("software").collection("requests").get()).getDocuments();
                for (DocumentSnapshot request : allRequests) {
                    Request currentRequest = request.toObject(Request.class);
                    UserType currentRequestType = currentRequest.getUserType();
                    if (currentRequestType == DOCTOR) {
                        if (currentRequest.getEmployeeNumber().equals(employeeNumber)) {
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
                Log.d("Zone of despair", "Please never reach this");
                throw new RuntimeException("Something went wrong while checking to DB");
            }
            return false;
        };
        CompletableFuture<Boolean> employeeNumberInRequests = supplyAsync(isEmployeeNumberInRequests);
        return employeeNumberInRequests.join();
    }

    private boolean checkPhoneIsInRequests(UserType userType, String phoneNumber) {
        Supplier<Boolean> isPhoneNumberInRequests = () -> {
            try {
                List<DocumentSnapshot> allRequests = await(db.collection("users").document("software").collection("requests").get()).getDocuments();
                for (DocumentSnapshot request : allRequests) {
                    Request currentRequest = request.toObject(Request.class);
                    UserType currentRequestType = currentRequest.getUserType();
                    if (userType == currentRequestType) {
                        if (currentRequest.getPhoneNumber().equals(phoneNumber)) {
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
                Log.d("Zone of despair", "Please never reach this");
                throw new RuntimeException("Something went wrong while checking to DB");
            }
            return false;
        };
        CompletableFuture<Boolean> isPhoneNumberInRequest = supplyAsync(isPhoneNumberInRequests);
        return isPhoneNumberInRequest.join();
    }

    private RequestStatus getRequestStatusFromRequests(String email) {
        try {
            QuerySnapshot requestsSnapshot = await(db.collection("users").document("software").collection("requests").whereEqualTo("email", email).get());
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

    /* implementation of a sendEmail method meant to be used to send an email to users
     *   this confirms whether or not they have been granted access to the system.
     *   This implementation uses the an instance of the user and RequestStatus;
     *   however, these two variables can be swapped out given a change in the implementation.
     *
     *
     * @param */

    public static void sendEmail(User user, RequestStatus status) {
        try {
            new Thread(() -> {
                // should be put into an on click for both the approve and reject buttons.
                final String username = "controlgroup72023@gmail.com";
                final String password = "tgke vwtf jepo shjs"; //this is an app-specific password.
                String msgToSend = "", msgSbjct = ""; // message to send and message subject.
                switch (status) {
                    case APPROVED:
                        msgToSend = "Hello " + user.getLastName() + " " + user.getFirstName() + "," + "\n" + "\n" +
                                "This is just to inform you that your request to the Best Health Care Appointment app on the planet has been approved.\n" +
                                "We are glad to now count you among our members!" + "\n" + "\n" +
                                "Best, " + "Admin";
                        msgSbjct = "Welcome to the Best Health Care Appointment app on the Planet! :D";
                        break;
                    case REJECTED:
                        msgToSend = "Hello " + user.getLastName() + " " + user.getFirstName() + "," + "\n" + "\n" +
                                "We are sad to inform you, that your request to the best Health Care Appointment app on the planet has been rejected.\n" +
                                "If you'd like to inquire further about the reasons of our refusal, please contact the admin at: " + "\n" +
                                "(819)-123-1234" +
                                " Standard call charges or fees may apply when using this phone number." + "\n" + "\n" +
                                "Best, " + "Admin";
                        msgSbjct = "We are sorry to inform you, that your account registration has been denied. >:0 ";
                        break;
                }
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", "smtp.gmail.com"); // set as smtp.gmail.com because of email domain.
                // change if we the email has another domain.
                props.put("mail.smtp.port", "587"); // 587 most common port for sending emails.


                Session session = Session.getInstance(props,
                        new javax.mail.Authenticator() {
                            @Override
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(username, password);
                            }
                        });

                try { // try block for sending the email.
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(username));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
                    // we set recipients of the email as the user that we verify or reject.
                    message.setSubject(msgSbjct);
                    message.setText(msgToSend);
                    Transport.send(message);


                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        } catch (Exception e) {
            Log.d("View ", "So that happened: " + e.getStackTrace());
        }

    }
}