package com.e.shelter;

import android.content.Intent;
import android.os.Bundle;

import com.e.shelter.utilities.FavoriteCard;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Objects;

public class FavoritesActivity extends AppCompatActivity {

    private ArrayList<FavoriteCard> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        setTitle("Favorites");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        list = new ArrayList<>();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void createFavoriteCardList() {
        Intent intent = getIntent();
        String userEmail = intent.getStringExtra("userEmail");
        MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
        MongoDatabase database = mongoClient.getDatabase("SafeZone_DB");
        MongoCollection<Document> shelter_db_collection = database.getCollection("FavoriteShelters");

    }
}
