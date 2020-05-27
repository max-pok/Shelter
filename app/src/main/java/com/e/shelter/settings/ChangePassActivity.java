package com.e.shelter.settings;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.e.shelter.LoginActivity;
import com.e.shelter.R;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class ChangePassActivity extends AppCompatActivity {
    public static String oldPass;
    public static String newPass1;
    public static String newPass2;
    static String sha1(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepass);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Button changePassButton = (Button) findViewById(R.id.changepass_button);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //Click on login button
        changePassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText oldPasswordnput = (EditText) findViewById(R.id.oldpass);
                EditText newPassword1Input = (EditText) findViewById(R.id.newpass);
                EditText newPassword2Input = (EditText) findViewById(R.id.newpass2);
                oldPass = oldPasswordnput.getText().toString();
                newPass1 = newPassword1Input.getText().toString();
                newPass2 = newPassword2Input.getText().toString();
                if (check_old_password()) {
                    if (check_new_password()) {
                        if (change_password()) {
                            builder.setMessage("Password changed successfully")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                    else {
                        builder.setMessage("The new passwords don't match")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
                else {
                    builder.setMessage("The password is incorrect")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }

            }
        });
    }

    public boolean check_old_password() {
        try {

            //Connect to MongoDB
            MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
            MongoDatabase database = mongoClient.getDatabase("SafeZone_DB");
            MongoCollection<Document> mongoCollection = database.getCollection("users");
            //Find if the user exist in users collection according to email and password.
            LoginActivity loginActivity = new LoginActivity();
            String email = loginActivity.getEmail();
            System.out.println(email);
            Document myDoc = mongoCollection.find(and(eq("email",email), eq("password",sha1(oldPass)))).first();
            if(myDoc !=null) {
                if (myDoc.get("password").equals(sha1(oldPass))) {
                    mongoClient.close();
                    return true;
                }
            }
            mongoClient.close();
        } catch (MongoException | NoSuchAlgorithmException e) {
            Log.e("Error " + e, "" + e);
        }
        return false;
    }
    public boolean check_new_password(){
        if (newPass1.equals(newPass2)){
            return true;
        }
        return false;
    }
    public boolean change_password(){
        try {
            //Connect to MongoDB
            MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
            MongoDatabase database = mongoClient.getDatabase("SafeZone_DB");
            MongoCollection<Document> mongoCollection = database.getCollection("users");
            LoginActivity loginActivity = new LoginActivity();
            String email = loginActivity.getEmail();
            System.out.println(email);
            System.out.println(sha1(oldPass));

            Document myDoc = mongoCollection.find(and(eq("email", email), eq("password", sha1(oldPass)))).first();
            myDoc.getObjectId("_id");
            Document updateDoc = new Document();
            updateDoc.put("email",myDoc.get("email"));
            updateDoc.put("password",sha1(newPass1));
            updateDoc.put("user_type",myDoc.get("user_type"));
            mongoCollection.replaceOne(and(eq("email", email), eq("password", sha1(oldPass))),updateDoc);
            /*
            BasicDBObject query = new BasicDBObject();
            query.put("password", sha1(oldPass));

            BasicDBObject newDocument = new BasicDBObject();
            newDocument.put("password", sha1(newPass1));

            BasicDBObject updateObject = new BasicDBObject();
            updateObject.put("$set", newDocument);

          mongoCollection.updateOne(query, updateObject);*/
            mongoClient.close();
            return true;


        }
        catch (MongoException | NoSuchAlgorithmException e){
            Log.e("Error " + e, "" + e);
        }
        return false;
    }

}
