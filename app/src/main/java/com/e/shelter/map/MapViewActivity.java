package com.e.shelter.map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import com.e.shelter.ShowUsersActivity;
import com.e.shelter.contactus.ContactPage;
import com.e.shelter.EditShelterDetails;
import com.e.shelter.FavoritesActivity;
import com.e.shelter.GlobalMessage;
import com.e.shelter.LoginActivity;
import com.e.shelter.R;
import com.e.shelter.settings.SettingsActivity;
import com.e.shelter.review.ShowReview;
import com.e.shelter.utilities.FavoriteCard;
import com.e.shelter.utilities.Review;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static com.e.shelter.LoginActivity.email;

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
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private TextView infoTitle;
    private TextView infoSnippet;
    private TextView statusTxt;
    private TextView capacityTxt;
    private TextView ratingTxt;
    private TextView ratingCountTxt;
    private List<String> suggestions = new ArrayList<>();

    private String userEmail;
    private String userFullName;
    private String permission = "admin";
    private String uid;
    private MaterialButton saveShelterButton;
    private MaterialButton editShelterButton;
    private MaterialButton rateShelterButton;
    private Marker selectedMarker;
    private List<String> favoriteShelters;
    private LinearLayout bottomSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private HashMap<String, Shelter> sheltersList;
    private Shelter selectedShelter;
    private String selectedShelterUID;
    private AppRatingDialog appRatingDialog;
    private AppCompatRatingBar ratingBarInfoDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        checkPermission(Manifest.permission.SEND_SMS, 1);
        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, 2);
        checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, 3);
        checkPermission(Manifest.permission.INTERNET, 4);

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
                if (selectedMarker != null && selectedMarker.isInfoWindowShown())
                    selectedMarker.hideInfoWindow();
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

        userEmail = getIntent().getStringExtra("email");
        userFullName = getIntent().getStringExtra("full_name");
        uid = getIntent().getStringExtra("uid");
        //permission = getIntent().getStringExtra("permission");
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
        this.googleMap.setOnMarkerClickListener(this);

        // Add shelters to google map
        addSheltersIntoGoogleMap();
        
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
            Log.i("MapViewActivity", "From edit screen - finished");
            updateSelectedShelter();
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        selectedMarker = null;
        selectedShelter = null;
        selectedShelterUID = null;
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //if selected marker is a search location marker - do nothing
        if (searchLocationMarker != null && searchLocationMarker.getTitle().equals(marker.getTitle())) return false;

        //opens bottom dialog sheet when shelter marker is selected
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        selectedMarker = marker;

        //finds selected shelter & shelter uid from shelter list
        for (Map.Entry<String, Shelter> shelter: sheltersList.entrySet()) {
            if (shelter != null && shelter.getValue().getName().equals(selectedMarker.getTitle())) {
                selectedShelter = shelter.getValue();
                selectedShelterUID = shelter.getKey();
                break;
            }
        }
        //dialog init
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
        //init
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
                i.putExtra("name", selectedMarker.getTitle());
                i.putExtra("address", selectedMarker.getSnippet());
                i.putExtra("status", selectedShelter.getStatus());
                i.putExtra("capacity", selectedShelter.getCapacity());
                startActivityForResult(i, 3);
            }
        });
        //Rate Button Function
        rateShelterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appRatingDialog.show();
            }
        });

        //bottom sheet onSlide animation
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                bottomSheet.setAlpha(1 + slideOffset);
            }
        });

        if (permission.equals("user")) {
            editShelterButton.setVisibility(View.INVISIBLE);
        }
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
        database.collection("FavoriteShelters").document(uid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                @SuppressWarnings("unchecked")
                                ArrayList<Map> favShelters = (ArrayList<Map>) documentSnapshot.get("favoriteShelters");
                                for (int i = 0; i < favShelters.size(); i++) {
                                    favoriteShelters.add(favShelters.get(i).get("name").toString());
                                }
                            }
                        }
                    }
                });

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
     * Adding the shelters location from Firebase into the map.
     */
    public void addSheltersIntoGoogleMap() {
        sheltersList = new HashMap<>();
        database.collection("Shelters").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Shelter shelter = document.toObject(Shelter.class);
                        sheltersList.put(document.getId(), shelter);
                        LatLng latLng = new LatLng(Double.parseDouble(shelter.getLat()), Double.parseDouble(shelter.getLon()));
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng).snippet(shelter.getAddress()).title(shelter.getName());
                        googleMap.addMarker(markerOptions);
                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });
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
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    /**
     * Search given address from 'addresses.json' file. If address didn't found it will display a proper massage on screen.
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
     * Starts specific function/intent from the navigation bar based on the item selected.
     * @param item - selected item from side navigation bar.
     * @return true to keep item selected, false otherwise.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_info:
                Intent contactsIntent = new Intent(this, ContactPage.class);
                contactsIntent.putExtra("permission", permission);
                startActivity(contactsIntent);
                break;
            case R.id.nav_settings:
                Intent settingsActive = new Intent(this, SettingsActivity.class);
                startActivity(settingsActive);
                break;
            case R.id.nav_night_mode_switch:
                nightModeSwitch();
                break;
            case R.id.nav_show_user:
                Intent userActive = new Intent(this, ShowUsersActivity.class);
                startActivity(userActive);                break;
            case R.id.nav_show_reviews:
                Intent reviewActive = new Intent(this, ShowReview.class);
                startActivity(reviewActive);
                return false;
            case R.id.nav_global_message:
                Intent globalMessageActive = new Intent(this, GlobalMessage.class);
                //startActivity(globalMessageActive);
                startActivityForResult(globalMessageActive, 2);

                return false;

            case R.id.nav_favorite_shelters:
                Intent favIntent = new Intent(this, FavoritesActivity.class);
                favIntent.putExtra("uid", uid);
                startActivityForResult(favIntent, 2);
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MapViewActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
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
        DB db = mongoClient.getDB("SafeZone_DB");
        DBCollection dbCollection = db.getCollection("Shelters");
        DBCursor cursor = dbCollection.find();
        database = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = database.collection("Shelters");
        while (cursor.hasNext()) {
            BasicDBObject object = (BasicDBObject) cursor.next();
            Shelter shelter = new Shelter(object.getString("name"),
                    object.getString("address"),
                    object.getString("lat"),
                    object.getString("lon"),
                    object.getString("status"),
                    ThreadLocalRandom.current().nextInt(50, 76) + " square meter",
                    "0",
                    "0");
            collectionReference.add(shelter);
        }
    }


    /**
     * Find shelters addresses.
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
        return address;
    }

    /**
     * Adds selected shelter to favorite array list & database.
     * This function only used from bottom information dialog & only when the save button text equals to 'SAVE'.
     */
    public void addSelectedShelterToFavorites() {
        FavoriteCard favoriteCard = new FavoriteCard(selectedMarker.getTitle(), selectedMarker.getSnippet(),
                selectedMarker.getPosition().latitude, selectedMarker.getPosition().longitude);

        //add shelter name to favorite list
        favoriteShelters.add(selectedMarker.getTitle());

        //add shelter to database
        database.collection("FavoriteShelters").document(uid).update("favoriteShelters", FieldValue.arrayUnion(favoriteCard));

        Snackbar snackbar = Snackbar.make(bottomSheet, "Saved to favorites", Snackbar.LENGTH_LONG);
        snackbar.show();
    }


    public void removeSelectedShelterFromFavorites() {
        FavoriteCard favoriteCard = new FavoriteCard(selectedMarker.getTitle(), selectedMarker.getSnippet(),
                selectedMarker.getPosition().latitude, selectedMarker.getPosition().longitude);

        //remove shelter name from favorite list
        favoriteShelters.remove(selectedMarker.getTitle());

        //remove shelter from databse
        database.collection("FavoriteShelters").document(uid).update("favoriteShelters", FieldValue.arrayRemove(favoriteCard));

        Snackbar snackbar = Snackbar.make(bottomSheet, "Removed from favorites", Snackbar.LENGTH_LONG);
        snackbar.show();
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
            addReview(i, s);
        }
    }


    // Function to check and request permission.
    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(MapViewActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(MapViewActivity.this, new String[]{permission}, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            // Checking whether user granted the permission or not.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Showing the toast message
                Toast.makeText(MapViewActivity.this, "SEND SMS Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MapViewActivity.this, "SEND SMS Permission Denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MapViewActivity.this, "LOCATION Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MapViewActivity.this, "LOCATION Permission Denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 3) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MapViewActivity.this, "LOCATION Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MapViewActivity.this, "LOCATION Permission Denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 4) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MapViewActivity.this, "INTERNET Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MapViewActivity.this, "INTERNET Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void addReview(int stars, String review) {
        Date currentTime = Calendar.getInstance().getTime();
        Review newReview = new Review(selectedShelter.getName(), userFullName, userEmail, review, String.valueOf(stars), currentTime.toString());

        //add review for selected shelter
        database.collection("UserReviews").add(newReview);

        //update shelter total rating
        database.collection("UserReviews").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int amount = 0;
                    double averageRating, totalRating = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.get("shelterName").toString().equals(selectedMarker.getTitle())) {
                            amount++;
                            totalRating += Double.parseDouble(document.get("stars").toString());
                        }
                    }

                    averageRating = totalRating / amount;
                    DecimalFormat decimalFormat = new DecimalFormat("#.#");
                    database.collection("Shelters").document(selectedShelterUID).update("rating", decimalFormat.format(averageRating));
                    database.collection("Shelters").document(selectedShelterUID).update("rateCount", String.valueOf(amount));

                    //update marker window dialog
                    selectedShelter.setRating(decimalFormat.format(averageRating));
                    selectedShelter.setRateCount(String.valueOf(amount));
                    onMarkerClick(selectedMarker);
                } else {
                    Log.d("Show Review", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void updateSelectedShelter() {
        FirebaseFirestore.getInstance().collection("Shelters").document(selectedShelterUID)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    selectedShelter = task.getResult().toObject(Shelter.class);
                    sheltersList.replace(selectedShelterUID, selectedShelter);
                    selectedMarker.setTitle(selectedShelter.getName());
                    selectedMarker.setSnippet(selectedShelter.getAddress());
                    onMarkerClick(selectedMarker);
                }
            }
        });
    }

}

