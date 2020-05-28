package com.e.shelter;

import android.content.Intent;
import android.os.Bundle;

import com.e.shelter.utilities.Global;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends Global {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }


}
