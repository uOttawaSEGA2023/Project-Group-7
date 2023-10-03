package com.seg2105a.projectgroup7.hams.utils;

import static java.net.InetAddress.getByName;



import androidx.annotation.NonNull;
import java.net.UnknownHostException;

public class Validator {

        public static boolean textFieldIsEmpty(String stringField){
            return (stringField == null || stringField.isEmpty());
        }

    /**
     * @param emailAddress
     * @return -1 if the domain is invalid, -2 if the localPart is invalid, 1 if everything is fine.
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
     * @param name  : The name to validate
     * @return true if the string is a name, false if not.
     */
        public static boolean nameIsValid(@NonNull String name) {
            //Name is not valid, if contains non-alphabetic characters (sorry Elon's son)
            return name.matches("^[a-zA-ZàáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ð ,.'-]+$");
        }

        public static boolean passwordIsValid(@NonNull String password){
            return password.matches("^(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{8}$\n");
        }

        public static boolean phoneNumberIsValid(@NonNull String phoneNumber){
            throw new IllegalStateException("Not yet implemented");
        }

        public static boolean postalAddressIsValid(@NonNull String postalAddress){
            throw new IllegalStateException("Not yet implemented");
        }

        public static boolean healthCardNumberIsValid(@NonNull String healthCardNumber){
            throw new IllegalStateException("Not yet implemented");
        }

        public static boolean employeeNumberIsValid(@NonNull String employeeNumber){
            throw new IllegalStateException("Not yet implemented");
        }

    /**
     * @param domain
     * @return true if the domain is associated with an IP address, and false if not
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




