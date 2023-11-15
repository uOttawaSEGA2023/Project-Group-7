package com.quantumSamurais.hams.database;

import static com.google.android.gms.tasks.Tasks.await;
import static com.quantumSamurais.hams.user.UserType.DOCTOR;
import static com.quantumSamurais.hams.user.UserType.PATIENT;
import static com.quantumSamurais.hams.utils.ValidationType.EMAIL_ADDRESS;
import static com.quantumSamurais.hams.utils.ValidationType.EMPLOYEE_NUMBER;
import static com.quantumSamurais.hams.utils.ValidationType.HEALTH_CARD_NUMBER;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.quantumSamurais.hams.appointment.Appointment;
import com.quantumSamurais.hams.appointment.Shift;
import com.quantumSamurais.hams.database.callbacks.ResponseListener;
import com.quantumSamurais.hams.doctor.Doctor;
import com.quantumSamurais.hams.patient.Patient;
import com.quantumSamurais.hams.user.User;
import com.quantumSamurais.hams.user.UserType;
import com.quantumSamurais.hams.utils.ValidationType;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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

    private final Lock signUpLock;

    private Database() {
        signUpLock = new ReentrantLock();
    }

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
        //TODO: Send email
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

    // <editor-fold desc="Deliverable 3 & 4">
    public void addAppointmentRequest(Appointment appointment) {
        new Thread(() -> db.collection("users").document("software")
                .collection("appointments").add(appointment)).start();
    }

    public void approveAppointment(long appointmentID) {
        Appointment appointment = getAppointment(appointmentID);
        Shift shift = getShift(appointment.getShiftID());
        if (shift.takeAppointment(appointment)) {
            //If we successfully added the appointment to the shift
            db.runTransaction((Transaction.Function<Void>) transaction -> {
                updateShift(transaction, shift.getShiftID(), shift.getAppointments());
                updateAppointment(transaction, appointmentID, RequestStatus.APPROVED);
                return null;
            });
        }
    }

    public void rejectAppointment(long appointmentID) {
        db.runTransaction((Transaction.Function<Void>) transaction -> {
            updateAppointment(transaction, appointmentID, RequestStatus.REJECTED);
            return null;
        });
    }

    public void cancelAppointment(long appointmentID) {
        Appointment appointment = getAppointment(appointmentID);
        Shift shift = getShift(appointment.getShiftID());

        if (shift.cancelAppointment(appointment.getAppointmentID())) {
            //If we successfully removed the appointment from the shift
            db.runTransaction((Transaction.Function<Void>) transaction -> {
                updateShift(transaction, appointment.getShiftID(), shift.getAppointments());
                updateAppointment(transaction, appointmentID, RequestStatus.REJECTED);
                return null;
            });
        }


    }

    public Appointment getAppointment(long appointmentID) {
        Supplier<QuerySnapshot> findMatchingAppointment = () -> {
            QuerySnapshot appointment = null;
            try {
                appointment = await(db.collection("users").document("software").collection("appointments").whereEqualTo("appointmentID", appointmentID).get());
            } catch (ExecutionException | InterruptedException e) {
                Log.d("Database", "Something went wrong: " + e.getStackTrace());
            }
            if (appointment != null) {
                return appointment;
            }
            throw new NullPointerException("Appointment couldn't be initialized.");
        };
        CompletableFuture<QuerySnapshot> appointment = supplyAsync(findMatchingAppointment);
        //Takes the object obtained, and then transform into an appointment
        return appointment.join().getDocuments().get(0).toObject(Appointment.class);
    }


    private void updateShift(Transaction transaction, long shiftID, Map<Long, Appointment> updatedAppointments) {
        CollectionReference myShifts = db.collection("users").document("software").collection("shifts");
        Query query = myShifts.whereEqualTo("shiftID", shiftID);
        query.get().addOnCompleteListener(getShift -> {
            if (getShift.isSuccessful()) {
                for (QueryDocumentSnapshot document : getShift.getResult()) {
                    String documentId = document.getId();
                    DocumentReference docRef = myShifts.document(documentId);
                    transaction.update(docRef, "appointments", updatedAppointments);
                }
            } else {
                Log.d("Database", "Error getting documents: ", getShift.getException());
            }
        });
    }

    private void updateAppointment(Transaction transaction, long appointmentID, RequestStatus newRequestStatus){
        CollectionReference myAppointments = db.collection("users").document("software").collection("appointments");
        myAppointments.whereEqualTo("appointmentID", appointmentID).get().addOnCompleteListener(getAppointment -> {
            if (getAppointment.isSuccessful()){
                QuerySnapshot appointmentSnap = getAppointment.getResult(); //Should be unique
                for (QueryDocumentSnapshot singularAppointment: appointmentSnap){
                    String documentId = singularAppointment.getId();
                    DocumentReference docRef = myAppointments.document(documentId);
                    transaction.update(docRef, "requestStatus", newRequestStatus);
                }
            }
            else{
                Log.d("Database", "Error getting documents: ", getAppointment.getException());
            }

        });
    }






    public void addShift() {

    }

    public void deleteShift(long id) {

    }

    public void getPatientAppointments() {

    }

    public Shift getShift(long shiftID) {
        Supplier<QuerySnapshot> findMatchingShift = () -> {
            QuerySnapshot shift = null;
            try {
                shift = await(db.collection("users").document("software").collection("shifts").whereEqualTo("shiftID", shiftID).get());
            } catch (ExecutionException | InterruptedException e) {
                Log.d("Database", "Something went wrong: " + e.getStackTrace());
            }
            if (shift != null) {
                return shift;
            }
            throw new NullPointerException("Shift couldn't be initialized.");
        };
        CompletableFuture<QuerySnapshot> shift = supplyAsync(findMatchingShift);
        //Takes the object obtained, and then transform into an appointment
        return shift.join().getDocuments().get(0).toObject(Shift.class);
    }

    public ArrayList<Appointment> getDoctorAppointments(Doctor doctor) {
        Doctor theDoctor = getDoctor(doctor.getEmail());
        return theDoctor.getAppointments();
    }

    /**
     * A method to fetch the shifts from the database. IT RETURNS A COMPLETABLE FUTURE!
     * takes in a string, and returns a completableFuture.
     *
     * @param email : the email address of the doctor we're trying to access.
     **/
    public ArrayList<Shift> getShifts(String email) {
        Doctor doctor = getDoctor(email);
        return doctor.getShifts();
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
                Log.d("Database", email + ": this email is likely invalid. Error while fetching doctor from DB");
                throw new IllegalArgumentException("The email that was passed couldn't be found in db. (Or a problem occurred with the thread)");
            }
            return doctor;
        };

        CompletableFuture<QuerySnapshot> doctorSnapshot = supplyAsync(getMyDoctor);

        Doctor doctor = doctorSnapshot.join().getDocuments().get(0).toObject(Doctor.class);

        return doctor;
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
