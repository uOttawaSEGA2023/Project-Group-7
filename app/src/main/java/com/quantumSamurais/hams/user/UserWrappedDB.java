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
        
    }
    public UserWrappedDB(Doctor doctor){
        this.doctor=doctor;
        this.user=doctor;
        this.curtype=UserType.DOCTOR;
        if(mainData==null){
            this.mainData= new HashMap<String,Serializable>();
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
