package com.example.android.popularmovies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Elorri-user on 03/10/2015.
 * This class listen to internet connection changes and inform the activities needing internet that it's time to refresh
 */
public abstract class InternetReceiver extends BroadcastReceiver {

    private Context context;


    public InternetReceiver(Context context){
        this.context=context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (isConnected()) {
            // The device is connected to the internet, we can update the screen.
            refresh();
        }
    }


    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    /**
     * This method is called whenever the connection is back and it's time to refresh the screen.
     */
    protected abstract void refresh();
}
