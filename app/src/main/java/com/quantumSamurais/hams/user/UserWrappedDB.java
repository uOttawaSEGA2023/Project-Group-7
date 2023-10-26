package com.quantumSamurais.hams.user;

import com.google.firebase.firestore.Blob;
import com.quantumSamurais.hams.doctor.Doctor;
import com.quantumSamurais.hams.doctor.Specialties;
import com.quantumSamurais.hams.patient.Patient;
import com.quantumSamurais.hams.utils.ArrayUtils;

import org.checkerframework.checker.units.qual.A;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class UserWrappedDB {
    private Patient patient;
    private Doctor doctor;
    private User user;
    private HashMap<String,Serializable> mainData;
    private HashMap<String,Serializable> classData;
    private UserType curtype = UserType.PATIENT;

    public UserWrappedDB(){
        if(mainData==null){
            this.mainData= new HashMap<String,Serializable>();
        }
    }
    public UserWrappedDB(Patient patient){
        this.patient=patient;
        this.user=patient;
        this.curtype=UserType.PATIENT;
        this.classData = new HashMap<>();
        if(mainData==null){
            this.mainData= new HashMap<String,Serializable>();
        }
        classData.put("firstName", patient.getFirstName());
		classData.put("lastName", patient.getLastName());
		classData.put("emailAddress", patient.getEmail());
		classData.put("hashedPassword", ArrayUtils.packBytes(patient.getPassword()));
		classData.put("salt", ArrayUtils.packBytes(patient.getSalt()));
		classData.put("phoneNumber",patient.getPhone());
        classData.put("postalAddress", patient.getAddress());
        classData.put("healthCardNumber", patient.getHealthCardNumber());
    }


    public UserWrappedDB(Doctor doctor){
        this.doctor=doctor;
        this.user=doctor;
        this.curtype=UserType.DOCTOR;
        this.classData = new HashMap<>();

        if(mainData==null){
            this.mainData= new HashMap<String,Serializable>();
        }
        classData.put("firstName", doctor.getFirstName());
		classData.put("lastName", doctor.getLastName());
		classData.put("emailAddress", doctor.getEmail());
		classData.put("hashedPassword", ArrayUtils.packBytes(doctor.getPassword()));
		classData.put("salt", ArrayUtils.packBytes(doctor.getSalt()));
		classData.put("phoneNumber",doctor.getPhone());
		classData.put("postalAddress", doctor.getAddress());
		classData.put("employeeNumber", doctor.getEmployeeNumber());
		classData.put("specialties", doctor.getSpecialties());
    }

    public HashMap<String,Serializable> getClassData(){
        return this.classData;
    }
    public void reconstruct(){
        if(curtype==UserType.PATIENT){
            Patient tempat = new Patient((String) classData.get("firstName"),
                    (String) classData.get("lastName"),
                    (byte[]) classData.get("hashedPassword"),
                    (byte[]) classData.get("salt"),
                    (String) classData.get("emailAddress"),
                    (String) classData.get("phoneNumber"),
                    (String) classData.get("postalAddress"),
                    (String) classData.get("healthCardNumber"));
            this.patient=tempat;
        } else if(curtype==UserType.DOCTOR){
            Doctor doctemp = new Doctor((String) classData.get("firstName"),
                    (String) classData.get("lastName"),
                    (byte[]) classData.get("hashedPassword"),
                    (byte[]) classData.get("salt"),
                    (String) classData.get("emailAddress"),
                    (String) classData.get("phoneNumber"),
                    (String) classData.get("postalAddress"),
                    (String) classData.get("employeeNumber"),
                    (ArrayList<Specialties>) classData.get("specialties"));
            this.doctor=doctemp;
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
