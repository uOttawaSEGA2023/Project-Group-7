package com.quantumSamurais.hams.utils;

import static com.quantumSamurais.hams.user.UserType.DOCTOR;
import static com.quantumSamurais.hams.utils.ValidationTaskResult.ATTRIBUTE_ALREADY_REGISTERED;
import static com.quantumSamurais.hams.utils.ValidationTaskResult.ATTRIBUTE_IS_FREE_TO_USE;
import static com.quantumSamurais.hams.utils.ValidationTaskResult.VALID;
import static com.quantumSamurais.hams.utils.ValidationType.EMAIL_ADDRESS;
import static com.quantumSamurais.hams.utils.ValidationType.EMPLOYEE_ID;
import static com.quantumSamurais.hams.utils.ValidationType.HEALTH_CARD_NUMBER;
import static java.net.InetAddress.getByName;


import android.os.AsyncTask;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.quantumSamurais.hams.user.User;
import com.quantumSamurais.hams.user.UserType;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public final class Validator {
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static class DomainValidationTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... domains) {
            if (domains.length == 0) {
                return false; //No domains to validate
            }
            try {
                for (int i = 0; i < domains.length; i++) {
                    String domain = domains[i];
                    InetAddress address = InetAddress.getByName(domain);
                }
                return true; // Domain is/are all valid.
            } catch (UnknownHostException e) {
                return false; // At least one domain is not valid.
            }
        }
    }

    private static class IsInDatabaseTask extends AsyncTask<String, Void, ValidationTaskResult> {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        QuerySnapshot snap;
        String emailAddress;
        ValidationType validationType;
        UserType userType;

        IsInDatabaseTask(String emailAddress, ValidationType validationType, UserType userType){
            this.emailAddress = emailAddress;
            this.validationType = validationType;
            this.userType = userType;
        }


        @Override
        protected ValidationTaskResult doInBackground(String... valuesToCheckInDatabase)    {
            if (valuesToCheckInDatabase.length == 0) {
                return ValidationTaskResult.INVALID_FORMAT; //No email
            }
            // Verifies which database to check against
            switch (userType){
                case PATIENT:
                    try {
                        snap = Tasks.await(db.collection("users").document("software").collection("patients").get());
                    }
                    catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    break;

                case DOCTOR:
                    try {
                        snap = Tasks.await(db.collection("users").document("software").collection("doctors").get());
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case ADMIN:
                    break;
            }
            //
            switch(validationType){
                case EMAIL_ADDRESS:
                    for (int i = 0; i < valuesToCheckInDatabase.length; i++) {
                        for (QueryDocumentSnapshot profile : snap) {
                            Map<String, Object> userData = profile.getData();
                            if (Objects.equals(userData.get("emailAddress"), valuesToCheckInDatabase[i])) {
                                return ValidationTaskResult.ATTRIBUTE_ALREADY_REGISTERED;
                            }
                        }
                    }
                    break;
                case HEALTH_CARD_NUMBER:
                    for (int i = 0; i < valuesToCheckInDatabase.length; i++) {
                        for (QueryDocumentSnapshot profile : snap) {
                            Map<String, Object> userData = profile.getData();
                            if (Objects.equals(userData.get("healthCardNumber"), valuesToCheckInDatabase[i])) {
                                return ValidationTaskResult.ATTRIBUTE_ALREADY_REGISTERED;
                            }
                        }
                    }
                    break;
                case EMPLOYEE_ID:
                    for (int i = 0; i < valuesToCheckInDatabase.length; i++) {
                        for (QueryDocumentSnapshot profile : snap) {
                            Map<String, Object> userData = profile.getData();
                            if (Objects.equals(userData.get("employeeNumber"), valuesToCheckInDatabase[i])) {
                                return ValidationTaskResult.ATTRIBUTE_ALREADY_REGISTERED;
                            }
                        }
                    }
                    break;
            }
            return ATTRIBUTE_IS_FREE_TO_USE;
        }}


    /**
     * Checks if the string that was passed by user is empty.
     *
     * @param stringField: The string which we want to verify
     * @return {@code true} if yes, and {@code false} if it is NOT an empty string.
     */
    public static boolean textFieldIsEmpty(String stringField) {
        return (stringField == null || stringField.isEmpty());
    }

    public static boolean textFieldsAreEmpty(String... fields) {
        for (String i : fields) {
            if (i == null || i.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validates an email address by first checking if the domain associated with it exists, and then
     * checking if the local part has a suitable format (checked through regex)
     *
     * @param emailAddress: The email address to validate
     * @return {@code INVALID_FORMAT} if the email is not formatted as an email, {@code INVALID_DOMAIN} if the domain is invalid, {@code INVALID_LOCAL_EMAIL_ADDRESS} if the localPart is invalid, {@code VALID} if everything is fine.
     */

    public static ValidationTaskResult emailAddressIsValid(String emailAddress, UserType userType) throws ExecutionException, InterruptedException {
        //First check the email has a @
        if (!emailAddress.contains("@")) {
            return ValidationTaskResult.INVALID_FORMAT;
        }
        //Split the email address
        String[] splitEmail = emailAddress.split("@");

        //Check if the resulting string got a domain part to validate
        boolean proceed = splitEmail.length == 2;

        if (proceed) {
            String localPart = splitEmail[0];
            String domainPart = splitEmail[1];

            // Create and execute the DomainValidationTask
            DomainValidationTask validationTask = new DomainValidationTask();

            Boolean domainIsValid;

            domainIsValid = validationTask.execute(domainPart).get();

            if (domainIsValid) {
                if (!textFieldIsEmpty(localPart) && localPart.matches("^\\S+$")) {
                    ValidationTaskResult emailIsInDatabase = new IsInDatabaseTask(emailAddress, EMAIL_ADDRESS, userType).execute(new String[]{emailAddress}).get();
                    if (emailIsInDatabase== ATTRIBUTE_IS_FREE_TO_USE){
                        return VALID;
                    }
                    else{return ATTRIBUTE_ALREADY_REGISTERED;}
                }
                return ValidationTaskResult.INVALID_LOCAL_EMAIL_ADDRESS;
            }
            return ValidationTaskResult.INVALID_DOMAIN;


        }
        return ValidationTaskResult.INVALID_FORMAT;
    }

    private static void checkIfEmailExists(String emailAddress, UserType userType) {

    }


    /**
     * Validates a name to ensure it matches the regex pattern that would verify most names
     * (Sorry Elon's son, not yours)
     *
     * @param name: The name to validate
     * @return {@code true} if the string does match the pattern, {@code false} if not.
     */
    public static boolean nameIsValid(@NonNull String name) {
        //Name is not valid, if contains non-alphabetic characters (sorry Elon's son)
        return name.matches("^[a-zA-ZàáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ð ,.'-]+$");
    }

    public static boolean passwordIsValid(@NonNull String password) {
        return password.matches("^(?=[^a-z]*[a-z])(?=[^A-Z]*[A-Z])(?=\\D*\\d)(?=[^!@#$%^&\\*]*[!@#$%^&\\**])[A-Za-z0-9!@#$%^&\\**]{8,}$");
    }

    /**
     * Validates a phone number to ensure it consists of exactly 10 digits (0-9).
     *
     * @param phoneNumber: The phone number to be validated.
     * @return {@code true} if the phone number is valid (consists of exactly 10 digits), otherwise {@code false}.
     * @throws NullPointerException if {@code phoneNumber} is {@code null}.
     */
    public static boolean phoneNumberIsValid(@NonNull String phoneNumber) {
        return (phoneNumber.matches("[0-9]+") && phoneNumber.length() == 10);
    }


}







