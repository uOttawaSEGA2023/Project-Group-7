package com.quantumSamurais.hams.patient;
import android.content.Context;
import android.content.Intent;

import com.quantumSamurais.hams.database.DatabaseUtils;
import com.quantumSamurais.hams.login.LoginInteractiveMessage;
import com.quantumSamurais.hams.user.User;
import com.quantumSamurais.hams.user.UserType;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Patient extends User {

    private String healthCardNumber;

    public Patient() {

    }

    //Used during account creation
    public Patient(String firstName, String lastName, char[] rawPassword, String emailAddress, String phoneNumber, String postalAddress, String healthCardNumber){
        super(firstName, lastName, rawPassword, emailAddress, phoneNumber, postalAddress);
        this.healthCardNumber = healthCardNumber;
        DatabaseUtils db = new DatabaseUtils();
        db.addSignUpRequest(this);
    }

    //Used for logins
    public Patient(String firstName, String lastName, ArrayList<Integer> hashedPassword, ArrayList<Integer> salt, String emailAddress, String phoneNumber, String postalAddress, String healthCardNumber){
        super(firstName, lastName, hashedPassword, salt, emailAddress, phoneNumber, postalAddress);
        this.healthCardNumber = healthCardNumber;
    }

    @Override
    public void changeView(Context currentContext) {
        Intent patientView = new Intent(currentContext, LoginInteractiveMessage.class);
        patientView.putExtra("userType", UserType.PATIENT);
        currentContext.startActivity(patientView);
    }

    public String getHealthCardNumber() {
        return healthCardNumber;
    }

    public Patient setHealthCardNumber(String healthCardNumber) {
        this.healthCardNumber = healthCardNumber;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Patient patient = (Patient) o;
        return Objects.equals(healthCardNumber, patient.healthCardNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), healthCardNumber);
    }
}
