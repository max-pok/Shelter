package com.e.shelter;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class ContactPageThread extends Thread{
    public static String name;
    public static String phoneNumber;
    //c'tor
    ContactPageThread(){
        this.name="";
        this.phoneNumber="";
    }
    public void run()
    {
        try {

            //Connect to MongoDB
            MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
            MongoDatabase database = mongoClient.getDatabase("SafeZone_DB");
            MongoCollection<Document> contactCollection = database.getCollection("contactPage");
            //Find if the email exist in users collection according to email

            Document myDoc = contactCollection.find(eq("name", "Be'er Sheva municipality")).first();
            if(myDoc!=null)
            {
                mongoClient.close();
                System.out.println("you have this database");
            }
            else {
                //new Document for users collection
                ArrayList<Document> newContact = new ArrayList<Document>();
                newContact.add(new Document().append("name", "עיריית באר שבע").append("phoneNumber", "08-6463777/106"));
                newContact.add(new Document().append("name", "משטרת ישראל").append("phoneNumber", "100"));
                newContact.add(new Document().append("name", "מגן דוד אדום").append("phoneNumber", "101"));
                newContact.add(new Document().append("name", "כיבוי אש").append("phoneNumber", "102"));
                newContact.add(new Document().append("name", "פיקוד העורף").append("phoneNumber", "104"));
                newContact.add(new Document().append("name", "בית החולים סורוקה").append("phoneNumber", "08-6400111"));
                newContact.add(new Document().append("name", "חברת חשמל").append("phoneNumber", "103"));

                //insert the document to users collection
                contactCollection.insertMany(newContact);
            }

            //close the DB connection
            mongoClient.close();
        } catch (MongoException m) {
            Log.e("Error " + m, "" + m);
        }
    }
}
