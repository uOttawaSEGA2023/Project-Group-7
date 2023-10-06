package com.quantumSamurais.hams.doctor;

import com.quantumSamurais.hams.user.User;

import java.util.Set;

public class Doctor extends User {
	
	private String _employeeNumber;
	
	private Set<Specialties> _specialties;
	
	
	
	public Doctor(String firstName, String lastName, byte[] hashedPassword, String email,
			String phone, String address, String employeeNumber,Set<Specialties> specialties) {
			
			super(firstName, lastName, hashedPassword, email, phone, address);
			
			_employeeNumber = employeeNumber;
			_specialties = specialties;
	}


	public String get_employeeNumber() {
		return _employeeNumber;
	
	}
	
	public Set<Specialties> getSpecialties(){
		return _specialties;
	}

	@Override
	protected void signUp() {

	}

	@Override
	protected void changeView() {

	}
			
}
