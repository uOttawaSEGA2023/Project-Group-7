/*
package com.quantumSamurais.hams.patient;

import com.quantumSamurais.hams.appointment.Appointment;
import com.quantumSamurais.hams.doctor.Doctor;

import java.util.HashMap;

public class FutureAppView {
    public FutureAppView(){}


    public boolean bookApp(Doctor doctor, Appointment app, Patient pat){
        try{
            doctor.acceptAppointment(app);
            String id = app.getAppointmentID().toString();
            pat.appointments.put(id, app);
            return true;

        } catch (Exception e){
            return false;

        }





    }

    public void rateDoctor(Doctor doc, Patient pat, int rating){
        if(rating>5 || rating<0){
            throw new Exception("Invalid rating");
        } else{
            doc.setRate(pat,rating);
        }
    }

    public boolean removeApp(Doctor doctor, Appointment app, Patient pat){
        try{
            String id = app.getAppointmentID().toString();
            pat.appointments.put(id,null);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public HashMap<Long, Appointment> getAppointments(Patient pat){
        return pat.appointments;
    }



}
*/
