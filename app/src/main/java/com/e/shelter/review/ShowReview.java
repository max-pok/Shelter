package com.e.shelter.review;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.ListView;

import com.e.shelter.MainActivity;
import com.e.shelter.R;
import com.e.shelter.adapers.ReviewListAdapter;
import com.e.shelter.utilities.Review;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import java.util.ArrayList;

public class ShowReview extends MainActivity {

    private ListView reviewListView;
    private ArrayList<Review> reviewArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_review_activity);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>Reviews</font>"));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        reviewListView = findViewById(R.id.review_list_view);
        retrieveUserReviews();
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


    public void retrieveUserReviews() {
        firebaseFirestore.collection("UserReviews").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Review review = document.toObject(Review.class);
                                reviewArrayList.add(review);
                            }
                        } else {
                            Log.d("Show Review Class", "Error getting documents: ", task.getException());
                        }
                        ReviewListAdapter adapter = new ReviewListAdapter(getBaseContext(), R.layout.content_reviews, reviewArrayList);
                        reviewListView.setAdapter(adapter);
                    }
                });
    }
}




