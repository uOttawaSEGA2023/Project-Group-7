package com.quantumSamurais.hams.doctor;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.quantumSamurais.hams.database.DatabaseUtils;
import com.quantumSamurais.hams.login.LoginInteractiveMessage;
import com.quantumSamurais.hams.user.User;
import com.quantumSamurais.hams.user.UserType;

import java.util.ArrayList;
import java.util.Objects;

public class Doctor extends User {

	private String employeeNumber;
	private ArrayList<Specialties> specialties;

	public Doctor() {
	}

	public Doctor(String firstName, String lastName, char[] hashedPassword, String email,
				  String phone, String address, String employeeNumber, ArrayList<Specialties> specialties) {
		super(firstName, lastName, hashedPassword, email, phone, address);
		this.employeeNumber = employeeNumber;
		this.specialties = specialties;
		DatabaseUtils db = new DatabaseUtils();
		db.addSignUpRequest(this);
	}

	public Doctor(String firstName, String lastName, ArrayList<Integer> hashedPassword, ArrayList<Integer> salt, String email,
				  String phone, String address, String employeeNumber,ArrayList<Specialties> specialties) {
		super(firstName, lastName, hashedPassword, salt, email, phone, address);
		this.employeeNumber = employeeNumber;
		this.specialties = specialties;
	}
	@Override
	public void changeView(Context currentContext) {
		Intent doctorView = new Intent(currentContext, LoginInteractiveMessage.class);
		doctorView.putExtra("userType", UserType.DOCTOR);
		currentContext.startActivity(doctorView);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		Doctor doctor = (Doctor) o;
		return Objects.equals(employeeNumber, doctor.employeeNumber) && Objects.equals(specialties, doctor.specialties);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), employeeNumber, specialties);
	}

	@NonNull
	@Override
	public String toString() {
		return super.toString() + "\nEmployee Number: " +
				employeeNumber +
				"\nSpecialties: " +
				specialties.toString();
	}


//<editor-fold desc="Getters & Setters">
	public String getEmployeeNumber() {
		return employeeNumber;
	}
	public ArrayList<Specialties> getSpecialties(){
		return specialties;
	}
	public Doctor setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
		return this;
	}
	public Doctor setSpecialties(ArrayList<Specialties> specialties) {
		this.specialties = specialties;
		return this;
	}
//</editor-fold>

}
