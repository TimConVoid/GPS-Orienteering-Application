package com.example.myproject1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AdminDash extends AppCompatActivity {

    Button btnSet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dash);

        btnSet = findViewById(R.id.btn_set);

        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToMaps();
            }
        });
    }

    private void moveToMaps() {
        Intent intent = new Intent(AdminDash.this, MapsActivity.class);
        startActivity(intent);
    }
}
