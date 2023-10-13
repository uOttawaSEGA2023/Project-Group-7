package com.quantumSamurais.hams.login;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.quantumSamurais.hams.patient.Patient;
import com.quantumSamurais.hams.user.User;
import com.quantumSamurais.hams.user.UserType;

public class dbcom implements OnSuccessListener<DocumentSnapshot> {

    private User person;
    private String email;
    private UserType userType;
    private DocumentReference docref;

    private boolean dataReady = false;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public dbcom(String email, UserType usertype){
        this.email=email;
        this.userType=usertype;
        this.docref=db.collection("user").document("patient");
        docref.get().addOnSuccessListener(this);
    }

    @Override
    public void onSuccess(DocumentSnapshot documentSnapshot) {
       this.person= (User) documentSnapshot.toObject(Patient.class);
       dataReady=true;
    }
    public User getUser(){
        return this.person;
    }
    public boolean isDataReady() {
        return dataReady;
    }
}
