package com.example.dictionaryv1;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.lang.ref.WeakReference;

public class AsyncTaskWait extends AsyncTask<Void, Void, Void> {

    private WeakReference<Context> context;

    public AsyncTaskWait(WeakReference context){
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void...params){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void nothing){
        Intent intent = new Intent("result");
        LocalBroadcastManager.getInstance(context.get().getApplicationContext()).sendBroadcast
                (intent);
    }


}