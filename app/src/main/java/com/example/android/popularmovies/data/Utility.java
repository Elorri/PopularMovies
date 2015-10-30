package com.example.android.popularmovies.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.android.popularmovies.R;

/**
 * Created by Elorri on 30/10/2015.
 */
public class Utility {

    public static String getSortOrderPreferences(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPrefs.getString(context.getString(R.string.pref_sort_order_key), context.getString(R.string.pref_sort_order_popularity));

    }
}
