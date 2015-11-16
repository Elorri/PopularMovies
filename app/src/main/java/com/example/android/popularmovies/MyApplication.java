package com.example.android.popularmovies;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by Elorri on 15/11/2015.
 */
public class MyApplication extends Application {
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
