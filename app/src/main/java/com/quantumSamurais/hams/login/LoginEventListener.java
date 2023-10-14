package com.quantumSamurais.hams.login;

public interface LoginEventListener {
    void loginResponse(LoginReturnCodes returnCode);
}