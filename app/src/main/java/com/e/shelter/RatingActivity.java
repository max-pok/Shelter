package com.e.shelter;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.e.shelter.utilities.InfoWindowData;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.ObjectInput;

import static com.e.shelter.R.layout.rating_activity;
import static com.mongodb.client.model.Filters.eq;


public class RatingActivity extends AppCompatActivity {
    private ObjectId shelter_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(rating_activity);
        final InfoWindowData info = new InfoWindowData();
        shelter_id=info.getId();
        System.out.println("$$$$$$$$$"+shelter_id.toString());

        Button buttonUpdate = (Button) findViewById(R.id.Rate_button);
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rate_shelter();
            }
        });
    }
    public void rate_shelter(){
        EditText rateInput = (EditText)findViewById(R.id.rateInput);
        String rating=rateInput.getText().toString();
        int rating1=Integer.parseInt(rating);
        if(rating1>10 | rating1<0)
            startActivity(new Intent(getBaseContext(), RatingActivity.class));

        //Connect to MongoDB
        MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
        MongoDatabase database = mongoClient.getDatabase("SafeZone_DB");
        MongoCollection<Document> contactCollection = database.getCollection("Shelters");
        //Find if the name exist in contact collection according to input

        Document myDoc = contactCollection.find((eq("_id", this.shelter_id))).first();
        System.out.println("########"+myDoc);



    }
}
