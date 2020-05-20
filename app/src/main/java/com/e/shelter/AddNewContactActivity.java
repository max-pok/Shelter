package com.e.shelter;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

public class AddNewContactActivity extends AppCompatActivity {

    private TextInputEditText hebrewInput;
    private TextInputEditText englishInput;
    private TextInputEditText phoneNumber;
    private MaterialButton addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_contact);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        hebrewInput = findViewById(R.id.contact_name_hebrew_add_contact_page);
        englishInput = findViewById(R.id.contact_name_english_add_contact_page);
        phoneNumber = findViewById(R.id.contact_phone_number_add_contact_page);
        addButton = findViewById(R.id.add_new_contact_button);


        if (getIntent().hasExtra("name")) { //Edit contact
            addButton.setText("Change");
            hebrewInput.setText(getIntent().getStringExtra("name"));
            englishInput.setText(getIntent().getStringExtra("nameInEnglish"));
            phoneNumber.setText(getIntent().getStringExtra("phoneNumber"));
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (hebrewInput.getText().toString().isEmpty()) {
                        hebrewInput.setError("Please fill out this field");
                    }
                    if (englishInput.getText().toString().isEmpty()) {
                        englishInput.setError("Please fill out this field");
                    }
                    if (phoneNumber.getText().toString().isEmpty()) {
                        phoneNumber.setError("Please fill out this field");
                    }
                    if (!hebrewInput.getText().toString().isEmpty() && !englishInput.getText().toString().isEmpty() && !phoneNumber.getText().toString().isEmpty()) {
                        editContact(getIntent().getStringExtra("name"), hebrewInput.getText().toString(), englishInput.getText().toString(), phoneNumber.getText().toString());
                    }
                }
            });
        } else { //Add contact
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (hebrewInput.getText().toString().isEmpty()) {
                        hebrewInput.setError("Please fill out this field");
                    }
                    if (englishInput.getText().toString().isEmpty()) {
                        englishInput.setError("Please fill out this field");
                    }
                    if (phoneNumber.getText().toString().isEmpty()) {
                        phoneNumber.setError("Please fill out this field");
                    } if (!hebrewInput.getText().toString().isEmpty() && !englishInput.getText().toString().isEmpty() && !phoneNumber.getText().toString().isEmpty()) {
                        addContact(hebrewInput.getText().toString(), englishInput.getText().toString(), phoneNumber.getText().toString());
                    }
                }
            });
        }
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

    public void addContact(String hebrewInput, String englishInput, String phoneNumber) {
        MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
        MongoDatabase database = mongoClient.getDatabase("SafeZone_DB");
        MongoCollection<Document> mongoCollection = database.getCollection("contactPage");
        Document newContact = new Document()
                .append("name", hebrewInput)
                .append("nameInEnglish", englishInput)
                .append("phoneNumber", phoneNumber);

        mongoCollection.insertOne(newContact);
        mongoClient.close();
        Toast.makeText(AddNewContactActivity.this, "Contact Added", Toast.LENGTH_LONG).show();
        setResult(2);
        finish();
    }

    public void editContact(String oldName, String hebrewInput, String englishInput, String phoneNumber) {
        MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
        MongoDatabase database = mongoClient.getDatabase("SafeZone_DB");
        MongoCollection<Document> mongoCollection = database.getCollection("contactPage");
        Document contact = new Document()
                .append("name", hebrewInput)
                .append("nameInEnglish", englishInput)
                .append("phoneNumber", phoneNumber);

        mongoCollection.replaceOne(eq("name", oldName), contact);
        mongoClient.close();
        Toast.makeText(AddNewContactActivity.this, "Contact Updated", Toast.LENGTH_LONG).show();
        setResult(1);
        finish();
    }
}
