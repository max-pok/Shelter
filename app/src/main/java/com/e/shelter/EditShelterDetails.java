package com.e.shelter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class EditShelterDetails extends AppCompatActivity {
    public EditText nameEditText;
    public EditText statusEditText;
    public EditText addressEditText;
    public EditText capacityEditText;
    public Button update;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_shelter_details);
        nameEditText = findViewById(R.id.editText_name);
        statusEditText = findViewById(R.id.editText_status);
        addressEditText = findViewById(R.id.editText_address);
        capacityEditText = findViewById(R.id.editText_capacity);
        nameEditText.setText(getIntent().getStringExtra("name"));
        addressEditText.setText(getIntent().getStringExtra("address"));
        capacityEditText.setText(getIntent().getStringExtra("capacity"));
        statusEditText.setText(getIntent().getStringExtra("status"));
        //show_current_shelter_details();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder.setMessage("Worng details")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent myIntent = new Intent(getBaseContext(), MapViewActivity.class);
                        startActivity(myIntent);

                    }
                });
        final AlertDialog errorMessage = builder.create();
        // Good message
        builder2.setMessage("The update was successful!")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                });
        final AlertDialog goodMessage = builder2.create();
        update= findViewById(R.id.button_update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(update_details()){
                    setResult(3);
                    finish();
                }
                else{
                    errorMessage.show();
                }

            }

        });


    }
    public void show_current_shelter_details(){

    }
    public boolean update_details(){
            try {
                MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
                String lon = getIntent().getStringExtra("lon");
                String lat = getIntent().getStringExtra("lat");
                MongoDatabase database = mongoClient.getDatabase("SafeZone_DB");
                MongoCollection<Document> mongoCollection = database.getCollection("Shelters");
                Document myDoc = mongoCollection.find(and(eq("lat", lat), eq("lon", lon))).first();
                Document updateDoc = new Document();

                updateDoc.put("name", this.nameEditText.getText().toString());
                updateDoc.put("lat", lat);
                updateDoc.put("lon", lon);
                updateDoc.put("address", this.addressEditText.getText().toString());
                updateDoc.put("capacity", this.capacityEditText.getText().toString());
                updateDoc.put("status", this.statusEditText.getText().toString());
                updateDoc.put("rating", myDoc.get("rating"));
                updateDoc.put("rating_amount", myDoc.get("rating_amount"));
                mongoCollection.replaceOne(and(eq("lon", lon), eq("lat", lat)), updateDoc);
                mongoClient.close();
            }
            catch (Exception e){

                Log.e("Error " + e, "" + e);
                return false;

            }
            return true;


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }


}
