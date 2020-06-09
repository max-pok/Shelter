package com.e.shelter.validation;

public class PhoneValidator {
    public PhoneValidator(){}

    public static boolean isValidPhone(String phone){

        if (phone == null || phone.isEmpty()) return false;

        StringBuilder sb = new StringBuilder();
        boolean found = false;
        for (char c : phone.toCharArray()){
            if (Character.isAlphabetic(c)){
                return false;
            }
        }

        return true;
    }

}
