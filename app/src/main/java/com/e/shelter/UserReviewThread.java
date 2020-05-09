package com.e.shelter;

import android.util.Log;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import java.util.ArrayList;

import static com.mongodb.client.model.Filters.eq;

public class UserReviewThread extends Thread{
    public static String userID;
    public static String review;
    //c'tor
    UserReviewThread(String id,String review){
        this.userID=id;
        this.review=review;
    }
    public void run()
    {
        try {

            //Connect to MongoDB
            MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
            MongoDatabase database = mongoClient.getDatabase("SafeZone_DB");
            MongoCollection<Document> contactCollection = database.getCollection("userReviews");
            //Find if the email exist in users collection according to email

            Document myDoc = contactCollection.find(eq("userID", "")).first();
            if(myDoc!=null)
            {
                mongoClient.close();
                System.out.println("you have this database");
            }
            else {
                //new Document for users collection
                ArrayList<Document> newReview = new ArrayList<Document>();
                newReview.add(new Document().append("userID", userID).append("review", review));

                //insert the document to users collection
                contactCollection.insertMany(newReview);
            }

            //close the DB connection
            mongoClient.close();
        } catch (MongoException m) {
            Log.e("Error " + m, "" + m);
        }
    }
}
