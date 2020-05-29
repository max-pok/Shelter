package com.e.shelter.validation;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class TextInputValidator {

    public static boolean isValidEditText(String string, TextInputEditText textInputEditText) {
        if (string.isEmpty()) {
            textInputEditText.setError("Please fill out this field");
            return false;
        }
        return true;
    }

    public static boolean isValidShelterName(final String string, final String oldName, final TextInputEditText textInputEditText) {
        if (string.isEmpty()) {
            textInputEditText.setError("Please fill out this field");
            return false;
        }
        if (oldName.equals(string)) {
            return true;
        }
        final boolean[] returnValue = new boolean[1];
        FirebaseFirestore.getInstance().collection("Shelters").whereEqualTo("name", string)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.get("name").equals(string)) {
                            textInputEditText.setError("Shelter name already exist");
                            returnValue[0] = false;
                        }
                    }
                }
            }
        });
        return returnValue[0];
    }

}
