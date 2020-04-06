package com.e.shelter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
public class SignupActivity extends AppCompatActivity {
    public static String email;
    public static String password;
    public static String firstName;
    public static String lastName;
    public static String phone;
    public static String address;
    public boolean flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder.setMessage("This email exists, Choose another one!")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        final AlertDialog errorMessage = builder.create();
        // Good message
        builder2.setMessage("The registration was successful!")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ShowLoginPage();

                    }
                });
        final AlertDialog goodMessage = builder2.create();
        //sign up button
        Button SignupButton = (Button) findViewById(R.id.SignupButton2);
        SignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //Check if the new user is added to the system or not.
                    if(addUser()==true){
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
    }
    public boolean addUser() throws InterruptedException {
        boolean flag;
        //get strings from sign up text boxes
        EditText firstnameInput = (EditText)findViewById(R.id.fnameInput);
        EditText lastnameInput = (EditText)findViewById(R.id.lnameInput);
        EditText passwordInput = (EditText)findViewById(R.id.passInput);
        EditText emailInput = (EditText)findViewById(R.id.emailInput);
        EditText phoneInput = (EditText)findViewById(R.id.phoneInput);
        EditText addressInput = (EditText)findViewById(R.id.addressInput);
        firstName= firstnameInput.getText().toString();
        lastName= lastnameInput.getText().toString();
        password= passwordInput.getText().toString();
        phone= phoneInput.getText().toString();
        email = emailInput.getText().toString();
        address = addressInput.getText().toString();
        //start new thread to add a new user.
        SignupThread signupThread= new SignupThread(email,password,firstName,lastName,phone,address);
        signupThread.start();
        Thread t = Thread.currentThread();// The main thread
        t.sleep(1500);
        // flag that show if the registration was successful or not.
        flag= signupThread.getFlag();
        return flag;
    }

    public void ShowLoginPage() {
        //Going to login page
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

}
