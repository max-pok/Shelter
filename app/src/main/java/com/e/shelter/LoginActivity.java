package com.e.shelter;

import androidx.annotation.NonNull;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.e.shelter.map.MapViewActivity;
import com.e.shelter.validation.EmailValidator;
import com.e.shelter.validation.PasswordValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class LoginActivity extends MainActivity implements View.OnClickListener {
    public static String email;
    public static String password;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;

    private Boolean skipLogin = false;
    private ProgressBar loadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passInput);
        loadingProgressBar = findViewById(R.id.news_loading_spinner);
        loadingProgressBar.setVisibility(View.INVISIBLE);

        MaterialButton LoginButton = findViewById(R.id.LoginButton);
        LoginButton.setOnClickListener(this);
        MaterialButton register = findViewById(R.id.signUpButton);
        register.setOnClickListener(this);

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            Intent intent = new Intent(LoginActivity.this, MapViewActivity.class);
            intent.putExtra("uid", firebaseUser.getUid());
            intent.putExtra("full_name", firebaseUser.getDisplayName());
            intent.putExtra("email", firebaseUser.getEmail());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    public void signIn() {
        if (skipLogin) {
            firebaseAuth.signInWithEmailAndPassword("adirat@ac.sce.il", "123456")
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                loadingProgressBar.setVisibility(View.INVISIBLE);
                                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                Intent intent = new Intent(LoginActivity.this, MapViewActivity.class);
                                intent.putExtra("uid", firebaseUser.getUid());
                                intent.putExtra("full_name", firebaseUser.getDisplayName());
                                intent.putExtra("email", firebaseUser.getEmail());
                                startActivity(intent);
                            } else {
                                loadingProgressBar.setVisibility(View.INVISIBLE);
                                Log.d("Log In", "onFailure: " + task.getException().getMessage());
                                Toast.makeText(LoginActivity.this, "Login Failed. Try again.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else {
            firebaseAuth.signInWithEmailAndPassword(emailInput.getText().toString(), passwordInput.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(LoginActivity.this, MapViewActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                loadingProgressBar.setVisibility(View.INVISIBLE);
                                startActivity(intent);
                                finish();
                            } else {
                                loadingProgressBar.setVisibility(View.INVISIBLE);
                                Exception e = task.getException();
                                Log.d("Log In", "onFailure: " + e.getMessage());
                                Toast.makeText(LoginActivity.this, "Login Failed. Try again.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
    public void checkIfBlocked(){
        email = Objects.requireNonNull(emailInput.getText()).toString();
        password = Objects.requireNonNull(passwordInput.getText()).toString();
        if (EmailValidator.isValidEmailTextInputEditText(email, emailInput)
                & PasswordValidator.isValidEmailTextInputEditText(password, passwordInput)) {
            firebaseFirestore.collection("Users").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isComplete()) {
                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                    if (queryDocumentSnapshot.get("email").toString().equals(email)) {
                                        if (!queryDocumentSnapshot.getBoolean("blocked")) {
                                            signIn();
                                            break;
                                        }
                                        else {
                                            Toast.makeText(LoginActivity.this, "Login Failed. Try again.", Toast.LENGTH_LONG).show();
                                            loadingProgressBar.setVisibility(View.INVISIBLE);
                                            break;
                                        }
                                    }
                                }
                                Toast.makeText(LoginActivity.this, "Login Failed. Try again.", Toast.LENGTH_LONG).show();
                                loadingProgressBar.setVisibility(View.INVISIBLE);
                            }
                            else {
                                Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else {
            loadingProgressBar.setVisibility(View.INVISIBLE);
            hideSoftKeyboard();
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i) {
            case R.id.LoginButton:
                loadingProgressBar.setVisibility(View.VISIBLE);
                checkIfBlocked();
                //signIn();
                break;
            case  R.id.signUpButton:
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
        }
    }

}
