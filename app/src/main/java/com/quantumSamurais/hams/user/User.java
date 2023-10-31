package com.quantumSamurais.hams.user;

import android.content.Context;

import androidx.annotation.NonNull;

import com.quantumSamurais.hams.login.Login;
import com.quantumSamurais.hams.utils.ArrayUtils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class User {
    private String firstName;
    private String lastName;


    private ArrayList<Integer> password;
    private String email;
    private String phone;
    private String address;

    private ArrayList<Integer> salt;

    public User() {

    }

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
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = ArrayUtils.packBytes(Login.hashPassword(password, generateSalt()));
        this.email = email;
        this.phone = phone;
        this.address = address;
    }
    /***
     *
     * @param firstName The user's first name
     * @param lastName The user's last name
     * @param password The user's password hashed.
     * @param email The user's email
     * @param phone The user's phone number
     * @param address The user's address
     */
    public User(String firstName, String lastName, ArrayList<Integer> password, ArrayList<Integer> salt, String email, String phone, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.salt = salt;
    }

    /*
    * signUp should generate a map with strings for keys and objects for values
    * the keys should match the names of the private variables, with the underscore removed.
    * And then this map should be added to the static registeredUsers list in this class
    **/
    public abstract void changeView(Context currentContext);

    private byte[] generateSalt() {
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        this.salt = ArrayUtils.packBytes(salt);
        return salt;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(password, user.password) && Objects.equals(email, user.email) && Objects.equals(phone, user.phone) && Objects.equals(address, user.address) && Objects.equals(salt, user.salt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, password, email, phone, address, salt);
    }

    @Override
    @NonNull
    public String toString() {
        return "First name: " +
                firstName +
                ", Last Name: " +
                lastName +
                "\nEmail: " +
                email +
                "\nPassword: " +
                Arrays.toString(ArrayUtils.unpackBytes(password)) +
                "\nSalt: " +
                Arrays.toString(ArrayUtils.unpackBytes(salt)) +
                "\nPhone: " +
                phone +
                "\nAddress: " +
                address;
    }


    //<editor-fold desc="Getters & Setters">
    public String getFirstName() {
        return firstName;
    }

    public User setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public User setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public ArrayList<Integer> getPassword() {
        return password;
    }

    public User setPassword(char[] oldPassword, char[] newPassword, byte[] salt) {
        if(Arrays.equals(Login.hashPassword(oldPassword, salt), ArrayUtils.unpackBytes(password))) {
            password = ArrayUtils.packBytes(Login.hashPassword(newPassword,generateSalt()));
        }
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public User setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public User setAddress(String address) {
        this.address = address;
        return this;
    }

    public ArrayList<Integer> getSalt() {
        return salt;
    }
    // </editor-fold>
}
