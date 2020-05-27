package com.e.shelter.utilities;

public class StatusValidator {

    public static boolean isValidStatus(String status){
        return status.equals("open") || status.equals("close");
    }
}
