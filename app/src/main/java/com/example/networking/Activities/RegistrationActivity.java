package com.example.networking.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.networking.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationActivity extends AppCompatActivity {

    RelativeLayout relativeLayout;
    AppCompatEditText nameEditText,emailEditText,passwordEditText;
    AppCompatButton signUpButton,logInActivityButton;
    CircleImageView profilePicture;
    ProgressBar progressBar;


    Uri imageUri;

    private static int reqCode = 1,requesCode = 1;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        profilePicture = findViewById(R.id.profilePicture);
        relativeLayout = findViewById(R.id.relativeLayout);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        progressBar = findViewById(R.id.progressBar);

        logInActivityButton = findViewById(R.id.logInActivityButton);
        signUpButton = findViewById(R.id.signupButton);

        progressBar.setVisibility(View.INVISIBLE);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signUpButton.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                String name = nameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if(name.isEmpty() || email.isEmpty() || password.isEmpty()){
                    Toast.makeText(RegistrationActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                    signUpButton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);

                }else{

                    createUserAccount(name,email,password);
                }

            }
        });

        logInActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLogIn = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intentLogIn);
                finish();
            }
        });

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT>=22){

                    checkAndRequestForPermission();
                }else{

                    openGallery();
                }

            }
        });

        //
        mAuth = FirebaseAuth.getInstance();


    }

    private void createUserAccount(final String name, String email, String password) {

        if(imageUri!=null) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                //user created successfully
                                // Toast.makeText(RegistrationActivity.this, "User Created Successful", Toast.LENGTH_SHORT).show();
                                //update his profile picture, name, phone number

                                updateInfo(imageUri, name, mAuth.getCurrentUser());


                            } else {

                                Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                                signUpButton.setVisibility(View.VISIBLE);

                            }

                        }
                    });

        }else{
            Toast.makeText(this, "Please Click on Image and Select Image From Gallery", Toast.LENGTH_SHORT).show();
            signUpButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void updateInfo(final Uri imageUri, final String name,final FirebaseUser currentUser) {

        // Create a storage reference from our app


            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("users_photo");
            final StorageReference imageFilePath = storageRef.child(imageUri.getLastPathSegment());
            imageFilePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {


                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .setPhotoUri(uri)
                                    .build();

                            currentUser.updateProfile(profileUpdate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(RegistrationActivity.this, "Sign Up Process Completed!!", Toast.LENGTH_SHORT).show();
                                            updateUI();
                                        }
                                    });

                        }
                    });

                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RegistrationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    private void updateUI() {

        Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
        startActivity(intent);
        finish();

    }


    private void openGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,requesCode);


    }

    private void checkAndRequestForPermission() {

        if(ContextCompat.checkSelfPermission(RegistrationActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(RegistrationActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(this, "Please Accept for Request Permission", Toast.LENGTH_SHORT).show();
            }else{

                ActivityCompat.requestPermissions(RegistrationActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, reqCode);
            }

        }else{

            openGallery();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == requesCode && resultCode == RESULT_OK && data != null){

            imageUri = data.getData();
            profilePicture.setImageURI(imageUri);

        }


    }
}
