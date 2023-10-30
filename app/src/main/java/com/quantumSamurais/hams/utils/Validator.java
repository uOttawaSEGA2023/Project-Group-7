package com.quantumSamurais.hams.utils;

import static com.quantumSamurais.hams.user.UserType.DOCTOR;
import static com.quantumSamurais.hams.user.UserType.PATIENT;
import static com.quantumSamurais.hams.utils.ValidationTaskResult.ATTRIBUTE_ALREADY_REGISTERED;
import static com.quantumSamurais.hams.utils.ValidationTaskResult.VALID;
import static com.quantumSamurais.hams.utils.ValidationType.EMAIL_ADDRESS;
import static com.quantumSamurais.hams.utils.ValidationType.EMPLOYEE_NUMBER;
import static com.quantumSamurais.hams.utils.ValidationType.HEALTH_CARD_NUMBER;
import static com.quantumSamurais.hams.utils.ValidationType.PHONE_NUMBER;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.quantumSamurais.hams.database.Database;
import com.quantumSamurais.hams.user.UserType;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

public final class Validator {
    static Database dbTools = Database.getInstance();

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

                    if ((dbTools.checkUserIsInUsers(userType, EMAIL_ADDRESS, emailAddress) || dbTools.checkUserIsInRequests(userType, EMAIL_ADDRESS, emailAddress))) {
                        return ATTRIBUTE_ALREADY_REGISTERED;
                    } else {
                        return VALID;
                    }
                }
                return ValidationTaskResult.INVALID_LOCAL_EMAIL_ADDRESS;
            }
            return ValidationTaskResult.INVALID_DOMAIN;


        }
        return ValidationTaskResult.INVALID_FORMAT;
    }

    public static boolean checkIfHealthCardNumberExists(String healthCardNumber) throws ExecutionException, InterruptedException {
        return dbTools.checkUserIsInUsers(PATIENT, HEALTH_CARD_NUMBER, healthCardNumber) || dbTools.checkUserIsInRequests(PATIENT, HEALTH_CARD_NUMBER, healthCardNumber) ;

    }

    public static boolean checkIfEmployeeNumberExists(String employeeNumber) throws ExecutionException, InterruptedException {
        return dbTools.checkUserIsInUsers(DOCTOR, EMPLOYEE_NUMBER, employeeNumber) || dbTools.checkUserIsInRequests(DOCTOR, EMPLOYEE_NUMBER, employeeNumber) ;
    }

    public static boolean checkIfPhoneNumberExists(String phoneNumber, UserType userType) throws ExecutionException, InterruptedException {
        return dbTools.checkUserIsInUsers(userType, PHONE_NUMBER, phoneNumber) || dbTools.checkUserIsInRequests(userType, PHONE_NUMBER, phoneNumber);
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







