package com.quantumSamurais.hams.database;

import com.quantumSamurais.hams.user.User;
import com.quantumSamurais.hams.user.UserWrappedDB;

public class Request {
    long id;
    UserWrappedDB user;

    RequestStatus status;

    Request(){
    }

    Request(long id, UserWrappedDB user, RequestStatus status) {
        this.id = id;
        this.user = user;
        this.status = status;
    }

    public long getID() {
        return id;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public UserWrappedDB getUser() {
        return user;
    }
}
