package com.nqc.nqccuoiki;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;


import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;



public class MediaService extends Service implements MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener  {

    private final static String FOREGROUND_CHANNEL_ID = "foreground_channel_id";
    private final static String TAG = MediaService.class.getSimpleName();
    private static Song song;
    static private int sStateService = MusicConstants.STATE_SERVICE.NOT_INIT;
    static private String stateMedia = MusicConstants.STATE_MEDIA.NONE;
    private final Handler mHandler = new Handler();
    private NotificationManager mNotificationManager;
    private ArrayList<Song> songs;
    private MediaPlayer player;
    private boolean shuffle = false;
    private NotificationCompat.Builder lNotificationBuilder;
    private Runnable mDelayedShutdown = new Runnable() {

        public void run() {
            pause();
            stopForeground(true);
            stopSelf();
        }

    };
    private Runnable runTime = new Runnable() {
        public void run() {
            Bundle bundle = new Bundle();
            Log.d(TAG, "run: timerun");
            bundle.putInt("position", getCurrentPosition());
            bundle.putString("action",MusicConstants.ACTION.UPDATE_TIME_ACTION);
            sendAction(bundle);
            mHandler.postDelayed(runTime,1000);
        }

    };

    public static int getServiceState() {
        return sStateService;
    }

    public static String getMediaState() {
        return stateMedia;
    }

    public static Song getSong(){
        return song;
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(MediaService.class.getSimpleName(), "onCreate()");
        sStateService = MusicConstants.STATE_SERVICE.NOT_INIT;
        initPlayer();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    public void setSong(Song song){
        this.song = song;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        if (intent == null || intent.getAction() == null) {
            Log.d(TAG, "onStartCommand: null action");
            stopForeground(true);
            stopSelf();
            return START_NOT_STICKY;
        }
        switch (intent.getAction()) {
            case MusicConstants.ACTION.START_ACTION:
                Log.i(TAG, "Received start Intent ");
                song = (Song)intent.getSerializableExtra("song");
                songs = (ArrayList<Song>) intent.getSerializableExtra("songs");
                sStateService = MusicConstants.STATE_SERVICE.PLAY;
                play(song.getSource());
                Intent intentAudio = new Intent(this, AudioActivity.class);
                intentAudio.putExtra("song", song);
                intentAudio.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentAudio);
                startForeground(MusicConstants.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification());
                break;
            case MusicConstants.ACTION.PRE_ACTION:
                sStateService = MusicConstants.STATE_SERVICE.PLAY;
                Log.i(TAG, "Clicked Pre");
                int pos = songs.indexOf(song);
                song = songs.get((pos == 0)? songs.size()-1: pos-1);
                play(song.getSource());
                newSong();
                mNotificationManager.notify(MusicConstants.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification());
                break;
            case MusicConstants.ACTION.NEXT_ACTION:
                Log.i(TAG, "Clicked Next");
                sStateService = MusicConstants.STATE_SERVICE.PLAY;
                nextSong();
                mNotificationManager.notify(MusicConstants.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification());
                break;
            case MusicConstants.ACTION.PAUSE_ACTION:
                pause();
                break;
            case MusicConstants.ACTION.PLAY_ACTION:
                sStateService = MusicConstants.STATE_SERVICE.PLAY;
                Log.i(TAG, "Clicked Play");
                player.start();
                mNotificationManager.notify(MusicConstants.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification());
                mHandler.postDelayed(runTime,100);
                setAct();
                sendAction(MusicConstants.ACTION.PLAY_ACTION);
                break;

            case MusicConstants.ACTION.STOP_ACTION:
                Log.i(TAG, "Received Stop Intent");
                sStateService = MusicConstants.STATE_SERVICE.NOT_INIT;
                destroy();
                stopForeground(true);
                stopSelf();
                break;

            case MusicConstants.ACTION.FORWARD_ACTION:
                Log.d(TAG, "onStartCommand: forward");
                mHandler.removeCallbacks(runTime);
                int progress = intent.getIntExtra("progress",0);
                player.seekTo(progress);
                break;

            case MusicConstants.ACTION.LOOP_ACTION:
                Log.d(TAG, "onStartCommand: loop");
                setLoop();
                break;

            case MusicConstants.ACTION.SHUFFLE_ACTION:
                Log.d(TAG, "onStartCommand: shuffle");
                setShuffle();
                break;

            case MusicConstants.ACTION.CLOCK_ACTION:
                Log.d(TAG, "onStartCommand: clock");
                mHandler.postDelayed(mDelayedShutdown, 10000);
                break;
            default:
                Log.d(TAG, "onStartCommand: none");
                destroy();
                stopForeground(true);
                stopSelf();
                break;
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        destroy();
        sStateService = MusicConstants.STATE_SERVICE.NOT_INIT;
        super.onDestroy();
    }
    public void newSong(){
        if (stateMedia == MusicConstants.STATE_MEDIA.LOOP) stateMedia = MusicConstants.STATE_MEDIA.NONE;
        player.setLooping(false);
        Bundle bundle = new Bundle();
        bundle.putSerializable("song", song);
        bundle.putString("action", "new_song");
        sendAction(bundle);
    }
    public void pause(){
        sStateService = MusicConstants.STATE_SERVICE.PAUSE;
        Log.i(TAG, "Clicked Pause");
        player.pause();
        setAct();
        mNotificationManager.notify(MusicConstants.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification());
        sendAction(MusicConstants.ACTION.PAUSE_ACTION);
        mHandler.removeCallbacks(runTime);
    }
    public void initPlayer(){
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnErrorListener(this);
        player.setOnPreparedListener(this);
        player.setOnBufferingUpdateListener(this);
        player.setOnSeekCompleteListener(this);
        player.setOnCompletionListener(this);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        player.start();
        Bundle bundle = new Bundle();
        bundle.putString("action", MusicConstants.ACTION.PREPARED_ACTION);
        bundle.putInt("duration", getDuration());
        sendAction(bundle);
        mHandler.postDelayed(runTime, 1000);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        sendAction(MusicConstants.ACTION.ERROR_ACTION);
        play(song.getSource());
        return false;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.d(TAG, "Player onBufferingUpdate():" + percent);
    }

    @Override
    public void onCompletion(MediaPlayer md){
        if(shuffle){
            Log.d(TAG, "onCompletion: random");
            play(songs.get(new Random().nextInt(songs.size())).getSource());
            newSong();
        }
        else{
            nextSong();
            Log.d(TAG, "onCompletion: next");
        }
    }

    @Override
    public void onSeekComplete (MediaPlayer mp){
        mHandler.postDelayed(runTime, 1000);
        if(!player.isPlaying()) player.start();
    }

    public void destroy() {
        if (player != null) {
            try {
                player.reset();
                player.release();
                Log.d(TAG, "Player destroyed");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                player = null;
            }
        }

    }
    public void play(String source){
        player.reset();
        try {
            player.setDataSource(source);
            player.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public int getCurrentPosition(){
        return player.getCurrentPosition();
    }

    public int getDuration(){
        return player.getDuration();
    }


    public void nextSong(){
        int pos = songs.indexOf(song);
        pos = (pos == (songs.size() - 1)) ? 0 : (pos + 1);
        song = songs.get(pos);
        play(song.getSource());
        newSong();
    }
    public void setLoop()
    {
        if (player.isLooping()) {
            stateMedia = MusicConstants.STATE_MEDIA.NONE;
            player.setLooping(false);
        }
        else{
            player.setLooping(true);
            stateMedia = MusicConstants.STATE_MEDIA.LOOP;

        }
    }

    public void setShuffle(){
        if(shuffle){
            shuffle = false;
            stateMedia = MusicConstants.STATE_MEDIA.NONE;
        }
        else{
            player.setLooping(false);
            shuffle = true;
            stateMedia = MusicConstants.STATE_MEDIA.SHUFFLE;
        }

    }
    private void setAct(){
        Intent lPauseIntent = new Intent(this, MediaService.class);
        lPauseIntent.setAction(MusicConstants.ACTION.PAUSE_ACTION);
        PendingIntent lPendingPauseIntent = PendingIntent.getService(this, 0, lPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent playIntent = new Intent(this, MediaService.class);
        playIntent.setAction(MusicConstants.ACTION.PLAY_ACTION);
        PendingIntent lPendingPlayIntent = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        switch (sStateService) {
            case MusicConstants.STATE_SERVICE.PAUSE:
                Log.d(TAG, "setAct: pause");
                lNotificationBuilder.addAction(R.drawable.outline_play_circle_outline_white_48,"play",lPendingPlayIntent);
                break;

            case MusicConstants.STATE_SERVICE.PLAY:
                Log.d(TAG, "setAct: play");
                lNotificationBuilder.addAction(R.drawable.pause,"pause",lPendingPauseIntent);
                break;
        }
    }
    private Notification prepareNotification() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O &&
                mNotificationManager.getNotificationChannel(FOREGROUND_CHANNEL_ID) == null) {
            // The user-visible name of the channel.
            CharSequence name = "getString(R.string.text_value_radio_notification)";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(FOREGROUND_CHANNEL_ID, name, importance);
            mChannel.setSound(null, null);
            mChannel.enableVibration(false);
            mNotificationManager.createNotificationChannel(mChannel);
        }
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(MusicConstants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent lNextIntent = new Intent(this, MediaService.class);
        lNextIntent.setAction(MusicConstants.ACTION.NEXT_ACTION);
        PendingIntent lPendingNextIntent = PendingIntent.getService(this, 0, lNextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent lPreIntent = new Intent(this, MediaService.class);
        lPreIntent.setAction(MusicConstants.ACTION.PRE_ACTION);
        PendingIntent lPendingPreIntent = PendingIntent.getService(this, 0, lPreIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            lNotificationBuilder = new NotificationCompat.Builder(this, FOREGROUND_CHANNEL_ID);
        } else {
            lNotificationBuilder = new NotificationCompat.Builder(this);
        }

        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(this, "tag");
        lNotificationBuilder
                .setSmallIcon(R.drawable.ic_baseline_music_note_24)
                .setSubText("Audio")
                .setOnlyAlertOnce(true)
                .setContentTitle(song.getSong())
                .setContentText(song.getSinger())
                .addAction(R.drawable.back, "back", lPendingPreIntent)
                .addAction(R.drawable.next, "next", lPendingNextIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 2, 1)
                        .setMediaSession(mediaSessionCompat.getSessionToken()));
        setAct();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            lNotificationBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        }
        return lNotificationBuilder.build();

    }

    public void sendAction(String action){
        Intent intent = new Intent("receive data");
        Bundle bundle = new Bundle();
        bundle.putString("action",action);
        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    public void sendAction(Bundle bundle){
        Intent intent = new Intent("receive data");
        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


}