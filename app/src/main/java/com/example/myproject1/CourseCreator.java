package com.example.myproject1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;

public class CourseCreator extends FragmentActivity implements OnMapReadyCallback {

        private static final int CHOOSE_IMAGE = 100;
        private GoogleMap mMap;
        private LocationListener locationListener;
        LocationManager locationManager;
        private final long MIN_TIME = 1000; // 1 second
        private final long MIN_DISTANCE = 5; // 5 metres
        private LatLng latLng;
        Button button;
        Button buttonEnd;
        ImageView imgMap;
        MarkerOptions place1;
        MarkerOptions place2;
        Polyline pLine;
        EditText desc;
        EditText name;
        EditText difficulty;
        int id = 0;
        Uri uriMapImg;
        String mapImgURL;



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
        imgMap = findViewById(R.id.imageMap);

        desc = findViewById(R.id.editTextDesc);

        name = findViewById(R.id.edittxt_coursename);

        difficulty = findViewById(R.id.txt_difficulty);

        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);



        imgMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });



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

                        Waypoint waypoint = new Waypoint(id, latLng, desc.getText().toString(), mapImgURL, name.getText().toString(), difficulty.getText().toString());
                        desc.setText("");
                        desc.setHint("Next instruction");
                        arrayList.add(waypoint);

                        float zoomLevel = 10.0f; //This goes up to 21
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));




                        buttonEnd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                uploadDetails(arrayList);
                                System.out.println(arrayList.toString());
                                locationManager.removeUpdates(locationListener);
                                Toast.makeText(getApplicationContext(), "Course added", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CourseCreator.this,AdminDash.class);
                                startActivity(intent);

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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){

            uriMapImg = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriMapImg);
                imgMap.setImageBitmap(bitmap);
                uploadImageToFirebase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void uploadImageToFirebase() {
        final StorageReference profileImgRef = FirebaseStorage.getInstance().getReference("MapImages/" + System.currentTimeMillis() + ".jpg");

        if (uriMapImg != null){
            profileImgRef.putFile(uriMapImg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mapImgURL = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CourseCreator.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }

    }
    public void showImageChooser(){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Proifle Picture"), CHOOSE_IMAGE);
    }
}
