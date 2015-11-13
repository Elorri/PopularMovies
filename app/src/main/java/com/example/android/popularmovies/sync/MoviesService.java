package com.example.android.popularmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.android.popularmovies.Utility;

public class MoviesService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static MoviesSyncAdapter sMoviesSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility.thread() + " : " +
                " : MoviesService :  object created");
        Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility.thread() + " : " +
                " : sSyncAdapterLock :  object created");
        synchronized (sSyncAdapterLock) {
            if (sMoviesSyncAdapter == null) {
                Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility.thread() + " : " +
                        " : MoviesSyncAdapter :  object created");
                sMoviesSyncAdapter = new MoviesSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sMoviesSyncAdapter.getSyncAdapterBinder();
    }
}