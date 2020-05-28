package com.e.shelter;

import android.os.Bundle;

import com.e.shelter.adapers.FavoriteListAdapter;
import com.e.shelter.utilities.FavoriteCard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import android.widget.ListView;


import java.util.ArrayList;
import java.util.Map;


public class FavoritesActivity extends MainActivity {

    private ArrayList<FavoriteCard> list = new ArrayList<>();
    private ListView shelterCardListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        setTitle("Favorites");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        shelterCardListView = findViewById(R.id.favListView);
        createFavoriteCardList();
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
        firebaseFirestore.collection("FavoriteShelters").document(firebaseAuth.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                @SuppressWarnings("unchecked")
                                ArrayList<Map> favShelters = (ArrayList<Map>) documentSnapshot.get("favoriteShelters");
                                for (int i = 0; i < favShelters.size(); i++) {
                                    FavoriteCard favoriteCard = new FavoriteCard(favShelters.get(i).get("name").toString(), favShelters.get(i).get("address").toString(),
                                            Double.parseDouble(favShelters.get(i).get("latitude").toString()), Double.parseDouble(favShelters.get(i).get("longitude").toString()));
                                    list.add(favoriteCard);
                                }
                            }
                        }
                        FavoriteListAdapter adapter = new FavoriteListAdapter(getBaseContext(), R.layout.content_favorites, list, getIntent().getStringExtra("userEmail"));
                        shelterCardListView.setAdapter(adapter);
                    }
                });
    }


}
