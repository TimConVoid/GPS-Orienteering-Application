package com.example.myproject1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class UserDash extends AppCompatActivity {

    Button btnEditProfile;
    Button btnBrowseCourses;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dash);

        btnEditProfile = findViewById(R.id.btn_edit);
        btnBrowseCourses = findViewById(R.id.btn_browse);
        textView = findViewById(R.id.textView2);


        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToProfile();
            }
        });

        btnBrowseCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToCoursePicker();
            }
        });


    }

    private void moveToProfile() {
        Intent intent = new Intent(UserDash.this, ProfileActivity.class);
        startActivity(intent);

    }
    private void moveToCoursePicker() {
        Intent intent = new Intent(UserDash.this, CoursePicker.class);
        startActivity(intent);
    }


}
