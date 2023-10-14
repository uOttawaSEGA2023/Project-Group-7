package com.quantumSamurais.hams.patient;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.FirebaseFirestore;
import com.quantumSamurais.hams.LoginInteractiveMessage;
import com.quantumSamurais.hams.user.User;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Patient extends User {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static List<Map<String, Object>> registeredPatients = new ArrayList<Map<String, Object>>();

    public HashMap<String, Object> getNewUserInformation() {
        return newUserInformation;
    }

    private HashMap<String, Object> newUserInformation = new HashMap<String, Object>(8);

    private String _healthCardNumber;

    //Used during account creation
    public Patient(String firstName, String lastName, char[] rawPassword, String emailAddress, String phoneNumber, String postalAddress, String healthCardNumber){
        super(firstName, lastName, rawPassword, emailAddress, phoneNumber, postalAddress);
        _healthCardNumber = healthCardNumber;


        //Stores the info to user map
        newUserInformation.put("firstName", getFirstName());
        newUserInformation.put("lastName", getLastName());
        newUserInformation.put("emailAddress", getEmail());
        newUserInformation.put("hashedPassword", Blob.fromBytes(getPassword()));
        newUserInformation.put("salt", Blob.fromBytes(getSalt()));
        newUserInformation.put("phoneNumber",getPhone());
        newUserInformation.put("postalAddress", getAddress());
        newUserInformation.put("healthCardNumber", _healthCardNumber);

        db.collection("users").document("software").collection("patients").add(newUserInformation);

        //Uploads the map to Class List
        registeredPatients.add(newUserInformation);
    }

    //Used for logins
    public Patient(String firstName, String lastName, byte[] hashedPassword, String emailAddress, String phoneNumber, String postalAddress, String healthCardNumber){
        super(firstName, lastName, hashedPassword, emailAddress, phoneNumber, postalAddress);
        _healthCardNumber = healthCardNumber;
    }

    @Override
    public void changeView(Context currentContext) {
        Intent patientView = new Intent(currentContext, LoginInteractiveMessage.class);
        currentContext.startActivity(patientView);
    }

    public static List<Map<String, Object>> getRegisteredPatients(){
        return registeredPatients;
    }

    public String getHealthCardNumber() {
        return _healthCardNumber;
    }

    public void setHealthCardNumber(String _healthCardNumber) {
        this._healthCardNumber = _healthCardNumber;
    }



}
