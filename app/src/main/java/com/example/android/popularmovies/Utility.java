package com.example.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Elorri on 30/10/2015.
 */
public class Utility {

    private static final String LOG_TAG = Utility.class.getSimpleName();


    public static String getSortOrderPreferences(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPrefs.getString(context.getString(R.string.pref_sort_order_key), context.getString(R.string.pref_sort_order_popularity));

    }


    public static String getShortString(String aString, int size) {
        if (aString.length() > size)
            return aString.substring(0, size - 3) + "...";
        return aString;
    }

    public static boolean isFavorite(int value) {
        if (value == 1) return true;
        return false;
    }


    public static int convert(int n) {
        return Integer.valueOf(String.valueOf(n), 16);
    }

    public static int getDbFavoriteValue(boolean mFavoriteValue) {
        if (mFavoriteValue) return 1;
        return 0;
    }

    public static URL buildYoutubeThumbnailTrailerURL(String key) {
        try {
            final String BASE_URL = "http://img.youtube.com/vi/";
            final String DEFAULT_PARAM = "default.jpg";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(key)
                    .appendPath(DEFAULT_PARAM)
                    .build();
            return new URL(builtUri.toString());

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error " + e);
            return null;
        }
    }

    public static Uri buildYoutubeVideoURI(String key) {
        final String BASE_URL = "https://www.youtube.com/watch";
        final String QUERY_PARAM = "v";

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, key)
                .build();
        return builtUri;

    }

    public static String thread() {
        if (Looper.getMainLooper().getThread() == Thread.currentThread())
            return "ThreadUI";
        else return "Background";
    }

    /**
     * Compare the second Uri to the first and return true if equals, false if not
     * @param uri1 first uri
     * @param uri2 second uri to compare to the first
     * @return true if the 2 uris are equals, false otherwise
     */
    public static boolean compareUris(Uri uri1, Uri uri2) {
        return uri1.toString().equals(uri2.toString());
    }


    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService
                (Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}
