package com.quantumSamurais.hams.login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;
public class Login {
   private String email;
   private String password;


   public Login(String email, String password){
       this.email=email;
       this.password=password;
   }

   public boolean loginSuccess(){

        boolean success = false;
       final FirebaseDatabase db = FirebaseDatabase.getInstance();
       DatabaseReference ref=db.getReference("Email");
       String dbPass;

        ref.child(this.email).addOnComplete(new OnCompleteListener<DataSnapshot>(){
            @Override
            public void onComplete(Task<DataSnapshot> Task){
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        Datasnapshot datasnapshot = task.getResult();
                        String dbPass=String.valueOf(datasnapshot.child("password").getValue());
                    }
                }
            }
        })
       if(dbPass!=null && dbPass==this.password){
           success=true;


       }
       return success;

   }
   public void changeView(){
       //to be implemented
   }

   public String loginSuccessPrompt(){
       if(loginSuccess()==true){
           changeView();
           return "Login successful: Welcome to HAMS";
       } else{
           return "Login unsuccessful: Please try again";

       }
   }


}
