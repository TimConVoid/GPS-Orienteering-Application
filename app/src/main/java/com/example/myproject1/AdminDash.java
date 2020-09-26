package com.example.myproject1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AdminDash extends AppCompatActivity {

    Button btnSet,btnLeader,btnTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dash);

        btnSet = findViewById(R.id.btn_set);
        btnLeader = findViewById(R.id.btn_coursetimes);
        btnTracker = findViewById(R.id.btn_usertracker);

        btnTracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToParticipantTracker();
            }
        });

        btnLeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToCoursePicker();
            }
        });
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToMaps();
            }
        });
    }

    private void moveToMaps() {
        Intent intent = new Intent(AdminDash.this, CourseCreator.class);
        startActivity(intent);
    }
    private  void moveToCoursePicker(){
        Intent intent = new Intent(AdminDash.this, CoursePicker.class);
        startActivity(intent);
    }
    private void moveToParticipantTracker(){
        Intent intent = new Intent(AdminDash.this, ParticipantTracker.class);
        startActivity(intent);
    }
}
