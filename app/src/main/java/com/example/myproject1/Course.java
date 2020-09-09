package com.example.myproject1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.media.Image;
import android.nfc.cardemulation.OffHostApduService;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Constants;
import com.google.firebase.database.core.Context;


import java.util.ArrayList;
import java.util.List;

public class Course extends AppCompatActivity implements SensorEventListener {


    private float GEOFENCE_RADIUS = 25;
    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";

    // Compass
    private ImageView imageView;
    private float[] rMat = new float[9];
    private float[] orientation = new float[9];
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetic = new float[3];
    int mAzimuth;
    private SensorManager sensorManager;
    private Sensor mRotationV, mAccelerometer, mMagnetometer;
    private boolean haveSensor = false, haveSensor2 = false;
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagneticSet = false;


    private static final String TAG = "MyTag";
    private static final int FINE_LOCATION_REQUEST_CODE = 5;
    private GeofencingClient geofencingClient;
    private GeofencingRequest geofencingRequest;
    //private Geofence geofence;
    private PendingIntent geofencePendingIntent;
    private GeofenceHelper geofenceHelper;
    List<Geofence> geofenceList = new ArrayList<>();
    TextView desc;
    Button btnNext, btnLeader;
    String courseName;
    GoogleMap mMap;
    Chronometer chronometer;
    ArrayList<Waypoint> waypoints = new ArrayList<>();
    final int RADIUS = 25;
    Location location;
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;

    LocationListener locationListener;

    // user details
    TextView userName;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userid = user.getDisplayName();



    GeofenceBroadcastReceiver geofenceBroadcastReceiver;

     int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        desc = findViewById(R.id.textView_desc);


        btnNext = findViewById(R.id.button_next);

        btnLeader = findViewById(R.id.btnLeaders);
        btnLeader.setVisibility(View.GONE);
        btnLeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToLeaders();
            }
        });

        userName = findViewById(R.id.txt_username);

        Bundle extras = getIntent().getExtras();

        courseName = extras.getString("CourseName");

        geofencingClient = LocationServices.getGeofencingClient(this);

        geofenceHelper = new GeofenceHelper(this);

        imageView = (ImageView) findViewById(R.id.compass);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        userName.setText(userid);

        chronometer = findViewById(R.id.chronometer);


        chronometer.start();

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {



                location.getLatitude();
                location.getLongitude();

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

        btnNext.setEnabled(false);

        start();

        //fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);

        // locationRequest.setInterval(2000);


        enableUserLocation();


        Log.d("myExtra", courseName);

        LatLng latLng = new LatLng(65.03949, 154.39039);



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

    public void start() {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null) {
            if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null ||
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) {
                noSensorAlert();
            } else {
                mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mMagnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

                haveSensor = sensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
                haveSensor2 = sensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI);

            }
        } else {
            mRotationV = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            haveSensor = sensorManager.registerListener(this, mRotationV, SensorManager.SENSOR_DELAY_UI);

        }
    }

    public void noSensorAlert() {
        AlertDialog.Builder alertDialogue = new AlertDialog.Builder(this);
        alertDialogue.setMessage("Your device doesn't support the compass")
                .setCancelable(false)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
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

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        LatLng firstLatLng = first.getCoordinates();
        addGeofence(firstLatLng,GEOFENCE_RADIUS);
        String firstInst = first.getDescription();
        desc.setText(firstInst);


        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (count == wp.size()) {
                    btnNext.setVisibility(View.GONE);
                    btnLeader.setVisibility(View.VISIBLE);
                    double elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
                    String chronoText = chronometer.getText().toString();
                    chronometer.stop();
                    Toast.makeText(getApplicationContext(), "Course finished! Your time was: " + elapsedMillis*0.001, Toast.LENGTH_SHORT).show();
                    uploadTime(elapsedMillis);

                } else {
                    Waypoint nextWaypoint = wp.get(count);
                    LatLng latLng = nextWaypoint.getCoordinates();
                    addGeofence(latLng,GEOFENCE_RADIUS);
                    String next = wp.get(count).getDescription();
                    desc.setText(next);
                    count++;
                }


            }
        });


    }

    private void moveToLeaders() {

        Intent intent = new Intent(Course.this, LeaderBoard.class);
        intent.putExtra("CourseName", courseName);
        startActivity(intent);
    }


    private void addGeofence(LatLng latLng, float radius) {

        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
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
                        Log.d(TAG, "onSuccess: Geofence Added...");
                        Toast.makeText(getApplicationContext(), "Geofence added", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.d(TAG, "onFailure: " + errorMessage);
                    }
                });
    }

    public void stop() {
        if (haveSensor && haveSensor2) {
            sensorManager.unregisterListener(this, mAccelerometer);
            sensorManager.unregisterListener(this, mMagnetometer);
        } else {
            if (haveSensor) {
                sensorManager.unregisterListener(this, mRotationV);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        start();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rMat, event.values);
            mAzimuth = (int) ((Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360);

        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mLastMagnetic, 0, event.values.length);
            mLastMagneticSet = true;

        }
        if (mLastMagneticSet && mLastAccelerometerSet) {
            SensorManager.getRotationMatrix(rMat, null, mLastAccelerometer, mLastMagnetic);
            SensorManager.getOrientation(rMat, orientation);
            mAzimuth = (int) ((Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360);

        }
        mAzimuth = Math.round(mAzimuth);
        imageView.setRotation(-mAzimuth);


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void uploadTime(final double time) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        final double realTime = time*0.001;

        DatabaseReference myRef3 = database.getReference("Users/"+user.getUid()+"/Name");
        myRef3.setValue(user.getDisplayName());

        DatabaseReference myRef2 = database.getReference().child("Users").child(user.getUid()).child("Times").child(courseName);


        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    double origTime = (double) snapshot.getValue();

                    if (origTime > realTime) {
                        final DatabaseReference myRef1 = database.getReference("Users/" + user.getUid() + "/Times/" + courseName);
                        myRef1.setValue(realTime);

                    }

                } else {
                    final DatabaseReference myRef1 = database.getReference("Users/" + user.getUid() + "/Times/" + courseName);
                    myRef1.setValue(realTime);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    public void showNextBtn(){
        btnNext.setEnabled(true);

    }
}