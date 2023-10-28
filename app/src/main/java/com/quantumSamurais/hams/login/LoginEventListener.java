package com.quantumSamurais.hams.login;

import com.quantumSamurais.hams.database.RequestStatus;

public interface LoginEventListener {
    void loginResponse(LoginStatusCodes status, RequestStatus requestStatus);
}