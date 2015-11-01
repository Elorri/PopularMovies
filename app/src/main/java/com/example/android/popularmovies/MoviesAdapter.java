package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.net.URL;

/**
 * {@link MoviesAdapter} exposes a list of movie thumbnail
 * from a {@link android.database.Cursor} to a {@link android.widget.GridView}.
 */
public class MoviesAdapter extends CursorAdapter {

    private static final String LOG_TAG = "PopularMovies";

    TmdbAccess tmdbAccess;


    public MoviesAdapter(Context context, Cursor c, int flags, TmdbAccess tmdbAccess) {
        super(context, c, flags);
        this.tmdbAccess = tmdbAccess;
    }


    //No need for a ViewHolder class because the view parameter of bindView method gives us the root view, and that's the one we want to set. No need to travel the view tree in this case


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_imageview, parent, false);
        URL posterURL = tmdbAccess.constructPosterImageURL(cursor.getString(MainFragment.COL_POSTER_PATH));
//        TextDrawable noPoster = new TextDrawable(cursor.getString(MainFragment.COL_TITLE), context);
//        Picasso.with(context).load(posterURL.toString()).error(noPoster).into((ImageView) view);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        URL posterURL = tmdbAccess.constructPosterImageURL(cursor.getString(MainFragment.COL_POSTER_PATH));
        TextDrawable noPoster = new TextDrawable(cursor.getString(MainFragment.COL_TITLE), context);
        Picasso.with(context).load(posterURL.toString()).error(noPoster).into((ImageView) view);

    }


}
