package com.seg2105a.projectgroup7.hams.utils;

import static java.net.InetAddress.getByName;



import androidx.annotation.NonNull;
import java.net.UnknownHostException;

public class Validator {

    /**
     * Checks if the string that was passed by user is empty.
     * @param stringField: The string which we want to verify
     * @return {@code true} if yes, and {@code false} if it is NOT an empty string.
     */
        public static boolean textFieldIsEmpty(String stringField){
            return (stringField == null || stringField.isEmpty());
        }

    /**
     * Validates an email address by first checking if the domain associated with it exists, and then
     * checking if the local part has a suitable format (checked through regex)
     *
     * @param emailAddress: The email address to validate
     * @return {@code -1} if the domain is invalid, {@code -2} if the localPart is invalid, {@code 1} if everything is fine.
     */
        public static int emailAddressIsValid(@NonNull String emailAddress) {
            //Split the email address
            String[] splitEmail = emailAddress.split("@");
            String localPart = splitEmail[0];
            String domainPart = splitEmail[1];

            if (domainIsValid(domainPart)){
                if(localPart.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$")){
                    return 1;
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

        public static boolean passwordIsValid(@NonNull String password){
            return password.matches("^(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{8}$\n");
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

    /**
     * Validates a domain, by checking if there is an IP address associated with that hostname.
     * Makes use of INetAddress.getByName method.
     *
     * @param domain: The domain to validate
     * @return {@code true} if the domain is associated with an IP address, and {@code false} if not
     */
        private static boolean domainIsValid(String domain){
            try {
                getByName(domain);

            } catch(UnknownHostException e) {
                return false;
            }
            return true;

        }
    }




