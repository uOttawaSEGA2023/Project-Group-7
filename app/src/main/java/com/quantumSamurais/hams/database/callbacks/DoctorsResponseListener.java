package com.quantumSamurais.hams.database.callbacks;

import com.quantumSamurais.hams.doctor.Doctor;

public interface DoctorsResponseListener {
    void onSuccess(Doctor[] doctors);
    void onFailure(Error error);
}
