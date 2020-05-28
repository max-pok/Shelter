package com.e.shelter.contactus;

import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.e.shelter.MainActivity;
import com.e.shelter.R;
import com.e.shelter.utilities.Contact;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddNewContactActivity extends MainActivity {

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


        if (getIntent().hasExtra("nameInEnglish")) { //Edit contact
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
                        editContact(getIntent().getStringExtra("nameInEnglish"), hebrewInput.getText().toString(), englishInput.getText().toString(), phoneNumber.getText().toString());
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
        finish();
    }

    public void addContact(String hebrewInput, String englishInput, String phoneNumber) {
        Contact contact = new Contact(hebrewInput, englishInput, phoneNumber);
        firebaseFirestore.collection("ContactUsInformation").document(englishInput).set(contact);
        Toast.makeText(AddNewContactActivity.this, "Contact Added", Toast.LENGTH_LONG).show();
        setResult(2);
        finish();
    }

    public void editContact(String oldName, String hebrewInput, String englishInput, String phoneNumber) {
        firebaseFirestore.collection("ContactUsInformation").document(englishInput).set(new Contact(hebrewInput,englishInput, phoneNumber));
        firebaseFirestore.collection("ContactUsInformation").document(oldName).delete();
        Toast.makeText(AddNewContactActivity.this, "Contact Updated", Toast.LENGTH_LONG).show();
        setResult(2);
        finish();
    }
}
