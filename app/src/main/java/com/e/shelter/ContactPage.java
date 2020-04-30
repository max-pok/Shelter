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

public class ContactPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_of_municipality);
        createContactDataBase();

    }
    public void createContactDataBase()
    {
        ContactPageThread contactThread= new ContactPageThread();
        contactThread.start();
    }




}
