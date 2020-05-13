package com.e.shelter;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
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
import androidx.appcompat.app.AppCompatActivity;

import org.bson.Document;
import org.json.JSONException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import static com.e.shelter.R.layout.contacts_of_municipality;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import java.security.NoSuchAlgorithmException;

public class ContactPage extends AppCompatActivity {
    private TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(contacts_of_municipality);
        showPage();




    }

    public void createContactDataBase() {
        ContactPageThread contactThread = new ContactPageThread();
        contactThread.start();
    }

    public void showPage() {
        //Connect to MongoDB


        // Get MongoDb Database. If The Database Doesn't Exists, MongoDb Will Automatically Create It For You

        MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
        DB shelter_db = mongoClient.getDB("SafeZone_DB");
        DBCollection shelter_db_collection = shelter_db.getCollection("contactPage");
        if(shelter_db_collection==null)
            createContactDataBase();

        DBCursor cursor = shelter_db_collection.find();
        int countContacts = 0;
        while (cursor.hasNext()) {
            System.out.println("countContacts= "+countContacts);
            BasicDBObject object = (BasicDBObject) cursor.next();
            this.tv = (TextView) findViewById(R.id.mycontact1+countContacts);
            //System.out.println(object.get("name"));
            TextView tv1 = (TextView) findViewById(R.id.phoneInput1+countContacts);
            tv.setText((CharSequence) object.get("name"));
            tv1.setText((String) object.get("phoneNumber"));
            countContacts+=1;


        }
            mongoClient.close();

    }


    }

