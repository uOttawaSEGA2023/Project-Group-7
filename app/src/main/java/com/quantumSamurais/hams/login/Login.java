package com.quantumSamurais.hams.login;

//Firebase
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
//App
import com.quantumSamurais.hams.admin.Administrator;
import com.quantumSamurais.hams.doctor.Doctor;
import com.quantumSamurais.hams.doctor.Specialties;
import com.quantumSamurais.hams.patient.Patient;
import com.quantumSamurais.hams.user.User;
import com.quantumSamurais.hams.user.UserType;
//Java
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class Login {

//   public boolean loginSuccess(){
//
//        boolean success = false;
//       final FirebaseDatabase db = FirebaseDatabase.getInstance();
//       DatabaseReference ref=db.getReference("Email");
//       String dbPass;
//
//        ref.child(this.email).addOnComplete(new OnCompleteListener<DataSnapshot>(){
//            @Override
//            public void onComplete(Task<DataSnapshot> Task){
//                if(task.isSuccessful()){
//                    if(task.getResult().exists()){
//                        Datasnapshot datasnapshot = task.getResult();
//                        String dbPass=String.valueOf(datasnapshot.child("password").getValue());
//                    }
//                }
//            }
//        })
//       if(dbPass!=null && dbPass==this.password){
//           success=true;
//
//
//       }
//       return success;
//
//   }

//   public String loginSuccessPrompt(){
//       if(loginSuccess()){
//           return "Login successful: Welcome to HAMS";
//       } else{
//           return "Login unsuccessful: Please try again";
//
//       }
//   }
    public static LoginReturnCodes login(String email, char[] password, UserType userType) {
        return  login(email,password,userType,User.registeredPatients,User.registeredDoctors);
    };

    /**
     *
     * @param email The email to look for the user with
     * @param password The inputted password
     * @param userType The type of user trying to login
     * @return Login codes that represent success or failure to login
     */
    private static LoginReturnCodes login(String email, char[] password, UserType userType,
                                         List<Map<String, Object>> patientList, List<Map<String, Object>> doctorList) {
        Map<String, Object> userData = null;
        User loggedInUser = null;
        switch (userType) {
            case DOCTOR:
                userData = searchLoop(doctorList,email);
                if(userData == null)
                    return LoginReturnCodes.UserDoesNotExist;
                if(hashPassword(password) != userData.get("password"))
                    return LoginReturnCodes.IncorrectPassword;

                loggedInUser = new Doctor(
                        (String)userData.get("firstName"),
                        (String) userData.get("lastName"),
                        (byte[]) userData.get("password"),
                        (String)userData.get("email"),
                        (String)userData.get("phone"),
                        (String)userData.get("address"),
                        (String)userData.get("employeeNumber"),(Set<Specialties>) userData.get("specialites")
                );
                break;
            case PATIENT:
                userData = searchLoop(patientList,email);
                if(userData == null)
                    return LoginReturnCodes.UserDoesNotExist;
                if(hashPassword(password) != userData.get("password"))
                    return LoginReturnCodes.IncorrectPassword;

                loggedInUser = new Patient(
                        (String)userData.get("firstName"),
                        (String) userData.get("lastName"),
                        (byte[]) userData.get("password"),
                        (String)userData.get("email"),
                        (String)userData.get("phone"),
                        (String)userData.get("address"),
                        (String) userData.get("healthCardNumber")
                );
                break;
            case ADMIN:
                if(!Administrator.getInstance().getEmail().equals(email))
                    return LoginReturnCodes.UserDoesNotExist;
                if(hashPassword(password) != userData.get("password"))
                    return LoginReturnCodes.IncorrectPassword;

                loggedInUser = Administrator.getInstance();
                break;
        }
        loggedInUser.changeView();
        return LoginReturnCodes.Success;
    }
    private static Map<String, Object> searchLoop(List<Map<String, Object>> toSearch, String emailToSearch) {
        for (Map<String, Object> userData : toSearch) {
            if(!Objects.equals(userData.get("email"), emailToSearch)) continue;
            return userData;
        }
        return null;
    }

    /**
     *
     * @param password The password to hash
     * @return The password hashed using PBKDF2WithHmacSHA1
     */
    public static byte[] hashPassword(char[] password) {
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        KeySpec spec = new PBEKeySpec(password, salt, 65536, 128);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            return factory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ignored) {
        }
        throw new BadPasswordException("Password was: " + Arrays.toString(password));
    }

}
