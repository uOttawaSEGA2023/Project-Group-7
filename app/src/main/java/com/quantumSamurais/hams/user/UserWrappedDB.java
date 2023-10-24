import java.util.HashMap;

public class UserWrappedDB {
    private Pateint patient;
    private Doctor doctor;
    private User user;
    private HashMap<Object,Object> mainData;
    private UserType curtype;

    public UserWrappedDB(){
        if(mainData==null){
            this.mainData= new HashMap<Object,Object>;
        }
    }
    public UserWrappedDB(Pateint patient){
        this.patient=patient;
        this.user=patient;
        this.curtype=PATIENT;
        if(mainData==null){
            this.mainData= new HashMap<Object,Object>;
        }
        
    }
    public UserWrappedDB(Doctor doctor){
        this.doctor=doctor;
        this.user=doctor;
        this.curtype=DOCTOR;
        if(mainData==null){
            this.mainData= new HashMap<Object,Object>;
        }
    }
    public UserWrappedDB(User user){
        this.user=user;
        if(mainData==null){
            this.mainData= new HashMap<Object,Object>;
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
    public void storeData(Object key, Object data) throws Exception{
        if(curtype==null){throw new Exception("User type not defined")}

        if(mainData==null){
            this.mainData= new HashMap<Object,Object>;
        }

        this.mainData.put(key,data);
    }
    public Object getData(Object key) throws Exception{
        if(mainData==null){
            throw new Exception("Data hash is null");
        }
        if(this.mainData.get(key)==null){
            throw new Exception("Key not found")
        }
        return this.mainData.get(key);


    }


}
