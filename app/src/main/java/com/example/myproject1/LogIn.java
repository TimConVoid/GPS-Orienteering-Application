package com.example.myproject1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseUser;

public class LogIn extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    EditText  txtPass, txtEmail;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        txtEmail = findViewById(R.id.edit_email);
        txtPass = findViewById(R.id.edit_password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        findViewById(R.id.btn_login).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();


    }

    private void userLogin() {

        final String email = txtEmail.getText().toString().trim();
        final String pass = txtPass.getText().toString().trim();


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

        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    Intent intent = new Intent(LogIn.this, UserDash.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else if (email.equals("admin@email.com") && pass.equals("password")) {
                    Intent intent = new Intent(LogIn.this, AdminDash.class);
                    startActivity(intent);

                }else{
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_login:
                userLogin();
                break;
        }
    }
}
