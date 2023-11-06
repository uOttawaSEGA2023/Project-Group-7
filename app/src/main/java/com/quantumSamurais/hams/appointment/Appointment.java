package com.quantumSamurais.hams.appointment;

import static java.time.temporal.ChronoUnit.MINUTES;

import com.quantumSamurais.hams.doctor.Doctor;
import com.quantumSamurais.hams.patient.Patient;

import java.time.LocalDateTime;

public class Appointment {
    LocalDateTime startTime, endTime;
    Doctor myDoctor;
    Patient myPatient;
    public Appointment(LocalDateTime startTime, LocalDateTime endTime, Doctor doctor, Patient patient){
        long interval = MINUTES.between(startTime, endTime);
        boolean isIncrementOf30Minutes = interval % 30 == 0 ? true:false;
        if (doctor == null || patient == null){
            throw new NullPointerException("Please do not pass null objects.");
        }
        if (endTime.isBefore(startTime) || endTime.isEqual(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        if (!isIncrementOf30Minutes){
            throw new IllegalArgumentException("The passed startTime and endTime are not 30 minutes apart.");
        }
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public boolean appointmentsOverlap(Appointment someAppointment){
        // Either smth like 10:00 - 11:00 and 10:30-11:00 (or more)
        if (startTime.isBefore(someAppointment.getStartTime()) && !endTime.isBefore(someAppointment.getStartTime())){
            return true;
        } // or 10:00 - 11:00 (other) and 10:30 - 12:00(this)
        else if (endTime.isAfter(someAppointment.getEndTime()) && !startTime.isAfter(someAppointment.getEndTime())){
            return true;
        }
        //If there's any overlap
        else if ( startTime.isEqual(someAppointment.getStartTime()) || endTime.isEqual(someAppointment.getEndTime())){
            return true;
        } else{
            return false;
        }
    }

    public LocalDateTime getStartTime(){
        return startTime;
    }

    public LocalDateTime getEndTime(){
        return endTime;
    }
}

