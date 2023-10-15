package com.quantumSamurais.hams.database.callbacks;

import com.quantumSamurais.hams.database.Request;

public interface RequestsResponseListener {
    void onSuccess(Request[] requests);
    void onFailure(Error error);
}
