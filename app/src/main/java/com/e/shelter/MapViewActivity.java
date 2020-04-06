package com.e.shelter;

import androidx.fragment.app.FragmentActivity;
import android.content.Context;
import android.os.Bundle;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import android.os.StrictMode;

public class MapViewActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_window);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapAPI);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    /**
     *  Creates all the necessary settings for the map.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        add_shelters_to_mongodb();
        map = googleMap;
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.night_map));
        map.getUiSettings().setMapToolbarEnabled(false);
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);

        add_shelters_into_map(map);
    }

    /**
     * Adding the shelters location from mongoDB into the map.
     */
    public void add_shelters_into_map(GoogleMap googleMap) {
        MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
        DB shelter_db = mongoClient.getDB("SafeZone_DB");
        DBCollection shelter_db_collection = shelter_db.getCollection("Shelters");
        DBCursor cursor = shelter_db_collection.find();
        while(cursor.hasNext()) {
            BasicDBObject object = (BasicDBObject) cursor.next();
            LatLng latLng = new LatLng(Double.parseDouble(object.getString("lat")), Double.parseDouble(object.getString("lon")));
            googleMap.addMarker(new MarkerOptions().position(latLng).title(object.getString("name")));
        }
    }

    /**
     * Adding the shelters information from the local shelters.json file to mongoDB.
     * TODO: You must place the 'shelters.json' file in 'app/src/main/assets' directory before you use the current function.
     * Use this function only to add the file information into your local db.
     */
    public void add_shelters_to_mongodb() {
        // TODO: add more information, for example: open/close, accessibility, capacity.
        try {
            JSONArray obj = new JSONArray(loadJSONFromAsset(getApplicationContext()));
            MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
            MongoDatabase shelter_db = mongoClient.getDatabase("SafeZone_DB");
            MongoCollection shelter_db_collection = shelter_db.getCollection("Shelters");
            for (int i = 0 ; i < obj.length() ; i++) {
                JSONObject value = (JSONObject) obj.get(i);
                BasicDBObject document = new BasicDBObject();
                document.put("name", value.get("name"));
                document.put("lat", value.get("lat"));
                document.put("lon", value.get("lon"));
                shelter_db_collection.insertOne(document);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     *  This function loads the json file from asset folder into a string.
     */
    public String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("shelters.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }
}
