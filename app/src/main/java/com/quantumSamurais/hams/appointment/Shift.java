package com.quantumSamurais.hams.appointment;

import static java.time.temporal.ChronoUnit.MINUTES;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.quantumSamurais.hams.database.Database;
import com.quantumSamurais.hams.doctor.Doctor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@IgnoreExtraProperties
public class Shift {
    List<Appointment> appointments;
    String doctorEmailAddress;
    Timestamp startTimeStamp, endTimeStamp;
    long shiftID;
    private boolean pastShiftFlag;

    public Shift(){}
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Shift(String emailAddress, LocalDateTime startTime, LocalDateTime endTime){
        //Basic sanity checks
        if (endTime.isBefore(startTime)){
            throw new IllegalArgumentException("The shift cannot end before it begins. End Time < Start Time.");
        } else if (!isValidShiftTime(startTime, endTime)) {
            throw new IllegalArgumentException("The shift time must be in increments of 30 minutes");
        }

        doctorEmailAddress = emailAddress;
        //Converts to timestamp for serialization and deserialization
        Date date = Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant());
        this.startTimeStamp = new Timestamp(date);
        date = Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant());
        this.endTimeStamp = new Timestamp(date);
        appointments = new ArrayList<>();
        shiftID = -1;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean overlapsWith(LocalDateTime otherStartTime, LocalDateTime otherEndTime) {
        LocalDateTime startTime = convertTimeStampToLocalDateTime(startTimeStamp);
        LocalDateTime endTime = convertTimeStampToLocalDateTime(endTimeStamp);
        return !endTime.isBefore(otherStartTime) && !startTime.isAfter(otherEndTime);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean takeAppointment(Appointment appointment){
        LocalDateTime startTime = convertTimeStampToLocalDateTime(startTimeStamp);
        LocalDateTime endTime = convertTimeStampToLocalDateTime(endTimeStamp);
        //If the appointment is compatible with the shift
        if (appointment.getStartTimeLocalDate().isBefore(startTime) || appointment.getEndTimeLocalDate().isAfter(endTime)){
            throw new IllegalArgumentException("The appointment passed is not compatible with this shift");
        }
        for (Appointment acceptedAppointment: appointments){
            if (acceptedAppointment.overlaps(appointment)){
                return false;
            }
        }
        appointments.add(appointment);
        return true;
    }
    public LocalDateTime convertTimeStampToLocalDateTime(Timestamp timestamp){
        return timestamp.toDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
    public boolean containsKey(long appointmentID){
        for (Appointment appointment: appointments){
            if (appointment.getAppointmentID() == appointmentID){
                return true;
            }
        }
        return false;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean cancelAppointment(long appointmentID){
        LocalDateTime startTime = convertTimeStampToLocalDateTime(startTimeStamp);
        LocalDateTime endTime = convertTimeStampToLocalDateTime(endTimeStamp);
        if(containsKey(appointmentID)){
            //check to make
            boolean atLeast60HoursBefore = MINUTES.between(startTime, endTime) >= 60;
            if (atLeast60HoursBefore){
                appointments.remove(appointmentID);
                return true;}
        }
        //in all other cases we couldn't return.
        return false;
    }
    public List<Appointment> getAppointments() {
        return appointments;
    }

    public Timestamp getStartTimeStamp(){
        return startTimeStamp;
    }

    public Timestamp getEndTimeStamp(){
        return endTimeStamp;
    }
    @Exclude
    public LocalDateTime getStartTime(){
        return convertTimeStampToLocalDateTime(startTimeStamp);
    }
    @Exclude
    public LocalDateTime getEndTime(){
        return convertTimeStampToLocalDateTime(endTimeStamp);
    }

    public long getShiftID() { return shiftID; }

    @Exclude
    public Doctor getDoctor() {
        return Database.getInstance().getDoctor(doctorEmailAddress);
    }

    public void setDoctorEmailAddress(String emailAddress){
        doctorEmailAddress = emailAddress;
    }


    public String getDoctorEmailAddress(){
        return doctorEmailAddress;
    }

    public boolean isVacant(){
        return appointments.isEmpty();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isValidShiftTime(LocalDateTime startTime, LocalDateTime endTime) {
        long interval = startTime.until(endTime, java.time.temporal.ChronoUnit.MINUTES);
        return interval % 30 == 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean shiftIsPassed(){
        pastShiftFlag = convertTimeStampToLocalDateTime(endTimeStamp).isBefore(LocalDateTime.now());
        return pastShiftFlag;
    }

    public void setShiftID(long shiftID) {
        this.shiftID = shiftID;
    }
}