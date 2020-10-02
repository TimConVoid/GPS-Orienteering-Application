package com.example.myproject1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.Provider;
import java.util.ArrayList;
import java.util.Collections;

public class LeaderBoard extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private ListView listView;
    FirebaseDatabase database;
    DatabaseReference reference;
    ArrayList<String> userTimes = new ArrayList<>();
    ArrayList<UserTimes> myUserTimes = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    String courseName;
    int count = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);
        database = FirebaseDatabase.getInstance();
        listView = findViewById(R.id.listView);
        reference = database.getReference("Users");

        Bundle extras = getIntent().getExtras();


        courseName = extras.getString("CourseName");


        arrayAdapter = new ArrayAdapter<String>(this, R.layout.user_info, R.id.user_info, userTimes);


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (count == 1){
                for (DataSnapshot sc : snapshot.getChildren()) {
                    DataSnapshot times = sc.child("Times").child(courseName);
                    if (times.exists()) {
                        String name = (String) sc.child("Name").getValue();
                        double dubTime = (Double) sc.child("Times").child(courseName).getValue();

                        String time = Double.toString(dubTime);
                        UserTimes userTime = new UserTimes(name, "Time", time + " minutes");

                        myUserTimes.add(userTime);
                        populateList();

                    }

                    }
                }
                count++;


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        // listView.setAdapter(arrayAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (FirebaseAuth.getInstance().getCurrentUser().getEmail().equals("admin@email.com")) {

            return false;
        } else {
            getMenuInflater().inflate(R.menu.profile, menu);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (!FirebaseAuth.getInstance().getCurrentUser().getEmail().equals("admin@email.com")) {
            switch (item.getItemId()) {
                case R.id.myProfile:
                    startActivity(new Intent(LeaderBoard.this, UserDash.class));
                    return true;
                case R.id.help:
                    startActivity(new Intent(LeaderBoard.this, Help.class));
                    return true;
                case R.id.logout:
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("Users/"+FirebaseAuth.getInstance().getUid()+"/Current Location/");
                    myRef.removeValue();
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(LeaderBoard.this, LogIn.class));
                    return true;
            }
            return false;

        }
        return false;


    }
    public void populateList(){
        Collections.sort(myUserTimes);
        UserTimesAdapter userTimesAdapter = new UserTimesAdapter(LeaderBoard.this,R.layout.adapter_viewl_layout,myUserTimes);
        listView.setAdapter(userTimesAdapter);
    }




}