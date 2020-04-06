package com.e.shelter;

import android.util.Log;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class LoginThread extends Thread {
    private static boolean[] flag;
    private static String email;
    private static String password;
    LoginThread(String email,String password){
        this.flag= new boolean[2];
        this.flag[0]=false;
        this.flag[1]=true;
        this.email=email;
        this.password=password;


    }
    public boolean[] getFlag(){
        return flag;
    }
    public void run() {
        try {
            //Connect to MongoDB
            MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
            MongoDatabase database = mongoClient.getDatabase("SafeZone_DB");
            MongoCollection<Document> mongoCollection = database.getCollection("users");
            //Find if the user exist in users collection according to email and password.
            Document myDoc = mongoCollection.find(and(eq("email", email), eq("password", password))).first();
            if (myDoc!=null){//The user exist
                //flag[0] show if the user exist
                flag[0]=true;
                if(myDoc.get("user_type")=="admin"){
                    flag[1]=true;
                }


                System.out.println(myDoc.toJson());
            }
            mongoClient.close();
        } catch (MongoException m) {
            Log.e("Error " + m, "" + m);
        }
    }
}
