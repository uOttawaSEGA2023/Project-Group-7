package com.quantumSamurais.hams.doctor;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.firestore.Exclude;
import com.quantumSamurais.hams.appointment.Appointment;
import com.quantumSamurais.hams.appointment.Shift;
import com.quantumSamurais.hams.database.Database;
import com.quantumSamurais.hams.doctor.activities.DoctorMain;
import com.quantumSamurais.hams.patient.Patient;
import com.quantumSamurais.hams.user.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Doctor extends User implements Serializable {

	private String employeeNumber;
	private ArrayList<Specialties> specialties;
	private boolean acceptsAppointmentsByDefault = false;
	private ArrayList<Long> shiftIDs;
	private ArrayList<Shift> shifts;

	private HashMap<String, Float> ratings;


	public Doctor() {
		this.ratings = new HashMap<>();
	}

	public Doctor(String firstName, String lastName, char[] hashedPassword, String email,
				  String phone, String address, String employeeNumber, ArrayList<Specialties> specialties, boolean acceptsByDefault) {
		super(firstName, lastName, hashedPassword, email, phone, address);
		this.employeeNumber = employeeNumber;
		this.specialties = specialties;
		this.ratings = new HashMap<>();
		acceptsAppointmentsByDefault = acceptsByDefault;
		shiftIDs = new ArrayList<>(); //In the empty case
		Database db = Database.getInstance();
		db.addSignUpRequest(this);

	}


	public Doctor(String firstName, String lastName, ArrayList<Integer> hashedPassword, ArrayList<Integer> salt, String email,
				  String phone, String address, String employeeNumber,ArrayList<Specialties> specialties, boolean acceptsByDefault) {
		super(firstName, lastName, hashedPassword, salt, email, phone, address);
		this.employeeNumber = employeeNumber;
		this.specialties = specialties;
		this.ratings = new HashMap<>();
		shiftIDs = new ArrayList<>();
		acceptsAppointmentsByDefault = acceptsByDefault;
	}

	//When we read from DB
	public Doctor(String firstName, String lastName, ArrayList<Integer> hashedPassword, ArrayList<Integer> salt, String email,
				  String phone, String address, String employeeNumber,ArrayList<Specialties> specialties, boolean acceptsByDefault, ArrayList<Long> shiftIDs) {
		super(firstName, lastName, hashedPassword, salt, email, phone, address);
		this.employeeNumber = employeeNumber;
		this.specialties = specialties;
		this.shiftIDs = shiftIDs;
		this.ratings = new HashMap<>();
		acceptsAppointmentsByDefault = acceptsByDefault;
	}

	@Override
	public void changeView(Context currentContext) {
		Intent doctorView = new Intent(currentContext, DoctorMain.class);
		doctorView.putExtra("doctorEmailAddress", getEmail());
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

	public boolean getAcceptsAppointmentsByDefault(){
		return acceptsAppointmentsByDefault;
	}


	public void setAcceptsAppointmentsByDefault(boolean value){
		acceptsAppointmentsByDefault = value;

		Database db = Database.getInstance();
		db.updateAcceptsByDefault(getEmail(), value); //errors running in with this method.
	}

	public HashMap<String, Float> getRatings() {
		return this.ratings;
	}

	public void setRatings(HashMap<String, Float> ratings) {
		this.ratings = ratings;
	}

	public ArrayList<Long> getShiftIDs(){
		return shiftIDs;
	}


	public void setShiftIDs(ArrayList<Long> shiftIDs){
		this.shiftIDs = shiftIDs;
	}

	public boolean hasThisShift(long shiftID){
		return shiftIDs.contains(shiftID);
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

	@Exclude
	public ArrayList<Appointment> getAppointments(){
		return null;
		//return Database.getInstance().getDoctorAppointments(getEmail());
	}
//</editor-fold>

}
