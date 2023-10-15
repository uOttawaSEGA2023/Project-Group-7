package com.quantumSamurais.hams.database.callbacks;

import com.quantumSamurais.hams.patient.Patient;

public interface PatientsResponseListener {
    void onSuccess(Patient[] patients);
    void onFailure(Error error);
}
