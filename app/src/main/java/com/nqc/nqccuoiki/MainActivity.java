package com.nqc.nqccuoiki;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nqc.nqccuoiki.fragment.ViewPagerAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{
    private ConstraintLayout audioBottom;
    private ArrayList<Song> onlsong;
    private ArrayList<Song> favsong;
    private BottomNavigationView bottomNavigationView;
    private ViewPager viewPager;
    private ImageView imgpause, imgpre, imgnext, imgsong;
    private TextView txtSinger, txtSong;
    private Song song;
    private int position;
    private ImageView profile;
    private EditText txtSearch;
    private Database db;
    private Context context;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String action = bundle.getString("action");
            switch (action){
                case MusicConstants.ACTION.PREPARED_ACTION:
                    song.setDuration(bundle.getInt("duration"));
//                    initTime(bundle.getInt("duration"));
                    break;
                case MusicConstants.ACTION.UPDATE_TIME_ACTION:
                    position = bundle.getInt("position");
                    break;
                case MusicConstants.ACTION.PAUSE_ACTION:
                    imgpause.setImageResource(R.drawable.outline_play_circle_outline_white_48);
                    break;
                case MusicConstants.ACTION.PLAY_ACTION:
                    imgpause.setImageResource(R.drawable.outline_pause_circle_outline_white_48);
                    break;
                case MusicConstants.ACTION.ERROR_ACTION:
                    Toast toast = Toast.makeText(context, "error", Toast.LENGTH_LONG);
                    toast.show();
                default:
                    song = (Song)bundle.getSerializable("song");
                    init(song);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new Database();
        context = this;
        Anhxa();

        event();
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

    }
    @Override
    protected void onResume() {
        super.onResume();
        if (isMyServiceRunning(MediaService.class)){
            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("receive data"));
            init(MediaService.getSong());
            audioBottom.setVisibility(View.VISIBLE);
        }
        else audioBottom.setVisibility(View.GONE);
    }

    private void event() {

        initUI();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home_nav_bottom:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.list_nav_bottom:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.more_nav_bottom:
                        viewPager.setCurrentItem(2);
                        break;
                }
                return true;
            }
        });

        Intent intentAudio = new Intent(this, AudioActivity.class);
        imgsong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentAudio.putExtra("song", MediaService.getSong());
                startActivity(intentAudio);

            }
        });
        imgnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAction(MusicConstants.ACTION.NEXT_ACTION);
            }
        });
        imgpre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAction(MusicConstants.ACTION.PRE_ACTION);
            }
        });
        imgpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MediaService.getServiceState() == MusicConstants.STATE_SERVICE.PLAY)
                    sendAction(MusicConstants.ACTION.PAUSE_ACTION);
                else
                    sendAction(MusicConstants.ACTION.PLAY_ACTION);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(db.getUser() == null)
                    startActivity(new Intent(context, LoginActivity.class));
                else
                    startActivity(new Intent(context, ProfileActivity.class));
            }
        });

        txtSearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, LoginActivity.class));
            }
        });
    }

    private void initUI() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, onlsong, favsong);
        viewPager.setAdapter(viewPagerAdapter);

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public void init(Song s){
        if(MediaService.getServiceState()==MusicConstants.STATE_SERVICE.PAUSE)
            imgpause.setImageResource(R.drawable.outline_play_circle_outline_white_48);
        else imgpause.setImageResource(R.drawable.outline_pause_circle_outline_white_48);
        Picasso.get().load(s.getImage()).into(imgsong);
        txtSong.setText(s.getSong());
        txtSinger.setText(s.getSinger());
    }

    private void Anhxa() {
        Log.d("TAG", "Anhxa: init ");
        bottomNavigationView = findViewById(R.id.bottom_nav);
        audioBottom = findViewById(R.id.audio_bottom);
        imgsong = findViewById(R.id.imageSong);
        imgpause = findViewById(R.id.imagePause);
        imgnext = findViewById(R.id.imageNext);
        imgpre = findViewById(R.id.imagePre);
        txtSinger = findViewById(R.id.textViewSinger);
        txtSong = findViewById(R.id.textViewSong);
        profile = findViewById(R.id.profile);
        txtSearch = findViewById(R.id.txtSearch);
        viewPager = findViewById(R.id.view_pager);

        Intent intent = getIntent();
        onlsong = (ArrayList<Song>) intent.getSerializableExtra("onlsong");
        favsong = (ArrayList<Song>) intent.getSerializableExtra("favsong");
    }
    public void sendAction(String action){
        Intent intent = new Intent(this, MediaService.class);
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }


}