package com.example.myproject1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Register extends AppCompatActivity implements View.OnClickListener{

    ProgressBar progressBar;
    EditText txtUser, txtPass, txtEmail, txtName;
    private FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);


        txtEmail = findViewById(R.id.txt_email);
        txtPass = findViewById(R.id.txt_pass);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        txtName = findViewById(R.id.txt_fullname);


        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.btn_register).setOnClickListener(this);
    }



    private void registerUser() {
       // String userName = txtUser.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();
        String pass = txtPass.getText().toString().trim();
        String name = txtName.getText().toString().trim();
      //  String post = txtPost.getText().toString().trim();
        final UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name).build();



        if (email.isEmpty()){
            txtEmail.setError("Email is required");
            txtEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            txtEmail.setError("Please enter a valid email");
            txtEmail.requestFocus();
            return;
        }
        if (pass.isEmpty()){
            txtPass.setError("Password is required");
            txtPass.requestFocus();
            return;
        }
        if(pass.length() < 6){
            txtPass.setError("Password must be greater than 6 characters");
            txtPass.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);



        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);


                if (task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    user.updateProfile(profileUpdates);
                    Toast.makeText(getApplicationContext(), "User registered successfully", Toast.LENGTH_SHORT).show();

                } else {
                    if(task.getException() instanceof FirebaseAuthUserCollisionException){
                        Toast.makeText(getApplicationContext(), "User already registered", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_register:
                registerUser();
                break;



        }

    }
}
