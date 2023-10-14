package com.quantumSamurais.hams.patient;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.FirebaseFirestore;
import com.quantumSamurais.hams.login.LoginInteractiveMessage;
import com.quantumSamurais.hams.user.User;
import com.quantumSamurais.hams.user.UserType;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Patient extends User {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final HashMap<String, Object> newUserInformation = new HashMap<>(8);

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
    public Patient(String firstName, String lastName, byte[] hashedPassword, byte[] salt, String emailAddress, String phoneNumber, String postalAddress, String healthCardNumber){
        super(firstName, lastName, hashedPassword, salt, emailAddress, phoneNumber, postalAddress);
        _healthCardNumber = healthCardNumber;
    }

    public HashMap<String, Object> getNewUserInformation() {
        return newUserInformation;
    }
    @Override
    public void changeView(Context currentContext) {
        Intent patientView = new Intent(currentContext, LoginInteractiveMessage.class);
        patientView.putExtra("userType", UserType.PATIENT);
        currentContext.startActivity(patientView);
    }

    public static List<Map<String, Object>> getRegisteredPatients(){
        return User.registeredPatients;
    }

    public String getHealthCardNumber() {
        return _healthCardNumber;
    }

    public Patient setHealthCardNumber(String healthCardNumber) {
        this._healthCardNumber = healthCardNumber;
        return this;
    }



}
