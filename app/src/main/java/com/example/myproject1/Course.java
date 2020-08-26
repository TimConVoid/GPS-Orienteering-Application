package com.example.myproject1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Constants;
import com.google.firebase.database.core.Context;


import java.util.ArrayList;
import java.util.List;

public class Course extends AppCompatActivity {


    private static final String TAG = "MyTag";
    private static final int FINE_LOCATION_REQUEST_CODE = 5;
    private GeofencingClient geofencingClient;
    private GeofencingRequest geofencingRequest;
    //private Geofence geofence;
    private PendingIntent geofencePendingIntent;
    private GeofenceHelper geofenceHelper;
    List<Geofence> geofenceList = new ArrayList<>();
    TextView desc;
    Button btnNext;
    String courseName;
    GoogleMap mMap;
    ArrayList<Waypoint> waypoints = new ArrayList<>();
    final int RADIUS = 10;
    Location location;
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;


    int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        desc = findViewById(R.id.textView_desc);

        btnNext = findViewById(R.id.button_next);

        Bundle extras = getIntent().getExtras();

        courseName = extras.getString("CourseName");

        geofencingClient = LocationServices.getGeofencingClient(this);

        geofenceHelper = new GeofenceHelper(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);

        // locationRequest.setInterval(2000);


        enableUserLocation();


        Log.d("myExtra", courseName);

        LatLng latLng = new LatLng(65.03949, 154.39039);
        addGeofence("One", latLng, 10);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference().child("Courses").child(courseName);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    for (DataSnapshot wp : ds.getChildren()) {
                        int id = wp.child("id").getValue(Integer.class);
                        String desc = wp.child("description").getValue(String.class);
                        String imgSrc = wp.child("imgSrc").getValue(String.class);
                        double lat = wp.child("coordinates").child("latitude").getValue(Double.class);
                        double lon = wp.child("coordinates").child("longitude").getValue(Double.class);
                        final String courseName = wp.child("courseName").getValue(String.class);
                        LatLng latLng = new LatLng(lat, lon);

                        addGeofence(Integer.toString(id), latLng, 10);
                        Waypoint waypoint = new Waypoint(id, latLng, desc, imgSrc, courseName);
                        waypoints.add(waypoint);
                    }
                }
                setInstructions(waypoints);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Success :)", Toast.LENGTH_SHORT).show();

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.
                        permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.
                        permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_REQUEST_CODE);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FINE_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                // mMap.setMyLocationEnabled(true);
                Toast.makeText(getApplicationContext(), "Permission Granted hoe :)", Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(getApplicationContext(), "Dennniiiiieed :(", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void setInstructions(final ArrayList<Waypoint> wp) {
        Waypoint first = wp.get(0);
        String firstInst = first.getDescription();
        desc.setText(firstInst);


        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String next = wp.get(count).getDescription();
                desc.setText(next);
                count++;

            }
        });
    }


    private void addGeofence(String ID, LatLng latLng, float radius) {
        Geofence geofence = geofenceHelper.getGeofence(ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER
                | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Geofences added...");
                        Toast.makeText(getApplicationContext(), "Geofences added", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.d(TAG, "OnFailure" + errorMessage);
                        Toast.makeText(getApplicationContext(), "Geofences fucked", Toast.LENGTH_SHORT).show();


                    }
                });

    }


}