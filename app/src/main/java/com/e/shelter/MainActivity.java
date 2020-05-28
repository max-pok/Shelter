package com.e.shelter;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.e.shelter.map.MapViewActivity;
import com.e.shelter.utilities.Global;
import com.e.shelter.utilities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends Global {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        } else {
            DocumentReference docRef = firebaseFirestore.collection("Users").document(firebaseUser.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        User user = task.getResult().toObject(User.class);
                        Intent i = new Intent(MainActivity.this, MapViewActivity.class);
                        i.putExtra("uid", firebaseAuth.getUid());
                        i.putExtra("full_name", user.getName());
                        i.putExtra("permission", user.getPermission());
                        startActivity(i);
                    }
                }
            });

        }
    }

}
