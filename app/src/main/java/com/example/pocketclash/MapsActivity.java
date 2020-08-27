package com.example.pocketclash;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends Dialog implements OnMapReadyCallback {

    private static final String TAG = "pttt";
    private static final float DEFAULT_ZOOM = 15f;
    private Context context;
    private GoogleMap mMap;
    private MapView mapView;
    private Double lat;
    private Double lon;

    public MapsActivity(@NonNull Context context, MyLocation myLocation) {
        super(context);
        this.lat = myLocation.getLat();
        this.lon = myLocation.getLon();
        this.context = context;
        Log.d(TAG, "MapsActivity: lat = " + lat);
        Log.d(TAG, "MapsActivity: lon = " + lon);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initMap();
    }

    private void initMap() {
        Log.d(TAG, "initMap: initing map");

        mapView = findViewById(R.id.maps_MAP_map);
        mapView.onCreate(onSaveInstanceState());
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: Moving the camera to: lat: " + latLng.latitude + " lon: " +
                latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: Map is ready");
        mMap = googleMap;
        LatLng latLng = new LatLng(lat, lon);
        moveCamera(latLng, DEFAULT_ZOOM);
        /** Display current location*/
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        mMap.setMyLocationEnabled(true);
        /** Remove center location button*/
//        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        addScoreLocationMarker(latLng);
    }

    /**
     * A method to add the score location marker on the map
     */
    private void addScoreLocationMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        mMap.clear();
        markerOptions.title("Current Position");
        markerOptions.getPosition();
        mMap.addMarker(markerOptions);
    }
}