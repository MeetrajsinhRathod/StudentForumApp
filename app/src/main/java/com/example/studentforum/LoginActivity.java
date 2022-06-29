package com.example.studentforum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    private TextView question;
    private EditText emailEd, passworded ;
    private Button login;
    private CheckBox rememberMe;

    private ProgressDialog loader;              //for loading screen
    private FirebaseAuth mAuth;

    public void gotoRegistrationpage(View view){                                //A OnClick function which takes user to the registration page if he has not made an account before
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        question = findViewById(R.id.loginPageQuestion);
        emailEd = findViewById(R.id.loginEmail);
        passworded = findViewById(R.id.loginPassword);
        login = findViewById(R.id.loginBtn);
        rememberMe = findViewById(R.id.rememberMe);        //A Remember me check box so that app saves the users login details into the device

        Paper.init(this);                          // Paper is a framework which stores data to users device

        loader = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                   //A OnClick function which takes email id and password from user
                String email = emailEd.getText().toString();
                String password = passworded.getText().toString();

                // Email id and password must not be empty

                if(TextUtils.isEmpty(email)){
                    emailEd.setError("Email is required");
                }
                if(TextUtils.isEmpty(password)){
                    passworded.setError("Password is required");
                }else{
                    loader.setMessage("Login in progress");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    if(rememberMe.isChecked()){                                      // Checks whether the user has ticked on the remember me checkbox or not

                        Paper.book().write(Prevalent.userEmail, email);             // It saves users login details on Prevalent class
                        Paper.book().write(Prevalent.userPass, password);
                    }

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {            // A function which allows user to sign in to the app
                            if(task.isSuccessful()){
                                Toast.makeText(LoginActivity.this, "Login is successful. Loged in as: "+mAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, homeActivity.class);         //Intent to take user directly to the home page if sign in is successfully done
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(LoginActivity.this, "Login failed "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}