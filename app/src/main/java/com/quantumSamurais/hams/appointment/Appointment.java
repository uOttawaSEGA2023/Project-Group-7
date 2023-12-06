package com.quantumSamurais.hams.appointment;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.quantumSamurais.hams.database.RequestStatus;
import com.quantumSamurais.hams.doctor.Specialties;
import com.quantumSamurais.hams.patient.Patient;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class Appointment implements Serializable  {
    private RequestStatus appointmentStatus;

    @Exclude
    private LocalDateTime startTime, endTime;
    private long appointmentID, shiftID;
    private boolean pastAppointmentFlag;

    private Patient patient;

    private String docName;
    private ArrayList<Specialties> specs;
    private boolean rated;

    public Appointment(){
    }

    public Appointment(LocalDateTime startTime, LocalDateTime endTime, Shift shift, String docName, ArrayList<Specialties> specs, Patient patient, RequestStatus requestStatus){
        if (inputsAreValid(startTime, endTime, shift, patient)){
            //Set the time
            this.startTime = startTime;
            this.endTime = endTime;
            this.patient = patient;
            this.shiftID = shift.getShiftID();
            this.specs = specs;
            this.docName = docName;
            this.appointmentStatus = requestStatus;
            return;
        }
        throw new IllegalArgumentException("Something happened");
    }

    public boolean inputsAreValid(LocalDateTime startTime, LocalDateTime endTime, Shift shift, Patient patient){
        Duration interval = Duration.between(startTime, endTime);
        boolean isIncrementOf30Minutes = interval.toMinutes() % 30 == 0;
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

    public boolean getRated() {
        return rated;
    }

    public void setRated(boolean rated) {
        this.rated = rated;
    }

    public RequestStatus getAppointmentStatus(){
        return appointmentStatus;
    }

    public Long getAppointmentID(){
        return Long.valueOf(appointmentID);
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

    public ArrayList<Specialties> getSpecs() {
        return specs;
    }

    public void setSpecs(ArrayList<Specialties> specs) {
        this.specs = specs;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getDocName() {
        return docName;
    }

    public Long getShiftID(){
        return Long.valueOf(shiftID);
    }


    public void setAppointmentID(long newID){
        appointmentID = newID;
    }

    @Exclude
    public boolean appointmentIsPassed(){
        pastAppointmentFlag = endTime.isBefore(LocalDateTime.now());
        return  pastAppointmentFlag;
    }
}

