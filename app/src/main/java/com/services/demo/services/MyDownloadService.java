package com.services.demo.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.os.ResultReceiver;
import android.util.Log;

import com.services.demo.DownloadThread;
import com.services.demo.MainActivity;
import com.services.demo.MyDownloadAsyncTask;

public class MyDownloadService extends Service {
    private static final String TAG = "MyDownnload";

    private DownloadThread downloadThread;
    public MyDownloadService() {
    }

    @Override
    public void onCreate() {

        Log.d(TAG, "onCreate: called ");
        super.onCreate();
        downloadThread =  new DownloadThread();
        downloadThread.start();

        while (downloadThread.mHandler == null){

        }
        downloadThread.mHandler.setDownloadService(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String songName=intent.getStringExtra(MainActivity.MESSAGE_KEY);

        MyDownloadAsyncTask myDownloadAsyncTask = new MyDownloadAsyncTask();
        myDownloadAsyncTask.execute(songName);

        downloadThread.mHandler.setResultReceiver((ResultReceiver) intent.getParcelableExtra(Intent.EXTRA_RESULT_RECEIVER));

        Message message = Message.obtain();
        message.obj = songName;
        message.arg1 = startId;
        downloadThread.mHandler.sendMessage(message);
        Log.d(TAG, "onStartCommand: called " + Thread.currentThread().getName());
        Log.d(TAG, "onStartCommand: called " + intent.getStringExtra(MainActivity.MESSAGE_KEY)
         + "with startId "  + startId);
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: called");
       return null;
    }
    private void downloadSong(final String songName){
        Log.d(TAG, "run: staring download");
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        Log.d(TAG, "downloadSong: "+songName+" Downloaded...");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: called");
        super.onDestroy();
    }


}
