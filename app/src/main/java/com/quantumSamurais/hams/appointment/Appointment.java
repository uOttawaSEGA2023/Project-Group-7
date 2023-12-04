package com.quantumSamurais.hams.appointment;

import static java.time.temporal.ChronoUnit.MINUTES;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.quantumSamurais.hams.database.RequestStatus;
import com.quantumSamurais.hams.patient.Patient;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class Appointment implements Serializable  {
    RequestStatus appointmentStatus;
    LocalDateTime startTime, endTime;
    long appointmentID;
    long shiftID;
    private boolean pastAppointmentFlag;

    Patient patient;
    public Appointment(LocalDateTime startTime, LocalDateTime endTime, Shift shift, Patient patient){
        if (inputsAreValid(startTime, endTime, shift, patient)){
            //Set the time
            this.startTime = startTime;
            this.endTime = endTime;
            this.shiftID = shift.getShiftID();
            appointmentStatus = RequestStatus.PENDING;
        }
    }

    public Appointment(LocalDateTime startTime, LocalDateTime endTime, Shift shift, Patient patient, RequestStatus requestStatus){
        if (inputsAreValid(startTime, endTime, shift, patient)){
            //Set the time
            this.startTime = startTime;
            this.endTime = endTime;
            this.appointmentStatus = requestStatus;

        }
    }

    public boolean inputsAreValid(LocalDateTime startTime, LocalDateTime endTime, Shift shift, Patient patient){
        long interval = MINUTES.between(startTime, endTime);
        boolean isIncrementOf30Minutes = interval % 30 == 0;
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
        if (startTime.isBefore(someAppointment.getEndTimeLocalDate()) && someAppointment.getStartTimeLocalDate().isBefore(endTime)){
            return true;
        } return false;
    }

    public Patient getPatient(){
        return patient;
    }

    public RequestStatus getAppointmentStatus(){
        return appointmentStatus;
    }

    public long getAppointmentID(){
        return appointmentID;
    }

    @Exclude
    public LocalDateTime getStartTimeLocalDate(){
        return startTime;
    }

    @Exclude
    public LocalDateTime getEndTimeLocalDate(){
        return endTime;
    }

    public Timestamp getStartTime() {
        //Converts to timestamp for serialization and deserialization
        Date date = Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant());
        return new Timestamp(date);
    }

    public Timestamp getEndTime() {
        Date date = Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant());
        return new Timestamp(date);
    }

    public void setStartTime(Timestamp time) {
        startTime = convertTimeStampToLocalDateTime(time);
    }

    public void setEndTime(Timestamp time) {
        endTime = convertTimeStampToLocalDateTime(time);
    }

    @Exclude
    public LocalDateTime convertTimeStampToLocalDateTime(Timestamp timestamp){
        return timestamp.toDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public long getShiftID(){
        return shiftID;
    }
    @Exclude
    public void setAppointmentID(long newID){
        appointmentID = newID;
    }

    public boolean appointmentIsPassed(){
        pastAppointmentFlag = endTime.isBefore(LocalDateTime.now());
        return  pastAppointmentFlag;
    }
}

