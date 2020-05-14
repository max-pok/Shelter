package com.e.shelter;

import android.util.Log;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import java.util.ArrayList;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class UserReviewThread extends Thread{
    public static String userID;
    public static String review;
    public static String address;
    public static String email;


    //c'tor
    UserReviewThread(String id,String review,String add,String email){
        this.userID=id;
        this.review=review;
        this.address=add;
        this.email=email;
    }
    public void run()
    {
        try {

            //Connect to MongoDB
            MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
            MongoDatabase database = mongoClient.getDatabase("SafeZone_DB");
            MongoCollection<Document> reviewCollection = database.getCollection("userReviews");
            //Find if the email exist in users collection according to email

            Document myDoc = reviewCollection.find(and(eq("email", email),  eq("address", address))).first();
            if(myDoc!=null)
            {
                mongoClient.close();
                System.out.println("you have already submitted review for this adress.");
            }
            else {
                //new Document for users collection
                ArrayList<Document> newReview = new ArrayList<Document>();
                newReview.add(new Document().append("userID", userID).append("review", review).append("address",address).append("email",email));

                //insert the document to users collection
                reviewCollection.insertMany(newReview);
            }

            //close the DB connection
        } catch (MongoException m) {
            Log.e("Error " + m, "" + m);
        }
    }
}
