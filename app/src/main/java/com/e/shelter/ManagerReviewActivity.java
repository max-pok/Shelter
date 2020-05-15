package com.e.shelter;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.e.shelter.adapers.ReviewAdapter;
import com.e.shelter.utilities.FavoriteCard;
import com.e.shelter.utilities.reviewBox;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.mongodb.client.model.Filters.eq;

public class ManagerReviewActivity extends AppCompatActivity {
    private ArrayList<reviewBox> list;
    private ListView reviewBoxListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        setTitle("Reviews");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        list = new ArrayList<>();
        reviewBoxListView = findViewById(R.id.reviewListView);
        createFavoriteCardList();

        ReviewAdapter adapter = new ReviewAdapter(this, R.layout.content_review, list, getIntent().getStringExtra("userEmail"));
        reviewBoxListView.setAdapter(adapter);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(2);
        finish();
    }

    public void createFavoriteCardList() {
        String userEmail = getIntent().getStringExtra("userEmail");
        MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
        MongoDatabase database = mongoClient.getDatabase("SafeZone_DB");
        MongoCollection<Document> mongoCollection = database.getCollection("userReviews");
        List<Document> favList = (List<Document>) database.getCollection("userReviews");
        for (int i = 0; i < favList.size(); i++) {
            list.add(new reviewBox(Objects.requireNonNull(favList.get(i).get("userID")).toString(),
                    Objects.requireNonNull(favList.get(i).get("review")).toString(),
                    Objects.requireNonNull(favList.get(i).get("address")).toString(),
                    Objects.requireNonNull(favList.get(i).get("email")).toString()));
        }

//
//        MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
//        DB shelter_db = mongoClient.getDB("SafeZone_DB");
//        DBCollection user_collection = shelter_db.getCollection("userReviews");
//
//        DBCursor cursor = user_collection.find();
//        Document reviews = new Document();
//
//        while (cursor.hasNext()) {
//            BasicDBObject object = (BasicDBObject) cursor.next();
//            reviews.append("userID",object.get("name")).append("review",object.get("review")).append("address",object.get("address")).append("email",object.get("email"));
//            list.add(reviews);
//        }
    }
}
