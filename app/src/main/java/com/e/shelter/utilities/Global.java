package com.e.shelter.utilities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.e.shelter.map.MapViewActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public abstract class Global {
    protected FirebaseAuth firebaseAuth;
    protected FirebaseFirestore firebaseFirestore;

}
