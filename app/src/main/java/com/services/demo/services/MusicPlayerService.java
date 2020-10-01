package com.services.demo.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.services.demo.MainActivity;
import com.services.demo.R;
import com.services.demo.constants.Constants;

public class MusicPlayerService extends Service {

    private final Binder binder =new MyserviceBinder();
    public static final String MUSIC_COMPLETE = "MusicComplete";

    private static final String TAG = "MusicPlayer";
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.test);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Intent intent=new Intent(MUSIC_COMPLETE);
                intent.putExtra(MainActivity.MESSAGE_KEY,"done");
                LocalBroadcastManager.getInstance(getApplicationContext())
                        .sendBroadcast(intent);
                stopForeground(true);

                stopSelf();

            }
        });
        Log.d(TAG, "onCreate: called");
    }

    public  class MyserviceBinder extends Binder{
        public MusicPlayerService getService(){
            return MusicPlayerService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: called");

        switch (intent.getAction()) {

            case Constants.MUSIC_SERVICE_ACTION_PLAY: {
                Log.d(TAG, "onStartCommand: play called");
                play();
                break;
            }
            case Constants.MUSIC_SERVICE_ACTION_PAUSE: {
                Log.d(TAG, "onStartCommand: pause called");
                pause();
                break;
            }
            case Constants.MUSIC_SERVICE_ACTION_STOP: {
                Log.d(TAG, "onStartCommand: stop called");
                stopForeground(true);
                stopSelf();
            }
            case Constants.MUSIC_SERVICE_ACTION_START: {
                Log.d(TAG, "onStartCommand: start called");
                showNotification();
                break;
            }
            default: {

            }
        }
        return START_NOT_STICKY;
    }


    private void showNotification() {

        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,"FileDownload");

        //Intent for play button
        Intent pIntent=new Intent(this,MusicPlayerService.class);
        pIntent.setAction(Constants.MUSIC_SERVICE_ACTION_PLAY);

        PendingIntent playIntent=PendingIntent.getService(this,100,pIntent,0);

        //Intent for pause button
        Intent psIntent=new Intent(this,MusicPlayerService.class);
        psIntent.setAction(Constants.MUSIC_SERVICE_ACTION_PAUSE);

        PendingIntent pauseIntent=PendingIntent.getService(this,100,psIntent,0);

        //Intent for stop button
        Intent sIntent=new Intent(this,MusicPlayerService.class);
        sIntent.setAction(Constants.MUSIC_SERVICE_ACTION_STOP);

        PendingIntent stopIntent=PendingIntent.getService(this,100,sIntent,0);

        builder.setContentTitle("U4Universe Music Player")
                .setContentText("This is demo music player")
                .setSmallIcon(R.mipmap.ic_launcher)
                .addAction(new NotificationCompat.Action(android.R.drawable.ic_media_play,"Play",playIntent))
                .addAction(new NotificationCompat.Action(android.R.drawable.ic_media_play,"Pause",pauseIntent))
                .addAction(new NotificationCompat.Action(android.R.drawable.ic_media_play,"Stop",stopIntent));


        startForeground(123,builder.build());

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: ");
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(TAG, "onRebind: ");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
        mediaPlayer.release();
    }

    //public client methods

    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }

    public void play(){
        mediaPlayer.start();
    }

    public void pause(){
        mediaPlayer.pause();
    }

}
