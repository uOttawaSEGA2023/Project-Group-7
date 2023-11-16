package com.quantumSamurais.hams.appointment;

import static java.time.temporal.ChronoUnit.MINUTES;

import com.quantumSamurais.hams.database.Database;
import com.quantumSamurais.hams.database.RequestStatus;
import com.quantumSamurais.hams.patient.Patient;

import java.time.LocalDateTime;

public class Appointment {
    Database db;
    RequestStatus appointmentStatus;
    static long APPOINTMENT_ID = 0;
    LocalDateTime startTime, endTime;
    long appointmentID;
    private boolean pastAppointmentFlag;

    Shift shift;
    Patient myPatient;
    public Appointment(LocalDateTime startTime, LocalDateTime endTime, Shift shift, Patient patient){
        db = Database.getInstance();
        if (inputsAreValid(startTime, endTime, shift, patient)){
            //Set the time
            this.startTime = startTime;
            this.endTime = endTime;
            this.shift = shift;
            appointmentID = APPOINTMENT_ID;
            APPOINTMENT_ID++;
            appointmentStatus = RequestStatus.PENDING;
        }
        db.addAppointmentRequest(this);

    }

    public Appointment(LocalDateTime startTime, LocalDateTime endTime, Shift shift, Patient patient, RequestStatus requestStatus){
        db = Database.getInstance();
        if (inputsAreValid(startTime, endTime, shift, patient)){
            //Set the time
            this.startTime = startTime;
            this.endTime = endTime;

            appointmentID = APPOINTMENT_ID;
            APPOINTMENT_ID++;
            this.appointmentStatus = requestStatus;

        }


    }

    public boolean inputsAreValid(LocalDateTime startTime, LocalDateTime endTime, Shift shift, Patient patient){
        long interval = MINUTES.between(startTime, endTime);
        boolean isIncrementOf30Minutes = interval % 30 == 0 ? true:false;
        if (shift == null || patient == null){
            throw new NullPointerException("Please do not pass null objects.");
        }
        if (endTime.isBefore(startTime) || endTime.isEqual(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        if (!isIncrementOf30Minutes){
            throw new IllegalArgumentException("The passed startTime and endTime are not 30 minutes apart.");
        }
        return true;
    }

    public boolean overlaps(Appointment someAppointment){
        if (startTime.isBefore(someAppointment.getEndTime()) && someAppointment.getStartTime().isBefore(endTime)){
            return true;
        } return false;
    }

    public Patient getMyPatient(){
        return myPatient;
    }

    public RequestStatus getAppointmentStatus(){
        return appointmentStatus;
    }

    public long getAppointmentID(){
        return appointmentID;
    }

//    public boolean tieAppointment(long shiftID, Doctor doctor, Patient patient){
//        if (doctor.hasThisShift(shiftID)){
//            myDoctor = doctor;
//            myPatient = patient;
//            return true;
//        }
//        return false;
//    }

    public LocalDateTime getStartTime(){
        return startTime;
    }

    public LocalDateTime getEndTime(){
        return endTime;
    }

    public long getShiftID(){
        return shift.getShiftID();
    }

    public boolean appointmentIsPassed(){
        pastAppointmentFlag = endTime.isBefore(LocalDateTime.now());
        return  pastAppointmentFlag;
    }
}

