package com.e.shelter.validation;

public class NameValidator {
    public NameValidator(){

    }


    public static boolean isValidName(String name){

            if(name == null || name.isEmpty()) return false;

            StringBuilder sb = new StringBuilder();
            boolean found = false;
            for(char c : name.toCharArray()){
                if(Character.isDigit(c)){
                    return false;
                }
            }

            return true;
        }

}
