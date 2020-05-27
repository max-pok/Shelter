package com.e.shelter;

import android.os.Bundle;
import android.text.Html;
import android.widget.ListView;

import com.e.shelter.adapers.ReviewListAdapter;
import com.e.shelter.utilities.Review;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ShowReview extends AppCompatActivity {

    private ListView reviewListView;
    private ArrayList<Review> reviewArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_review_activity);

        retrieveReviews();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>Reviews</font>"));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        reviewListView = findViewById(R.id.review_list_view);


        ReviewListAdapter adapter = new ReviewListAdapter(this, R.layout.content_reviews, reviewArrayList);
        reviewListView.setAdapter(adapter);
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void retrieveReviews() {
                MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
                DB db = mongoClient.getDB("SafeZone_DB");
                DBCollection dbCollection = db.getCollection("UserReviews");
                DBCursor cursor = dbCollection.find();
                while (cursor.hasNext()) {
                    BasicDBObject object = (BasicDBObject) cursor.next();
                    reviewArrayList.add(new Review(object.getString("shelter_name"), object.getString("user_name"),object.getString("user_email"),object.getString("review"),object.getString("stars")));
                }
            }
}

