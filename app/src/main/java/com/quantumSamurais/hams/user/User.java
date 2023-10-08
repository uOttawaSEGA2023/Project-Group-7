package com.quantumSamurais.hams.user;

import android.content.Context;

import com.quantumSamurais.hams.login.Login;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class User {
    public static List<Map<String,Object>> registeredPatients = new LinkedList<>();
    public static List<Map<String,Object>> registeredDoctors = new LinkedList<>();
    private String _firstName;
    private String _lastName;


    private byte[] _hashedPassword;
    private String _email;
    private String _phone;
    private String _address;

    /***
     *
     * @param firstName The user's first name
     * @param lastName The user's last name
     * @param password The user's password
     * @param email The user's email
     * @param phone The user's phone number
     * @param address The user's address
     */
    public User(String firstName, String lastName, char[] password, String email, String phone, String address) {
        _firstName = firstName;
        _lastName = lastName;
        _hashedPassword = Login.hashPassword(password);
        _email = email;
        _phone = phone;
        _address = address;
    }
    /***
     *
     * @param firstName The user's first name
     * @param lastName The user's last name
     * @param hashedPassword The user's password hashed.
     * @param email The user's email
     * @param phone The user's phone number
     * @param address The user's address
     */
    public User(String firstName, String lastName, byte[] hashedPassword, String email, String phone, String address) {
        _firstName = firstName;
        _lastName = lastName;
        _hashedPassword = hashedPassword;
        _email = email;
        _phone = phone;
        _address = address;
    }
    /*
    * signUp should generate a map with strings for keys and objects for values
    * the keys should match the names of the private variables, with the underscore removed.
    * And then this map should be added to the static registeredUsers list in this class
    **/
    public abstract void changeView(Context currentContext);


    //<editor-fold desc="Getters & Setters">
    public String getFirstName() {
        return _firstName;
    }

    public User setFirstName(String firstName) {
        _firstName = firstName;
        return this;
    }

    public String getLastName() {
        return _lastName;
    }

    public User setLastName(String lastName) {
        _lastName = lastName;
        return this;
    }

    public byte[] getPassword() {
        return _hashedPassword;
    }

    public User setPassword(char[] oldPassword, char[] newPassword) {
        if(Arrays.equals(Login.hashPassword(oldPassword), _hashedPassword)) {
            _hashedPassword = Login.hashPassword(newPassword);
        }
        return this;
    }

    public String getEmail() {
        return _email;
    }

    public User setEmail(String email) {
        _email = email;
        return this;
    }

    public String getPhone() {
        return _phone;
    }

    public User setPhone(String phone) {
        _phone = phone;
        return this;
    }

    public String getAddress() {
        return _address;
    }

    public User setAddress(String address) {
        _address = address;
        return this;
    }
    // </editor-fold>
}