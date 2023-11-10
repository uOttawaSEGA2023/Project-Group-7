package com.quantumSamurais.hams.appointment;

import static java.time.temporal.ChronoUnit.MINUTES;

import com.quantumSamurais.hams.doctor.Doctor;
import com.quantumSamurais.hams.patient.Patient;

import java.time.LocalDateTime;

public class Appointment {
    static long APPOINTMENT_ID = 0;
    LocalDateTime startTime, endTime;
    long appointmentID;

    Doctor myDoctor;
    Patient myPatient;
    public Appointment(LocalDateTime startTime, LocalDateTime endTime, Shift shift, Patient patient){
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
        //Set the time
        this.startTime = startTime;
        this.endTime = endTime;
        //Checks if the appointment would overlap with whatever is in shift.
        boolean appointmentProperlyAssigned = shift.takeAppointment(this);
        if (appointmentProperlyAssigned){
            appointmentID = APPOINTMENT_ID;
            APPOINTMENT_ID++;
        }
        throw new IllegalArgumentException("This appointment overlaps with other appointments.");


    }

    public boolean overlaps(Appointment someAppointment){
        if (startTime.isBefore(someAppointment.getEndTime()) && someAppointment.getStartTime().isBefore(endTime)){
            return true;
        } return false;
    }

    public long getAppointmentID(){
        return appointmentID;
    }

    public boolean tieAppointment(long shiftID, Doctor doctor, Patient patient){
        if (doctor.hasThisShift(shiftID)){
            myDoctor = doctor;
            myPatient = patient;
            return true;
        }
        return false;
    }

    public LocalDateTime getStartTime(){
        return startTime;
    }

    public LocalDateTime getEndTime(){
        return endTime;
    }
}

