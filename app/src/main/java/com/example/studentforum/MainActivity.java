package com.example.studentforum;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import io.paperdb.Paper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private static final int SPLASH = 2500;     // time in mili seconds for animation
    Animation topAnimation, bottomAnimation;   //To get animation of logo on SLASH screen
    ImageView logo;
    TextView  tagline;

    private FirebaseAuth mAuth;               // Firebaseauth variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        logo = findViewById(R.id.logo);
        tagline = findViewById(R.id.tagline);

        mAuth = FirebaseAuth.getInstance();

        Paper.init(this);       // Paper is a framework which stores data to users device

        logo.setAnimation(topAnimation);
        tagline.setAnimation(bottomAnimation);

        // If user has already logged in and selected remember me option then to get his stored data from prevelant class ..

        String UserEmail = Paper.book().read(Prevalent.userEmail);          //string which stores users email
        String UserPass = Paper.book().read(Prevalent.userPass);            //string which stores users password

        //If log in details of user has been stored then it (Email and password) must not be empty
            if(!TextUtils.isEmpty(UserEmail) && !TextUtils.isEmpty(UserPass)){

                mAuth.signInWithEmailAndPassword(UserEmail, UserPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {            // A function which allows user to sign in to the app
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Logged in as : "+mAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, homeActivity.class);          //Intent to take user directly to the home page if sign in is successfully done
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(MainActivity.this, "Login failed. Please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Intent to take user from SPLASH screen to the login screen if there is no log in details found of the user
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            },SPLASH);
        }
    }
}