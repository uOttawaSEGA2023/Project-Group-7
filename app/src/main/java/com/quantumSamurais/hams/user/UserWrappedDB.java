package com.quantumSamurais.hams.user;

import com.quantumSamurais.hams.doctor.Doctor;
import com.quantumSamurais.hams.patient.Patient;

import java.io.Serializable;
import java.util.HashMap;

public class UserWrappedDB {
    private Patient patient;
    private Doctor doctor;
    private User user;
    private HashMap<String,Serializable> mainData;
    private HashMap<String,Serializable> classData;
    private UserType curtype;

    public UserWrappedDB(){
        if(mainData==null){
            this.mainData= new HashMap<String,Serializable>();
        }
    }
    public UserWrappedDB(Patient patient){
        this.patient=patient;
        this.user=patient;
        this.curtype=UserType.PATIENT;
        if(mainData==null){
            this.mainData= new HashMap<String,Serializable>();
        }
        classData.put("firstName", Patient.getFirstName());
		classData.put("lastName", PatientgetLastName());
		classData.put("emailAddress", Patient.getEmail());
		classData.put("hashedPassword", Blob.fromBytes(Patient.getPassword()));
		classData.put("salt", Blob.fromBytes(Patient.getSalt()));
		classData.put("phoneNumber",Patient.getPhone());
        classData.put("postalAddress", Patient.getAddress());
        classData.put("healthCardNumber", Patient.getHealthCardNumber());
        
    }
    public UserWrappedDB(Doctor doctor){
        this.doctor=doctor;
        this.user=doctor;
        this.curtype=UserType.DOCTOR;
        if(mainData==null){
            this.mainData= new HashMap<String,Serializable>();
        }
        classData.put("firstName", Doctor.getFirstName());
		classData.put("lastName", Doctor.getLastName());
		classData.put("emailAddress", Doctor.getEmail());
		classData.put("hashedPassword", Blob.fromBytes(Doctor.getPassword()));
		classData.put("salt", Blob.fromBytes(Doctor.getSalt()));
		classData.put("phoneNumber",Doctor.getPhone());
		classData.put("postalAddress", Doctor.getAddress());
		classData.put("employeeNumber", Doctor.getEmployeeNumber());
		classData.put("specialties", Doctor.getSpecialties());
    }

    public HashMap<String,Serializable> getClassData(){
        return this.classData;
    }
    public void reconstruct(){
        if(curtype==UserType.PATIENT){
            Patient tempat = new Patient(classData.get("firstName"),
            classData.get("lastName"),
            classData.get("hashedPassword"),
            classData.get("salt"),
            classData.get("emailAddress"),
            classData.get("phoneNumber"),
            classData.get("postalAddress"),
            classData.get("healthCardNumber"));
            this.Patient=tempat;
        } else if(curtype==UserType.DOCTOR){
            Doctor doctemp = new Doctor(classData.get("firstName"),
            classData.get("lastName"),
            classData.get("hashedPassword"),
            classData.get("salt"),
            classData.get("emailAddress"),
            classData.get("phoneNumber"),
            classData.get("postalAddress"),
            classData.get("employeeNumber"),
            classData.get("specialties"))
        }
    }



    public UserWrappedDB(User user){
        this.user=user;
        if(mainData==null){
            this.mainData= new HashMap<String,Serializable>();
        }
    }
    public void userToPatient(){
        if(curtype==UserType.PATIENT){return;
        }else{
            this.patient= (Patient) user;
            curtype=UserType.PATIENT;
        }
    }
    public void userToDoctor(){
        if(curtype==UserType.DOCTOR){return;
        }else{
            this.doctor= (Doctor) user;
            curtype=UserType.DOCTOR;
        }
    }
    public void storeData(String key, Serializable data) throws Exception{
        if(curtype==null){throw new Exception("User type not defined");}

        if(mainData==null){
            this.mainData= new HashMap<String,Serializable>();
        }

        this.mainData.put(key,data);
    }
    public Serializable getData(String key) throws Exception{
        if(mainData==null){
            throw new Exception("Data hash is null");
        }
        if(this.mainData.get(key)==null){
            throw new Exception("Key not found");
        }
        return this.mainData.get(key);


    }

    public UserType getUserType() {
        return this.curtype;
    }

    public HashMap<String, Serializable> getMainData() {
        return mainData;
    }

}
