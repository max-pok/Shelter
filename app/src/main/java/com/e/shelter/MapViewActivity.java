package com.e.shelter;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.view.menu.MenuView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import com.e.shelter.utilities.InfoWindowData;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.android.quadtree.PointQuadTree;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

/*
import com.e.shelter.utilities.Member;
import com.e.shelter.utilities.Shelter;
*/


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
    private FirebaseFirestore database;
    private ViewGroup infowindow;
    private TextView infoTitle;
    private TextView infoSnippet;
    private TextView statusTxt;
    private TextView capacityTxt;
    private TextView ratingTxt;
    private OnInfoWindowElemTouchListener infoButtonListener;
    private Button edit_btn;
    private Button favorite_btn;
    private Button review_btn;
    private Button rating_btn;
    private MenuItem see_review;
    public Context ctx =this;
    private String currentAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
            public void onSearchStateChanged(boolean enabled) {
            }

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
                        .setCountry("israel")
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

        //Header
        View header = navigationView.getHeaderView(0);
        TextView header_email = header.findViewById(R.id.email_header);
        Intent intent = getIntent();
        String value = intent.getStringExtra("email");


        if (value != null) header_email.setText(value);


        //Switch
        navigationView.getMenu().findItem(R.id.nav_night_mode_switch).setActionView(new SwitchCompat(this));
        ((SwitchCompat) navigationView.getMenu().findItem(R.id.nav_night_mode_switch).getActionView()).setChecked(false);
        ((SwitchCompat) navigationView.getMenu().findItem(R.id.nav_night_mode_switch).getActionView()).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getBaseContext(), R.raw.night_map));
                } else
                    googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getBaseContext(), R.raw.day_map));
            }
        });

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
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(31.2530, 34.7915), 12));
        this.googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getBaseContext(), R.raw.day_map));

        addSheltersToFireBaseDataBase();
        add_shelters_into_map(this.googleMap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 50 && resultCode == RESULT_OK) {
            getDeviceLocation();
        }
    }

    /**
     * Finds device location, if it fails the function retrieves the last known location.
     */
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
        LoginActivity loginActivity = new LoginActivity();
        final MapWrapperLayout mapWrapperLayout = (MapWrapperLayout) findViewById(R.id.map_relative_layout);
        mapWrapperLayout.init(googleMap, getPixelsFromDp(this, 39 + 20));
        //connect to DB
        final MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
        DB shelter_db = mongoClient.getDB("SafeZone_DB");
        final DBCollection shelter_db_collection = shelter_db.getCollection("Shelters");
        DBCursor cursor = shelter_db_collection.find();

        while (cursor.hasNext()) {
            BasicDBObject object = (BasicDBObject) cursor.next();
            LatLng latLng = new LatLng(Double.parseDouble(object.getString("lat")), Double.parseDouble(object.getString("lon")));
            final MarkerOptions markerOptions = new MarkerOptions();
            //Save the information about the shelter
            markerOptions.position(latLng).snippet(object.getString("address")).title(object.getString("name"));
            final InfoWindowData info = new InfoWindowData();
            info.setName(object.getString("name"));
            info.setAddress(object.getString("address"));
            info.setStatus(object.getString("status"));
            info.setCapacity( object.getString("capacity"));
            info.setRating(object.getString("rating"));
            info.setid(object.getObjectId("_id"));


            this.infowindow = (ViewGroup)getLayoutInflater().inflate(R.layout.info_window, null);
            this.infoTitle = (TextView)infowindow.findViewById(R.id.nameTxt);
            this.infoSnippet = (TextView)infowindow.findViewById(R.id.addressTxt);
            this.statusTxt = (TextView)infowindow.findViewById(R.id.statusTxt);
            this.capacityTxt = (TextView)infowindow.findViewById(R.id.capacityTxt);
            this.ratingTxt = (TextView)infowindow.findViewById(R.id.ratingTxt);

            this.favorite_btn = (Button)infowindow.findViewById(R.id.favorite_btn);


            this.edit_btn = (Button)infowindow.findViewById(R.id.edit_btn);
            if (loginActivity.checkuser[1]== true){
                edit_btn.setVisibility(View.VISIBLE);
            }

            //Buttons clicks
            this.infoButtonListener = new OnInfoWindowElemTouchListener(favorite_btn, getResources().getDrawable(R.drawable.btn_bg), getResources().getDrawable(R.drawable.btn_bg)){
                @Override
                protected void onClickConfirmed(View v, Marker marker) {
                    // Here we can perform some action triggered after clicking the button
                    Toast.makeText(MapViewActivity.this, "click on add to Favorite", Toast.LENGTH_SHORT).show();
                }
            };
            this.favorite_btn.setOnTouchListener(infoButtonListener);

            //add user review
            this.review_btn = (Button)infowindow.findViewById(R.id.review_button);
            if (loginActivity.checkuser[1]== true){
                review_btn.setVisibility(View.INVISIBLE);
            }

            this.infoButtonListener = new OnInfoWindowElemTouchListener(review_btn, getResources().getDrawable(R.drawable.btn_bg), getResources().getDrawable(R.drawable.btn_bg)){
                @Override
                protected void onClickConfirmed(View v, Marker marker) {
                    // Here we can perform some action triggered after clicking the button
                    Intent i =new  Intent(MapViewActivity.this, UserReviewActivity.class);
                    if(i !=null) {
                        i.putExtra("address", currentAddress);
                        startActivity(i);
                    }
                    Toast.makeText(MapViewActivity.this, "click on add review", Toast.LENGTH_SHORT).show();
                }
            };
            this.review_btn.setOnTouchListener(infoButtonListener);

            //ratings

            this.rating_btn = (Button)infowindow.findViewById(R.id.rate_btn);
            if (loginActivity.checkuser[1]== true){
                rating_btn.setVisibility(View.VISIBLE);
            }

            this.infoButtonListener = new OnInfoWindowElemTouchListener(rating_btn, getResources().getDrawable(R.drawable.btn_bg), getResources().getDrawable(R.drawable.btn_bg)){
                @Override
                protected void onClickConfirmed(View v, Marker marker) {
                    //Intent i =new  Intent(MapViewActivity.this, RatingActivity.class);
                    Intent intent = new Intent(getBaseContext(), RatingActivity.class);
                    intent.putExtra("address", currentAddress);
                    startActivity(intent);

                    Toast.makeText(MapViewActivity.this, "click on add rating", Toast.LENGTH_SHORT).show();
                }
            };
            this.rating_btn.setOnTouchListener(infoButtonListener);

            infoButtonListener = new OnInfoWindowElemTouchListener(edit_btn, getResources().getDrawable(R.drawable.btn_bg),getResources().getDrawable(R.drawable.btn_bg)){
                @Override
                protected void onClickConfirmed(View v, Marker marker) {
                    String lon= Double.toString(marker.getPosition().longitude);
                    String lat=Double.toString(marker.getPosition().latitude);
                    MongoDatabase database = mongoClient.getDatabase("SafeZone_DB");
                    MongoCollection<Document> mongoCollection = database.getCollection("Shelters");
                    Document myDoc = mongoCollection.find(and(eq("lat", lat), eq("lon", lon))).first();

                    Intent i =new  Intent(MapViewActivity.this, EditShelterDetails.class);

                    if(i !=null){
                        i.putExtra("name",marker.getTitle());
                        i.putExtra("address",marker.getSnippet());
                        i.putExtra("status",myDoc.get("status").toString());
                        i.putExtra("capacity",myDoc.get("capacity").toString());
                        i.putExtra("lon",lon);
                        i.putExtra("lat",lat);
                        startActivity(i);
                    }
                    Toast.makeText(getApplicationContext(), "click on edit buttun", Toast.LENGTH_LONG).show();
                }
            };
            edit_btn.setOnTouchListener(infoButtonListener);
            //Set the information window
            googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    currentAddress=marker.getSnippet();
                    System.out.println("@@@@@@@@"+marker.getSnippet());
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    // Setting up the infoWindow with current's marker info
                    infoTitle.setText(marker.getTitle());
                    infoSnippet.setText(marker.getSnippet());
                    statusTxt.setText(info.getStatus());
                    capacityTxt.setText(info.getCapacity());
                    ratingTxt.setText(info.getRating());
                    infoButtonListener.setMarker(marker);

                    // We must call this to set the current marker and infoWindow references
                    // to the MapWrapperLayout
                    mapWrapperLayout.setMarkerWithInfoWindow(marker, infowindow);
                    return infowindow;
                }
            });
            //Add markers
            googleMap.addMarker(new MarkerOptions()
                    .title(info.getName())
                    .snippet(info.getAddress())
                    .position(latLng)
            );

            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        }
        //mongoClient.close();
    }
    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }

    /**
     * Adding the shelters information from the local shelters.json file to mongoDB.
     * TODO: You must place the 'shelters.json' file in 'app/src/main/assets' directory before you use the current function.
     * Use this function only to add the file information into your local db.
     */
    public void add_shelters_to_mongodb() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray obj = new JSONArray(loadJSONFromAsset(getApplicationContext(), "shelters.json"));
                    MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
                    MongoDatabase database = mongoClient.getDatabase("SafeZone_DB");
                    MongoCollection<Document> shelter_db_collection = database.getCollection("Shelters");
                    System.out.println("connected to DB " + obj.length());
                    for (int i = 0; i < obj.length(); i++) {
                        try {
                            JSONObject value = (JSONObject) obj.get(i);
                            String address = findSheltersAddresses(Double.parseDouble(value.get("lat").toString()), Double.parseDouble(value.get("lon").toString()));
                            Document document = new Document("name", value.get("name"))
                                    .append("lat", value.get("lat"))
                                    .append("lon", value.get("lon"))
                                    .append("address", address)
                                    .append("status", "open")
                                    .append("capacity", "1.25 square meters per person");
                            shelter_db_collection.insertOne(document);
                        } catch (IOException e) {
                            System.out.println("Error. Trying to find the address again.");
                            i--;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
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

    /**
     * search given address from 'addresses.json' file. If address didn't found it will display a proper massage on screen.
     * @param address - input received from search bar.
     */
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
                return;
            }
        }
        Toast.makeText(MapViewActivity.this, "Address not found", Toast.LENGTH_LONG).show();
    }

    /**
     * Adding the shelters information from the local shelters.json file to mongoDB.
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

    /**
     *
     * @param item - selected item from side navigation bar.
     * @return true to keep item selected, false otherwise.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {



        switch (item.getItemId()) {
            case R.id.nav_info:
                Intent intent = new Intent(this, ContactPage.class);
                startActivity(intent);
                return false;
            case R.id.review_info:
                LoginActivity loginActivity = new LoginActivity();
                this.see_review = (MenuItem)findViewById(R.id.review_info);
                if (loginActivity.checkuser[1]== true){
                    see_review.setVisible(true);
                }
                Intent seeReviews = new Intent(this, UserReviewActivity.class);
                startActivity(seeReviews);
                return false;
            case R.id.nav_settings:
                Intent settingsActive = new Intent(this, SettingsActivity.class);
                startActivity(settingsActive);
                return false;
            case R.id.nav_night_mode_switch:
                nightModeSwitch();
                return false;
        }
        return false;
    }

    /**
     * Back button press functionality
     */
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void nightModeSwitch() {
        if (((SwitchCompat) navigationView.getMenu().findItem(R.id.nav_night_mode_switch).getActionView()).isChecked()) {
            ((SwitchCompat) navigationView.getMenu().findItem(R.id.nav_night_mode_switch).getActionView()).setChecked(false);
        } else
            ((SwitchCompat) navigationView.getMenu().findItem(R.id.nav_night_mode_switch).getActionView()).setChecked(true);

        ((SwitchCompat) navigationView.getMenu().findItem(R.id.nav_night_mode_switch).getActionView()).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getBaseContext(), R.raw.night_map));
                } else
                    googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getBaseContext(), R.raw.day_map));
            }
        });
    }

    public void addSheltersToFireBaseDataBase() {
        /*MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
        DB shelter_db = mongoClient.getDB("SafeZone_DB");
        DBCollection shelter_db_collection = shelter_db.getCollection("Shelters");
        DBCursor cursor = shelter_db_collection.find();
        database = FirebaseFirestore.getInstance();
        CollectionReference Shelters = database.collection("Shelters");
        while (cursor.hasNext()) {
            BasicDBObject object = (BasicDBObject) cursor.next();
            Shelter shelter = new Shelter(object.getString("name"),
                    object.getString("address"),
                    object.getString("lat"),
                    object.getString("lon"),
                    object.getString("status"),
                    object.getString("capacity"));
            Shelters.add(shelter);
        }*/
    }

    public String findSheltersAddresses(double latitude, double longitude) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(MapViewActivity.this, Locale.getDefault());

        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        System.out.println("address = " + address);
        return address;
    }

}
