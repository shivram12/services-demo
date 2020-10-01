package com.services.demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.services.demo.constants.Constants;
import com.services.demo.services.MusicPlayerService;
import com.services.demo.services.MyDownloadService;
import com.services.demo.services.MyForgroundService;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyTag";
    public static final String MESSAGE_KEY = "message_key";
    private ScrollView mScroll;
    private TextView mLog;
    private ProgressBar mProgressBar;
    private Handler mHandler;

    private MusicPlayerService musicPlayerService;
    private boolean mBound = false;
    private Button mPlayButton;

    private ServiceConnection mServiceCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {

            MusicPlayerService.MyserviceBinder myServiceBinder=
                    (MusicPlayerService.MyserviceBinder) iBinder;
            musicPlayerService=myServiceBinder.getService();
            mBound=true;
            Log.d(TAG, "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
        }
    };


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            String songName=intent.getStringExtra(MESSAGE_KEY);
            String result=intent.getStringExtra(MESSAGE_KEY);
            if(result == "done")
                mPlayButton.setText("Play");

            //log(songName+" Downloaded...");

            Log.d(TAG, "onReceive: Thread name: "+Thread.currentThread().getName());
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        mHandler=new Handler();

    }

    public void runCode(View v) {
        log("Running code");
        displayProgressBar(true);

        //send intent to download service

//        ResultReceiver resultReceiver=new MyDownlaodResultReceiver(null);


        Intent intent=new Intent(MainActivity.this, MyForgroundService.class);
        startService(intent);

        /*for (String song:Playlist.songs){
            Intent intent=new Intent(MainActivity.this, MyDownloadService.class);
            intent.putExtra(MESSAGE_KEY,song);
            intent.putExtra(Intent.EXTRA_RESULT_RECEIVER,resultReceiver);

            startService(intent);
        }*/

    }

    public void onBtnMusicClicked(View view) {

        if(mBound){

            if(musicPlayerService.isPlaying()){
                musicPlayerService.pause();
                mPlayButton.setText("Play");
            }else{
                Intent intent=new Intent(MainActivity.this,MusicPlayerService.class);
                intent.setAction(Constants.MUSIC_SERVICE_ACTION_START);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent);
                }else{
                    startService(intent);
                }

                musicPlayerService.play();
                mPlayButton.setText("Pause");
            }

        }

    }


    private void initViews() {
        mScroll = (ScrollView) findViewById(R.id.scrollLog);
        mLog = (TextView) findViewById(R.id.tvLog);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mPlayButton=findViewById(R.id.btnPlayMusic);

    }

    public void clearOutput(View v) {
        Intent intent=new Intent(MainActivity.this, MyDownloadService.class);
        stopService(intent);
        mLog.setText("");
        scrollTextToEnd();
    }

    public void log(String message) {
        Log.i(TAG, message);
        mLog.append(message + "\n");
        scrollTextToEnd();
    }

    private void scrollTextToEnd() {
        mScroll.post(new Runnable() {
            @Override
            public void run() {
                mScroll.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    public void displayProgressBar(boolean display) {
        if (display) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();

        Intent intent=new Intent(MainActivity.this,MusicPlayerService.class);
        bindService(intent,mServiceCon, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mReceiver,new IntentFilter(MusicPlayerService.MUSIC_COMPLETE));
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mBound){
            unbindService(mServiceCon);
            mBound=false;
        }
        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(mReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called");
    }

    public class MyDownlaodResultReceiver extends ResultReceiver {

        public MyDownlaodResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

            if (resultCode == RESULT_OK && resultData != null) {

                Log.d(TAG, "onReceiveResult: Thread name: " + Thread.currentThread().getName());

                final String songName = resultData.getString(MESSAGE_KEY);

//                MainActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        log(songName+" Downloaded");
//                    }
//                });

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        log(songName + " Downloaded");
                    }
                });

            }

        }
    }
}