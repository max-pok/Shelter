package com.e.shelter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import org.bson.Document;
import java.util.Arrays;
import com.mongodb.Block;

import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.*;

import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.*;
import java.util.logging.Logger;
import static com.mongodb.client.model.Filters.eq;



public class LoginActivity extends AppCompatActivity {
    private static String email;
    private static String password;
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
                builder2.setMessage("Welcome!")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog goodMessage = builder2.create();
                // Check if the user exist
                try {
                    if (CheckLogin(email,password)){
                        goodMessage.show();
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

    }

    public void ShowSignupPage() {
        //Going to sign up page
        Intent i = new Intent(this, SignupActivity.class);
        startActivity(i);
    }

    //Connecting to MongoDB in new thread and find if the user exist , return True or False
    public boolean CheckLogin(final String email, final String password) throws InterruptedException {
         boolean flag =false;
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
