package com.e.shelter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.e.shelter.adapers.ContactListAdapter;
import com.e.shelter.utilities.Contact;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.bson.Document;

import java.util.ArrayList;

import static com.mongodb.client.model.Filters.eq;

public class ContactPage extends AppCompatActivity {

    private ListView contactsListView;
    private FloatingActionButton addButton;
    private ArrayList<Contact> contactsArrayList = new ArrayList<>();
    private ContactListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_of_municipality);

        retrieveContacts();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>Contacts</font>"));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        contactsListView = findViewById(R.id.contacts_list_view);

        addButton = findViewById(R.id.add_contacts_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactPage.this, AddNewContactActivity.class);
                startActivityForResult(intent, 2);
            }
        });

        adapter = new ContactListAdapter(this, R.layout.content_contacts, contactsArrayList, getIntent().getStringExtra("userType"));
        contactsListView.setAdapter(adapter);
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            Log.i("TAG", "From Add Contact Screen");
            retrieveContacts();
            adapter = new ContactListAdapter(this, R.layout.content_contacts, contactsArrayList, getIntent().getStringExtra("userType"));
            contactsListView.setAdapter(adapter);
        }
        if (requestCode == 1) {
            Log.i("TAG", "From Edit Contact Screen");
            retrieveContacts();
            adapter = new ContactListAdapter(this, R.layout.content_contacts, contactsArrayList, getIntent().getStringExtra("userType"));
            contactsListView.setAdapter(adapter);
        }
    }

    public void retrieveContacts() {
        contactsArrayList = new ArrayList<>();
        MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
        DB shelter_db = mongoClient.getDB("SafeZone_DB");
        DBCollection shelter_db_collection = shelter_db.getCollection("contactPage");
        DBCursor cursor = shelter_db_collection.find();
        while (cursor.hasNext()) {
            BasicDBObject object = (BasicDBObject) cursor.next();
            contactsArrayList.add(new Contact(object.getString("name"),object.getString("nameInEnglish"), object.getString("phoneNumber")));
        }
        mongoClient.close();
    }


}

