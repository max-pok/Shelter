package com.e.shelter;

import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapViewActivity extends FragmentActivity implements OnMapReadyCallback {
    GoogleMap mapAPI;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_window);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapAPI);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapAPI = googleMap;
        mapAPI.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.day_map));
        LatLng latLng = new LatLng(31.067197, 35.034576);
        mapAPI.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
        mapAPI.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mapAPI.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
    }

    public void add_shelters_into_map(GoogleMap googleMap) { }

    public void add_shelters_to_mongodb() { }

    public void current_location(GoogleMap googleMap) { }


}
