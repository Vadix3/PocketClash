package com.example.pocketclash;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Dialog implements OnMapReadyCallback {
    private Double lat = 0d;
    private Double lon = 0d;
    private Context context;
    private LatLng latLng;
    private GoogleMap mMap;
    private Location currentLocation;
    private static final float DEFAULT_ZOOM = 15f;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;



    public MapFragment(@NonNull Context context, MyLocation location) {
        super(context);
        this.context = context;
        this.lat = location.getLat();
        this.lon = location.getLon();
        this.latLng = new LatLng(this.lat, this.lon);
        Log.d("pttt", "Latlng: " + this.lat + "   " + this.lon);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_maps);
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("pttt", "Lat = " + this.lat + " Lon = " + this.lon);
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
//        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Hi Im here");
//        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
//        googleMap.addMarker(markerOptions);
    }

    @Override
    public void onBackPressed() {
        dismiss();
        super.onBackPressed();
    }
}
