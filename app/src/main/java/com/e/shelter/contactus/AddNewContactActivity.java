package com.e.shelter.contactus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.e.shelter.MainActivity;
import com.e.shelter.R;
import com.e.shelter.utilities.Contact;
import com.e.shelter.validation.TextInputValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

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
        firebaseFirestore.collection("ContactUsInformation").add(contact);
        Toast.makeText(AddNewContactActivity.this, "Contact Added", Toast.LENGTH_LONG).show();
        setResult(2);
        finish();
    }

    public void editContact(String oldName, final String hebrewInput, final String englishInput, final String phoneNumber) {
        firebaseFirestore.collection("ContactUsInformation").whereEqualTo("nameInEnglish", oldName)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<Object, String> map = new HashMap<>();
                        map.put("name", hebrewInput);
                        map.put("nameInEnglish", englishInput);
                        map.put("phoneNumber", phoneNumber);
                        firebaseFirestore.collection("ContactUsInformation").document(document.getId()).set(map, SetOptions.merge());
                        Toast.makeText(AddNewContactActivity.this, "Contact Updated", Toast.LENGTH_LONG).show();
                        setResult(3);
                        finish();
                    }
                }
            }
        });

    }
}
