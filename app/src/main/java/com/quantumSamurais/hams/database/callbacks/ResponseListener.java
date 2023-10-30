package com.quantumSamurais.hams.database.callbacks;

public interface ResponseListener<T> {
    void onSuccess(T data);
    void onFailure(Exception error);

}
