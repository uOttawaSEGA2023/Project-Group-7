package com.quantumSamurais.hams.doctor;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.FirebaseFirestore;
import com.quantumSamurais.hams.database.DatabaseUtils;
import com.quantumSamurais.hams.login.LoginInteractiveMessage;
import com.quantumSamurais.hams.user.User;
import com.quantumSamurais.hams.user.UserType;

import java.util.ArrayList;
import java.util.HashMap;

public class Doctor extends User {

	private String _employeeNumber;
	private ArrayList<Specialties> _specialties;

	public Doctor(String firstName, String lastName, char[] hashedPassword, String email,
				  String phone, String address, String employeeNumber, ArrayList<Specialties> specialties) {
		super(firstName, lastName, hashedPassword, email, phone, address);
		DatabaseUtils db = new DatabaseUtils();
		db.addSignUpRequest(this);
	}

	public Doctor(String firstName, String lastName, ArrayList<Integer> hashedPassword, ArrayList<Integer> salt, String email,
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
	public Doctor setEmployeeNumber(String employeeNumber) {
		this._employeeNumber = employeeNumber;
		return this;
	}
	public Doctor setSpecialties(ArrayList<Specialties> specialties) {
		this._specialties = specialties;
		return this;
	}
//</editor-fold>

}
