package com.seg2105a.projectgroup7.hams.patient;
import com.seg2105a.projectgroup7.hams.user.*;
import android.widget.EditText;


import com.seg2105a.projectgroup7.hams.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Patient extends User {

    private static List<Map<String, Object>> registeredPatients = new ArrayList<Map<String, Object>>();
    private HashMap<String, Object> newUserInformation = new HashMap<String, Object>(7);

    private String _healthCardNumber;

    //Used during account creation
    public Patient(String firstName, String lastName, char[] rawPassword, String emailAddress, String phoneNumber, String postalAddress, String healthCardNumber){
        super(firstName, lastName, rawPassword, emailAddress, phoneNumber, postalAddress);
        _healthCardNumber = healthCardNumber;


        //Stores the info to user map
        newUserInformation.put("firstName", getFirstName());
        newUserInformation.put("lastName", getLastName());
        newUserInformation.put("emailAddress", getEmail());
        newUserInformation.put("hashedPassword", getPassword());
        newUserInformation.put("phoneNumber",getPhone());
        newUserInformation.put("postalAddress", getAddress());
        newUserInformation.put("healthCardNumber", _healthCardNumber);

        //Uploads the map to Class List
        registeredPatients.add(newUserInformation);
    }

    //Used for logins
    public Patient(String firstName, String lastName, byte[] hashedPassword, String emailAddress, String phoneNumber, String postalAddress, String healthCardNumber){
        super(firstName, lastName, hashedPassword, emailAddress, phoneNumber, postalAddress);
        _healthCardNumber = healthCardNumber;
    }

    @Override
    public void signUp(){
        //Gathers information from fields
        //Check if the email, and other fields respect formats (no numbers in names, actual email format for email, etc.)
        //Check email address and Health Card Number are unique (for now, just access the hashmap and see if there's a user with them)
        //If everything is fine, call constructor
        //Else change the text on the page to notify user when something doesn't work
        //All this is done each time user pressed "Sign Up" button.

    }

    @Override
    protected void changeView() {

    }

    public String getHealthCardNumber() {
        return _healthCardNumber;
    }

    public void setHealthCardNumber(String _healthCardNumber) {
        this._healthCardNumber = _healthCardNumber;
    }



}
