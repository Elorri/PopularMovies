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

    private static final String LOG_TAG = "PopularMovies";
    private static final int VIEW_TYPE_IMAGE_VIEW = 0;
    private static final int VIEW_TYPE_TEXT_VIEW = 1;
    TmdbAccess tmdbAccess;


    private static final int VIEW_TYPE_COUNT = 2;
    private boolean mIsPosterImage=false;


    public MoviesAdapter(Context context, Cursor c, int flags, TmdbAccess tmdbAccess) {
        super(context, c, flags);
        this.tmdbAccess = tmdbAccess;
    }


    //No need for a ViewHolder class because the view parameter of bindView method gives us the root view, and that's the one we want to set. No need to travel the view tree in this case


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        if (cursor.getString(MainFragment.COL_POSTER_PATH)==null) { //The poster image doesn't exist. Display the movie title instead
            mIsPosterImage = false;
        } else {//The poster image exists, we can display the image
            mIsPosterImage = true;
        }
        View customView=null;
        LayoutInflater inflater = LayoutInflater.from(context);
        int viewType = getItemViewType(0); //Our getItemViewType does not make use of the position, any int parameter would work, we put 0
        switch (viewType) {
            case VIEW_TYPE_TEXT_VIEW: {
                customView = inflater.inflate(R.layout.grid_item_textview, parent, false);
                ((TextView) customView).setText(cursor.getString(MainFragment.COL_TITLE));
                break;
            }
            case VIEW_TYPE_IMAGE_VIEW: {
                customView = inflater.inflate(R.layout.grid_item_imageview, parent, false);
                URL posterURL = tmdbAccess.constructPosterImageURL(cursor.getString(MainFragment.COL_POSTER_PATH));
                Picasso.with(context).load(posterURL.toString()).into((ImageView) customView);
                break;
            }
        }
        customView.setTag(parent);
        return customView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (cursor.getString(MainFragment.COL_POSTER_PATH)==null) { //The poster image doesn't exist. Display the movie title instead
            mIsPosterImage = false;
        } else {//The poster image exists, we can display the image
            mIsPosterImage = true;
        }
        int viewType = getItemViewType(0); //Our getItemViewType does not make use of the position, any int parameter would work, we put 0
        ViewGroup parent=((ViewGroup)view.getTag());
        switch (viewType) {
            case VIEW_TYPE_TEXT_VIEW: {
                if (view instanceof ImageView) {
                    LayoutInflater inflater = LayoutInflater.from(context);
                    parent.removeViewInLayout(view);
                    view = inflater.inflate(R.layout.grid_item_textview, parent, false); //change the previous TextView by a new ImageView
                }
                ((TextView) view).setText(cursor.getString(MainFragment.COL_TITLE));
                break;
            }
            case VIEW_TYPE_IMAGE_VIEW: {
                if (view instanceof TextView) {
                    LayoutInflater inflater = LayoutInflater.from(context);
                    view = inflater.inflate(R.layout.grid_item_imageview, parent, false); //change the previous ImageView by a new TextView
                    parent.removeAllViews();
                    parent.addView(view);
                }
                URL posterURL = tmdbAccess.constructPosterImageURL(cursor.getString(MainFragment.COL_POSTER_PATH));
                Picasso.with(context).load(posterURL.toString()).into((ImageView) view);
                break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mIsPosterImage ? VIEW_TYPE_IMAGE_VIEW : VIEW_TYPE_TEXT_VIEW;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }
}
