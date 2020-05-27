package com.e.shelter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.e.shelter.validation.EmailValidator;
import com.e.shelter.validation.PasswordValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    public static String email;
    public static String password;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passInput);

        //Login Button
        MaterialButton LoginButton = findViewById(R.id.LoginButton);
        LoginButton.setOnClickListener(this);

        MaterialButton register = findViewById(R.id.signUpButton);
        register.setOnClickListener(this);
    }

    public void signIn() {
        if (EmailValidator.isValidEmailTextInputEditText(emailInput.getText().toString(), emailInput)
                & PasswordValidator.isValidEmailTextInputEditText(passwordInput.getText().toString(), passwordInput)) {

            firebaseAuth.signInWithEmailAndPassword(emailInput.getText().toString(), passwordInput.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(LoginActivity.this, MapViewActivity.class);
                                //intent.putExtra("email", email);
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

    public  String getPassword(){
        return password;
    }

    public  void setEmail(String email){
        LoginActivity.email = email;
    }

    public void setPassword(String password){
        LoginActivity.password =password;
    }

    public void ShowSignUpPage() {
        //Going to sign up page
        Intent i = new Intent(this, SignupActivity.class);
        startActivity(i);
    }

    public boolean CheckLogin(String email, String password)  {

        return true;
    }

    /**
     * Creates users collection and inserts admin user.
     * Use this function only to add the users collection into your mongoDB.
     * TODO: delete function and admin user before deployment.
     */
    public void create_user_db() {
        BasicDBObject document = new BasicDBObject();
        document.put("email", "admin@admin.com");
        document.put("password", "admin");
        document.put("user_type", "admin");
        MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
        DB db = mongoClient.getDB("SafeZone_DB");
        DBCollection collection = db.createCollection("users",new BasicDBObject());
        collection.insert(document);
        mongoClient.close();
    }


}
