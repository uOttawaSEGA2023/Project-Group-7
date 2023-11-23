package com.quantumSamurais.hams.appointment;

import static java.time.temporal.ChronoUnit.MINUTES;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.quantumSamurais.hams.database.Database;
import com.quantumSamurais.hams.doctor.Doctor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

public class Shift {
    Database db;
    static long SHIFT_ID = 0;
    Map<Long, Appointment> appointments;
    Doctor aDoctor;
    LocalDateTime startTime, endTime;
    long shiftID;
    private boolean pastShiftFlag;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Shift(String emailAddress, LocalDateTime startTime, LocalDateTime endTime){
        //Basic sanity checks
        if (endTime.isBefore(startTime)){
            throw new IllegalArgumentException("The shift cannot end before it begins. End Time < Start Time.");
        } else if (!isValidShiftTime(startTime, endTime)) {
            throw new IllegalArgumentException("The shift time must be in increments of 30 minutes");
        }
        //Check if this shift would overlap with other shifts.

        db = Database.getInstance();
        aDoctor = db.getDoctor(emailAddress);
        this.startTime = startTime;
        this.endTime = endTime;
        shiftID = SHIFT_ID;
        SHIFT_ID++;
        db.addShift(this);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean overlapsWith(Shift otherShift) {
        return !this.endTime.isBefore(otherShift.startTime) && !this.startTime.isAfter(otherShift.endTime);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean takeAppointment(Appointment appointment){
        //If the appointment is compatible with the shift
        if (appointment.getStartTime().isBefore(startTime) || appointment.getEndTime().isAfter(endTime)){
            throw new IllegalArgumentException("The appointment passed is not compatible with this shift");
        }
        for (Appointment acceptedAppointment: appointments.values()){
            if (acceptedAppointment.overlaps(appointment)){
                return false;
            }
        }
        appointments.put(appointment.getAppointmentID(), appointment);
        return true;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean cancelAppointment(long appointmentID){
        if(appointments.containsKey(appointmentID)){
            //check to make
            boolean atLeast60HoursBefore = MINUTES.between(startTime, endTime) >= 60;
            if (atLeast60HoursBefore){
                appointments.remove(appointmentID);
                return true;}
        }
        //in all other cases we couldn't return.
        return false;
    }
    public Map<Long, Appointment> getAppointments() {
        return appointments;
    }
    public LocalDateTime getStartTime(){
        return startTime;
    }
    public LocalDateTime getEndTime(){
        return endTime;
    }

    public long getShiftID() { return shiftID; }

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
        pastShiftFlag = endTime.isBefore(LocalDateTime.now());
        return pastShiftFlag;
    }

    public ArrayList<Appointment> appointmentsAsList(){
        ArrayList<Appointment> myAppointments = new ArrayList<>();
        for (Appointment appointment: appointments.values()){
            myAppointments.add(appointment);
        }
        return myAppointments;
    }
}