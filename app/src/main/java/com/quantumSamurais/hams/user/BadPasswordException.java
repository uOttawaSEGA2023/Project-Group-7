package com.quantumSamurais.hams.user;

public class BadPasswordException extends RuntimeException {

    public BadPasswordException() {
        super();
    }
    public BadPasswordException(String message) {
        super(message);
    }

}
