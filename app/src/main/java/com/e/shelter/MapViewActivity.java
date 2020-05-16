package com.e.shelter;

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
import android.view.MenuItem;
import android.view.View;

import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import com.e.shelter.utilities.Shelter;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import java.util.Objects;

import static com.mongodb.client.model.Filters.eq;


public class MapViewActivity extends FragmentActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener,
        GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, RatingDialogListener {

    private GoogleMap googleMap;
    private SupportMapFragment mapFragment;
    private MaterialSearchBar searchBar;
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
    private View infowindow;
    private TextView infoTitle;
    private TextView infoSnippet;
    private TextView statusTxt;
    private TextView capacityTxt;
    private TextView ratingTxt;
    private TextView ratingCountTxt;
    private OnInfoWindowElemTouchListener infoButtonListener;
    private MaterialButton edit_btn;
    private MaterialButton favorite_btn;
    private List<String> suggestions = new ArrayList<>();
    private String userEmail;
    private String userName = "Max";
    private String userLastName = "Pok";
    private MaterialButton saveShelterButton;
    private MaterialButton editShelterButton;
    private MaterialButton rateShelterButton;
    private Marker selectedMarker;
    private List<String> favoriteShelters;
    private LinearLayout bottomSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private List<Shelter> sheltersList;
    private Shelter selectedShelter;
    private AppRatingDialog appRatingDialog;
    private AppCompatRatingBar ratingBarInfoDialog;


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
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                if (selectedMarker.isInfoWindowShown()) 
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
                Log.d("LOG_TAG", getClass().getSimpleName() + " text changed " + searchBar.getText());
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (searchLocationMarker != null) {
                    searchLocationMarker.remove();
                    searchBar.clearSuggestions();
                }
            }

        });
        searchBar.setCardViewElevation(10);
        searchBar.setSuggstionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {
            }
        });

        //Header
        View header = navigationView.getHeaderView(0);
        TextView header_email = header.findViewById(R.id.email_header);
        //userEmail = getIntent().getStringExtra("email");
        userEmail = "adir123";
        if (userEmail != null) header_email.setText(userEmail);

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

        //Bottom Information Window
        createBottomSheetDialog();

        //Rating Dialog Window
        createRatingDialog();
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
            //My Location Position
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 40, 325);

            //Toolbar position
            View toolbar = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("4"));
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) toolbar.getLayoutParams();
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            rlp.addRule(RelativeLayout.ALIGN_RIGHT, RelativeLayout.TRUE);
            rlp.setMargins(0, 0, 0, 970);
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






        // Navigation toolbar
        this.googleMap.getUiSettings().setMapToolbarEnabled(true);
        this.googleMap.setOnMapClickListener(this);

        // Add shelters to google map
        add_shelters_into_map();

        // Get favorite shelters from DB
        retrieveFavoriteShelters();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 50 && resultCode == RESULT_OK) {
            getDeviceLocation();
        }
        if (requestCode == 2) {
            retrieveFavoriteShelters();
            if (selectedMarker != null) {
                if (checkIfShelterInFavorite(selectedMarker.getTitle())) {
                    saveShelterButton.setText("SAVED");
                    saveShelterButton.setIconResource(R.drawable.savedbookmark_icon_white);
                } else {
                    saveShelterButton.setText("SAVE  ");
                    saveShelterButton.setIconResource(R.drawable.savebookmark_icon_white);
                }
            }
        }
        if (requestCode == 3) {
            Log.i("TAG", "From Edit Screen");
            add_shelters_into_map();
            onMarkerClick(selectedMarker);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        selectedMarker = null;
        selectedShelter = null;
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (searchLocationMarker != null && searchLocationMarker.getTitle().equals(marker.getTitle())) return false;
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        selectedMarker = marker;

        for (Shelter shelter : sheltersList) {
            if (shelter != null && shelter.getName().equals(selectedMarker.getTitle())) {
                selectedShelter = shelter;
                break;
            }
        }
        infoTitle.setText(selectedMarker.getTitle());
        infoSnippet.setText(selectedMarker.getSnippet());
        ratingCountTxt.setText(("(" + selectedShelter.getRateCount() + ")"));
        ratingTxt.setText(String.valueOf(Double.parseDouble(selectedShelter.getRating())));
        ratingBarInfoDialog.setRating(Float.parseFloat(selectedShelter.getRating()));
        capacityTxt.setText(selectedShelter.getCapacity());
        if (selectedShelter.getStatus().equals("open")) {
            statusTxt.setTextColor(getResources().getColor(R.color.quantum_googgreen));
        } else statusTxt.setTextColor(getResources().getColor(R.color.quantum_googred));
        statusTxt.setText(selectedShelter.getStatus());

        if (checkIfShelterInFavorite(marker.getTitle())) {
            saveShelterButton.setText("SAVED");
            saveShelterButton.setIconResource(R.drawable.savedbookmark_icon_white);
        } else {
            saveShelterButton.setText("SAVE  ");
            saveShelterButton.setIconResource(R.drawable.savebookmark_icon_white);
        }
        return false;
    }

    public void createBottomSheetDialog() {
        bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        saveShelterButton = findViewById(R.id.info_window_save_button);
        editShelterButton = findViewById(R.id.info_window_edit_button);
        rateShelterButton = findViewById(R.id.info_window_rate_button);

        infoTitle = findViewById(R.id.info_window_title);
        infoSnippet = findViewById(R.id.info_window_address);
        ratingTxt = findViewById(R.id.info_window_rating);
        ratingCountTxt = findViewById(R.id.info_window_rating_count);
        capacityTxt = findViewById(R.id.info_window_capacity);
        statusTxt = findViewById(R.id.info_window_status);
        ratingBarInfoDialog = findViewById(R.id.rating_bar_info_window);

        //Save Button Function
        saveShelterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saveShelterButton.getText().equals("SAVE  ")) {
                    addSelectedShelterToFavorites();
                    saveShelterButton.setText("SAVED");
                    saveShelterButton.setIconResource(R.drawable.savedbookmark_icon_white);

                } else {
                    removeSelectedShelterFromFavorites();
                    saveShelterButton.setText("SAVE  ");
                    saveShelterButton.setIconResource(R.drawable.savebookmark_icon_white);
                }
            }
        });
        //Edit Button Function
        editShelterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MapViewActivity.this, EditShelterDetails.class);
                if (i != null) {
                    i.putExtra("name", selectedMarker.getTitle());
                    i.putExtra("address", selectedMarker.getSnippet());
                    i.putExtra("status", selectedShelter.getStatus());
                    i.putExtra("capacity", selectedShelter.getCapacity());
                    i.putExtra("lon", selectedShelter.getLon());
                    i.putExtra("lat", selectedShelter.getLat());
                    startActivityForResult(i, 3);
                }
            }
        });
        //Rate Button Function
        rateShelterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appRatingDialog.show();
            }
        });
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                bottomSheet.setAlpha(1 + slideOffset);
            }
        });
    }

    public void createRatingDialog() {
        appRatingDialog = new AppRatingDialog.Builder()
                .setPositiveButtonText("Send Feedback")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite OK", "Very Good", "Excellent"))
                .setDefaultRating(5)
                .setTitle("Rate this shelter")
                .setDescription("Please select some stars and give your feedback")
                .setCommentInputEnabled(true)
                .setStarColor(R.color.quantum_googblue)
                .setNoteDescriptionTextColor(R.color.quantum_googblue)
                .setTitleTextColor(R.color.quantum_black_100)
                .setDescriptionTextColor(R.color.quantum_grey)
                .setHint("|")
                .setHintTextColor(R.color.quantum_googblue)
                .setCommentTextColor(R.color.quantum_googblue)
                .setCommentBackgroundColor(R.color.quantum_bluegrey50)
                .setWindowAnimation(R.style.MyDialogFadeAnimation)
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .create(MapViewActivity.this);
    }

    public boolean checkIfShelterInFavorite(String shelterName) {
        return favoriteShelters.contains(shelterName);
    }

    public void retrieveFavoriteShelters() {
        favoriteShelters = new ArrayList<>();
        MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
        MongoDatabase database = mongoClient.getDatabase("SafeZone_DB");
        MongoCollection<Document> mongoCollection = database.getCollection("FavoriteShelters");
        Document myDoc = mongoCollection.find(eq("user_email", userEmail)).first();
        assert myDoc != null;
        List<Document> favList = (List<Document>) myDoc.get("favorite_shelters");
        for (int i = 0; i < favList.size(); i++) {
            favoriteShelters.add(Objects.requireNonNull(favList.get(i).get("shelter_name")).toString());
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
    public void add_shelters_into_map() {
        //final MapWrapperLayout mapWrapperLayout = (MapWrapperLayout) findViewById(R.id.map_relative_layout);
        //mapWrapperLayout.init(googleMap, getPixelsFromDp(this, 39 + 20));
        //connect to DB
        sheltersList = new ArrayList<>();
        final MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
        DB shelter_db = mongoClient.getDB("SafeZone_DB");
        final DBCollection shelter_db_collection = shelter_db.getCollection("Shelters");
        DBCursor cursor = shelter_db_collection.find();
        while (cursor.hasNext()) {
            BasicDBObject object = (BasicDBObject) cursor.next();
            LatLng latLng = new LatLng(Double.parseDouble(object.getString("lat")), Double.parseDouble(object.getString("lon")));
            MarkerOptions markerOptions = new MarkerOptions();
            //Save the information about the shelter
            markerOptions.position(latLng).snippet(object.getString("address")).title(object.getString("name"));
            Shelter shelter = new Shelter(object.getString("name"), object.getString("address"),
                    object.getString("lat"), object.getString("lon"),
                    object.getString("status"), object.getString("capacity"),
                    object.getString("rating"), object.getString("rating_amount"));
            System.out.println(shelter);
            sheltersList.add(shelter);
            ////            if (loginActivity.checkuser[1] == true) {
////            edit_btn.setVisibility(View.VISIBLE);
////            }
//
//            //Buttons clicks
//            infoButtonListener = new OnInfoWindowElemTouchListener(favorite_btn, null, null) {
//                @Override
//                protected void onClickConfirmed(View v, Marker marker) {
//                    // Here we can perform some action triggered after clicking the button
//                    if (favorite_btn.getText().equals("SAVE")) {
//                        addSelectedShelterToFavorites();
//                        favorite_btn.setText("SAVED");
//                        favorite_btn.setIconResource(R.drawable.savedbookmark_icon_white);
//                    } else {
//                        removeSelectedShelterFromFavorites();
//                        favorite_btn.setText("SAVE");
//                        favorite_btn.setIconResource(R.drawable.savebookmark_icon_white);
//                    }
//                    selectedMarker.showInfoWindow();
//                }
//            };
//            favorite_btn.setOnTouchListener(infoButtonListener);
//
//            infoButtonListener = new OnInfoWindowElemTouchListener(edit_btn, null, null) {
//                @Override
//                protected void onClickConfirmed(View v, Marker marker) {
//                    String lon = Double.toString(marker.getPosition().longitude);
//                    String lat = Double.toString(marker.getPosition().latitude);
//                    MongoDatabase database = mongoClient.getDatabase("SafeZone_DB");
//                    MongoCollection<Document> mongoCollection = database.getCollection("Shelters");
//                    Document myDoc = mongoCollection.find(and(eq("lat", lat), eq("lon", lon))).first();
//
//                    Intent i = new Intent(MapViewActivity.this, EditShelterDetails.class);
//                    if (i != null) {
//                        i.putExtra("name", marker.getTitle());
//                        i.putExtra("address", marker.getSnippet());
//                        i.putExtra("status", myDoc.get("status").toString());
//                        i.putExtra("capacity", myDoc.get("capacity").toString());
//                        i.putExtra("lon", lon);
//                        i.putExtra("lat", lat);
//                        startActivity(i);
//                    }
//                    Toast.makeText(getApplicationContext(), "click on edit buttun", Toast.LENGTH_LONG).show();
//                }
//            };
//            edit_btn.setOnTouchListener(infoButtonListener);
//            //Set the information window
//            googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
//                @Override
//                public View getInfoWindow(Marker marker) {
//                    return null;
//                }
//
//                @Override
//                public View getInfoContents(Marker marker) {
//                    String lon = Double.toString(marker.getPosition().longitude);
//                    String lat = Double.toString(marker.getPosition().latitude);
//                    MongoDatabase database = mongoClient.getDatabase("SafeZone_DB");
//                    MongoCollection<Document> mongoCollection = database.getCollection("Shelters");
//                    Document myDoc = mongoCollection.find(and(eq("lat", lat), eq("lon", lon))).first();
//                    // Setting up the infoWindow with current's marker info
//                    infoTitle.setText(marker.getTitle());
//                    infoSnippet.setText(marker.getSnippet());
//                    statusTxt.setText(myDoc.get("status").toString());
//                    capacityTxt.setText(myDoc.get("capacity").toString());
//                    ratingTxt.setText(myDoc.get("rating").toString());
//                    infoButtonListener.setMarker(marker);
//
//                    // We must call this to set the current marker and infoWindow references
//                    // to the MapWrapperLayout
//                    mapWrapperLayout.setMarkerWithInfoWindow(marker, infowindow);
//                    return infowindow;
//                }
//            });


            //Add markers
            googleMap.addMarker(markerOptions);

            googleMap.setOnMarkerClickListener(this);



        }
        mongoClient.close();
    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * Adding the shelters information from the local shelters.json file to mongoDB.
     * Use this function only to add the file information into your local db.
     */
    public void addSheltersToMongodb() {
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
                                    .append("capacity", "1.25 square meters per person")
                                    .append("rating", 0.0);
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
     * Search given address from 'addresses.json' file. If address didn't found it will display a proper massage on screen.
     *
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
        //Toast.makeText(MapViewActivity.this, "Address not found", Toast.LENGTH_LONG).show();
        Snackbar.make(Objects.requireNonNull(getCurrentFocus()), "Address not found", Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    /**
     * Adding the shelters information from the local shelters.json file to mongoDB.
     * Use this function only to add the file information into your local db.
     */
    public void addAddressesToMongodb() {
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
     * Starts specific function/intent from the navigation bar based on the item selected.
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
            case R.id.nav_settings:
                Intent settingsActive = new Intent(this, SettingsActivity.class);
                startActivity(settingsActive);
                return false;
            case R.id.nav_night_mode_switch:
                nightModeSwitch();
                return false;
            case R.id.nav_favorite_shelters:
                Intent favIntent = new Intent(this, FavoritesActivity.class);
                favIntent.putExtra("userEmail", userEmail);
                startActivityForResult(favIntent, 2);
                return false;
            case R.id.nav_logout:
                break;
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

    /**
     * Switches the application theme to [night mode/day mode].
     */
    public void nightModeSwitch() {
        if (((SwitchCompat) navigationView.getMenu().findItem(R.id.nav_night_mode_switch).getActionView()).isChecked()) {
            ((SwitchCompat) navigationView.getMenu().findItem(R.id.nav_night_mode_switch).getActionView()).setChecked(false);
        } else
            ((SwitchCompat) navigationView.getMenu().findItem(R.id.nav_night_mode_switch).getActionView()).setChecked(true);

        ((SwitchCompat) navigationView.getMenu().findItem(R.id.nav_night_mode_switch).getActionView()).setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
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
     * Adds the shelters from mongoDB to firebase DB.
     * TODO: Use only once.
     */
    public void addSheltersToFireBaseDataBase() {
        MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
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
                    object.getString("capacity"),
                    object.getString("rating"),
                    object.getString("rating_amount"));
            Shelters.add(shelter);
        }
    }

    /**
     * Find shelters addresses.
     *
     * @param latitude  - shelter latitude
     * @param longitude - shelter longitude
     * @return address
     * @throws IOException when gpc or internet is offline
     */
    public String findSheltersAddresses(double latitude, double longitude) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(MapViewActivity.this, Locale.getDefault());

        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        System.out.println("address = " + address);
        return address;
    }


    public void addSelectedShelterToFavorites() {
        MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
        MongoDatabase database = mongoClient.getDatabase("SafeZone_DB");
        MongoCollection<Document> mongoCollection = database.getCollection("FavoriteShelters");
        Document newShelter = new Document()
                .append("shelter_name", selectedMarker.getTitle())
                .append("address", selectedMarker.getSnippet())
                .append("lat", selectedMarker.getPosition().latitude)
                .append("lon", selectedMarker.getPosition().longitude);

        //adding shelter to DB
        mongoCollection.updateOne(eq("user_email", userEmail), Updates.addToSet("favorite_shelters", newShelter));
        mongoClient.close();

        //adding shelter to array list
        favoriteShelters.add(selectedMarker.getTitle());

        Snackbar snackbar = Snackbar.make(bottomSheet, "Saved to favorites", Snackbar.LENGTH_LONG);
        snackbar.show();
        //Toast.makeText(MapViewActivity.this, "Saved to favorites", Toast.LENGTH_LONG).show();
    }

    public void removeSelectedShelterFromFavorites() {
        MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
        MongoDatabase database = mongoClient.getDatabase("SafeZone_DB");
        MongoCollection<Document> mongoCollection = database.getCollection("FavoriteShelters");
        Document shelterToRemove = new Document()
                .append("shelter_name", selectedMarker.getTitle())
                .append("address", selectedMarker.getSnippet())
                .append("lat", selectedMarker.getPosition().latitude)
                .append("lon", selectedMarker.getPosition().longitude);

        //removing shelter from DB
        mongoCollection.updateOne(eq("user_email", userEmail), Updates.pull("favorite_shelters", shelterToRemove));

        //removing shelter from array list
        favoriteShelters.remove(selectedMarker.getTitle());

        Snackbar snackbar = Snackbar.make(bottomSheet, "Removed from favorites", Snackbar.LENGTH_LONG);
        snackbar.show();
        //Toast.makeText(MapViewActivity.this, "Removed from favorites", Toast.LENGTH_LONG).show();
    }

    public void addShelterToMapFromFireBase() {
    }

    @Override
    public void onNegativeButtonClicked() {
        // Do Nothing
    }

    @Override
    public void onNeutralButtonClicked() {
        // Do Nothing
    }

    @Override
    public void onPositiveButtonClicked(int i, String s) {
        if (s.isEmpty()) {
            Toast.makeText(MapViewActivity.this, "Please write a feedback", Toast.LENGTH_LONG).show();
            appRatingDialog.show();
        } else {
            //add review for selected shelter
            MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
            MongoDatabase database = mongoClient.getDatabase("SafeZone_DB");
            MongoCollection<Document> mongoCollection = database.getCollection("UserReviews");
            Document newReview = new Document()
                    .append("shelter_name", selectedMarker.getTitle())
                    .append("user_email", userEmail)
                    .append("user_name", userName + " " + userLastName)
                    .append("review", s)
                    .append("stars", String.valueOf(i));
            mongoCollection.insertOne(newReview);

            //update shelter total rating
            int amount = 0;
            double averageRating, totalRating = 0;
            DB reviews_db = mongoClient.getDB("SafeZone_DB");
            DBCollection shelter_db_collection = reviews_db.getCollection("UserReviews");
            DBCursor cursor = shelter_db_collection.find();
            while (cursor.hasNext()) {
                BasicDBObject object = (BasicDBObject) cursor.next();
                if (object.get("shelter_name").equals(selectedMarker.getTitle())) {
                    amount++;
                    totalRating += Double.parseDouble(object.get("stars").toString());
                }
            }
            averageRating = totalRating / amount;
            DecimalFormat REAL_FORMATTER = new DecimalFormat("#.#");
            MongoCollection<Document> mongoCollection2 = database.getCollection("Shelters");
            mongoCollection2.updateOne(eq("name", selectedMarker.getTitle()), Updates.set("rating", REAL_FORMATTER.format(averageRating)));
            mongoCollection2.updateOne(eq("name", selectedMarker.getTitle()), Updates.set("rating_amount", String.valueOf(amount)));
            mongoClient.close();

            //update marker window dialog
            selectedShelter.setRating(REAL_FORMATTER.format(averageRating));
            selectedShelter.setRateCount(String.valueOf(amount));
            onMarkerClick(selectedMarker);
        }
    }

    public void insertRatingCount() {
        MongoClient mongoClient = new MongoClient("10.0.2.2", 27017);
        MongoDatabase database = mongoClient.getDatabase("SafeZone_DB");
        MongoCollection<Document> mongoCollection = database.getCollection("Shelters");
        mongoCollection.updateMany(eq("rating","0"), Updates.set("rating", String.valueOf(0)));
        mongoCollection.updateMany(eq("rating","0"), Updates.set("rating_amount", String.valueOf(0)));
        mongoClient.close();
    }
}

