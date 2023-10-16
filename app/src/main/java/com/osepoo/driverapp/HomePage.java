// HomePage.java

package com.osepoo.driverapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import android.Manifest;
import com.google.android.gms.location.LocationRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.atomic.AtomicBoolean;

public class HomePage extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2161;

    private GoogleMap googleMap;
    private Marker currentPositionMarker;
    private FusedLocationProviderClient locationProviderClient;
    private LocationRequest locationRequest;
    private boolean locationFlag = true;

    private final AtomicBoolean driverOnlineFlag = new AtomicBoolean(false);

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location location = locationResult.getLastLocation();
            if (location == null) return;
            if (locationFlag) {
                locationFlag = false;
                animateCamera(location);
            }
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                String userId = auth.getCurrentUser().getUid();
                DatabaseReference userLocationReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
                userLocationReference.child("latitude").setValue(location.getLatitude());
                userLocationReference.child("longitude").setValue(location.getLongitude());
            }
            if (driverOnlineFlag.get()) {
                // Update location in Firebase or perform any other actions
                Log.d("DriverApp", "Driver online, updating location");
            }
            showOrAnimateMarker(location);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page2);
        FirebaseApp.initializeApp(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);



        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Intent mainIntent = new Intent(HomePage.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
            return;
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.supportMap);
        assert mapFragment != null;
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                HomePage.this.googleMap = googleMap;
                locationProviderClient = LocationServices.getFusedLocationProviderClient(HomePage.this);
                if (checkAndRequestLocationPermission()) {
                    locationRequest = getLocationRequest();
                    requestLocationUpdates();
                }

                SwitchCompat driverStatusSwitch = findViewById(R.id.driverStatusSwitch);
                driverStatusSwitch.setOnCheckedChangeListener((buttonView, b) -> {
                    driverOnlineFlag.set(b);
                    if (b)
                        Toast.makeText(HomePage.this, getResources().getString(R.string.online), Toast.LENGTH_SHORT).show();
                    else {
                        Toast.makeText(HomePage.this, getResources().getString(R.string.offline), Toast.LENGTH_SHORT).show();
                        // Update status in Firebase or perform any other actions
                    }
                });
            }
        });
    }


    private void animateCamera(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(latLng, 15);
        googleMap.animateCamera(cameraUpdate);
    }

    private void showOrAnimateMarker(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (currentPositionMarker == null) {
            currentPositionMarker = googleMap.addMarker(new com.google.android.gms.maps.model.MarkerOptions().position(latLng));
        } else {
            currentPositionMarker.setPosition(latLng);
        }
    }

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        return locationRequest;
    }






    private boolean checkAndRequestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return false;  // Permission not granted
        } else {
            return true;  // Permission already granted
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates();
            } else {
                Toast.makeText(this, "Location Permission denied", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        } else {
            // You don't have the permission, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        requestLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationProviderClient.removeLocationUpdates(locationCallback);
    }
}
