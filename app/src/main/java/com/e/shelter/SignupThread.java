package com.e.shelter;

import android.util.Log;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignupThread extends Thread {
    public static String email;
    public static String password;
    public static String firstName;
    public static String lastName;
    public static String phone;
    public static String address;
    public static boolean flag= false;


    static String sha1(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    //Ctor
    SignupThread(String email,String password,String firstName,String lastName,String phone ,String address){
        this.email= email;
        this.password=password;
        this.firstName= firstName;
        this.lastName= lastName;
        this.phone=phone;
        this.address=address;
    }

    public boolean getFlag(){
        return flag;
    }

    public void run(){
        try {

            //Connect to MongoDB
            MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
            MongoDatabase database = mongoClient.getDatabase("SafeZone_DB");
            MongoCollection<Document> usersCollection = database.getCollection("users");
            MongoCollection<Document> simpleUsersCollection = database.getCollection("simpleUsers");
            //Find if the email exist in users collection according to email
            Document myDoc = usersCollection.find(eq("email", email)).first();
            if (myDoc!=null){//The user exist
                System.out.println("this email exsit");
                System.out.println(sha1(password));


            }
            else{
                //new Document for users collection
                Document newUser = new Document();
                newUser.put("email", email);
                newUser.put("password", sha1(password));
                newUser.put("user_type", "simpleUser");
                //insert the document to users collection
                usersCollection.insertOne(newUser);
                //new document to simpleUsers collection
                Document newSimpleUser = new Document();
                newSimpleUser.put("email",email);
                newSimpleUser.put("firstName",firstName);
                newSimpleUser.put("lastName",lastName);
                newSimpleUser.put("phone",phone);
                newSimpleUser.put("address",address);
                //insert the document to simpleUsers ollection
                simpleUsersCollection.insertOne(newSimpleUser);
                //change the flag to true because the registration was successful
                flag=true;
            }
            //close the DB connection
            mongoClient.close();
        } catch (MongoException | NoSuchAlgorithmException m) {
            Log.e("Error " + m, "" + m);
        }
    }

    public void create_simpleUser_DB() {
        // TODO
    }


}
