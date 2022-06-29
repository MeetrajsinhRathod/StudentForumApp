package com.example.studentforum;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.internal.TaskUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class RegistrationActivity extends AppCompatActivity {
    private CircleImageView profileImage;
    private EditText username, fullname, email, password;
    private Button registrationButton;
    private TextView question;
    private CheckBox rememberMe;

    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private ProgressDialog loader;              //for loading screen
    private String onlineUserID = "";
    private Uri resultUri;

    public void gotoLoginpage(View view){                                   //A OnClick function which takes user to the login page if he already has an account
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        profileImage = findViewById(R.id.profileImage);
        username = findViewById(R.id.username);
        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.regEmail);
        password = findViewById(R.id.regPassword);
        registrationButton = findViewById(R.id.Registerbtn);
        question = findViewById(R.id.regPageQuestion);
        rememberMe = findViewById(R.id.rememberMe);

        Paper.init(this);                             // Paper is a framework which stores data to users device

        mAuth = FirebaseAuth.getInstance();
        loader = new ProgressDialog(this);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {               // When user clicks on the profile image it allows user to select an image as a profile picture from gallery
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                       //If all details are non empty then this function will create an account for the user
                final String userName = username.getText().toString();
                final String fullName = fullname.getText().toString();
                final String emailText = email.getText().toString();
                String passwordText = password.getText().toString();

                if(TextUtils.isEmpty(userName)){
                    username.setError("Username is required");
                }
                if(TextUtils.isEmpty(fullName)){
                    fullname.setError("Full name is required");
                }
                if(TextUtils.isEmpty(emailText)){
                    email.setError("Email is required");
                }
                if(TextUtils.isEmpty(passwordText)){
                    password.setError("Password is required");
                }
                if(resultUri == null){
                    Toast.makeText(RegistrationActivity.this, "Profile image is required", Toast.LENGTH_SHORT).show();
                }
                else {
                    loader.setMessage("Registration is in progress");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    if(rememberMe.isChecked()){
                        Paper.book().write(Prevalent.userEmail, emailText);
                        Paper.book().write(Prevalent.userPass, passwordText);
                    }

                    mAuth.createUserWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {            // A function which allows user to sign in to the app
                            if(task.isSuccessful()){
                                onlineUserID = mAuth.getCurrentUser().getUid();
                                reference = FirebaseDatabase.getInstance().getReference().child("users").child(onlineUserID);
                                Map hashMap = new HashMap();
                                hashMap.put("username", userName);
                                hashMap.put("fullname", fullName);
                                hashMap.put("id", onlineUserID);
                                hashMap.put("email", emailText);
                                reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(RegistrationActivity.this, "Details set Successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(RegistrationActivity.this, "Failed to upload data" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                        finish();
                                        loader.dismiss();
                                    }
                                });

                                //To store users profile image in database

                                final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profile images").child(onlineUserID);
                                Bitmap bitmap =null;
                                try{
                                    bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), resultUri);
                                }catch(IOException e){
                                    e.printStackTrace();
                                }
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 20,byteArrayOutputStream);
                                byte[] data = byteArrayOutputStream.toByteArray();
                                UploadTask uploadTask = filepath.putBytes(data);

                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        if(taskSnapshot.getMetadata().getReference() !=null){
                                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String imageUrl = uri.toString();
                                                    Map hashMap = new HashMap();
                                                    hashMap.put("profileimageurl", imageUrl);
                                                    reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                                        @Override
                                                        public void onComplete(@NonNull Task task) {
                                                            if(task.isSuccessful()){
                                                                Toast.makeText(RegistrationActivity.this, "Profile image added successfully", Toast.LENGTH_SHORT).show();
                                                            }else {
                                                                Toast.makeText(RegistrationActivity.this, "Proccess failed"+task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                    finish();
                                                }
                                            });
                                        }
                                    }
                                });

                                Intent intent = new Intent(RegistrationActivity.this, homeActivity.class);        //Intent to take user directly to the home page if sign in is successfully done
                                startActivity(intent);
                                finish();
                                loader.dismiss();
                            }else {
                                Toast.makeText(RegistrationActivity.this, "Registration failed" +task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data !=null){
            resultUri = data.getData();
            profileImage.setImageURI(resultUri);
        }else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }
}