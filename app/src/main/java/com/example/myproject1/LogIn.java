package com.example.myproject1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.security.auth.login.LoginException;

public class LogIn extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    EditText  txtPass, txtEmail;
    TextView textView;
    ProgressBar progressBar;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        txtEmail = findViewById(R.id.edit_email);
        txtPass = findViewById(R.id.edit_password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        textView = findViewById(R.id.register);
        findViewById(R.id.btn_login).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser != null){
            finish();
            Intent intent = new Intent(LogIn.this, UserDash.class);
            startActivity(intent);
        }

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToRegister();
            }
        });




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
                if (email.equals("admin@email.com") && pass.equals("password")) {
                    Intent intent = new Intent(LogIn.this, AdminDash.class);
                    startActivity(intent);
                }else if(task.isSuccessful()){
                    Intent intent = new Intent(LogIn.this, UserDash.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
    public void moveToRegister(){
        startActivity(new Intent(LogIn.this,Register.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            getMenuInflater().inflate(R.menu.profile, menu);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.myProfile:
                startActivity(new Intent(LogIn.this,ProfileEditor.class));
                return true;

            case R.id.help:
                startActivity(new Intent(LogIn.this,Help.class));
                return true;
            case R.id.register:
                startActivity(new Intent(LogIn.this,Register.class));
                return true;


        }


        return false;
    }
}
