package com.quantumSamurais.hams.doctor;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.FirebaseFirestore;
import com.quantumSamurais.hams.LoginInteractiveMessage;
import com.quantumSamurais.hams.user.User;
import com.quantumSamurais.hams.user.UserType;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

public class Doctor extends User {

	FirebaseFirestore db = FirebaseFirestore.getInstance();
	private String _employeeNumber;
	private ArrayList<Specialties> _specialties;
	private final HashMap<String, Object> newUserInformation = new HashMap<>(9);

	public Doctor(String firstName, String lastName, char[] hashedPassword, String email,
				  String phone, String address, String employeeNumber, ArrayList<Specialties> specialties) {
		super(firstName, lastName, hashedPassword, email, phone, address);

		_employeeNumber = employeeNumber;
		_specialties = specialties;
		newUserInformation.put("firstName", getFirstName());
		newUserInformation.put("lastName", getLastName());
		newUserInformation.put("emailAddress", getEmail());
		newUserInformation.put("hashedPassword", Blob.fromBytes(getPassword()));
		newUserInformation.put("salt", Blob.fromBytes(getSalt()));
		newUserInformation.put("phoneNumber",getPhone());
		newUserInformation.put("postalAddress", getAddress());
		newUserInformation.put("employeeNumber", _employeeNumber);
		newUserInformation.put("specialties", _specialties);
		db.collection("users").document("software").collection("doctors").add(newUserInformation);
		registeredDoctors.add(newUserInformation);

	}

	public Doctor(String firstName, String lastName, byte[] hashedPassword, byte[] salt, String email,
				  String phone, String address, String employeeNumber,ArrayList<Specialties> specialties) {
		super(firstName, lastName, hashedPassword, salt, email, phone, address);
		_employeeNumber = employeeNumber;
		_specialties = specialties;
	}
	@Override
	public void changeView(Context currentContext) {
		Intent doctorView = new Intent(currentContext, LoginInteractiveMessage.class);
		doctorView.putExtra("userType", UserType.DOCTOR);
		currentContext.startActivity(doctorView);
	}

	@NonNull
	@Override
	public String toString() {
		return super.toString() + "\nEmployee Number: " +
				_employeeNumber +
				"\nSpecialties: " +
				_specialties.toString();
	}


//<editor-fold desc="Getters & Setters">
	public String getEmployeeNumber() {
		return _employeeNumber;
	}
	public ArrayList<Specialties> getSpecialties(){
		return _specialties;
	}
	public HashMap<String, Object> getNewUserInformation() {
		return newUserInformation;
	}
	public Doctor setEmployeeNumber(String _employeeNumber) {
		this._employeeNumber = _employeeNumber;
		return this;
	}
	public Doctor setSpecialties(ArrayList<Specialties> _specialties) {
		this._specialties = _specialties;
		return this;
	}
//</editor-fold>

}
