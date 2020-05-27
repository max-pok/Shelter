package com.e.shelter;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.FirebaseAuthCredentialsProvider;
import com.mongodb.BasicDBList;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

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
    public FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public FirebaseAuth firebaseAuth;


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
//        try {
//            //Connect to MongoDB
//            MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
//            MongoDatabase database = mongoClient.getDatabase("SafeZone_DB");
//            MongoCollection<Document> usersCollection = database.getCollection("users");
//            MongoCollection<Document> simpleUsersCollection = database.getCollection("simpleUsers");
//            //Find if the email exist in users collection according to email
//            Document myDoc = usersCollection.find(eq("email", email)).first();
//            if (myDoc!=null){//The user exist
//                System.out.println("this email exsit");
//                System.out.println(sha1(password));
//
//
//            }
//            else{
//                //new Document for users collection
//                Document newUser = new Document();
//                newUser.put("email", email);
//                newUser.put("password", sha1(password));
//                newUser.put("user_type", "simpleUser");
//                //insert the document to users collection
//                usersCollection.insertOne(newUser);
//                //new document to simpleUsers collection
//                Document newSimpleUser = new Document();
//                newSimpleUser.put("email",email);
//                newSimpleUser.put("firstName",firstName);
//                newSimpleUser.put("lastName",lastName);
//                newSimpleUser.put("phone",phone);
//                newSimpleUser.put("address",address);
//                //insert the document to simpleUsers collection
//                simpleUsersCollection.insertOne(newSimpleUser);
//                //change the flag to true because the registration was successful
//                flag=true;
//
//                //Create favorite shelter document for new user
//                MongoCollection<Document> favoriteSheltersCollection = database.getCollection("FavoriteShelters");
//                Document favShelterDocument = new Document();
//                favShelterDocument.put("user_email", email);
//                favShelterDocument.put("favorite_shelters", new BasicDBList());
//                favoriteSheltersCollection.insertOne(favShelterDocument);
//            }
//            //close the DB connection
//            mongoClient.close();
//        } catch (MongoException | NoSuchAlgorithmException m) {
//            Log.e("Error " + m, "" + m);
//        }
        CollectionReference collectionReference = firebaseFirestore.collection("Users");


    }
}
