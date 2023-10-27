package com.quantumSamurais.hams.database;

import com.quantumSamurais.hams.doctor.Doctor;
import com.quantumSamurais.hams.patient.Patient;
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
}
