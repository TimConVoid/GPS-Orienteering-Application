package com.example.myproject1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CoursePicker extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {


    GoogleMap mMap;
    Marker myMarker;
    final ArrayList<LatLng> latLngs = new ArrayList<>();
    ArrayList<Waypoint> myWaypoints = new ArrayList<>();
    //ArrayList<MarkerOptions> markers = new ArrayList<>();
    String courseName;
    Marker[] markers;

    //GoogleMap googleMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_course_picker);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mMap = googleMap;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference().child("Courses");


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
        mMap.setMyLocationEnabled(true);





        String refValue = myRef.toString();

        Log.d("Hello", refValue);


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    DataSnapshot waypoints = ds.child("Waypoints");
                    for (DataSnapshot wp : waypoints.getChildren()) {
                        int id = wp.child("id").getValue(Integer.class);

                            String desc = wp.child("description").getValue(String.class);
                            String imgSrc = wp.child("imgSrc").getValue(String.class);
                            double lat = wp.child("coordinates").child("latitude").getValue(Double.class);
                            double lon = wp.child("coordinates").child("longitude").getValue(Double.class);
                            final String courseName = wp.child("courseName").getValue(String.class);
                            String difficulty = (String) wp.child("difficulty").getValue();
                            LatLng latLng = new LatLng(lat, lon);
                            latLngs.add(latLng);
                            Waypoint waypoint = new Waypoint(id, latLng, desc, imgSrc, courseName, difficulty);
                            myWaypoints.add(waypoint);



                        //Plotting only first waypoint for each course
                          if(waypoint.getId() == 1) {

                           MarkerOptions marker = new MarkerOptions().position(latLng).title(courseName);
                           if(difficulty.equals("Hard")){
                              marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                           }else if (difficulty.equals("Intermediate")){
                               marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                           }else if (difficulty.equals("Easy")){
                               marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                           } else {
                               marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

                           }

                           Marker marker1 = googleMap.addMarker(marker);


                           googleMap.addMarker(marker);



                              float zoomLevel = 10.0f; //This goes up to 21
                              googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));





                         }


                       googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                           @Override
                           public void onInfoWindowClick(Marker marker) {
                               if(getIntent().getExtras().containsKey("Leaders")){
                                   Intent intent = new Intent(CoursePicker.this, LeaderBoard.class);
                                   intent.putExtra("CourseName", marker.getTitle());
                                   startActivity(intent);

                               }else {
                                   Intent intent = new Intent(CoursePicker.this, Course.class);
                                   intent.putExtra("CourseName", marker.getTitle());
                                   startActivity(intent);


                               }


                               }



                       });




                    }
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //LatLng latLng = latLngs.get(0);


        Log.d("yolo", latLngs.toString());

/*
       Waypoint newWay = myWaypoints.get(0);

        markers = new Marker[myWaypoints.size() - 1];
        for (int i = 0; i < myWaypoints.size(); i++) {
            markers[i] = createMarker(myWaypoints.get(i).getCoordinates().latitude, myWaypoints.get(i).getCoordinates().longitude, myWaypoints.get(i).getCourseName(), "", 1);
        }
        mMap.setOnMarkerClickListener(CoursePicker.this);

 */



    }

/*

@Override
public void onInfoWindowClick(Marker marker) {

    Integer clickCount = (Integer) marker.getTag();

    // Check if a click count was set, then display the click count.
    if (clickCount != null) {
        clickCount = clickCount + 1;
        marker.setTag(clickCount);
        Toast.makeText(this,
                marker.getTitle() +
                        " has been clicked " + clickCount + " times.",
                Toast.LENGTH_SHORT).show();
    }
}

 */


    protected Marker createMarker(double latitude, double longitude, String title, String snippet, int iconResID) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 9f));

        myMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title));
        return myMarker;

    }


    @Override
    public boolean onMarkerClick(Marker marker) {

        Intent intent = new Intent(CoursePicker.this, Course.class);
        intent.putExtra("CourseName", marker.getTitle());
        startActivity(intent);


        return true;
    }
}



