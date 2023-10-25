package com.quantumSamurais.hams.database.callbacks;

import com.quantumSamurais.hams.database.Request;

import java.util.ArrayList;

public interface RequestsResponseListener {
    void onSuccess(ArrayList<Request> requests);
    void onFailure(Error error);
}
