package com.example.studentforum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class homeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer_layout;
    private Toolbar toolbar;
    public TextView E;
    public TextView N;
    private FirebaseUser mUser ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);


        mUser = FirebaseAuth.getInstance().getCurrentUser();
        System.out.println("Logged in  User   : -" + mUser.getUid());


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.inflateHeaderView(R.layout.activity_navheader);

        /*String s=FirebaseAuth.getInstance().getCurrentUser().getEmail();

        System.out.println("Value of s : " + s);
        System.out.println("Value of navigationView : " + navigationView);

        email = navigationView.findViewById(R.id.nav_header_email);*/
        //email.setText(s);

        drawer_layout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Student Forum");



        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer_layout, toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close );

        drawer_layout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_myquestions :
                Intent intent = new Intent(this, myQuestions.class);
                startActivity(intent);
                break;

        }
        drawer_layout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

}