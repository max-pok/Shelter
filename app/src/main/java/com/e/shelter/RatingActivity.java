package com.e.shelter;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.e.shelter.utilities.InfoWindowData;
import com.google.android.gms.maps.GoogleMap;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.ObjectInput;
import java.util.ArrayList;

import static com.e.shelter.R.layout.rating_activity;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;


public class RatingActivity extends AppCompatActivity {
    private GoogleMap googleMap;

    private String shelter_add;
    private int oldcount;
    private int old_avg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(rating_activity);
        InfoWindowData info = new InfoWindowData();


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

        //current address
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            shelter_add = extras.getString("address");
            //The key argument here must match that used in the other activity
        }
        if (rating1>10 | rating1<0) {
          //  startActivity(new Intent(getBaseContext(), RatingActivity.class));
            Toast.makeText(RatingActivity.this, "Error", Toast.LENGTH_LONG).show();
        }
        else {
            //Connect to MongoDB
            MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
            MongoDatabase database = mongoClient.getDatabase("SafeZone_DB");
            MongoCollection<Document> sheltersCollection = database.getCollection("Shelters");
            MongoCollection<Document> ratingCollection = database.getCollection("ratingDB");
            //Find if the name exist in contact collection according to input
            Document myDoc = ratingCollection.find(eq("shelter_id", this.shelter_add)).first();
            Document myDoc1 = sheltersCollection.find(eq("address", this.shelter_add)).first();

            //new shelter for rating list
            if(myDoc==null) {
                //adding rating through shelter id to rating db and counter.
                Document newShelter = new Document();
                newShelter.append("shelter_id", this.shelter_add).append("counter", 1).append("average",rating1);
                ratingCollection.insertOne(newShelter);

                //updating the shelter db
                Document updateDoc = new Document();
                updateDoc.put("name",myDoc1.getString("name").toString());
                updateDoc.put("lat",myDoc1.getString("lat").toString());
                updateDoc.put("lon",myDoc1.getString("lon").toString());
                updateDoc.put("address",myDoc1.getString("address").toString());
                updateDoc.put("status",myDoc1.getString("status").toString());
                updateDoc.put("capacity",myDoc1.getString("capacity").toString());
                updateDoc.put("rating",rating1);
                sheltersCollection.replaceOne((eq("rating", 0)),updateDoc);

            }
            else
            {
                oldcount=myDoc.getInteger("counter");
                old_avg=myDoc.getInteger("average");
                Document updateDoc = new Document();
                updateDoc.put("shelter_id", this.shelter_add);
                updateDoc.put("counter", oldcount+1);
                updateDoc.put("average",(old_avg+rating1)/(oldcount+1));
                ratingCollection.replaceOne(and(eq("shelter_id", this.shelter_add), eq("counter", oldcount),eq("average",old_avg)),updateDoc);

                int new_avg=(old_avg+rating1)/(oldcount+1);

                //updating the shelter db
                Document updateDoc1 = new Document();
                updateDoc1.put("name",myDoc1.getString("name").toString());
                updateDoc1.put("lat",myDoc1.getString("lat").toString());
                updateDoc1.put("lon",myDoc1.getString("lon").toString());
                updateDoc1.put("address",myDoc1.getString("address").toString());
                updateDoc1.put("status",myDoc1.getString("status").toString());
                updateDoc1.put("capacity",myDoc1.getString("capacity").toString());
                updateDoc1.put("rating",new_avg);
                sheltersCollection.replaceOne((eq("address", this.shelter_add)),updateDoc1);
            }

            startActivity(new Intent(getBaseContext(), MapViewActivity.class));
        }

    }
}
