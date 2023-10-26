package com.quantumSamurais.hams.database.callbacks;

import com.quantumSamurais.hams.patient.Patient;

import java.util.ArrayList;

public interface PatientsResponseListener {
    void onSuccess(ArrayList<Patient> patients);
    void onFailure(Error error);
}
