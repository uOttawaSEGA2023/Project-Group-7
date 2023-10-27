package com.quantumSamurais.hams.login;

//Firebase
import android.content.Context;
import android.util.Log;

//App
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.quantumSamurais.hams.admin.Administrator;
import com.quantumSamurais.hams.doctor.Doctor;
import com.quantumSamurais.hams.doctor.Specialties;
import com.quantumSamurais.hams.patient.Patient;
import com.quantumSamurais.hams.user.User;
import com.quantumSamurais.hams.user.UserType;
import com.quantumSamurais.hams.utils.ArrayUtils;
//Java
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class Login {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    /**
     *
     * @param email The email to look for the user with
     * @param password The inputted password
     * @param userType The type of user trying to login
     * @param currentContext The current context so that we can switch the activity
     * @return Login codes that represent success or failure to login
     */
    public static void login(String email, char[] password, UserType userType, Context currentContext, LoginEventListener loginEventListener) {
        db.collection("users").document("software").collection("patients").whereEqualTo("email",email);
        new Thread(() -> {
            try {
                loginEventListener.loginResponse(
                        login(
                                email,
                                password,
                                userType,
                                currentContext,
                                Tasks.await(db.collection("users").document("software")
                                .collection("patients").whereEqualTo("email",email)
                                .get()),
                                Tasks.await(db.collection("users").document("software")
                                        .collection("doctors").whereEqualTo("email",email)
                                .get())));
            } catch (ExecutionException | InterruptedException e) {
                Log.d("Login Thread Error:", String.valueOf(e.getCause()));
            }
        }).start();
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
        User userData = null;
        User loggedInUser = null;
        byte[] salt= null;
        switch (userType) {
            case DOCTOR:
                userData = doctorList.getDocuments().get(0).toObject(Doctor.class);
                if(userData == null)
                    return LoginReturnCodes.USER_DOES_NOT_EXIST;
                if(!Arrays.equals(hashPassword(password,ArrayUtils.unpackBytes(userData.getSalt())), ArrayUtils.unpackBytes(userData.getPassword())))
                    return LoginReturnCodes.INCORRECT_PASSWORD;
                loggedInUser = userData;
                break;
            case PATIENT:
                userData = patientList.getDocuments().get(0).toObject(Patient.class);
                if(userData == null)
                    return LoginReturnCodes.USER_DOES_NOT_EXIST;

                if(!Arrays.equals(hashPassword(password,ArrayUtils.unpackBytes(userData.getSalt())), ArrayUtils.unpackBytes(userData.getPassword())))
                    return LoginReturnCodes.INCORRECT_PASSWORD;
                loggedInUser = userData;
                break;
            case ADMIN:
                userData = new Administrator();
                if(!userData.getEmail().equals(email))
                    return LoginReturnCodes.USER_DOES_NOT_EXIST;
                if(!Arrays.equals(hashPassword(password,ArrayUtils.unpackBytes(userData.getSalt())), ArrayUtils.unpackBytes(userData.getPassword())))
                    return LoginReturnCodes.INCORRECT_PASSWORD;
                loggedInUser = userData;
                break;
        }
        loggedInUser.changeView(currentContext);
        return LoginReturnCodes.SUCCESS;
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
