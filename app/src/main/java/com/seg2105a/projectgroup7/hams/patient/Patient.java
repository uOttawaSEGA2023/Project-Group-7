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
    private String _firstName, _lastName;
    private String _emailAddress;
    private byte[] _hashedPassword;

    private String _phoneNumber;
    private String _postalAddress;
    private String _healthCardNumber;

    public Patient(String firstName, String lastName, char[] password, String emailAddress, String phoneNumber, String postalAddress, UserType userType){
        super();
        //Stores the info in attributes
        _firstName = firstName;
        _lastName = lastName;
        _emailAddress = emailAddress;
        _hashedPassword = hashPassword(password);
        _phoneNumber = phoneNumber;
        _postalAddress = postalAddress;

        //Stores the info to map
        newUserInformation.put("firstName", _firstName);
        newUserInformation.put("lastName", _lastName);
        newUserInformation.put("emailAddress", _emailAddress);
        newUserInformation.put("password", _hashedPassword);
        newUserInformation.put("phoneNumber",phoneNumber);
        newUserInformation.put("postalAddress", postalAddress);

        //Uploads the map to Class List
        registeredPatients.add(newUserInformation);
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


}
