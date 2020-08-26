package com.example.myproject1;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

        private GoogleMap mMap;
        private LocationListener locationListener;
        LocationManager locationManager;
        private final long MIN_TIME = 1000; // 1 second
        private final long MIN_DISTANCE = 5; // 5 metres
        private LatLng latLng;
        Button button;
        Button buttonEnd;
        MarkerOptions place1;
        MarkerOptions place2;
        Polyline pLine;
        EditText desc;
        EditText name;
        int id = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFrag);
        mapFragment.getMapAsync(this);

        button = findViewById(R.id.btnTakePoint);
        buttonEnd = findViewById(R.id.buttonEnd);

        desc = findViewById(R.id.editTextDesc);

        name = findViewById(R.id.edittxt_coursename);

        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);






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
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        final ArrayList<Waypoint> arrayList = new ArrayList<Waypoint>();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**

                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                try {
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                } catch (SecurityException e){
                    e.printStackTrace();
                }
                **/

                id++;

                locationListener = new LocationListener()



                {
                    @Override
                    public void onLocationChanged(Location location) {










                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng).title("My Location" +id));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                        Waypoint waypoint = new Waypoint(id, latLng, desc.getText().toString(), "wiajdiawj", name.getText().toString());
                        desc.setText("");
                        arrayList.add(waypoint);




                        buttonEnd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                uploadDetails(arrayList);
                                System.out.println(arrayList.toString());
                                locationManager.removeUpdates(locationListener);

                            }
                        });




                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };


                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


                Intent intent = new Intent(MapsActivity.this, MapsActivity.class);



                Looper locationLooper = Looper.myLooper();


                try {
                    locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER,locationListener, null);
                } catch (SecurityException e){
                    e.printStackTrace();
                }



            }
        });








            }

    public void uploadDetails(ArrayList<Waypoint> wp){


        for(Waypoint a : wp){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Courses/" +name.getText().toString()+"/Waypoints").push();

            myRef.setValue(a);

        }
    }
}
