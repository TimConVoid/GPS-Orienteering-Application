package com.example.myproject1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class AdminDash extends AppCompatActivity {

    CardView cardView,cardView2,cardView3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dash);

        cardView = findViewById(R.id.cardViewadmin1);
        cardView2 = findViewById(R.id.cardViewadmin2);
        cardView3 = findViewById(R.id.cardViewadmin3);

        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToParticipantTracker();
            }
        });

        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToCoursePicker();
            }
        });
        cardView.setOnClickListener(new View.OnClickListener() {
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
        intent.putExtra("Leaders", "leader");
        startActivity(intent);
    }
    private void moveToParticipantTracker(){
        Intent intent = new Intent(AdminDash.this, ParticipantTracker.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.help:
                startActivity(new Intent(AdminDash.this,Help.class));
                return true;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(AdminDash.this, LogIn.class));
                return true;
        }
        return false;
    }
}
