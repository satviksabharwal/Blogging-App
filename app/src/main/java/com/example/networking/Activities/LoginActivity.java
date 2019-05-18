package com.example.networking.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.networking.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private AppCompatEditText emailLoginEditText,passwordLoginEditText;
    private AppCompatButton loginButton,registrationActivityButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private String email,password;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailLoginEditText = findViewById(R.id.emailLoginEditText);
        passwordLoginEditText = findViewById(R.id.passwordLoginEditText);
        progressBar = findViewById(R.id.progressBar);
        loginButton = findViewById(R.id.loginButton);
        registrationActivityButton = findViewById(R.id.registrationActivityButton);

        loginButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();

        intent = new Intent(getApplicationContext(),HomeActivity.class);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = emailLoginEditText.getText().toString();
                password = passwordLoginEditText.getText().toString();
                loginButton.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);


                if(email.isEmpty() || password.isEmpty()){

                    Toast.makeText(LoginActivity.this, "Please Fill the details", Toast.LENGTH_SHORT).show();
                    loginButton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }else{
                    logIn(email,password);

                }

            }
        });

        registrationActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRegistration = new Intent(getApplicationContext(),RegistrationActivity.class);
                startActivity(intentRegistration);
                finish();
            }
        });

    }

    private void logIn(String email, String password) {

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    loginButton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    updateUI();
                }else{
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    loginButton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }

            }
        });

    }

    private void updateUI() {
        startActivity(intent);
        finish();

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if(user!=null){
            updateUI();
        }

    }
}
