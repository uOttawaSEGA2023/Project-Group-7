package com.quantumSamurais.hams.database;

import android.util.Log;

import com.quantumSamurais.hams.doctor.Doctor;
import com.quantumSamurais.hams.patient.Patient;
import com.quantumSamurais.hams.user.User;
import com.quantumSamurais.hams.user.UserType;

public class Request {
    private long id;
    private UserType userType;

    private Doctor doctor;
    private Patient patient;

    private RequestStatus status;

    Request() {

    }

    public Request(long id, Patient patient, RequestStatus status) {
        if (patient == null || status == null){
            throw new NullPointerException("Do not pass null arguments to this constructor");
        }
        this.id = id;
        this.userType = UserType.PATIENT;
        this.status = status;
        this.patient = patient;
        this.doctor = null;
    }
    public Request(long id, Doctor doctor, RequestStatus status) {
        if (doctor == null || status == null){
            throw new NullPointerException("Do not pass null arguments to this constructor");
        }
        this.id = id;
        this.userType = UserType.DOCTOR;
        this.status = status;
        this.patient = null;
        this.doctor = doctor;
    }

    public long getID() {
        return id;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public Patient getPatient() {
        return patient;
    }
    public Doctor getDoctor() {
        return doctor;
    }

    public UserType getUserType() {
        return userType;
    }

    //These getters are defined since really nifty and convenient for validation checks.
    public String getEmail(){
        Doctor someDoctor = getDoctor();
        Patient somePatient = getPatient();
        if (someDoctor != null) {
            return someDoctor.getEmail();
        }
        return somePatient.getEmail();
    }
    public String getPhoneNumber(){
        Doctor someDoctor = getDoctor();
        Patient somePatient = getPatient();
        if (someDoctor != null) {
            return someDoctor.getPhone();
        }
        return somePatient.getPhone();

    }
    public String getHealthCardNumber(){
        Patient somePatient = getPatient();
        if (somePatient != null) {
            return somePatient.getHealthCardNumber();
        }
        Log.d("Request","This request does not hold a patient.");
        return "";

    }
    public String getEmployeeNumber(){
        Doctor someDoctor = getDoctor();
        if (someDoctor != null) {
            return someDoctor.getEmployeeNumber();
        }
        Log.d("Request","This request does not hold a doctor.");
        return "";

    }
    public static User getUserFromRequest(Request request){
        if (request == null){
            throw new NullPointerException("Please do not pass a null object to this method");
        }
        switch(request.getUserType()){
            case DOCTOR:
                return request.getDoctor();
            case PATIENT:
                return request.getPatient();
            case ADMIN:
                // We shouldn't get here
        }
        // We shouldn't get here either, since request shouldn't be null.
        return null;
    }


}
