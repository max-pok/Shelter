package com.e.shelter.validation;

public class StatusValidator {

    public static boolean isValidStatus(String status){
        return status.equals("open") || status.equals("close");
    }
}
