package com.e.shelter.validation;

import com.google.android.material.textfield.TextInputEditText;

public class TextInputValidator {

    public static boolean isValidEditText(String string, TextInputEditText textInputEditText) {
        if (string.isEmpty()) {
            textInputEditText.setError("Please fill out this field");
            return false;
        }
        return true;
    }

}
