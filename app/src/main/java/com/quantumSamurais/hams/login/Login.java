package com.quantumSamurais.hams.login;

//Firebase
import android.content.Context;
import android.util.Log;

//App
import com.quantumSamurais.hams.admin.Administrator;
import com.quantumSamurais.hams.database.DatabaseUtils;
import com.quantumSamurais.hams.database.RequestStatus;
import com.quantumSamurais.hams.database.callbacks.ResponseListener;
import com.quantumSamurais.hams.doctor.Doctor;
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

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class Login implements ResponseListener<RequestStatus> {

    LoginEventListener listener;
    String email;
    char[] password;
    UserType userType;

    Context currentContext;

    DatabaseUtils db;


    public Login(String email, char[] password, UserType userType, Context currentContext, LoginEventListener loginEventListener) {
        this.email = email;
        this.password = password;
        this.userType = userType;
        this.currentContext = currentContext;
        this.listener = loginEventListener;
        this.db = new DatabaseUtils();
    }



    public void attemptLogin() {
        db.getRequestStatus(email,userType,this);
    }

    /**
     *
     * @param email The email to look for the user with
     * @param password The inputted password
     * @param userType The type of user trying to login
     * @param currentContext The current context so that we can switch the activity
     * @return Login codes that represent success or failure to login
     */
    private LoginStatusCodes attemptLogin(String email, char[] password, UserType userType, Context currentContext, ArrayList<Patient> patientList, ArrayList<Doctor> doctorList) {
        User userData = null;
        User loggedInUser = null;
        byte[] salt = null;
        switch (userType) {
            case DOCTOR:
                userData = doctorList.get(0);
                if(userData == null)
                    return LoginStatusCodes.USER_DOES_NOT_EXIST;
                if(!Arrays.equals(hashPassword(password,ArrayUtils.unpackBytes(userData.getSalt())), ArrayUtils.unpackBytes(userData.getPassword())))
                    return LoginStatusCodes.INCORRECT_PASSWORD;
                loggedInUser = userData;
                break;
            case PATIENT:
                userData = patientList.get(0);
                if(userData == null)
                    return LoginStatusCodes.USER_DOES_NOT_EXIST;

                if(!Arrays.equals(hashPassword(password,ArrayUtils.unpackBytes(userData.getSalt())), ArrayUtils.unpackBytes(userData.getPassword())))
                    return LoginStatusCodes.INCORRECT_PASSWORD;
                loggedInUser = userData;
                break;
            case ADMIN:
                userData = new Administrator();
                if(!userData.getEmail().equals(email))
                    return LoginStatusCodes.USER_DOES_NOT_EXIST;
                if(!Arrays.equals(hashPassword(password,ArrayUtils.unpackBytes(userData.getSalt())), ArrayUtils.unpackBytes(userData.getPassword())))
                    return LoginStatusCodes.INCORRECT_PASSWORD;
                loggedInUser = userData;
                break;
        }
        loggedInUser.changeView(currentContext);
        return LoginStatusCodes.SUCCESS;
    }

    @Override
    public void onSuccess(RequestStatus requestStatus) {
        switch (requestStatus) {
            case APPROVED:
                switch (userType) {
                    case PATIENT:
                        db.getPatients(new ResponseListener<ArrayList<Patient>>() {
                            @Override
                            public void onSuccess(ArrayList<Patient> data) {
                                listener.loginResponse(attemptLogin(email,password,userType,currentContext,data, null),requestStatus);
                            }
                            @Override
                            public void onFailure(Exception error) {
                                Log.d("Login", "Database Access Error");
                            }
                        });
                        break;
                    case DOCTOR:
                        db.getDoctors(new ResponseListener<ArrayList<Doctor>>() {
                            @Override
                            public void onSuccess(ArrayList<Doctor> data) {
                                listener.loginResponse(attemptLogin(email,password,userType,currentContext,null, data),requestStatus);
                            }

                            @Override
                            public void onFailure(Exception error) {
                                Log.d("Login", "Database Access Error");
                            }
                        });
                        break;
                    case ADMIN:
                        listener.loginResponse(attemptLogin(email,password,userType,currentContext,null, null),requestStatus);
                        break;
                }
                break;
            case PENDING:
            case REJECTED:
                listener.loginResponse(LoginStatusCodes.REQUEST_ERROR,requestStatus);
                break;
        }
    }

    @Override
    public void onFailure(Exception error) {
        Log.d("Login", "Database Access Error");
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
