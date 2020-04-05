package com.e.shelter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.logging.Level;
import java.util.logging.Logger;
import static com.mongodb.client.model.Filters.eq;



public class LoginActivity extends AppCompatActivity {
    private static String email;
    private static String password;
    private boolean[] checkuser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.SEVERE);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        //Login Button
        Button LoginButton = (Button) findViewById(R.id.LoginButton);
        //Click on login button
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailInput = (EditText) findViewById(R.id.emailInput);
                EditText passwordInput = (EditText) findViewById(R.id.passowrdInput);
                email = emailInput.getText().toString();
                password = passwordInput.getText().toString();
                //Error message
                builder.setMessage("Worng Email/Password, Try Again!")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog errorMessage = builder.create();
                // Good message
                builder2.setMessage("Login successful\n!")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog goodMessage = builder2.create();
                // Check if the user exist
                try {
                    checkuser=CheckLogin(email,password);
                    if (checkuser[0] == true ){
                        if(checkuser[1]== true){// This is Admin
                            System.out.println("this is admin\n");
                            goodMessage.show();
                        }
                        else{//This is simple user

                            goodMessage.show();

                        }
                    }
                    else{
                        errorMessage.show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        //Signup button
        Button SignupButton = (Button) findViewById(R.id.SignUpButton);
        SignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//Click to Sign up button
                ShowSignupPage();
            }

        });
        Button contactButton = (Button) findViewById(R.id.contact_button);
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowContactPage();
            }

        });


    }
    public void ShowContactPage() {
        //Going to sign up page

    }

    public void ShowSignupPage() {
        //Going to sign up page
        Intent i = new Intent(this, SignupActivity.class);
        startActivity(i);
    }

    //Connecting to MongoDB in new thread and find if the user exist , return True or False
    public boolean[] CheckLogin(final String email, final String password) throws InterruptedException {
         boolean[] flag= new boolean[2];

        Thread t = Thread.currentThread();// The main thread
        //New Thread that connect to DB and find the use.
        LoginThread loginThread= new LoginThread(email,password);
        loginThread.start();
        t.sleep(1000);//wait to answer from the login thread.
        //Get True if the user exist else False.
         flag= loginThread.getFlag();
        System.out.println(flag);
        return flag;
    }
}
