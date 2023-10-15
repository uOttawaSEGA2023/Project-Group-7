package com.quantumSamurais.hams.database;

import java.util.ArrayList;

public class PatientData {
    private String _firstName;
    private String _lastName;
    private ArrayList<Integer> _hashedPassword;
    private ArrayList<Integer> _salt;
    private String _email;
    private String _phone;
    private String _address;

    private String _healthCardNumber;
    public PatientData(String firstName, String lastName, ArrayList<Integer> hashedPassword, ArrayList<Integer> salt, String email, String phone, String address, String healthCardNumber) {
        _firstName = firstName;
        _lastName  = lastName;
        _hashedPassword = hashedPassword;
        _salt = salt;
        _email = email;
        _phone = phone;
        _address = address;
        _healthCardNumber = healthCardNumber;
    }

    public String getFirstName() {
        return _firstName;
    }

    public String getLastName() {
        return _lastName;
    }

    public ArrayList<Integer> getHashedPassword() {
        return _hashedPassword;
    }

    public ArrayList<Integer> getSalt() {
        return _salt;
    }

    public String getEmail() {
        return _email;
    }

    public String getPhone() {
        return _phone;
    }

    public String getAddress() {
        return _address;
    }

    public String getHealthCardNumber() {
        return _healthCardNumber;
    }
}
