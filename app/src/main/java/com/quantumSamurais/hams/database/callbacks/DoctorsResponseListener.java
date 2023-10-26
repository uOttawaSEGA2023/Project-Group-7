package com.quantumSamurais.hams.database.callbacks;

import com.quantumSamurais.hams.doctor.Doctor;

import java.util.ArrayList;

public interface DoctorsResponseListener {
    void onSuccess(ArrayList<Doctor> doctors);
    void onFailure(Error error);
}
