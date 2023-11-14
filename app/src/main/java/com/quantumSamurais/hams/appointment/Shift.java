package com.quantumSamurais.hams.appointment;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.quantumSamurais.hams.doctor.Doctor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public class Shift {
    static long SHIFT_ID = 0;
    Map<Long, Appointment> appointments;
    Doctor aDoctor;
    LocalDate shiftDay;
    LocalDateTime startTime, endTime;
    long shiftID;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Shift(Doctor myDoctor, LocalDate day, LocalDateTime startTime, LocalDateTime endTime){
        //Basic sanity checks
        if (day.isBefore(LocalDate.now())){
            throw new IllegalArgumentException("The date you passed is in the past");
        }
        else if (endTime.isBefore(startTime)){
            throw new IllegalArgumentException("The shift cannot end before it begins. End Time < Start Time.");
        } else if (!isValidShiftTime(startTime, endTime)) {
            throw new IllegalArgumentException("The shift time must be in increments of 30 minutes");
        }
        //Check if this shift would overlap with other shifts.
        aDoctor = myDoctor;

        this.shiftDay = day;
        this.startTime = startTime;
        this.endTime = endTime;
        shiftID = SHIFT_ID;
        SHIFT_ID++;
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
    public void cancelAppointment(long appointmentID){
        if(appointments.containsKey(appointmentID)){
            appointments.remove(appointmentID);
        }
    }
    public Map<Long, Appointment> getAppointments() {
        return appointments;
    }

    public LocalDate getShiftDay(){
        return shiftDay;
    }
    public LocalDateTime getStartTime(){
        return startTime;
    }
    public LocalDateTime getEndTime(){
        return endTime;
    }

    public long getShiftID() {
        return shiftID;
    }

    public boolean isVacant(){
        return appointments.isEmpty();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isValidShiftTime(LocalDateTime startTime, LocalDateTime endTime) {
        long interval = startTime.until(endTime, java.time.temporal.ChronoUnit.MINUTES);
        return interval % 30 == 0;
    }    
}