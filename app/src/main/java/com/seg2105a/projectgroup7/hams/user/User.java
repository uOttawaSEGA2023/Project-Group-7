package com.seg2105a.projectgroup7.hams.user;

import android.util.Log;

import com.seg2105a.projectgroup7.hams.admin.Administrator;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public abstract class User {

   // private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static List<Map<String,Object>> registeredUsers;
    private final UserType _userType;
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

    public User(String firstName, String lastName, char[] password, String email, String phone, String address, UserType userType) {
        _firstName = firstName;
        _lastName = lastName;
        _hashedPassword = hashPassword(password);
        _email = email;
        _phone = phone;
        _address = address;
        _userType = userType;
    }

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
        return new byte[0];
    }

    /*
    * signUp should generate a map with strings for keys and objects for values
    * the keys should match the names of the private variables, with the underscore removed.
    * And then this map should be added to the static registeredUsers list in this class
    **/
    protected abstract void signUp();
    protected abstract void changeView();

    public static LoginReturnCodes login(String email, char[] password) {

        Map<String, Object> userData = null;
        for (Map<String, Object> userDataStored : registeredUsers) {
            userData = userDataStored;
            if(!Objects.equals(userData.get("email"), email)) continue;
            if(Objects.equals(userData.get("password"), hashPassword(password))) {
                break;
            } else {
                return LoginReturnCodes.IncorrectPassword;
            }
        }
        if(userData != null) {
            switch ((UserType) Objects.requireNonNull(userData.get("userType"))) {
                case DOCTOR:
                    //Doctor doctor = new Doctor();
                    //doctor.changeView();
                    break;
                case PATIENT:
                    //Patient patient = new Patient();
                    //doctor.changeView();
                    break;
                case ADMIN:
                    Administrator.getInstance().changeView();
                    break;
            }
        } else {
            return LoginReturnCodes.UserDoesNotExist;
        }
        return LoginReturnCodes.Success;
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
        if(hashPassword(oldPassword).equals(_hashedPassword)) {
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
    public UserType getUserType() {
        return _userType;
    }
    // </editor-fold>
}
