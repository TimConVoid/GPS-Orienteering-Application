package com.example.myproject1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class UserDash extends AppCompatActivity {

    Button btnEditProfile;
    Button btnBrowseCourses;
    Button btnLogout;
    CardView cardView1,cardView2,cardView3;
    TextView textView;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    TextView userName;
    ImageView imageView;
    StorageReference storageReference;
    DatabaseReference storage;
    String generatedFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dash);

        cardView1 = findViewById(R.id.cardView1);
        cardView2 = findViewById(R.id.cardView2);

        cardView3 = findViewById(R.id.cardView3);

        userName = findViewById(R.id.profileName);

        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if(userEmail.equals("admin@email.com")){
            finish();
            startActivity(new Intent(UserDash.this,AdminDash.class));
        }

        userName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        imageView = findViewById(R.id.userPic);

        FirebaseStorage storage = FirebaseStorage.getInstance();

        storageReference = storage.getReference();

        storageReference.child("profilepics/" + FirebaseAuth.getInstance().getUid() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Uri downloadUri = uri;
                generatedFilePath = downloadUri.toString(); /// The string(file link) that you need
                Picasso.get().load(generatedFilePath).into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });








        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToProfile();
            }
        });

        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToCoursePicker();
            }
        });

        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToLeaderBoards();
            }
        });



    }

    private void moveToProfile() {
        Intent intent = new Intent(UserDash.this, ProfileEditor.class);
        startActivity(intent);

    }
    private void moveToCoursePicker() {
        Intent intent = new Intent(UserDash.this, CoursePicker.class);
        intent.putExtra("Courses", "courses");
        startActivity(intent);
    }

    public void moveToLeaderBoards(){
        Intent intent = new Intent(UserDash.this, CoursePicker.class);
        intent.putExtra("Leaders", "leaders");
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
            case R.id.myProfile:

                return true;
            case R.id.help:
                startActivity(new Intent(UserDash.this,Help.class));
                return true;
            case R.id.logout:
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Users/"+FirebaseAuth.getInstance().getUid()+"/Current Location/");
                myRef.removeValue();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(UserDash.this, LogIn.class));
                return true;
        }
        return false;
    }

}
