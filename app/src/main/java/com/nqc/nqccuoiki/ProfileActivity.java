package com.nqc.nqccuoiki;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {
    private Database db;
    private NavigationView navigationView;
    private ImageView imgBack;
    private ConstraintLayout da_login;
    private FirebaseAuth firebaseAuth;
    GoogleSignInClient mGoogleSignInClient;
    private TextView txtuname;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        db = new Database();
        firebaseAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        init();

    }

    private void logout() {
        firebaseAuth.signOut();
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                });
    }

    private void init(){
        navigationView = findViewById(R.id.nav_profile);
        imgBack = findViewById(R.id.imgBack);
        da_login = findViewById(R.id.da_login);
        txtuname = findViewById(R.id.txtUserName);
        txtuname.setText(db.getUser().getDisplayName());
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.logout:
                        logout();
                        break;
                    case R.id.add_music:
                        startActivity(new Intent(ProfileActivity.this,AddMusicActivity.class));
                        break;
                }
                return true;
            }
        });
    }

}