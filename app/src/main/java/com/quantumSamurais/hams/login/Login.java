package com.quantumSamurais.hams.login;

//Firebase
import android.content.Context;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
//App
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class Login {

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    /**
     *
     * @param email The email to look for the user with
     * @param password The inputted password
     * @param userType The type of user trying to login
     * @param currentContext The current context so that we can switch the activity
     * @return Login codes that represent success or failure to login
     */
    public static LoginReturnCodes login(String email, char[] password, UserType userType, Context currentContext) {
        return  login(email,password,userType,currentContext,User.registeredPatients,User.registeredDoctors);
    };

    private static User getUserData(UserType userType) {
        //db code
        dbcom L = new dbcom("123@gmail.com",userType);
        while(!L.isDataReady()) {}
        return L.getUser();

    }
    private static byte[] saltFromDatabase(Map<String, Object> userData) {
        return null;
    }


    /**
     *
     * @param email The email to look for the user with
     * @param password The inputted password
     * @param userType The type of user trying to login
     * @param currentContext The current context so that we can switch the activity
     * @param patientList List of registered patients
     * @param doctorList List of registered doctors
     * @return Login codes that represent success or failure to login
     */
    private static LoginReturnCodes login(String email, char[] password, UserType userType, Context currentContext,
                                         List<Map<String, Object>> patientList, List<Map<String, Object>> doctorList) {
        Map<String, Object> userData = null;
        User loggedInUser = null;
        byte[] salt= null;
        switch (userType) {
            case DOCTOR:
                userData = searchLoop(doctorList,email);
                salt = saltFromDatabase(userData);
                if(!Arrays.equals(hashPassword(password,salt), passwordFromDatabase(userData)))
                    return LoginReturnCodes.IncorrectPassword;

                loggedInUser = new Doctor(
                        (String)userData.get("firstName"),
                        (String) userData.get("lastName"),
                        (byte[]) userData.get("password"),
                        (String)userData.get("email"),
                        (String)userData.get("phone"),
                        (String)userData.get("address"),
                        (String)userData.get("employeeNumber"),(EnumSet<Specialties>) userData.get("specialites")
                );
                break;
            case PATIENT:
                userData = searchLoop(patientList,email);
                if(userData == null)
                    return LoginReturnCodes.UserDoesNotExist;

                salt = saltFromDatabase(userData);
                if(!Arrays.equals(hashPassword(password,salt), passwordFromDatabase(userData)))
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
                User admin = new Administrator();
                salt=admin.getSalt();
                if(!admin.getEmail().equals(email))
                    return LoginReturnCodes.UserDoesNotExist;
                if(!Arrays.equals(hashPassword(password,salt), admin.getPassword()))
                    return LoginReturnCodes.IncorrectPassword;

                loggedInUser = admin;
                break;
        }
        loggedInUser.changeView(currentContext);
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
    public static byte[] hashPassword(char[] password, byte[] salt) {
      //  byte[] salt = new byte[16];
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
