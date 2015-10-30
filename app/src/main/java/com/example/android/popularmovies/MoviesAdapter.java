package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;

/**
 * {@link MoviesAdapter} exposes a list of movie thumbnail
 * from a {@link android.database.Cursor} to a {@link android.widget.GridView}.
 */
public class MoviesAdapter extends CursorAdapter {

    TmdbAccess tmdbAccess;


    public MoviesAdapter(Context context, Cursor c, int flags, TmdbAccess tmdbAccess) {
        super(context, c, flags);
        this.tmdbAccess=tmdbAccess;
    }


    //No need for a ViewHolder class because the view parameter of bindView method gives us the root view, and that's the one we want to set. No need to travel the view tree in this case

    /*
       These views are reused as needed.
    */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View customView;
        LayoutInflater inflater = LayoutInflater.from(context);
        if (cursor.getString(MainFragment.COL_POSTER_PATH).equals(null)) { //The poster image doesn't exist. Display the movie title instead
                customView = inflater.inflate(R.layout.grid_item_layout_default, parent, false);
            ((TextView) customView).setText(cursor.getString(MainFragment.COL_TITLE));
        } else {//The poster image exists, we can display the image
                customView = inflater.inflate(R.layout.grid_item_layout, parent, false);
            URL posterURL = tmdbAccess.constructPosterImageURL(cursor.getString(MainFragment.COL_POSTER_PATH));
            Picasso.with(context).load(posterURL.toString()).into((ImageView) customView);
        }
        return customView;
    }

    /*
     This is where we fill-in the views with the contents of the cursor.
  */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (cursor.getString(MainFragment.COL_POSTER_PATH).equals(null)) { //The poster image doesn't exist. Display the movie title instead
            if  (view instanceof ImageView) {
                LayoutInflater inflater = LayoutInflater.from(context);
                view = inflater.inflate(R.layout.grid_item_layout_default, null, false); //change the previous ImageView by a new TextView
            }
            ((TextView) view).setText(cursor.getString(MainFragment.COL_TITLE));
        } else {//The poster image exists, we can display the image
            if (view instanceof TextView) {
                LayoutInflater inflater = LayoutInflater.from(context);
                view = inflater.inflate(R.layout.grid_item_layout, null, false); //change the previous TextView by a new ImageView
                 }
            URL posterURL = tmdbAccess.constructPosterImageURL(cursor.getString(MainFragment.COL_POSTER_PATH));
            Picasso.with(context).load(posterURL.toString()).into((ImageView) view);
        }
    }
}
