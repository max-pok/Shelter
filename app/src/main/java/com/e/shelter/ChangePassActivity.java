package com.e.shelter;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassActivity extends MainActivity {
    private EditText oldpass;
    private EditText newpass;
    private EditText newpass2;
    private Button changeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepass);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        oldpass = (EditText) findViewById(R.id.oldpass);
        newpass = (EditText) findViewById(R.id.newpass);
        newpass2 = (EditText) findViewById(R.id.newpass2);
        changeBtn = (Button) findViewById(R.id.changepass_button);
        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                ChangePass(oldpass.getText().toString(),newpass.getText().toString(),newpass2.getText().toString());
            }
        });


    }

    public void ChangePass(String oldPass, final String newPass, final String newPass2) {

        if (newPass.equals(newPass2)) {
            final FirebaseUser user = firebaseAuth.getCurrentUser();
            AuthCredential credential = EmailAuthProvider
                    .getCredential(user.getEmail().toString(), oldPass);
// Prompt the user to re-provide their sign-in credentials
            assert user != null;
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isComplete()) {
                                            Toast.makeText(ChangePassActivity.this, "password changed", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.d("", "Error password not changed");
                                        }
                                    }
                                });
                            } else {
                                Log.d("", "Error auth failed");
                            }
                        }
                    });


        }
        else {
            Toast.makeText(ChangePassActivity.this, "The new passwords are not equal", Toast.LENGTH_SHORT).show();


        }

    }


}
