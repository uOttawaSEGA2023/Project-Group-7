import java.util.HashMap;

public class UserWrappedDB {
    private Pateint patient;
    private Doctor doctor;
    private User user;
    private HashMap<String,E ? implements Serializable> mainData;
    private UserType curtype;

    public UserWrappedDB(){
        if(mainData==null){
            this.mainData= new HashMap<String,E ? implements Serializable>;
        }
    }
    public UserWrappedDB(Pateint patient){
        this.patient=patient;
        this.user=patient;
        this.curtype=PATIENT;
        if(mainData==null){
            this.mainData= new HashMap<String,E ? implements Serializable>;
        }
        
    }
    public UserWrappedDB(Doctor doctor){
        this.doctor=doctor;
        this.user=doctor;
        this.curtype=DOCTOR;
        if(mainData==null){
            this.mainData= new HashMap<String,E ? implements Serializable>;
        }
    }
    public UserWrappedDB(User user){
        this.user=user;
        if(mainData==null){
            this.mainData= new HashMap<String,E ? implements Serializable>;
        }
    }
    public void userToPatient(){
        if(curtype==PATIENT){return;
        }else{
            this.patient= (Pateint) user;
            curtype=PATIENT;
        }
    }
    public void userToDoctor(){
        if(curtype==DOCTOR){return;
        }else{
            this.DOCTOR= (DOCTOR) user;
            curtype=DOCTOR;
        }
    }
    public void storeData(String key, E implements Serializable data) throws Exception{
        if(curtype==null){throw new Exception("User type not defined")}

        if(mainData==null){
            this.mainData= new HashMap<String,E ? implements Serializable>;
        }

        this.mainData.put(key,data);
    }
    public E getData(String key) throws Exception{
        if(mainData==null){
            throw new Exception("Data hash is null");
        }
        if(this.mainData.get(key)==null){
            throw new Exception("Key not found")
        }
        return this.mainData.get(key);


    }


}
