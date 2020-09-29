package com.example.myproject1;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Course extends AppCompatActivity implements SensorEventListener {

    //DB
    StorageReference storageReference;
    String generatedFilePath;

    // Geofence
    private float GEOFENCE_RADIUS = 30;
    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";
    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;
    private static final String TAG = "MyTag";

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

    // UI
    Uri downloadUri;
    private Animator currentAnimator;
    private int shortAnimationDuration;
    TextView desc;
    Button btnNext, btnLeader,btnStart;
    String courseName;
    Chronometer chronometer;
    ImageView imageMap;
    ArrayList<Waypoint> waypoints = new ArrayList<>();

    // Location
    private LocationListener locationListener;
    LocationManager locationManager;

    // User details
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userid = user.getDisplayName();

    //Counter for clickListener
    int count = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        registerReceiver(broadcastReceiver, new IntentFilter("GEOFENCE_TRIGGERED"));
        desc = findViewById(R.id.textView_desc);
        desc.setMovementMethod(new ScrollingMovementMethod());
        btnNext = findViewById(R.id.button_next);
        btnNext.setVisibility(Button.INVISIBLE);
        btnLeader = findViewById(R.id.btnLeaders);
        btnStart = findViewById(R.id.button_start);
        btnLeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToLeaders();
            }
        });
        imageMap = findViewById(R.id.img_map);
        final LatLng latLng = new LatLng(65.03949, 154.39039);
        Bundle extras = getIntent().getExtras();
        courseName = extras.getString("CourseName");
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);
        imageView = (ImageView) findViewById(R.id.compass);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        chronometer = findViewById(R.id.chronometer);
        start();
        enableUserLocation();
        getLocationUpdates();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference().child("Courses").child(courseName);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    for (DataSnapshot wp : ds.getChildren()) {
                        int id = wp.child("id").getValue(Integer.class);
                        String description = wp.child("description").getValue(String.class);
                        String imgSrc = wp.child("imgSrc").getValue(String.class);
                        double lat = wp.child("coordinates").child("latitude").getValue(Double.class);
                        double lon = wp.child("coordinates").child("longitude").getValue(Double.class);
                        final String courseName = wp.child("courseName").getValue(String.class);
                        String difficulty = (String) wp.child("difficulty").getValue();
                        LatLng latLng = new LatLng(lat, lon);


                        Waypoint waypoint = new Waypoint(id, latLng, description, imgSrc, courseName, difficulty);
                        waypoints.add(waypoint);
                        setInitialDesc(difficulty,courseName,waypoints);

                    }
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 29) {
                    //We need background permission
                    if (ContextCompat.checkSelfPermission(Course.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        setInstructions(waypoints);
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(Course.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                            //We show a dialog and ask for permission
                            ActivityCompat.requestPermissions(Course.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                        } else {
                            ActivityCompat.requestPermissions(Course.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                        }
                    }

                } else {
                    setInstructions(waypoints);
                }
                btnStart.setVisibility(Button.INVISIBLE);
                btnNext.setVisibility(Button.VISIBLE);
                btnLeader.setVisibility(Button.INVISIBLE);
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();


            }
        });
        setMapImg();
    }
    public void setMapImg(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        storageReference.child("MapImages/" + courseName + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                downloadUri = uri;
                generatedFilePath = downloadUri.toString(); /// The string(file link) that you need
                Picasso.get().load(generatedFilePath).into(imageMap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        imageMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(downloadUri);
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                        DownloadManager.Request.NETWORK_MOBILE);

                request.setTitle("File downloading")
                        .setDescription("Download using download manager");

                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"/images/"+"/"+"maps");
                request.setMimeType("*/*");
                downloadManager.enqueue(request);

            }
        });





    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            showNextBtn();
        }
    };

    private void setInitialDesc(String difficulty, String CourseName, ArrayList<Waypoint> wp){
        desc.setText("Course: "+CourseName+"\n Difficulty : "+difficulty + "\n Starting location: "+wp.get(0).getDescription());
    }

    private void getLocationUpdates() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                LatLng latLng;
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Users/"+user.getUid()+"/Current Location/");
                myRef.setValue(latLng);
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

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5,0,locationListener);

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
        getLocationUpdates();
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

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogue = new AlertDialog.Builder(this);
        alertDialogue.setMessage("Are you sure you want to exit? You will automatically be forfeited from this course.")
                .setCancelable(true)
                .setPositiveButton("Exit course", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        AlertDialog alertDialog = alertDialogue.create();
        alertDialog.show();

    }

    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Success :)", Toast.LENGTH_SHORT).show();

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.
                        permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.
                        permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // first bg 2nd course
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
                Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(getApplicationContext(), "Denied", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                Toast.makeText(this, "You can add geofences...", Toast.LENGTH_SHORT).show();
            } else {
                //We do not have the permission..
                Toast.makeText(this, "Background location access is neccessary for geofences to trigger...", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void setInstructions(final ArrayList<Waypoint> wp) {
        hideNextBtn();
        Waypoint first = wp.get(1);
        LatLng firstLatLng = first.getCoordinates();
        addGeofence(firstLatLng,GEOFENCE_RADIUS);
        String firstInst = first.getDescription();
        desc.setText(firstInst);
        String difficulty = first.getDifficulty();


        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (count == wp.size()) {
                    btnNext.setVisibility(View.GONE);
                    btnLeader.setVisibility(View.VISIBLE);
                    double elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
                    DecimalFormat df = new DecimalFormat("#.##");
                    chronometer.stop();
                    Toast.makeText(getApplicationContext(), "Course finished! Your time was: " + df.format(elapsedMillis*0.00001), Toast.LENGTH_SHORT).show();
                    uploadTime(elapsedMillis);
                    PendingIntent pendingIntent = geofenceHelper.getPendingIntent();
                    geofencingClient.removeGeofences(pendingIntent);

                } else {
                    Waypoint nextWaypoint = wp.get(count);
                    LatLng latLng = nextWaypoint.getCoordinates();

                    addGeofence(latLng,GEOFENCE_RADIUS);
                    String next = wp.get(count).getDescription();
                    desc.setText(next);
                    count++;
                }
                hideNextBtn();
            }
        });


    }
    public void moveToLeaders() {

        Intent intent = new Intent(Course.this, LeaderBoard.class);
        intent.putExtra("CourseName", courseName);
        startActivity(intent);
    }

    public void addGeofence(LatLng latLng, float radius) {
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
                        Toast.makeText(getApplicationContext(), "Geofence unavailable", Toast.LENGTH_SHORT).show();

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

        locationManager.removeUpdates(locationListener);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users/"+user.getUid()+"/Current Location/");
        myRef.removeValue();

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
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();
        geofencingClient.removeGeofences(pendingIntent);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DecimalFormat df = new DecimalFormat("#.##");
        final double realTime = time*0.00001;
        DatabaseReference myRef3 = database.getReference("Users/"+user.getUid()+"/Name");
        myRef3.setValue(user.getDisplayName());
        DatabaseReference myRef2 = database.getReference().child("Users").child(user.getUid()).child("Times").child(courseName);
        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String origTime = (String) snapshot.getValue();
                    double originalTime = Double.parseDouble(origTime);
                    if (originalTime > realTime) {
                        final DatabaseReference myRef1 = database.getReference("Users/" + user.getUid() + "/Times/" + courseName);
                        myRef1.setValue(df.format(realTime));

                    }
                } else {
                    final DatabaseReference myRef1 = database.getReference("Users/" + user.getUid() + "/Times/" + courseName);
                    myRef1.setValue(df.format(realTime));
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
    public void hideNextBtn(){
        btnNext.setEnabled(false);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.myProfile:
                AlertDialog.Builder alertDialogue = new AlertDialog.Builder(this);
                alertDialogue.setMessage("Are you sure you want to exit? You will automatically be forfeited from this course.")
                        .setCancelable(true)
                        .setPositiveButton("Exit course", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                AlertDialog alertDialog = alertDialogue.create();
                alertDialog.show();
                startActivity(new Intent(Course.this,ProfileEditor.class));
                return true;
            case R.id.help:
                startActivity(new Intent(Course.this,Help.class));
                return true;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Course.this, LogIn.class));
                return true;
        }
        return false;
    }

}