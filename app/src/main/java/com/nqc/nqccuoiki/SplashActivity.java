package com.nqc.nqccuoiki;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {
    private ArrayList<Song> onlsong;
    private ArrayList<Song> favsong;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Database db = new Database();
        onlsong = new ArrayList<>();
        favsong = new ArrayList<>();onlsong = db.getOnlineSongs();
        if(db.getUser()!=null)
            favsong = db.getFavouriteSongs();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                intent.putExtra("onlsong",onlsong);
                intent.putExtra("favsong",favsong);
                startActivity(intent);
                finish();
            }
        },3000);
    }

}