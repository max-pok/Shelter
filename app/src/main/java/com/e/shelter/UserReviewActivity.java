package com.e.shelter;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

import static com.e.shelter.R.layout.activity_user_review;

public class UserReviewActivity extends AppCompatActivity {
    public static String user;
    public static String review;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(activity_user_review);
//        createReviewDataBase();
        Button SendButton = (Button) findViewById(R.id.SendButton);
        SendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReview();
            }
        });

//        //Header
//        View header = navigationView.getHeaderView(0);
//        TextView header_email = header.findViewById(R.id.email_header);
//        Intent intent = getIntent();
//        String value = intent.getStringExtra("email");
//
//
//        if (value != null) header_email.setText(value);
//        System.out.println("#########################");
//        System.out.println("#########################");
//        System.out.println(value);
    }

    public void addReview() {
        //get strings from sign up text boxes
        EditText nameInput = (EditText)findViewById(R.id.nameInput);
        EditText reviewInput = (EditText)findViewById(R.id.reviewInput);

        user= nameInput.getText().toString();
        review= reviewInput.getText().toString();
        //start new thread to add a new user.
        UserReviewThread signupThread= new UserReviewThread(user,review);
        signupThread.start();
        Thread t = Thread.currentThread();// The main thread

    }

    public void showPage() {
        //Connect to MongoDB
//        MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
//        MongoDatabase database = mongoClient.getDatabase("SafeZone_DB");
//        MongoCollection<Document> contactCollection = database.getCollection("contactPage");

        // Get MongoDb Database. If The Database Doesn't Exists, MongoDb Will Automatically Create It For You
        MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
        DB shelter_db = mongoClient.getDB("SafeZone_DB");
        DBCollection shelter_db_collection = shelter_db.getCollection("userReviews");
        DBCursor cursor = shelter_db_collection.find();
        int countContacts=1;
        while (cursor.hasNext()) {
            BasicDBObject object = (BasicDBObject) cursor.next();
            TextView tv = (TextView)findViewById(R.id.textView+countContacts);
            tv.setText((String)object.get("userID"));
            TextView tv1 = (TextView)findViewById(R.id.textView+countContacts);
            tv1.setText((String)object.get("review"));
            countContacts++;

        }
        mongoClient.close();
    }


}