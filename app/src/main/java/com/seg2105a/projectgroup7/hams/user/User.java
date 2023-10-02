package com.seg2105a.projectgroup7.hams.user;

import android.util.Log;

import com.seg2105a.projectgroup7.hams.admin.Administrator;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public abstract class User {

   // private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static List<Map<String,Object>> registeredPatients = new LinkedList<>();
    public static List<Map<String,Object>> registeredDoctors = new LinkedList<>();
    private String _firstName;
    private String _lastName;


    private byte[] _hashedPassword;
    private String _email;
    private String _phone;
    private String _address;


//    public static void read() {
//        String TAG = "Firebase";
//        db.collection("users")
//                .get()
//                .addOnCompleteListener(task -> {
//
//                    if (task.isSuccessful()) {
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            Log.d(TAG, document.getId() + " => " + document.getData());
//                        }
//                    } else {
//                        Log.w(TAG, "Error getting documents.", task.getException());
//                    }
//                });
//    }

    /***
     *
     * @param firstName The user's first name
     * @param lastName The user's last name
     * @param password The user's password
     * @param email The user's email
     * @param phone The user's phone number
     * @param address The user's address
     */
    public User(String firstName, String lastName, char[] password, String email, String phone, String address) {
        _firstName = firstName;
        _lastName = lastName;
        _hashedPassword = hashPassword(password);
        _email = email;
        _phone = phone;
        _address = address;
    }
    /***
     *
     * @param firstName The user's first name
     * @param lastName The user's last name
     * @param hashedPassword The user's password hashed.
     * @param email The user's email
     * @param phone The user's phone number
     * @param address The user's address
     */
    public User(String firstName, String lastName, byte[] hashedPassword, String email, String phone, String address) {
        _firstName = firstName;
        _lastName = lastName;
        _hashedPassword = hashedPassword;
        _email = email;
        _phone = phone;
        _address = address;
    }

    /***
     *
     * @param password The password to hash
     * @return The password hashed using PBKDF2WithHmacSHA1
     */
    private static byte[] hashPassword(char[] password) {
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

    /*
    * signUp should generate a map with strings for keys and objects for values
    * the keys should match the names of the private variables, with the underscore removed.
    * And then this map should be added to the static registeredUsers list in this class
    **/
    protected abstract void signUp();
    protected abstract void changeView();

    //TODO: Remove login return codes.

    /***
     *
     * @param email The email to look for the user with
     * @param password The inputted password
     * @param userType The type of user trying to login
     * @return Login codes that represent success or failure to login
     */
    public static LoginReturnCodes login(String email, char[] password, UserType userType) {

        Map<String, Object> userData = null;
        User loggedInUser = null;
        switch (userType) {
            case DOCTOR:
                userData = searchLoop(registeredDoctors,email);
                if(userData != null) {
                    if(checkPassword(password,(byte[]) userData.get("password"))) {
                        //loggedInUser = new Doctor();
                    } else {
                        return LoginReturnCodes.IncorrectPassword;
                    }
                } else {
                    return LoginReturnCodes.UserDoesNotExist;
                }
                break;
            case PATIENT:
                userData = searchLoop(registeredPatients,email);
                if(userData != null) {
                    if(checkPassword(password,(byte[]) userData.get("password"))) {
                        //loggedInUser = new Doctor();
                    } else {
                        return LoginReturnCodes.IncorrectPassword;
                    }
                } else {
                    return LoginReturnCodes.UserDoesNotExist;
                }
                break;
            case ADMIN:
                if(Administrator.getInstance().getEmail().equals(email)) {
                    if(checkPassword(password,(byte[]) userData.get("password"))) {
                        loggedInUser = Administrator.getInstance();
                    } else {
                        return LoginReturnCodes.IncorrectPassword;
                    }
                } else {
                    return LoginReturnCodes.UserDoesNotExist;
                }
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
    private static boolean checkPassword(char[] password, byte[] hashedPassword) {
        return hashPassword(password) == hashedPassword;
    }

    //<editor-fold desc="Getters & Setters">
    public String getFirstName() {
        return _firstName;
    }

    public User setFirstName(String firstName) {
        _firstName = firstName;
        return this;
    }

    public String getLastName() {
        return _lastName;
    }

    public User setLastName(String lastName) {
        _lastName = lastName;
        return this;
    }

    public byte[] getPassword() {
        return _hashedPassword;
    }

    public User setPassword(char[] oldPassword, char[] newPassword) {
        if(Arrays.equals(hashPassword(oldPassword), _hashedPassword)) {
            _hashedPassword = hashPassword(newPassword);
        }
        return this;
    }

    public String getEmail() {
        return _email;
    }

    public User setEmail(String email) {
        _email = email;
        return this;
    }

    public String getPhone() {
        return _phone;
    }

    public User setPhone(String phone) {
        _phone = phone;
        return this;
    }

    public String getAddress() {
        return _address;
    }

    public User setAddress(String address) {
        _address = address;
        return this;
    }
    // </editor-fold>
}
