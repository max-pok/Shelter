package com.e.shelter;

import android.util.Log;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
<<<<<<< HEAD
=======
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
>>>>>>> ChangePass

import org.bson.Document;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

<<<<<<< HEAD
=======

>>>>>>> ChangePass
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
<<<<<<< HEAD
=======
    static String sha1(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }
>>>>>>> ChangePass
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
<<<<<<< HEAD
            Document myDoc = mongoCollection.find(and(eq("email", email), eq("password", password))).first();
=======
            Document myDoc = mongoCollection.find(and(eq("email", email), eq("password", sha1(password)))).first();
>>>>>>> ChangePass
            if (myDoc!=null){//The user exist
                //flag[0] show if the user exist
                flag[0]=true;
                if (myDoc.get("user_type")=="admin"){
                    flag[1]=true;
                }
                System.out.println(myDoc.toJson());
            }
            mongoClient.close();
<<<<<<< HEAD
        } catch (MongoException m) {
=======
        } catch (MongoException | NoSuchAlgorithmException m) {
>>>>>>> ChangePass
            Log.e("Error " + m, "" + m);
        }
    }
}
