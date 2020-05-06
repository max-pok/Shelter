package com.e.shelter;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.e.shelter.utilities.InfoWindowData;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class EditShelterDetails extends AppCompatActivity {
    public static EditText editText_name;
    public static EditText editText_status;
    public static EditText editText_address;
    public static EditText editText_capacity;
    public static Button update;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_shelter_details);
        show_current_shelter_details();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder.setMessage("Worng details")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
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
        update= (Button)findViewById(R.id.button_update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(update_details()){
                    goodMessage.show();

                }
                else{
                    errorMessage.show();
                }




            }

        });


    }
    public void show_current_shelter_details(){
        final InfoWindowData info = new InfoWindowData();
        EditText editText_name =(EditText) findViewById(R.id.editText_name);
        EditText editText_status =(EditText) findViewById(R.id.editText_status);
        EditText editText_address =(EditText) findViewById(R.id.editText_address);
        EditText editText_capacity =(EditText) findViewById(R.id.editText_capacity);
        editText_name.setText(getIntent().getStringExtra("name"));
        editText_address.setText(getIntent().getStringExtra("address"));
        editText_capacity.setText(getIntent().getStringExtra("capacity"));
        editText_status.setText(getIntent().getStringExtra("status"));
    }
    public boolean update_details(){

            final MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
            String lon = getIntent().getStringExtra("lon");
            String lat = getIntent().getStringExtra("lat");
            MongoDatabase database = mongoClient.getDatabase("SafeZone_DB");
            MongoCollection<Document> mongoCollection = database.getCollection("Shelters");
            Document myDoc = mongoCollection.find(and(eq("lat", lat), eq("lon", lon))).first();
            Document updateDoc = new Document();
            updateDoc.put("name", editText_name.getText());
            updateDoc.put("lat", lat);
            updateDoc.put("lon", lon);
            updateDoc.put("address", editText_address.getText());
            updateDoc.put("capacity", editText_capacity.getText());
            updateDoc.put("status", editText_status.getText());
            updateDoc.put("rating",myDoc.get("rating"));
            mongoCollection.replaceOne(and(eq("lon", lon), eq("lat", lat)), updateDoc);
            return false;


    }


}
