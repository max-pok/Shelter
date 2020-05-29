package com.e.shelter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.e.shelter.utilities.Global;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;


public class EditUserActivity  extends MainActivity {

    private EditText nameEditText;
    private EditText addressEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    public Button update;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        nameEditText = findViewById(R.id.nameEdit);
        emailEditText = findViewById(R.id.emailEdit);
        phoneEditText = findViewById(R.id.phoneEdit);
        InitEditText();
        //update Button
        update = findViewById(R.id.updateButton);
        //Click on update button
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(EditUser()) {
                    Toast.makeText(EditUserActivity.this, "The update was successful", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(EditUserActivity.this, "The update was not successful", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


        public void InitEditText () {
        try {
            firebaseFirestore.collection("Users").document(firebaseAuth.getUid()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()) {
                                    nameEditText.setText(documentSnapshot.get("name").toString());
                                    phoneEditText.setText(documentSnapshot.get("phoneNumber").toString());
                                    emailEditText.setText(firebaseAuth.getCurrentUser().getEmail());

                                }
                            }

                        }
                    });
        }
        catch(Exception e){

            System.out.println("Error: "+ e);

        }

        }

        public Boolean EditUser() {
            try {
                firebaseFirestore.collection("Users").document(firebaseAuth.getUid()).update("name", nameEditText.getText().toString());
                firebaseFirestore.collection("Users").document(firebaseAuth.getUid()).update("phoneNumber", phoneEditText.getText().toString());
                firebaseAuth.getCurrentUser().updateEmail(emailEditText.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("", "User email address updated.");
                        } else {
                            Log.d("Error: ", "update Failed.");
                        }
                    }
                });
                firebaseFirestore.collection("FavoriteShelters").document(firebaseAuth.getUid()).update("email", emailEditText.getText().toString());
            }
            catch (Exception e){
                System.out.println("Error: "+e);

            }

            return true;
        }

}