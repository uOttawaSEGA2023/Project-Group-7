package com.quantumSamurais.hams.database.callbacks;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public interface DatabaseResponseListener {
    void collectionResponse(QuerySnapshot snapshot);
    void documentResponse(DocumentSnapshot snapshot);
}