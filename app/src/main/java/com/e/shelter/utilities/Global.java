package com.e.shelter.utilities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.e.shelter.map.MapViewActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public abstract class Global extends AppCompatActivity {
    protected FirebaseAuth firebaseAuth;
    protected FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            Intent intent = new Intent(Global.this, MapViewActivity.class);
            intent.putExtra("uid", firebaseUser.getUid());
            intent.putExtra("full_name", firebaseUser.getDisplayName());
            intent.putExtra("email", firebaseUser.getEmail());
            startActivity(intent);
        }
    }
}
