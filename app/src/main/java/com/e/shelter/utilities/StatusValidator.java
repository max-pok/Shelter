package com.e.shelter.utilities;

public class StatusValidator {

    public StatusValidator() {
    }

    private static boolean isValidStatus(String status){

        if (status=="open" || status== "close"){
            return true;
        }
        return false;
    }
}
