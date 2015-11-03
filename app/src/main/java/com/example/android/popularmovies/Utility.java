package com.example.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Elorri on 30/10/2015.
 */
public class Utility {

    public static String getSortOrderPreferences(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPrefs.getString(context.getString(R.string.pref_sort_order_key), context.getString(R.string.pref_sort_order_popularity));

    }


    public static String getShortString(String aString, int size) {
        if (aString.length() > size)
            return aString.substring(0, size - 3) + "...";
        return aString;
    }

    public static boolean isFavorite(String favoriteDbString) {
        if (favoriteDbString.equals("1")) return true;
        return false;
    }


    public static int convert(int n) {
        return Integer.valueOf(String.valueOf(n), 16);
    }
}
