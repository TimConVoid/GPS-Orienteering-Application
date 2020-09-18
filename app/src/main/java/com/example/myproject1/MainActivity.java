package com.example.myproject1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    Button btnLogin;
    Button btnReg;
    Button btnCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        btnLogin = findViewById(R.id.btn_login);
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            btnLogin.setText("My Profile");
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(FirebaseAuth.getInstance().getCurrentUser() == null) {
                    moveToLogin();
                } else {
                    moveToProfile();
                }
            }
        });




        btnReg = findViewById(R.id.btn_reg);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                moveToRegister();
            }
        });

        btnCourse = findViewById(R.id.btn_browse);

        btnCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                moveToCoursePicker();
            }
        });
    }
    public void moveToLogin(){
        Intent intent = new Intent(MainActivity.this, LogIn.class);
        startActivity(intent);
    }

    public void moveToRegister(){
        Intent intent = new Intent(MainActivity.this, Register.class);
        startActivity(intent);
    }

    public void moveToCoursePicker(){
        Intent intent = new Intent(MainActivity.this, CoursePicker .class);
        startActivity(intent);
    }
    public void moveToProfile(){
        Intent intent = new Intent(MainActivity.this, UserDash.class);
        startActivity(intent);
    }


}
