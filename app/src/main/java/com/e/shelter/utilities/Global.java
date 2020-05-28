package com.e.shelter.utilities;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public abstract class Global extends AppCompatActivity {
    protected FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    protected FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    protected void hideSoftKeyboard() {
        if (this.getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
    }
}
