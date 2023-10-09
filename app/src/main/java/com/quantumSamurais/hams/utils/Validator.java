package com.quantumSamurais.hams.utils;

import static java.net.InetAddress.getByName;


import android.os.AsyncTask;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

public final class Validator {
    //Inner Class

    /**
     *
     */
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
    public static boolean textFieldsAreEmpty(String ...fields) {
        for(String i : fields) {
            if(i == null || i.isEmpty()) {
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
     * @return {@code -1} if the email is not formatted as an email, {@code -2} if the domain is invalid, {@code -3} if the localPart is invalid, {@code 1} if everything is fine.
     */

    public static int emailAddressIsValid(@NonNull String emailAddress) throws ExecutionException, InterruptedException {
        //First check the email has a @
        if (!emailAddress.contains("@")) {
            return -1;
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
                    return 1;
                }
                return -3;
            }
            return -2;


        }
        return -1;
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







