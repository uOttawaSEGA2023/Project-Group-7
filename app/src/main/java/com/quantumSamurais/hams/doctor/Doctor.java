package com.quantumSamurais.hams.doctor;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.quantumSamurais.hams.LoginInteractiveMessage;
import com.quantumSamurais.hams.user.User;

import java.util.EnumSet;
import java.util.HashMap;

public class Doctor extends User {

	private String _employeeNumber;
	private EnumSet<Specialties> _specialties;
	private final HashMap<String, Object> newUserInformation = new HashMap<>(8);

	public Doctor(String firstName, String lastName, char[] hashedPassword, String email,
			  String phone, String address, String employeeNumber,EnumSet<Specialties> specialties) {
		super(firstName, lastName, hashedPassword, email, phone, address);

		_employeeNumber = employeeNumber;
		_specialties = specialties;
		newUserInformation.put("firstName", getFirstName());
		newUserInformation.put("lastName", getLastName());
		newUserInformation.put("emailAddress", getEmail());
		newUserInformation.put("hashedPassword", getPassword());
		newUserInformation.put("phoneNumber",getPhone());
		newUserInformation.put("postalAddress", getAddress());
		newUserInformation.put("employeeNumber", _employeeNumber);
		newUserInformation.put("specialties", _specialties);
		registeredDoctors.add(newUserInformation);
	}

	public Doctor(String firstName, String lastName, byte[] hashedPassword, String email,
				  String phone, String address, String employeeNumber,EnumSet<Specialties> specialties) {
		super(firstName, lastName, hashedPassword, email, phone, address);
		_employeeNumber = employeeNumber;
		_specialties = specialties;
	}
	@Override
	public void changeView(Context currentContext) {
		Intent doctorView = new Intent(currentContext, LoginInteractiveMessage.class);
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
	public EnumSet<Specialties> getSpecialties(){
		return _specialties;
	}
	public HashMap<String, Object> getNewUserInformation() {
		return newUserInformation;
	}
	public Doctor setEmployeeNumber(String _employeeNumber) {
		this._employeeNumber = _employeeNumber;
		return this;
	}
	public Doctor setSpecialties(EnumSet<Specialties> _specialties) {
		this._specialties = _specialties;
		return this;
	}
//</editor-fold>

}
