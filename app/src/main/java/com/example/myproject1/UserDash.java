package com.example.myproject1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserDash extends AppCompatActivity {

    Button btnEditProfile;
    Button btnBrowseCourses;
    Button btnLogout;
    TextView textView;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dash);

        btnEditProfile = findViewById(R.id.btn_edit);
        btnBrowseCourses = findViewById(R.id.btn_browse);
        textView = findViewById(R.id.textView2);
        btnLogout = findViewById(R.id.btn_logout);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

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
    private void logOut(){

        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(UserDash.this, MainActivity.class);
        startActivity(intent);
    }


}
