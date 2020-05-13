package com.e.shelter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.DBCursor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.bson.Document;
import org.json.JSONException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import static com.e.shelter.R.layout.update_contact;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UpdateContactActivity extends AppCompatActivity {
    public static String contactName;
    public static String newNumber;
    public static String oldNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_contact);
        Button buttonUpdate = (Button) findViewById(R.id.Change_button);
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("fin");
                update_contact();
            }
        });

    }
    public void update_contact()
    {
        EditText nameInput = (EditText)findViewById(R.id.nameInput);
        contactName= nameInput.getText().toString();
        EditText numberInput = (EditText)findViewById(R.id.numberInput);
        newNumber= numberInput.getText().toString();
        //Connect to MongoDB
        MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
        MongoDatabase database = mongoClient.getDatabase("SafeZone_DB");
        MongoCollection<Document> contactCollection = database.getCollection("contactPage");
        //Find if the name exist in contact collection according to input

        Document myDoc = contactCollection.find((eq("name", contactName))).first();

        if(myDoc!=null) {
            oldNumber = myDoc.get("phoneNumber").toString();
            System.out.println("%%%%%" + oldNumber);
            myDoc.getObjectId("_id");
            Document updateDoc = new Document();
            updateDoc.put("name", contactName);
            updateDoc.put("phoneNumber", newNumber);
            contactCollection.replaceOne(and(eq("name", contactName), eq("phoneNumber", oldNumber)), updateDoc);

        }
        else {
            Document updateDoc = new Document();
            updateDoc.put("name", contactName);
            updateDoc.put("phoneNumber", newNumber);
            contactCollection.insertOne(updateDoc);
            setContentView(R.layout.contacts_of_municipality);
            LinearLayout llMain = findViewById(R.id.linearLayout0);
            TextView textView = new TextView(this);
            textView.setText("I am added dynamically to the view");
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            textView.setLayoutParams(params);
            llMain.addView(textView);
        }
        mongoClient.close();
        startActivity(new Intent(getBaseContext(), ContactPage.class));

    }
}
