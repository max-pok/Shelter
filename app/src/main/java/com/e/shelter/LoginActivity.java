package com.e.shelter;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.e.shelter.map.MapViewActivity;
import com.e.shelter.utilities.Global;
import com.e.shelter.validation.EmailValidator;
import com.e.shelter.validation.PasswordValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;

public class LoginActivity extends Global implements View.OnClickListener {
    public static String email;
    public static String password;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passInput);

        //Login Button
        MaterialButton LoginButton = findViewById(R.id.LoginButton);
        LoginButton.setOnClickListener(this);

        MaterialButton register = findViewById(R.id.signUpButton);
        register.setOnClickListener(this);
    }

    public void signIn() {
        email = emailInput.getText().toString();
        password = passwordInput.getText().toString();
        if (EmailValidator.isValidEmailTextInputEditText(email, emailInput)
                & PasswordValidator.isValidEmailTextInputEditText(password, passwordInput)) {

            firebaseAuth.signInWithEmailAndPassword(emailInput.getText().toString(), passwordInput.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(LoginActivity.this, MapViewActivity.class);
                                intent.putExtra("uid", firebaseAuth.getUid());
                                startActivity(intent);
                            } else {
                                Exception e = task.getException();
                                Log.d("Log In", "onFailure: " + e.getMessage());
                                Toast.makeText(LoginActivity.this, "Login Failed. Try again.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else {
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(LoginActivity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i) {
            case R.id.LoginButton:
                signIn();
                break;
            case  R.id.signUpButton:
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
        }
    }

    public String getEmail(){
        return email;
    }

    public  void setEmail(String email){
        LoginActivity.email = email;
    }
}
