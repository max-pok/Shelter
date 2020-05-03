package com.e.shelter;

import android.os.Bundle;
import android.os.StrictMode;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class ContactPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_of_municipality);
        createContactDataBase();
        showPage();
        updateInfo();


    }
    public void createContactDataBase()
    {
        ContactPageThread contactThread= new ContactPageThread();
        contactThread.start();
    }
    public void showPage() {
        // Get MongoDb Database. If The Database Doesn't Exists, MongoDb Will Automatically Create It For You
        MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
        DB shelter_db = mongoClient.getDB("SafeZone_DB");
        DBCollection shelter_db_collection = shelter_db.getCollection("contactPage");
        DBCursor cursor = shelter_db_collection.find();
        int countContacts=1;
        while (cursor.hasNext()) {
            BasicDBObject object = (BasicDBObject) cursor.next();
            TextView tv = (TextView)findViewById(R.id.textView+countContacts);
            tv.setText((String)object.get("name"));
            TextView tv1 = (TextView)findViewById(R.id.phoneInput+countContacts);
            tv1.setText((String)object.get("phoneNumber"));
            countContacts++;

        }
        mongoClient.close();
        }
    public void updateInfo()
    {
        //test test
    }

    }
