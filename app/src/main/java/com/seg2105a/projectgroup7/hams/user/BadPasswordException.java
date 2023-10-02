package com.seg2105a.projectgroup7.hams.user;

public class BadPasswordException extends RuntimeException {

    public BadPasswordException() {
        super();
    }
    public BadPasswordException(String message) {
        super(message);
    }

}
