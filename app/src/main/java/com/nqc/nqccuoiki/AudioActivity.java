package com.nqc.nqccuoiki;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;


public class  AudioActivity extends AppCompatActivity {
    ImageView imgview ,imgpre, imgnext,imgpause, imgloop, imgfav, imgshuff, imgback, imgclock;
    TextView txtviewname, txtvct, txtvet;
    SeekBar skbar;
    Song song;
    private final static String TAG = AudioActivity.class.getSimpleName();
    Database db;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String action = bundle.getString("action");
            switch (action){
                case MusicConstants.ACTION.PREPARED_ACTION:
                    initTime(bundle.getInt("duration"));
                    break;
                case MusicConstants.ACTION.UPDATE_TIME_ACTION:
                    Log.d(TAG, "onReceive: updatetime");
                    updateTime(bundle.getInt("position"));
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
                    break;
                default:
                    Song s = (Song)bundle.getSerializable("song");
                    init(s);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        imgview = findViewById(R.id.image);
        txtviewname = findViewById(R.id.name_song);
        imgnext = findViewById(R.id.nexr_audio);
        imgpause = findViewById(R.id.pause_audio);
        imgpre = findViewById(R.id.back_audio);
        txtvct = findViewById(R.id.timecurrent);
        txtvet = findViewById(R.id.timeend);
        skbar = findViewById(R.id.seekbar_audio);
        imgloop = findViewById(R.id.repeat_audio);
        imgfav = findViewById(R.id.favourite);
        imgshuff = findViewById(R.id.shuffle);
        imgback = findViewById(R.id.imageback);
        imgclock = findViewById(R.id.timer_audio);
        Intent intent = getIntent();
        db = new Database();
        song = (Song)intent.getSerializableExtra("song");
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("receive data"));
        if(savedInstanceState != null){
            initTime(savedInstanceState.getInt("duration"));
            updateTime(savedInstanceState.getInt("position"));
        }
        init(song);
        imgloop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MediaService.getMediaState() != MusicConstants.STATE_MEDIA.LOOP){
                    imgloop.setImageResource(R.drawable.outline_repeat_one_white_48);
                    imgshuff.clearColorFilter();
                }
                else{
                    imgloop.setImageResource(R.drawable.outline_repeat_white_48);
                }
                sendAction(MusicConstants.ACTION.LOOP_ACTION);
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
        imgshuff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MediaService.getMediaState() == MusicConstants.STATE_MEDIA.SHUFFLE){
                    imgshuff.clearColorFilter();
                }
                else{
                    imgshuff.setColorFilter(R.color.black);
                    imgloop.setImageResource(R.drawable.outline_repeat_white_48);
                }
                sendAction(MusicConstants.ACTION.SHUFFLE_ACTION);

            }
        });
        imgclock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAction(MusicConstants.ACTION.CLOCK_ACTION);
            }
        });
        skbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sendAction(MusicConstants.ACTION.FORWARD_ACTION);
                imgpause.setImageResource(R.drawable.outline_pause_circle_outline_white_48);
            }
        });
        imgfav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (song.getFav()){
                    imgfav.clearColorFilter();
                    song.setfav(false);
                    db.favourite(song.getKey(),"remove");
                }
                else{
                    imgfav.setColorFilter(R.color.rose);
                    song.setfav(true);
                    db.favourite(song.getKey(),"add");
                }
            }
        });
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void init(Song s){
        if ((s.getFav())) {
            imgfav.setColorFilter(R.color.rose);
        } else {
            imgfav.clearColorFilter();
        }
        if (MediaService.getMediaState() == MusicConstants.STATE_MEDIA.LOOP){
            imgloop.setImageResource(R.drawable.outline_repeat_one_white_48);
            imgshuff.clearColorFilter();
        }
        else if (MediaService.getMediaState() == MusicConstants.STATE_MEDIA.SHUFFLE){
            imgshuff.setColorFilter(R.color.black);
            imgloop.setImageResource(R.drawable.outline_repeat_white_48);
        }
        else {
            imgshuff.clearColorFilter();
            imgloop.setImageResource(R.drawable.outline_repeat_white_48);
        }
        imgpause.setImageResource(R.drawable.outline_pause_circle_outline_white_48);
        Picasso.get().load(s.getImage()).into(imgview);
        txtviewname.setText(s.getSong());
        txtvct.setText("00:00");
        txtvet.setText("00:00");
    }
    public void initTime(int duration){
        txtvet.setText(formatTime(duration));
        skbar.setMax(duration);
        skbar.setProgress(0);
    }
    public void updateTime(int position){
        txtvct.setText(formatTime(position));
        skbar.setProgress(position);
    }
    public String formatTime(long millis){
        int minutes = (int)(millis/1000)/60%60;
        int seconds = (int)(millis/1000)%60;
        return minutes+":"+seconds;
    }


    public void sendAction(String action){
        Intent intent = new Intent(this, MediaService.class);
        if(action == MusicConstants.ACTION.FORWARD_ACTION) intent.putExtra("progress", skbar.getProgress());
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }
}