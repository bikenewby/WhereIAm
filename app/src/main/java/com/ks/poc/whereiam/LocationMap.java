package com.ks.poc.whereiam;

import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Location currentLoc;
    private String who;
    private String dateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Get current location from Intent
        currentLoc = (Location) getIntent().getParcelableExtra("LOCATION");
        who = getIntent().getStringExtra("WHO");
        dateTime = getIntent().getStringExtra("DATETIME");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        if (currentLoc != null) {
            // Add a marker in current location and move the camera
            LatLng current = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());
            Marker whereamI = mMap.addMarker(new MarkerOptions().position(current).title(dateTime + " - " + who).snippet("(" + currentLoc.getLatitude() + ", " + currentLoc.getLongitude() + ")"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current,18f));
            whereamI.showInfoWindow();
        } else {
            Toast.makeText(LocationMap.this, "No current Location available.", Toast.LENGTH_LONG).show();
        }
    }
}
