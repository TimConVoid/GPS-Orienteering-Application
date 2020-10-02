package com.example.myproject1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class ProfileEditor extends AppCompatActivity {

    private static final int CHOOSE_IMAGE = 100;
    ImageView imageView;
    EditText editText;
    Uri uriProfileImg;
    String profileImgURL;
    Button btnSave;
    FirebaseAuth mAuth;
    ArrayList<UserTimes> myUserTimes = new ArrayList<>();
    ArrayList<String> userTimes = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    String courseName;
    FirebaseDatabase database;
    DatabaseReference reference;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imageView = findViewById(R.id.img_profile);
        editText = findViewById(R.id.txt_name);
        btnSave = findViewById(R.id.btn_save);
        mAuth = FirebaseAuth.getInstance();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showImageChooser();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInfo();
            }
        });
        database = FirebaseDatabase.getInstance();
        listView = findViewById(R.id.listView2);
        reference = database.getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("Times");


        arrayAdapter = new ArrayAdapter<String>(this, R.layout.user_info, R.id.user_info, userTimes);


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot sc : snapshot.getChildren()) {

                    if (sc.exists()) {
                        String name = sc.getKey();
                        double dubTime = (Double) sc.getValue();

                        String time = Double.toString(dubTime);
                        UserTimes userTime = new UserTimes(name, "Time", time);
                        myUserTimes.add(userTime);


                    }
                }
                Collections.sort(myUserTimes);
                UserTimesAdapter userTimesAdapter = new UserTimesAdapter(ProfileEditor.this,R.layout.adapter_viewl_layout,myUserTimes);
                listView.setAdapter(userTimesAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void saveUserInfo() {

        String displayName = editText.getText().toString();

        if(displayName.isEmpty()){
            editText.setError("Name Required");
            editText.requestFocus();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();

        if(user!=null && profileImgURL != null){
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .setPhotoUri(Uri.parse(profileImgURL))
                    .build();

            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ProfileEditor.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        startActivity(new Intent(ProfileEditor.this, UserDash.class));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){

            uriProfileImg = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImg);
                imageView.setImageBitmap(bitmap);
                uploadImageToFirebase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebase() {
        final StorageReference profileImgRef = FirebaseStorage.getInstance().getReference("profilepics/" + FirebaseAuth.getInstance().getUid() + ".jpg");

        if (uriProfileImg != null){
            profileImgRef.putFile(uriProfileImg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    profileImgURL = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();

                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileEditor.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }

    }

    private void showImageChooser(){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Map"), CHOOSE_IMAGE);
    }
}
