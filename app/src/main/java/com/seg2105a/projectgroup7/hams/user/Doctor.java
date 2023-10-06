
import java.util.Set;

public class Doctor extends User{
	
	private String employeeNumber;
	
	private Set<Specialty> specialties;
	
	
	
	public Doctor(String firstName, String lastName, byte[] hashedPassword, String email,
			String phone, String address, String employeeNumber,Set<Specialty> specialties) { 
			
			super(firstName, lastName, hashedPassword, email, phone, address);
			
			this.employeeNumber = employeeNumber;
			this.specialties = specialties;
	}


	public String getEmployeeNumber() {
		return employeeNumber;
	
	}
	
	public Set<Specialty> getSpecialties(){
		return specialties;
	}
	
	// Other abstract methods......
	
	public enum Specialty{
		FAMILY_MEDICINE,
		INTERNAL_MEDICINE,
		PEDIATRICS,
		OBSTETRICS,
		GYNECOLOGY
		
	}
			
}
