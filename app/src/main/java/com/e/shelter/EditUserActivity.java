package com.e.shelter;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;

import org.bson.Document;

import java.util.List;

import static com.e.shelter.LoginActivity.email;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class EditUserActivity  extends AppCompatActivity {

    private EditText FnameEditText;
    private EditText LnameEditText;
    private EditText addressEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private MongoClient mongoClient;
    private MongoDatabase database;

    public Button update;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        FnameEditText = findViewById(R.id.fnameInput);
        LnameEditText = findViewById(R.id.lnameInput);
        addressEditText = findViewById(R.id.addressInput);
        emailEditText = findViewById(R.id.emailInput);
        phoneEditText = findViewById(R.id.phoneInput);
        InitEditText();
        //update Button
        update = findViewById(R.id.updateButton2);
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
        public void MongoConnect () {
            mongoClient = new MongoClient("10.0.2.2", 27017);
            database = mongoClient.getDatabase("SafeZone_DB");

        }

        public void InitEditText () {
            //Connect to MongoDB
            try {
                MongoConnect();
                MongoCollection<Document> usersCollection = database.getCollection("users");
                MongoCollection<Document> simpleUsersCollection = database.getCollection("simpleUsers");
                //Find if the email exist in users collection according to email
                Document myDoc = simpleUsersCollection.find(eq("email", email)).first();
                FnameEditText.setText(myDoc.get("firstName").toString());
                LnameEditText.setText(myDoc.get("lastName").toString());
                addressEditText.setText(myDoc.get("address").toString());
                emailEditText.setText(myDoc.get("email").toString());
                phoneEditText.setText(myDoc.get("phone").toString());

            } catch (Exception e) {
                System.out.println("Error: " + e);

            }

        }

        public Boolean EditUser() {
            String fname, lname, phone, address;
            fname = FnameEditText.getText().toString();
            lname = LnameEditText.getText().toString();
            phone = phoneEditText.getText().toString();
            address = addressEditText.getText().toString();
            try {
                MongoCollection<Document> usersCollection = database.getCollection("users");
                MongoCollection<Document> simpleUsersCollection = database.getCollection("simpleUsers");
                MongoCollection<Document> favoriteCollection = database.getCollection("FavoriteShelters");
                Document myDoc = usersCollection.find(eq("email", email)).first();
                Document updateDoc = new Document();
                updateDoc.put("email", emailEditText.getText().toString());
                updateDoc.put("firstName", fname);
                updateDoc.put("lastName", lname);
                updateDoc.put("phone", phone);
                updateDoc.put("address", address);
                simpleUsersCollection.replaceOne(eq("email", email), updateDoc);

                Document updateDoc2 = new Document();
                updateDoc2.put("email", emailEditText.getText().toString());
                updateDoc2.put("password", myDoc.get("password").toString());
                updateDoc2.put("user_type", myDoc.get("user_type").toString());
                usersCollection.replaceOne(eq("email", email), updateDoc2);

                Document myDoc2 = favoriteCollection.find(eq("user_email", email)).first();
                List<Document> favList = (List<Document>) myDoc2.get("favorite_shelters");
                Document updateDoc3 = new Document();
                updateDoc3.put("user_email",emailEditText.getText().toString());
                updateDoc3.put("favorite_shelters",myDoc2.get("favorite_shelters"));
                favoriteCollection.replaceOne(eq("user_email", email), updateDoc3);

                for(int i=0;i<favList.size();i++){
                    favoriteCollection.updateOne(eq("user_email", email), Updates.addToSet("favorite_shelters", favList.get(i)));
                }
                LoginActivity.setEmail(emailEditText.getText().toString());
                TextView header_email = findViewById(R.id.email_header);
                header_email.setText(email);
            }
            catch (Exception e){
                System.out.println("error : "+ e);
                mongoClient.close();
                return false;

            }
            mongoClient.close();
            return true;


        }

}