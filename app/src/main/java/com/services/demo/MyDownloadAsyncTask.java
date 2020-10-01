package com.services.demo;

import android.os.AsyncTask;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class MyDownloadAsyncTask extends AsyncTask<String ,String,String> {
    @Override
    protected String doInBackground(String... songs) {

        for (String song  : songs){
            try {
                Thread.sleep(2000);
                publishProgress(song);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return "All songs have been downloaded";
    }


    @Override
    protected void onProgressUpdate(String... values) {
        Log.d(TAG, "onProgressUpdate: song Download " + values[0]);
        super.onProgressUpdate(values);
    }


    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, "onPostExecute: Result is " + s);
        super.onPostExecute(s);
    }
}
