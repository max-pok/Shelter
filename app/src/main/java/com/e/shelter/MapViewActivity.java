package com.e.shelter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.navigation.NavigationView;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class MapViewActivity extends FragmentActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {
    private GoogleMap googleMap;
    private SupportMapFragment mapFragment;
    private MaterialSearchBar searchBar;
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictions;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;
    private LocationCallback locationCallback;
    private View mapView;
    private final float defaultZoom = 18;
    private Marker searchLocationMarker;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_window);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Map
        this.mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapAPI);
        assert this.mapFragment != null;
        this.mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();

        //Location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapViewActivity.this);
        Places.initialize(MapViewActivity.this, getString(R.string.map_key));
        placesClient = Places.createClient(this);

        //Autocomplete search
        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        //Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        //Navigation drawer
        navigationView.bringToFront();
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //Search bar
        searchBar = findViewById(R.id.searchBar);
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {}

            @Override
            public void onSearchConfirmed(CharSequence text) {
                if (text != null && text.length() > 0) {
                    searchAddress(text.toString());
                }
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                if (buttonCode == MaterialSearchBar.BUTTON_NAVIGATION) {
                    searchBar.disableSearch();
                    toggle.syncState();
                    drawerLayout.openDrawer(GravityCompat.START);
                    searchBar.disableSearch();
                } else if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
                    searchBar.clearSuggestions();
                    searchBar.disableSearch();
                }
            }
        });
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
                        .setCountry("iw")
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .setSessionToken(token)
                        .setQuery(s.toString())
                        .build();
                placesClient.findAutocompletePredictions(predictionsRequest).addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
                        if (task.isSuccessful()) {
                            FindAutocompletePredictionsResponse predictionsResponse = task.getResult();
                            if (predictionsResponse != null) {
                                predictions = predictionsResponse.getAutocompletePredictions();
                                List<String> suggestionList = new ArrayList<>();
                                for (int i = 0; i < predictions.size(); i++) {
                                    AutocompletePrediction prediction = predictions.get(i);
                                    suggestionList.add(prediction.getFullText(null).toString());
                                }
                                searchBar.updateLastSuggestions(suggestionList);
                                if (!searchBar.isSuggestionsVisible()) {
                                    searchBar.showSuggestionsList();
                                }
                            }
                        } else {
                            Log.i("PlacesError", "prediction fetching task unsuccessful");
                        }
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (searchLocationMarker != null) {
                    searchLocationMarker.remove();
                }
            }
        });
        //Toggle Functions
    }

    /**
     * Creates all the necessary settings for the map.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Google map current location button change
        this.googleMap.setMyLocationEnabled(true);
        this.googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        if (this.mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 40, 325);
        }

        // Check if GPS is enabled or not. If GPS is disabled request user to enable it.
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(MapViewActivity.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
        task.addOnSuccessListener(MapViewActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
            }
        });
        task.addOnFailureListener(MapViewActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                    try {
                        resolvableApiException.startResolutionForResult(MapViewActivity.this, 50);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        // Google map settings
        this.googleMap.getUiSettings().setMapToolbarEnabled(false);
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);

        add_shelters_into_map(this.googleMap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 50 && resultCode == RESULT_OK) {
            getDeviceLocation();
        }
    }

    public void getDeviceLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    lastKnownLocation = task.getResult();
                    if (lastKnownLocation != null) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), defaultZoom));
                    } else {
                        final LocationRequest locationRequest = LocationRequest.create();
                        locationRequest.setInterval(10000);
                        locationRequest.setFastestInterval(5000);
                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                super.onLocationResult(locationResult);
                                if (locationResult == null) {
                                    return;
                                }
                                lastKnownLocation = locationResult.getLastLocation();
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), defaultZoom));
                            }
                        };
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    }
                } else {
                    Toast.makeText(MapViewActivity.this, "unable to get last location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Adding the shelters location from mongoDB into the map.
     */
    public void add_shelters_into_map(GoogleMap googleMap) {
        MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
        DB shelter_db = mongoClient.getDB("SafeZone_DB");
        DBCollection shelter_db_collection = shelter_db.getCollection("Shelters");
        DBCursor cursor = shelter_db_collection.find();
        while (cursor.hasNext()) {
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
            JSONArray obj = new JSONArray(loadJSONFromAsset(getApplicationContext(), "shelters.json"));
            MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
            DB shelter_db = mongoClient.getDB("SafeZone_DB");
            DBCollection shelter_db_collection = shelter_db.getCollection("Shelters");
            for (int i = 0; i < obj.length(); i++) {
                JSONObject value = (JSONObject) obj.get(i);
                BasicDBObject document = new BasicDBObject();
                document.put("name", value.get("name"));
                document.put("lat", value.get("lat"));
                document.put("lon", value.get("lon"));
                shelter_db_collection.insert(document);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function loads the json file from asset folder into a string.
     */
    public String loadJSONFromAsset(Context context, String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
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

    public void searchAddress(String address) {
        MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
        DB shelter_db = mongoClient.getDB("SafeZone_DB");
        DBCollection shelter_db_collection = shelter_db.getCollection("Addresses");
        DBCursor cursor = shelter_db_collection.find();
        while (cursor.hasNext()) {
            BasicDBObject object = (BasicDBObject) cursor.next();
            if ((object.get("StreetName") + " " + object.get("HouseNumber")).contains(address)) {
                double lat = Double.parseDouble(object.getString("lat"));
                double lon = Double.parseDouble(object.getString("lon"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), defaultZoom));
                searchLocationMarker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat, lon))
                        .title(object.get("StreetName") + " " + object.get("HouseNumber"))
                        .snippet(object.get("StreetName") + " " + object.get("HouseNumber"))
                        .icon(BitmapDescriptorFactory.defaultMarker(150)));
                break;
            }
        }

    }

    /**
     * Adding the shelters information from the local shelters.json file to mongoDB.
     * TODO: You must place the 'addresses.json' file in 'app/src/main/assets' directory before you use the current function.
     * Use this function only to add the file information into your local db.
     */
    public void add_addresses_to_mongodb() {
        try {
            JSONArray obj = new JSONArray(loadJSONFromAsset(getApplicationContext(), "addresses.json"));
            MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
            DB shelter_db = mongoClient.getDB("SafeZone_DB");
            DBCollection shelter_db_collection = shelter_db.getCollection("Addresses");
            for (int i = 0; i < obj.length(); i++) {
                JSONObject value = (JSONObject) obj.get(i);
                BasicDBObject document = new BasicDBObject();
                document.put("HouseNumber", value.get("HouseNuber"));
                document.put("StreetName", value.get("streetName"));
                document.put("lat", value.get("lat"));
                document.put("lon", value.get("lon"));
                shelter_db_collection.insert(document);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_info:
                Intent intent = new Intent(this, ContactPage.class);
                startActivity(intent);
                return false;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
