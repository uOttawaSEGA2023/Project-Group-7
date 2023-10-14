package com.quantumSamurais.hams.login;

//Firebase
import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
//App
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.ByteString;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class Login {

    public interface LoginEventListener {
        void loginResponse(LoginReturnCodes returnCode);
    }
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    /**
     *
     * @param email The email to look for the user with
     * @param password The inputted password
     * @param userType The type of user trying to login
     * @param currentContext The current context so that we can switch the activity
     * @return Login codes that represent success or failure to login
     */
    public static void login(String email, char[] password, UserType userType, Context currentContext, LoginEventListener loginEventListener) {
        new Thread(() -> loginEventListener.loginResponse(getUserData(email,password,userType,currentContext))).start();
    };

    private static LoginReturnCodes getUserData(String email, char[] password, UserType userType, Context currentContext) {
        QuerySnapshot snap = null;
        switch (userType) {
            case DOCTOR:
                try {
                    snap =  Tasks.await(db.collection("users").document("software").collection("doctors").get());
                } catch (ExecutionException | InterruptedException ignored) {
                }
                return login(email,password,UserType.DOCTOR,currentContext,null,snap);
            case PATIENT:
                try {
                    snap = Tasks.await(db.collection("users").document("software").collection("patients").get());
                } catch (ExecutionException | InterruptedException ignored) {
                }
                return login(email,password,UserType.PATIENT,currentContext,snap,null);
            case ADMIN:
                return login(email,password,UserType.ADMIN,currentContext,null,null);
        }
        return LoginReturnCodes.UserDoesNotExist;
    }
    private static byte[] passwordFromDatabase(Map<String,Object> userData) {
        Blob password = (Blob) userData.get("hashedPassword");
        assert password != null;
        byte[] pass = password.toBytes();
        return pass;
    }
    private static byte[] saltFromDatabase(Map<String, Object> userData) {
        Blob salt = (Blob) userData.get("salt");
        assert salt != null;
        return salt.toBytes();
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
                                         QuerySnapshot patientList, QuerySnapshot doctorList) {
        Map<String, Object> userData = null;
        User loggedInUser = null;
        byte[] salt= null;
;
        switch (userType) {
            case DOCTOR:
                userData = searchLoop(doctorList,email);
                if(userData == null)
                    return LoginReturnCodes.UserDoesNotExist;
                salt = saltFromDatabase(userData);
                if(!Arrays.equals(hashPassword(password,salt), passwordFromDatabase(userData)))
                    return LoginReturnCodes.IncorrectPassword;

                loggedInUser = new Doctor(
                        (String)userData.get("firstName"),
                        (String) userData.get("lastName"),
                        (byte[]) passwordFromDatabase(userData),
                        (byte[]) saltFromDatabase(userData),
                        (String)userData.get("email"),
                        (String)userData.get("phone"),
                        (String)userData.get("address"),
                        (String)userData.get("employeeNumber"),(ArrayList<Specialties>) userData.get("specialites")
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
                        (byte[]) passwordFromDatabase(userData),
                        (byte[]) saltFromDatabase(userData),
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
    private static Map<String, Object> searchLoop(QuerySnapshot toSearch, String emailToSearch) {
        for (QueryDocumentSnapshot document : toSearch) {
            Map<String, Object> userData = document.getData();
            if(!Objects.equals(userData.get("emailAddress"), emailToSearch)) continue;
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
        KeySpec spec = new PBEKeySpec(password, salt, 65536, 128);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            return factory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ignored) {
        }
        throw new BadPasswordException("Password was: " + Arrays.toString(password));
    }

}
