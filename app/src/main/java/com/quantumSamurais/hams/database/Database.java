package com.quantumSamurais.hams.database;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.quantumSamurais.hams.database.callbacks.ResponseListener;
import com.quantumSamurais.hams.doctor.Doctor;
import com.quantumSamurais.hams.patient.Patient;
import com.quantumSamurais.hams.user.User;
import com.quantumSamurais.hams.user.UserType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
        if(instance == null) {
            instance = new Database();
        }
        return instance;
    }

    /**
     *
     * @param user The patient to sign up.
     */
    public void addSignUpRequest(Patient user) {
        new Thread(() -> {
            // Acquire lock, this lock is used to make sure the request id is different between all requests.
               signUpLock.lock();
                try {
                   DocumentSnapshot software = Tasks.await(db.collection("users").document("software").get());
                   if(requestID == null)
                       requestID = (Long) software.get("requestID");
                   if(requestID == null) {
                       Tasks.await(db.collection("users").document("software").update("requestID",0));
                       requestID = 0L;
                   }
                   db.collection("users").document("software")
                           .collection("requests").add(new Request(requestID,user,RequestStatus.PENDING));
                   requestID++;
                   db.collection("users").document("software").update("requestID",requestID);
                } catch (ExecutionException | InterruptedException e) {
                    Log.d("Database Access Thread Error:", "Cause: " + e.getCause() + " Stack Trace: " + Arrays.toString(e.getStackTrace()));
                }
                // Release Lock
               signUpLock.unlock();
        }).start();
    }
    /**
     *
     * @param user The doctor to sign up.
     */
    public void addSignUpRequest(Doctor user) {
        new Thread(() -> {
            // Acquire lock, this lock is used to make sure the request id is different between all requests.
            signUpLock.lock();
            try {
                if(requestID == null) {
                    DocumentSnapshot software = Tasks.await(db.collection("users").document("software").get());
                    requestID = (Long) software.get("requestID");
                }
                if(requestID == null) {
                    Tasks.await(db.collection("users").document("software").update("requestID",0));
                    requestID = 0L;
                }
                db.collection("users").document("software")
                        .collection("requests").add(new Request(requestID,user,RequestStatus.PENDING));
                requestID++;
                db.collection("users").document("software").update("requestID",requestID);
            } catch (ExecutionException | InterruptedException e) {
                Log.d("Database Access Thread Error:", "Cause: " + e.getCause() + " Stack Trace: " + Arrays.toString(e.getStackTrace()));
            }
            // Release Lock
            signUpLock.unlock();
        }).start();
    }

    /**
     *
     * @param id Id of the request to approve
     */
    public void approveSignUpRequest(long id) {
        //TODO: Send email
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
    /**
     *
     * @param id Id of the request to reject
     */
    public void rejectSignUpRequest(long id) {
        //TODO: Send email
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
    /**
     *
     * @param listener Returns array list of all sign up requests by calling listener.onSuccess;
     */
    public void getSignUpRequests(ResponseListener<ArrayList<Request>> listener) {
        new Thread(() -> {
            ArrayList<Request> requestArrayList = new ArrayList<>();
            try {
                QuerySnapshot requests = Tasks.await(db.collection("users").document("software").collection("requests").get());
                for (QueryDocumentSnapshot document : requests) {
                    requestArrayList.add(document.toObject(Request.class));
                }
                listener.onSuccess(requestArrayList);
            } catch (ExecutionException | InterruptedException e) {
                listener.onFailure(e);
                Log.d("Database Access Thread Error:", "Cause: " + e.getCause() + " Stack Trace: " + Arrays.toString(e.getStackTrace()));
            }
        }).start();
    }

    /**
     *
     * @param listener Returns array list of all patients by calling listener.onSuccess;
     */
    public void getPatients(ResponseListener<ArrayList<Patient>> listener) {
        new Thread(() -> {
            ArrayList<Patient> patientArrayList = new ArrayList<>();
            try {
                QuerySnapshot patients = Tasks.await(db.collection("users").document("software").collection("patients").get());
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
    /**
     *
     * @param listener Returns array list of all doctor by calling listener.onSuccess;
     */
    public void getDoctors(ResponseListener<ArrayList<Doctor>> listener) {
        new Thread(() -> {
            try {
                ArrayList<Doctor> doctorstArrayList = new ArrayList<>();
                QuerySnapshot doctors = Tasks.await(db.collection("users").document("software").collection("doctors").get());
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
     *
     * @param email The patients email
     * @param listener Returns the patient object by calling listener.onSuccess(Patient);
     */
    public void getPatient(String email, ResponseListener<Patient> listener) {
        new Thread(() -> {
            try {
                QuerySnapshot doctors = Tasks.await(db.collection("users").document("software").collection("patients").whereEqualTo("email",email).get());
                listener.onSuccess(doctors.getDocuments().get(0).toObject(Patient.class));
            } catch (ExecutionException | InterruptedException e) {
                listener.onFailure(e);
                Log.d("Database Access Thread Error:", "Cause: " + e.getCause() + " Stack Trace: " + Arrays.toString(e.getStackTrace()));
            }
        }).start();
    }
    /**
     *
     * @param email The doctors email
     * @param listener Returns the doctor object by calling listener.onSuccess(Patient);
     */
    public void getDoctor(String email, ResponseListener<Doctor> listener) {
        new Thread(() -> {
            try {
                QuerySnapshot doctors = Tasks.await(db.collection("users").document("software").collection("doctors").whereEqualTo("email",email).get());
                listener.onSuccess(doctors.getDocuments().get(0).toObject(Doctor.class));
            } catch (ExecutionException | InterruptedException e) {
                listener.onFailure(e);
                Log.d("Database Access Thread Error:", "Cause: " + e.getCause() + " Stack Trace: " + Arrays.toString(e.getStackTrace()));
            }
        }).start();
    }

    /**
     * @param email Email of the user
     * @param userType Type of user
     * @param listener  Returns the request status by calling listener.onSuccess;
     */
    public void getRequestStatus(String email, UserType userType, ResponseListener<RequestStatus> listener) {
        new Thread(()-> listener.onSuccess(getStatus(email,userType))).start();
    }


    /**
     * @param email Email of the user
     * @param userType Type of user
     * @return The status of the users signUp request, returns null if the user cannot be found. Always Returns Approved for admin.
    * */
    private RequestStatus getStatus(String email, UserType userType) {
        boolean foundInPatients = checkUserInCollection("patients", email);
        boolean foundInDoctors = checkUserInCollection("doctors", email);
        boolean foundInRequests = checkUserInRequests(email);
        if(userType == UserType.ADMIN)
            return RequestStatus.APPROVED;

        if ((foundInPatients && userType == UserType.PATIENT) || (foundInDoctors && userType == UserType.DOCTOR)) {
            return RequestStatus.APPROVED;
        } else if (foundInRequests) {
            return getRequestStatusFromRequests(email);
        }
        return null;
    }

    private boolean checkUserInCollection(String collectionName, String email) {
        try {
            QuerySnapshot collectionSnapshot = Tasks.await(db.collection("users").document("software").collection(collectionName).whereEqualTo("email", email).get());
            return !collectionSnapshot.isEmpty();
        } catch (ExecutionException | InterruptedException e) {
            // Handle the exception.
            return false;
        }
    }

    private boolean checkUserInRequests(String email) {
        //TODO: Double Check this actually pulls the right request
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

    /* implementation of a sendEmail method meant to be used to send an email to users
     *   this confirms whether or not they have been granted access to the system.
     *   This implementation uses the an instance of the user and RequestStatus;
     *   however, these two variables can be swapped out given a change in the implementation.
     *
     *
     * @param */

    public static void sendEmail(Context context, User user, RequestStatus status) {
        try {
            new Thread(() -> {
                // should be put into an on click for both the approve and reject buttons.
                final String username = "admin@gmail.com"; // template admin email for the time being.
                final String password = "12345678"; // '' password.
                String msgToSend = "", msgSbjct = ""; // message to send and message subject.
                switch (status) {
                    case APPROVED:
                        msgToSend = "Your request to the health Management app has been approved.\n";
                        msgSbjct = "your account has been approved! :D";
                        break;
                    case REJECTED:
                        msgToSend = "Your request to the health Management app has been denied. \n ";
                        msgSbjct = "your account has been denied. >:0 ";
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
                    Toast.makeText(context, "patient has been notified.", Toast.LENGTH_LONG).show();


                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
        catch (Exception e){
            Log.d("View ", "So that happened: " + e.getStackTrace());
        }

    }
}
