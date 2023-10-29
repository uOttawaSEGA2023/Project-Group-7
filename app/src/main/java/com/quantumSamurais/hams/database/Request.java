package com.quantumSamurais.hams.database;

import com.quantumSamurais.hams.doctor.Doctor;
import com.quantumSamurais.hams.patient.Patient;
import com.quantumSamurais.hams.user.User;
import com.quantumSamurais.hams.user.UserType;

public class Request {
    long id;
    UserType userType;

    Doctor doctor;
    Patient patient;

    RequestStatus status;

    Request() {

    }

    public Request(long id, Patient patient, RequestStatus status) {
        this.id = id;
        this.userType = UserType.PATIENT;
        this.status = status;
        this.patient = patient;
        this.doctor = null;
    }
    public Request(long id, Doctor doctor, RequestStatus status) {
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
