package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
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

    /*
       These views are reused as needed.
    */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        Log.e(LOG_TAG, "new view poster_path :" + cursor.getString(MainFragment.COL_POSTER_PATH)+" - position : "+cursor.getPosition());
        Log.e(LOG_TAG, "new view poster_path : (cursor.getString(MainFragment.COL_POSTER_PATH)==null) " + (cursor.getString(MainFragment.COL_POSTER_PATH)==null));
        Log.e(LOG_TAG, "new view poster_path : COL_TITLE " + cursor.getString(MainFragment.COL_TITLE));
        if (cursor.getString(MainFragment.COL_POSTER_PATH)==null) { //The poster image doesn't exist. Display the movie title instead
            mIsPosterImage = false;
            Log.e(LOG_TAG, "poster image doesn't exist : " + cursor.getString(MainFragment.COL_POSTER_PATH));
        } else {//The poster image exists, we can display the image
            mIsPosterImage = true;
            Log.e(LOG_TAG, "poster image DOES exist : " + cursor.getString(MainFragment.COL_POSTER_PATH));
        }

        View customView=null;
        LayoutInflater inflater = LayoutInflater.from(context);
        int new_view_position=cursor.getPosition();
        int viewType = getItemViewType(new_view_position);
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
        return customView;
    }

    /*
     This is where we fill-in the views with the contents of the cursor.
  */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.e(LOG_TAG, "bindView poster_path :" + cursor.getString(MainFragment.COL_POSTER_PATH)+" - position : "+cursor.getPosition());
        Log.e(LOG_TAG, "bindView poster_path : (cursor.getString(MainFragment.COL_POSTER_PATH)==null) " + (cursor.getString(MainFragment.COL_POSTER_PATH) == null));
        Log.e(LOG_TAG, "bindView poster_path : COL_TITLE " + cursor.getString(MainFragment.COL_TITLE));
        if (cursor.getString(MainFragment.COL_POSTER_PATH)==null) { //The poster image doesn't exist. Display the movie title instead
            mIsPosterImage = false;
            Log.e(LOG_TAG, "poster image doesn't exist : " + cursor.getString(MainFragment.COL_POSTER_PATH));
//            if (view instanceof ImageView) {
//                Log.e(LOG_TAG, "in instanceof ImageView : " + cursor.getString(MainFragment.COL_POSTER_PATH));
//                LayoutInflater inflater = LayoutInflater.from(context);
//                view = inflater.inflate(R.layout.grid_item_textView, null, false); //change the previous ImageView by a new TextView
//            }

        } else {//The poster image exists, we can display the image
            mIsPosterImage = true;
            Log.e(LOG_TAG, "poster image DOES exist : " + cursor.getString(MainFragment.COL_POSTER_PATH));
//            if (view instanceof TextView) {
//                Log.e(LOG_TAG, "in instanceof TextView : " + cursor.getString(MainFragment.COL_POSTER_PATH)+" view.getText() : "+((TextView)view).getText());
//                LayoutInflater inflater = LayoutInflater.from(context);
//                view = inflater.inflate(R.layout.grid_item_imageView, null, false); //change the previous TextView by a new ImageView
//            }

        }

        int viewType = getItemViewType(0); //Our getItemViewType does not make use of the position, any int parameter would work, we put 0
        switch (viewType) {
            case VIEW_TYPE_TEXT_VIEW: {
                //customView = inflater.inflate(R.layout.grid_item_textView, parent, false);
                ((TextView) view).setText(cursor.getString(MainFragment.COL_TITLE));
                break;
            }
            case VIEW_TYPE_IMAGE_VIEW: {
                //customView = inflater.inflate(R.layout.grid_item_imageView, parent, false);
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
