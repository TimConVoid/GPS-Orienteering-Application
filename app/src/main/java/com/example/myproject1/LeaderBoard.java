package com.example.myproject1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class LeaderBoard extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private ListView listView;
    FirebaseDatabase database;
    DatabaseReference reference;
    ArrayList<String> userTimes = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    String courseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);


        database = FirebaseDatabase.getInstance();
        listView = findViewById(R.id.listView);
        reference = database.getReference("Users");

        Bundle extras = getIntent().getExtras();

        courseName = extras.getString("CourseName");


        arrayAdapter = new ArrayAdapter<String>(this,R.layout.user_info,R.id.user_info,userTimes);


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot sc : snapshot.getChildren()){
                    String name = (String) sc.child("Name").getValue();
                    double time = (double) sc.child("Times").child(courseName).getValue();
                    UserTimes userTime= new UserTimes(name,courseName,time);
                    userTimes.add(userTime.getTime()+ ":        " +userTime.getName());

                }
                Collections.sort(userTimes);
                listView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void populateList(ArrayList<UserTimes> userTimes){

    }
}