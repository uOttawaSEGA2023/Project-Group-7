package com.quantumSamurais.hams.login;

public class BadPasswordException extends RuntimeException {

    public BadPasswordException() {
        super();
    }
    public BadPasswordException(String message) {
        super(message);
    }

}
