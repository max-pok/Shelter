package com.e.shelter.utilities;

public class PasswordValidator {

    public static boolean isValidPassword(String pass) {
        return pass != null && pass.length() >=8 && pass.length() <= 16;
    }
}
