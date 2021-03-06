package com.example.myproject1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CoursePicker extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {



    final ArrayList<LatLng> latLngs = new ArrayList<>();
    ArrayList<Waypoint> myWaypoints = new ArrayList<>();
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


        /*

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference().child("Courses");


        String refValue = myRef.toString();

        Log.d("Hello", refValue);


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    DataSnapshot waypoints = ds.child("Waypoints");
                    for(DataSnapshot wp : waypoints.getChildren()) {



                        int id = wp.child("id").getValue(Integer.class);
                        String desc = wp.child("description").getValue(String.class);
                        String imgSrc = wp.child("imgSrc").getValue(String.class);
                        double lat = wp.child("coordinates").child("latitude").getValue(Double.class);
                        double lon = wp.child("coordinates").child("longitude").getValue(Double.class);
                        LatLng latLng = new LatLng(lat, lon);
                        latLngs.add(latLng);

                        Waypoint waypoint = new Waypoint(id, latLng, desc, imgSrc);
                        myWaypoints.add(waypoint);


                    }
                }
                Log.d("latLngAray", latLngs.toString());


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    } */
    /*
    public void retrieveData(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference().child("Courses");


        String refValue = myRef.toString();

        Log.d("Hello", refValue);


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    DataSnapshot waypoints = ds.child("Waypoints");
                    for(DataSnapshot wp : waypoints.getChildren()) {



                        int id = wp.child("id").getValue(Integer.class);
                        String desc = wp.child("description").getValue(String.class);
                        String imgSrc = wp.child("imgSrc").getValue(String.class);
                        double lat = wp.child("coordinates").child("latitude").getValue(Double.class);
                        double lon = wp.child("coordinates").child("longitude").getValue(Double.class);
                        LatLng latLng = new LatLng(lat, lon);
                        latLngs.add(latLng);

                        Waypoint waypoint = new Waypoint(id, latLng, desc, imgSrc);
                        myWaypoints.add(waypoint);
                    }
                }
                Log.d("latLngAray", latLngs.toString());


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

     */
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference().child("Courses");


        String refValue = myRef.toString();

        Log.d("Hello", refValue);


       myRef.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               for (DataSnapshot ds : dataSnapshot.getChildren()) {
                   DataSnapshot waypoints = ds.child("Waypoints");
                   for(DataSnapshot wp : waypoints.getChildren()) {

                       int id = wp.child("id").getValue(Integer.class);
                       String desc = wp.child("description").getValue(String.class);
                       String imgSrc = wp.child("imgSrc").getValue(String.class);
                       double lat = wp.child("coordinates").child("latitude").getValue(Double.class);
                       double lon = wp.child("coordinates").child("longitude").getValue(Double.class);
                       final String courseName = wp.child("courseName").getValue(String.class);

                       LatLng latLng = new LatLng(lat, lon);
                       latLngs.add(latLng);

                       Waypoint waypoint = new Waypoint(id, latLng, desc, imgSrc, courseName);
                       myWaypoints.add(waypoint);

                       // Plotting only first waypoint of each course
                       if(waypoint.getId() == 1) {
                           googleMap.addMarker(new MarkerOptions()
                                   .position(latLng)
                                   .title(courseName));

                           // [START_EXCLUDE silent]
                           float zoomLevel = 10.0f; //This goes up to 21
                           googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));


                           googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                               @Override
                               public void onInfoWindowClick(Marker marker) {
                                   Intent intent = new Intent(CoursePicker.this, Course.class);
                                   intent.putExtra("CourseName", courseName);
                                   startActivity(intent);

                               }
                           });



                       }






                   }
               }
               Log.d("latLngAray", latLngs.toString());




           }
           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });


        //LatLng latLng = latLngs.get(0);


       Log.d("yolo", latLngs.toString());



        }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

        /*
    private void placeMarker(GoogleMap googleMap, ArrayList<Waypoint> wp) {
        Waypoint firstWaypoint = wp.get(0);
        LatLng latLng = firstWaypoint.getCoordinates();

        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Marker in "));

        // [START_EXCLUDE silent]
        float zoomLevel = 10.0f; //This goes up to 21
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));


    }

         */


}



