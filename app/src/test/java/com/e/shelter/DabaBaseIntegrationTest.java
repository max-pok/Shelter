package com.e.shelter;

//import androidx.test.ext.junit.runners.AndroidJUnit4;


import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static org.junit.Assert.assertEquals;

public class DabaBaseIntegrationTest {
    public MongoCollection<Document> mongoCollection;
    MongoClient mongoClient;

    @Before
    public void setUp() {
    }

    @Test
    public void sheltersDBTest(){
        mongoClient = new MongoClient("10.0.2.2", 27017);
        MongoDatabase database = mongoClient.getDatabase("SafeZone_DB");
        mongoCollection = database.getCollection("Shelters");
        String lon="34.808214546000045";
        String lat="31.259018768000033";
         Document myDoc = mongoCollection.find(and(eq("lat", lat), eq("lon", lon))).first();
         assertEquals(myDoc.get("lat"),lat);
         assertEquals(myDoc.get("lon"),lon);

    }

}
