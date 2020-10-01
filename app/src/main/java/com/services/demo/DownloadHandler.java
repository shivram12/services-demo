package com.services.demo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.util.Log;

import com.services.demo.services.MyDownloadService;


public class DownloadHandler extends Handler {

    private static final String TAG = "MyTag";

    private MyDownloadService myDownloadService;

    private ResultReceiver resultReceiver;
    public DownloadHandler() {
    }

    @Override
    public void handleMessage(Message msg) {

//        myDownloadService.stopSelf(msg.arg1);
        downloadSong(msg.obj.toString());
        boolean stopSelfResult = myDownloadService.stopSelfResult(msg.arg1);
        Log.d(TAG, "handleMessage: Service Stop Result " + stopSelfResult  + " startId" + msg.arg1);

        Bundle bu = new Bundle();
        bu.putString(MainActivity.MESSAGE_KEY,msg.obj.toString());
        resultReceiver.send(MainActivity.RESULT_OK,bu);
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

    public void setDownloadService(MyDownloadService myDownloadService) {
        this.myDownloadService = myDownloadService;
    }

    public void setResultReceiver(ResultReceiver resultReceiver) {
        this.resultReceiver = resultReceiver;
    }
}
