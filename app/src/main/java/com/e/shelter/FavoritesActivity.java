package com.e.shelter;

import android.os.Bundle;

import com.e.shelter.adapers.FavoriteListAdapter;
import com.e.shelter.utilities.FavoriteCard;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.ListView;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.mongodb.client.model.Filters.eq;

public class FavoritesActivity extends AppCompatActivity {

    private ArrayList<FavoriteCard> list;
    private ListView shelterCardListView;

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
        shelterCardListView = findViewById(R.id.favListView);
        createFavoriteCardList();

        FavoriteListAdapter adapter = new FavoriteListAdapter(this, R.layout.content_favorites, list, getIntent().getStringExtra("userEmail"));
        shelterCardListView.setAdapter(adapter);
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
        MongoCollection<Document> mongoCollection = database.getCollection("FavoriteShelters");
        Document myDoc = mongoCollection.find(eq("user_email", userEmail)).first();
        List<Document> favList = (List<Document>) myDoc.get("favorite_shelters");
        for (int i = 0; i < favList.size(); i++) {
            list.add(new FavoriteCard(Objects.requireNonNull(favList.get(i).get("shelter_name")).toString(),
                    Objects.requireNonNull(favList.get(i).get("address")).toString(),
                    Double.parseDouble(Objects.requireNonNull(favList.get(i).get("lat")).toString()),
                    Double.parseDouble(Objects.requireNonNull(favList.get(i).get("lon")).toString())));
        }
    }

}
