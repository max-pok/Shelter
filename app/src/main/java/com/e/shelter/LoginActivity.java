package com.e.shelter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import java.util.logging.Level;
import java.util.logging.Logger;



public class LoginActivity extends AppCompatActivity {
    public static String email;
    public static String password;
    public static boolean[] checkuser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        //Login Button
        Button LoginButton = findViewById(R.id.LoginButton);
        //Click on login button
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailInput = findViewById(R.id.emailInput);
                EditText passwordInput = findViewById(R.id.passowrdInput);
                email = emailInput.getText().toString();
                password = passwordInput.getText().toString();
                //Error message
                builder.setMessage("Wrong Email/Password, Try Again!")
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
                        if(checkuser[1]== true){ // This is Admin
                            System.out.println("this is admin\n");
                            Intent myIntent = new Intent(getBaseContext(), MapViewActivity.class);
                            myIntent.putExtra("email", email); //Optional parameters
                            startActivity(myIntent);
                            finish();
                        }
                        else{ //This is simple user
                            System.out.println("this is simple user\n");
                            Intent myIntent = new Intent(getBaseContext(), MapViewActivity.class);
                            myIntent.putExtra("email", email); //Optional parameters
                            startActivity(myIntent);
                            finish();
                            startActivity(new Intent(getBaseContext(), MapViewActivity.class));

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
        Button SignupButton = findViewById(R.id.SignUpButton);
        SignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//Click to Sign up button
                ShowSignupPage();
            }

        });
       /* Button contactButton = (Button) findViewById(R.id.contact_button);
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowContactPage();
            }

        });*/

        //temporary
        Button button_update = findViewById(R.id.button_update);
        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowUpdateContact();
            }

        });


    }
    public void ShowContactPage() {
        startActivity(new Intent(getBaseContext(), ContactPage.class));
    }

    //temporary
    public void ShowUpdateContact() {
        startActivity(new Intent(getBaseContext(),UpdateContactActivity.class));
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
        Thread.sleep(1000);//wait to answer from the login thread.
        //Get True if the user exist else False.
         flag= loginThread.getFlag();
        System.out.println(flag);
        return flag;
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
        DB shelter_db = mongoClient.getDB("SafeZone_DB");
        DBCollection users_collection = shelter_db.createCollection("users",new BasicDBObject());
        users_collection.insert(document);
        mongoClient.close();
    }


}
