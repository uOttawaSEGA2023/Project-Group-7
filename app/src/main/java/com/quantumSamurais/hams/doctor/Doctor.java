package com.quantumSamurais.hams.doctor;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.quantumSamurais.hams.appointment.Appointment;
import com.quantumSamurais.hams.appointment.Shift;
import com.quantumSamurais.hams.database.Database;
import com.quantumSamurais.hams.login.LoginInteractiveMessage;
import com.quantumSamurais.hams.user.User;
import com.quantumSamurais.hams.user.UserType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Doctor extends User {

	private String employeeNumber;
	private ArrayList<Specialties> specialties;
	private boolean acceptsAppointmentsByDefault = false;
	private ArrayList<Shift> shifts;

	public Doctor() {
	}

	public Doctor(String firstName, String lastName, char[] hashedPassword, String email,
				  String phone, String address, String employeeNumber, ArrayList<Specialties> specialties, boolean acceptsByDefault) {
		super(firstName, lastName, hashedPassword, email, phone, address);
		this.employeeNumber = employeeNumber;
		this.specialties = specialties;
		acceptsAppointmentsByDefault = acceptsByDefault;
		shifts = new ArrayList<>(); //In the empty case
		Database db = Database.getInstance();
		db.addSignUpRequest(this);
	}


	public Doctor(String firstName, String lastName, ArrayList<Integer> hashedPassword, ArrayList<Integer> salt, String email,
				  String phone, String address, String employeeNumber,ArrayList<Specialties> specialties, boolean acceptsByDefault) {
		super(firstName, lastName, hashedPassword, salt, email, phone, address);
		this.employeeNumber = employeeNumber;
		this.specialties = specialties;
		shifts = new ArrayList<>();
		acceptsAppointmentsByDefault = acceptsByDefault;
	}

	//When we read from DB
	public Doctor(String firstName, String lastName, ArrayList<Integer> hashedPassword, ArrayList<Integer> salt, String email,
				  String phone, String address, String employeeNumber,ArrayList<Specialties> specialties, boolean acceptsByDefault, ArrayList<Shift> shifts) {
		super(firstName, lastName, hashedPassword, salt, email, phone, address);
		this.employeeNumber = employeeNumber;
		this.specialties = specialties;
		this.shifts = shifts;
		acceptsAppointmentsByDefault = acceptsByDefault;
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

	public boolean getAcceptAppointmentsByDefault(){
		return acceptsAppointmentsByDefault;
	}

	public void setAcceptAppointmentsByDefault(boolean value){
		acceptsAppointmentsByDefault = value;
	}

	public ArrayList<Shift> getShifts() {
		return shifts;
	}
	
	@RequiresApi(api = Build.VERSION_CODES.O)
	public boolean createShift(LocalDate dayOfShift, LocalDateTime startDate, LocalDateTime endDate){
		Shift shiftToAdd = new Shift(this, dayOfShift, startDate, endDate);
		if (!shiftOverlap(shiftToAdd)){
			shifts.add(shiftToAdd);
			return true;
		}
		return false;
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	private boolean shiftOverlap(Shift someShift){
		for (Shift shift: shifts){
			if (!someShift.getStartTime().isAfter(shift.getEndTime()) && !shift.getStartTime().isAfter(someShift.getEndTime())){
				return true;
			}
		}
		return false;
	}
	public void cancelShift(long shiftID) throws IllegalStateException{
		// sadly, O(n), but shouldn't be an issue.
		for (Shift shift: shifts){
			if (shift.getShiftID() == shiftID){
				if (shift.isVacant()){
					shifts.remove(shift);
				}
				else {
					throw new IllegalStateException("Attempt at deleting a shift which contained appointments.");
				}
			}
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	public void acceptAppointment(Appointment appointment, long shiftID){
		for (Shift shift: shifts){
			if (shift.getShiftID() == shiftID){
				boolean appointmentWasAdded = shift.takeAppointment(appointment);
				if (!appointmentWasAdded){
					throw new IllegalStateException("The appointment passed overlaps with other appointments in the shift");
				}
			}
		}
		throw new IllegalArgumentException("This shift ID does not belong to any shifts of this doctor.");

	}


	public void cancelAppointment(long appointmentID){
		for (Shift shift:shifts){
			//this is fine, since it does nothing if the id does not exist.
			shift.cancelAppointment(appointmentID);
		}
	}

	public boolean hasThisShift(long shiftID){
		for (Shift shift: shifts){
			if (shift.getShiftID() == shiftID){
				return true;
			}
		}
		return false;
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
